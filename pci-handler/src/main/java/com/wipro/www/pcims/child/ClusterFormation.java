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

package com.wipro.www.pcims.child;

import com.wipro.www.pcims.ConfigPolicy;
import com.wipro.www.pcims.Configuration;
import com.wipro.www.pcims.dao.ClusterDetailsRepository;
import com.wipro.www.pcims.entity.ClusterDetails;

import com.wipro.www.pcims.model.FapServiceList;
import com.wipro.www.pcims.utils.BeanUtil;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;

import org.slf4j.Logger;

public class ClusterFormation {

    private static final Logger log = org.slf4j.LoggerFactory.getLogger(ClusterFormation.class);
    private BlockingQueue<FapServiceList> queue;
    private ClusterModification clusterModification;
    private Detection detect;
    Properties confProp;
    ClusterDetails details = new ClusterDetails();

    public ClusterFormation() {
        this.detect = new Detection();
    }

    /**
     * parameterized constructor.
     *
     */
    public ClusterFormation(BlockingQueue<FapServiceList> queue) {
        super();
        this.queue = queue;
        this.detect = new Detection();
        this.clusterModification = new ClusterModification();
    }

    /**
     * Determines whether to trigger Oof or wait for notifications.
     */
    public Boolean triggerOrWait(Graph cluster, Map<String, ArrayList<Integer>> collisionConfusionResult) {
        // determine collision or confusion

        Configuration configuration = Configuration.getInstance();
        int collisionSum = 0;
        int confusionSum = 0;

        for (Map.Entry<String, ArrayList<Integer>> entry : collisionConfusionResult.entrySet()) {

            ArrayList<Integer> arr;
            arr = entry.getValue();
            // check for 0 collision and confusion
            if (!arr.isEmpty()) {
                collisionSum = collisionSum + arr.get(0);
                confusionSum = confusionSum + arr.get(1);
            }
        }
        return ((collisionSum >= configuration.getMinCollision()) && (confusionSum >= configuration.getMinConfusion()));

    }

    /**
     * Waits for notifications.
     */
    public Map<String, ArrayList<Integer>> waitForNotification(Map<String, ArrayList<Integer>> collisionConfusionResult,
            Graph cluster) {

        FapServiceList newNotification;
        ConfigPolicy config = ConfigPolicy.getInstance();
        int timer = 60;
        try {
            timer = (int) config.getConfig().get("PCI_NEIGHBOR_CHANGE_CLUSTER_TIMEOUT_IN_SECS");
        } catch (NullPointerException e) {
            log.debug("Policy config not available. Using default timeout - 60 seconds");
        }

        Timestamp currentTime = new Timestamp(System.currentTimeMillis());
        log.debug("Current Time {}", currentTime);

        Timestamp laterTime = new Timestamp(System.currentTimeMillis());
        log.debug("LaterTime {}", laterTime);

        long difference = laterTime.getTime() - currentTime.getTime();

        int flag = 0;

        while (difference < (timer * 1000)) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                log.error("Interrupted Exception is {}", e);
                Thread.currentThread().interrupt();
            }

            laterTime = new Timestamp(System.currentTimeMillis());
            difference = laterTime.getTime() - currentTime.getTime();

            if ((difference < (timer * 1000)) && (!queue.isEmpty())) {
                newNotification = queue.poll();
                cluster = clusterModification.clustermod(cluster, newNotification);

                // update cluster in DB
                String cellPciNeighbourString = cluster.getPciNeighbourJson();
                UUID clusterId = cluster.getGraphId();
                ClusterDetailsRepository clusterDetailsRepository = BeanUtil.getBean(ClusterDetailsRepository.class);
                clusterDetailsRepository.updateCluster(cellPciNeighbourString, clusterId.toString());
                flag++;

            }
        }
        if (flag != 0) {
            return detect.detectCollisionConfusion(cluster);

        }
        return collisionConfusionResult;
    }

}
