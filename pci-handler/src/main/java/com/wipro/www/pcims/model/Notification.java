/*******************************************************************************
 * ============LICENSE_START=======================================================
 * pcims
 *  ================================================================================
 *  Copyright (C) 2018 Wipro Limited.
 *  ==============================================================================
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 *   ============LICENSE_END=========================================================
 ******************************************************************************/

package com.wipro.www.pcims.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Notification {

    @JsonProperty("requestID")
    private String requestId;

    @JsonProperty("AAI")
    private String aai;

    @JsonProperty("from")
    private String from;

    @JsonProperty("version")
    private String version;

    @JsonProperty("Action")
    private String action;

    @JsonProperty("Payload")
    private NotificationPayload payload;

    public Notification() {

    }

    /**
     * Parameterized Constructor.
     */

    public Notification(String requestId, String aai, String from, String version, String action,
            NotificationPayload payload) {
        super();
        this.requestId = requestId;
        this.aai = aai;
        this.from = from;
        this.version = version;
        this.action = action;
        this.payload = payload;
    }

    public NotificationPayload getPayload() {
        return payload;
    }

    public void setPayload(NotificationPayload payload) {
        this.payload = payload;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getAai() {
        return aai;
    }

    public void setAai(String aai) {
        this.aai = aai;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

}
