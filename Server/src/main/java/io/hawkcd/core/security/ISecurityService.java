/*
 *   Copyright (C) 2016 R&D Solutions Ltd.
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 *
 *
 */

package io.hawkcd.core.security;

import io.hawkcd.model.User;

/**
 * Created by rado on 13.11.16.
 */
public interface ISecurityService {

    /*
    * Returns tue, false if the user being evaluated has rights to perform an operation
    * @param user
    */
    boolean isAuthorized(User user);

    /*
    * returns all users that have access to perform the operation
    *
    */
    void getAllUsersWithPermissions();
}
