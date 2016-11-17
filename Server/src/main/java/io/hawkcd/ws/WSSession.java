/*
 * Copyright (C) 2016 R&D Solutions Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.hawkcd.ws;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import io.hawkcd.core.RequestProcessor;
import io.hawkcd.core.WsObjectProcessor;
import io.hawkcd.core.session.ISessionManager;
import io.hawkcd.core.session.SessionFactory;
import io.hawkcd.model.SessionDetails;
import io.hawkcd.utilities.constants.LoggerMessages;
import io.hawkcd.utilities.deserializers.MaterialDefinitionAdapter;
import io.hawkcd.utilities.deserializers.TaskDefinitionAdapter;
import io.hawkcd.utilities.deserializers.TokenAdapter;
import io.hawkcd.utilities.deserializers.WsContractDeserializer;
import io.hawkcd.model.MaterialDefinition;
import io.hawkcd.model.ServiceResult;
import io.hawkcd.model.TaskDefinition;
import io.hawkcd.model.User;
import io.hawkcd.model.dto.WsContractDto;
import io.hawkcd.model.enums.NotificationType;
import io.hawkcd.model.payload.TokenInfo;
import io.hawkcd.services.UserService;
import io.hawkcd.services.filters.PermissionService;
import io.hawkcd.services.filters.factories.SecurityServiceInvoker;
import io.hawkcd.services.interfaces.IUserService;

import org.apache.log4j.Logger;
import org.eclipse.jetty.websocket.api.RemoteEndpoint;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.WebSocketAdapter;

import java.io.IOException;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.UUID;

public class WSSession extends WebSocketAdapter {
    private static final Logger LOGGER = Logger.getLogger(WSSession.class.getClass());
    private Gson jsonConverter;
    private String id;
    private SecurityServiceInvoker securityServiceInvoker;
    private User loggedUser;
    private PermissionService permissionService;
    private IUserService userService;
    private WsObjectProcessor wsObjectProcessor;
    private RequestProcessor requestProcessor;

    public SessionDetails getSessionDetails() {
        return this.sessionDetails;
    }

    private SessionDetails sessionDetails;

    public WSSession() {
        this.id = UUID.randomUUID().toString();
        this.jsonConverter = new GsonBuilder()
                .registerTypeAdapter(WsContractDto.class, new WsContractDeserializer())
                .registerTypeAdapter(TaskDefinition.class, new TaskDefinitionAdapter())
                .registerTypeAdapter(MaterialDefinition.class, new MaterialDefinitionAdapter())
                .create();
        this.securityServiceInvoker = new SecurityServiceInvoker();
        this.permissionService = new PermissionService();
        this.userService = new UserService();
        this.wsObjectProcessor = new WsObjectProcessor();
        this.requestProcessor = new RequestProcessor();
        this.sessionDetails =  new SessionDetails(this.getId());
    }

    public String getId() {
        return this.id;
    }

    public User getLoggedUser() {
        return this.loggedUser;
    }

    public User getLoggedUserFromDatabase() {
        return (User) this.userService.getById(this.loggedUser.getId()).getObject();
    }

    public void setLoggedUser(User loggedUser) {
        this.loggedUser = loggedUser;
    }

    @Override
    public void onWebSocketConnect(Session session) {
        super.onWebSocketConnect(session);
        initialize(session);
    }

    @Override
    public void onWebSocketText(String message) {
        execute(message);
    }

    @Override
    public void onWebSocketClose(int statusCode, String reason) {
        super.onWebSocketClose(statusCode, reason);

        //update sessionDetails
        this.sessionDetails.setActive(false);
        this.sessionDetails.setEndTime(ZonedDateTime.now(ZoneOffset.UTC).toLocalDateTime());

        final ISessionManager sessionManager = SessionFactory.getSessionManager();
        sessionManager.closeSession(this.id);
    }

    @Override
    public void onWebSocketError(Throwable cause) {
        super.onWebSocketError(cause);
        cause.printStackTrace(System.err);
    }

    public WsContractDto resolve(String message) {
        WsContractDto contract = null;
        try {
            contract = this.jsonConverter.fromJson(message, WsContractDto.class);
            //UserContext userContext = new UserContext(this);
            //contract.setUserContext(userContext); //new AppContext();
        } catch (JsonParseException e) {
            e.printStackTrace();
        }

        return contract;
    }

    public void send(WsContractDto contract) {
        if ((this.getSession() == null) || !this.getSession().isOpen()) {
            return;
        }

        RemoteEndpoint remoteEndpoint = this.getSession().getRemote();
        String jsonResult = this.jsonConverter.toJson(contract);
        remoteEndpoint.sendStringByFuture(jsonResult);
    }

    private void errorDetails(WsContractDto contract, Gson serializer, Exception e, RemoteEndpoint endPoint) {
        contract.setNotificationType(NotificationType.ERROR);
        contract.setErrorMessage(e.getMessage());
        try {
            String errDetails = serializer.toJson(contract);
            endPoint.sendString(errDetails);
        } catch (IOException | RuntimeException e1) {
            e1.printStackTrace();
        }
    }

    private void execute(String message) {

        if (this.getSession().isOpen())
        {
            try {

                WsContractDto contract  = this.resolve(message);
                if (contract == null) {
                    throw new RuntimeException("Resoluiton failed for object" + contract);
                }

                try {

                    this.requestProcessor.prorcessRequest1(contract, this.getLoggedUser(), this.getId());

                } catch (InstantiationException e) {
                    LOGGER.error(e);
                } catch (IllegalAccessException e) {
                    LOGGER.error(e);
                } catch (ClassNotFoundException e) {
                    LOGGER.error(e);
                }

            } catch (RuntimeException e) {
                LOGGER.error(e);
            }
        }
    }

    private void initialize(Session session) {

        String tokenQuery = session.getUpgradeRequest().getQueryString();

        if (!tokenQuery.equals("token=null")) {
            String token = tokenQuery.substring(6);
            TokenInfo tokenInfo = TokenAdapter.verifyToken(token);
            if (tokenInfo == null) {
                return;
            }

            User usr = tokenInfo.getUser();
            this.setLoggedUser(usr);

            //Fill in the sessionDetails
            this.sessionDetails.setUserId(usr.getId());
            this.sessionDetails.setUserEmail(usr.getEmail());
            this.sessionDetails.setActive(true);

            ISessionManager sessionManager = SessionFactory.getSessionManager();
            sessionManager.addSession(this);
        }
    }

}