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
import com.wipro.www.pcims.Topic;
import com.wipro.www.pcims.dao.DmaapNotificationsRepository;
import com.wipro.www.pcims.entity.DmaapNotifications;
import java.io.IOException;
import java.net.MalformedURLException;
import java.security.GeneralSecurityException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
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
    private static final String DESCRIPTION = "api keys for OOF PCI use case";
    private static final int PARTITION_COUNT = 1;
    private static final int REPLICATION_COUNT = 1;
    private NewNotification newNotification;
    private CambriaTopicManager topicManager;

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

        createAndConfigureTopics();
        startClient();
    }

    /**
     * create and configures topics.
     */
    private void createAndConfigureTopics() {

        try {
            topicManager = buildCambriaClient(new TopicManagerBuilder().usingHosts(configuration.getServers())
                    .authenticatedBy(configuration.getManagerApiKey(), configuration.getManagerSecretKey()));
        } catch (GeneralSecurityException | IOException e) {
            log.debug("exception during creating topic", e);
        }
        List<Topic> topics = configuration.getTopics();

        for (Topic topic : topics) {
            Set<String> topicsInDmaap = getAllTopicsFromDmaap();

            createTopic(topic, topicsInDmaap);
            subscribeToTopic(configuration.getServers(), topic.getName(), topic.getProducer(), PRODUCER);
            subscribeToTopic(configuration.getServers(), topic.getName(), topic.getConsumer(), CONSUMER);

        }

        topicManager.close();

    }

    /**
     * create topic.
     */
    private void createTopic(Topic topic, Set<String> topicsInDmaap) {
        if (topicsInDmaap.contains(topic.getName())) {
            log.debug("topic exists in dmaap");
        } else {
            try {
                topicManager.createTopic(topic.getName(), DESCRIPTION, PARTITION_COUNT, REPLICATION_COUNT);
            } catch (HttpException | IOException e) {
                log.debug("error while creating topic: {}", e);
            }
        }
    }

    /**
     * get all topics from dmaap.
     */
    private Set<String> getAllTopicsFromDmaap() {
        Set<String> topics = new HashSet<>();
        try {
            topics = topicManager.getTopics();
        } catch (IOException e) {
            log.debug("IOException while fetching topics");
        }
        return topics;

    }

    /**
     * start dmaap client.
     */
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

    /**
     * subscribe to topic.
     */
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
