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

package io.hawkcd.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.hawkcd.model.enums.MaterialType;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = GitMaterial.class, name = "GIT")})
public abstract class MaterialDefinition extends DbEntry {
    private String name;
    private String errorMessage;
    private MaterialType type;
    private boolean isPollingForChanges;

    public MaterialDefinition() {
        this.setErrorMessage("");
    }

    public String getName() {
        return this.name;
    }

    public void setName(String value) {
        this.name = value;
    }

    public String getErrorMessage() {
        return this.errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public MaterialType getType() {
        return this.type;
    }

    public void setType(MaterialType value) {
        this.type = value;
    }

    @JsonProperty("isPollingForChanges")
    public boolean isPollingForChanges() {
        return this.isPollingForChanges;
    }

    public void setPollingForChanges(boolean pollingForChanges) {
        this.isPollingForChanges = pollingForChanges;
    }
}