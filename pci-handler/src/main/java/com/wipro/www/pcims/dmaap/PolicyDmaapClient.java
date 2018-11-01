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

import com.wipro.www.pcims.Configuration;
import java.io.IOException;
import java.security.GeneralSecurityException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PolicyDmaapClient {

    private static Logger log = LoggerFactory.getLogger(PolicyDmaapClient.class);

    /**
     * Method stub for sending notification to policy.
     */
    public boolean sendNotificationToPolicy(String msg) {

        Configuration configuration = Configuration.getInstance();
        NotificationProducer notificationProducer = new NotificationProducer(configuration.getServers(),
                configuration.getPcimsApiKey(), configuration.getPcimsSecretKey());
        try {
            int result = notificationProducer.sendNotification(configuration.getPolicyTopic(), msg);
            log.debug("result: {}", result);
        } catch (GeneralSecurityException | IOException e) {
            log.debug("exception when sending notification to policy", e);
            return false;
        }
        return true;
    }
}
