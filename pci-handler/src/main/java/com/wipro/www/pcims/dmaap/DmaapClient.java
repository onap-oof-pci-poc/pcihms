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

import com.att.nsa.apiClient.http.HttpException;
import com.att.nsa.cambria.client.CambriaClient;
import com.att.nsa.cambria.client.CambriaClientBuilders;
import com.att.nsa.cambria.client.CambriaClientBuilders.ConsumerBuilder;
import com.att.nsa.cambria.client.CambriaClientBuilders.TopicManagerBuilder;
import com.att.nsa.cambria.client.CambriaConsumer;
import com.att.nsa.cambria.client.CambriaTopicManager;
import com.wipro.www.pcims.Configuration;
import com.wipro.www.pcims.NewNotification;
import com.wipro.www.pcims.dao.DmaapNotificationsRepository;
import com.wipro.www.pcims.entity.DmaapNotifications;
import java.io.IOException;
import java.net.MalformedURLException;
import java.security.GeneralSecurityException;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DmaapClient {

    @Autowired
    private DmaapNotificationsRepository dmaapNotificationsRepository;
    private Configuration configuration;
    private static Logger log = LoggerFactory.getLogger(DmaapClient.class);
    private static final String CONSUMER = "CONSUMER";
    private static final String PRODUCER = "PRODUCER";
    private NewNotification newNotification;

    public class NotificationCallback {
        DmaapClient dmaapClient;

        public NotificationCallback(DmaapClient dmaapClient) {
            this.dmaapClient = dmaapClient;
        }

        public void activateCallBack(String msg) {
            handleNotification(msg);
        }

        private void handleNotification(String msg) {
            DmaapNotifications dmaapNotification = new DmaapNotifications();
            dmaapNotification.setNotification(msg);
            if (log.isDebugEnabled()) {
                log.debug(dmaapNotification.toString());
            }
            dmaapNotificationsRepository.save(dmaapNotification);
            // WaitState waitState = WaitState.getInstance();
            // waitState.putSdnrNotification("notification in queue");
            newNotification.setNewNotif(true);
        }
    }

    /**
     * init dmaap client.
     */
    public void initClient(NewNotification newNotification) {
        log.debug("initializing client");
        configuration = Configuration.getInstance();
        if (log.isDebugEnabled()) {
            log.debug(configuration.toString());
        }
        this.newNotification = newNotification;
        createSdnrTopic();
        createPolicyTopic();
        subscribeToPolicyTopic();
        subscribeToSdnrTopic();
        subscribeSdnrToSdnrTopic();
        subscribePolicyToPolicyTopic();
        startClient();
    }

    private void subscribePolicyToPolicyTopic() {
        subscribeToTopic(configuration.getServers(), configuration.getPolicyTopic(), configuration.getPolicyApiKey(),
                CONSUMER);
    }

    private void createPolicyTopic() {
        String topicDescription = "PCI change notification topic";
        createTopic(configuration.getPolicyTopic(), topicDescription, 1, 1, configuration.getManagerApiKey(),
                configuration.getManagerSecretKey());

    }

    private void createSdnrTopic() {

        String topicDescription = "Neighbor list change notification topic";
        createTopic(configuration.getSdnrTopic(), topicDescription, 1, 1, configuration.getManagerApiKey(),
                configuration.getManagerSecretKey());

    }

    private void createTopic(String topicName, String topicDescription, int partitionCount, int replicationCount,
            String managerApiKey, String managerSecretKey) {

        CambriaTopicManager topicManager = null;

        try {
            topicManager = buildCambriaClient(new TopicManagerBuilder().usingHosts(configuration.getServers())
                    .authenticatedBy(managerApiKey, managerSecretKey));
            topicManager.createTopic(topicName, topicDescription, partitionCount, replicationCount);
            topicManager.close();
        } catch (GeneralSecurityException | HttpException | IOException e) {
            log.debug("exception during creating topic", e);
        }

    }

    private synchronized void startClient() {

        ScheduledExecutorService executorPool;
        CambriaConsumer cambriaConsumer = null;

        try {
            cambriaConsumer = new ConsumerBuilder()
                    .authenticatedBy(configuration.getPcimsApiKey(), configuration.getPcimsSecretKey())
                    .knownAs(configuration.getCg(), configuration.getCid()).onTopic(configuration.getSdnrTopic())
                    .usingHosts(configuration.getServers()).withSocketTimeout(configuration.getPollingTimeout() * 1000)
                    .build();

            // create notification consumers for SNDR and policy
            NotificationConsumer notificationConsumer = new NotificationConsumer(cambriaConsumer,
                    new NotificationCallback(this));

            // start notification consumer threads
            executorPool = Executors.newScheduledThreadPool(10);
            executorPool.scheduleAtFixedRate(notificationConsumer, 0, configuration.getPollingInterval(),
                    TimeUnit.SECONDS);
        } catch (MalformedURLException | GeneralSecurityException e) {
            log.debug("exception during starting client", e);
        }

    }

    private void subscribeToSdnrTopic() {
        log.debug("subscribing to SDNR topic");
        // subscribe to SDNR topic
        subscribeToTopic(configuration.getServers(), configuration.getSdnrTopic(), configuration.getPcimsApiKey(),
                CONSUMER);
    }

    private void subscribeToPolicyTopic() {
        log.debug("subscribing to policy topic");
        // subscribe to policy topic
        subscribeToTopic(configuration.getServers(), configuration.getPolicyTopic(), configuration.getPcimsApiKey(),
                PRODUCER);

    }

    private void subscribeSdnrToSdnrTopic() {

        subscribeToTopic(configuration.getServers(), configuration.getSdnrTopic(), configuration.getSdnrApiKey(),
                PRODUCER);
    }

    private void subscribeToTopic(List<String> servers, String topicName, String subscriberApiKey,
            String subscriberType) {
        CambriaTopicManager topicManager = null;
        try {
            topicManager = buildCambriaClient(new TopicManagerBuilder().usingHosts(servers)
                    .authenticatedBy(configuration.getManagerApiKey(), configuration.getManagerSecretKey()));
            if (subscriberType.equals(PRODUCER)) {
                topicManager.allowProducer(topicName, subscriberApiKey);
            } else if (subscriberType.equals(CONSUMER)) {
                topicManager.allowConsumer(topicName, subscriberApiKey);
            }
            topicManager.close();
        } catch (GeneralSecurityException | HttpException | IOException e) {
            log.debug("exception during topicManger creation", e);
        }

    }

    @SuppressWarnings("unchecked")
    private static <T extends CambriaClient> T buildCambriaClient(
            CambriaClientBuilders.AbstractAuthenticatedManagerBuilder<? extends CambriaClient> client)
            throws MalformedURLException, GeneralSecurityException {
        return (T) client.build();
    }

}
