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

import com.att.nsa.cambria.client.CambriaConsumer;
import com.wipro.www.pcims.dmaap.DmaapClient.NotificationCallback;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NotificationConsumer implements Runnable {

    private static Logger log = LoggerFactory.getLogger(NotificationConsumer.class);
    private CambriaConsumer cambriaConsumer;
    private NotificationCallback notificationCallback;

    /**
     * Parameterized Constructor.
     */
    public NotificationConsumer(CambriaConsumer cambriaConsumer, NotificationCallback notificationCallback) {
        super();
        this.cambriaConsumer = cambriaConsumer;
        this.notificationCallback = notificationCallback;
    }

    @Override
    public void run() {
        try {
            Iterable<String> msgs = cambriaConsumer.fetch();
            for (String msg : msgs) {
                log.debug(msg);
                notificationCallback.activateCallBack(msg);
            }
        } catch (IOException e) {
            log.debug("exception when fetching msgs from dmaap", e);
        }

    }
}
