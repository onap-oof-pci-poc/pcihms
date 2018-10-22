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

package com.wipro.www.pcims.dmaap;

import com.att.nsa.cambria.client.CambriaBatchingPublisher;
import com.att.nsa.cambria.client.CambriaClientBuilders.PublisherBuilder;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;

public class NotificationProducer {

    private List<String> servers;
    private String apiKey;
    private String secret;

    /**
     * Parameterised constructor.
     */
    public NotificationProducer(List<String> servers, String apiKey, String secret) {
        super();
        this.servers = servers;
        this.apiKey = apiKey;
        this.secret = secret;
    }

    /**
     * sends notification to dmaap.
     */
    public int sendNotification(String topic, String msg) throws GeneralSecurityException, IOException {
        CambriaBatchingPublisher cambriaBatchingPublisher = null;
        cambriaBatchingPublisher = new PublisherBuilder().usingHosts(servers).onTopic(topic)
                .authenticatedBy(apiKey, secret).build();
        return cambriaBatchingPublisher.send("", msg);

    }

}
