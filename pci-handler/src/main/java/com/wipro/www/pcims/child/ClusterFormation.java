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

import com.wipro.www.pcims.model.CellPciPair;
import com.wipro.www.pcims.model.FapServiceList;
import com.wipro.www.pcims.model.LteNeighborListInUseLteCell;
import com.wipro.www.pcims.model.Response;
import com.wipro.www.pcims.restclient.SdnrRestClient;
import com.wipro.www.pcims.utils.BeanUtil;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;

public class ClusterFormation {

    private static final Logger log = org.slf4j.LoggerFactory.getLogger(ClusterFormation.class);
    private BlockingQueue<List<String>> childStatusUpdate;
    private BlockingQueue<FapServiceList> queue;
    private Graph cluster;
    private ClusterModification clusterModification;
    private Detection detect;
    Properties confProp;
    ClusterDetails details = new ClusterDetails();

    public ClusterFormation() {

    }

    /**
     * <<<<<<< 01b3b6e2081dd189a8965135f49f47cb57923b3b Parameterized constructor.
     * ======= parameterized constructor. >>>>>>> child thread bugs fixed during
     * testing
     */
    public ClusterFormation(BlockingQueue<List<String>> childStatusUpdate, BlockingQueue<FapServiceList> queue) {
        super();
        this.childStatusUpdate = childStatusUpdate;
        this.queue = queue;
        this.cluster = new Graph();
        this.detect = new Detection();
        this.clusterModification = new ClusterModification();
    }

    /**
     * Returns a new Cluster for a given notification.
     */
    public Graph clusterForm(FapServiceList fapser) {

        log.debug("cluster formation started");
        int phycellId = fapser.getX0005b9Lte().getPhyCellIdInUse();
        String cellId = fapser.getCellConfig().getLte().getRan().getCellIdentity();
        CellPciPair val = new CellPciPair();
        val.setCellId(cellId);
        val.setPhysicalCellId(phycellId);
        List<LteNeighborListInUseLteCell> neighbourlist;
        neighbourlist = fapser.getCellConfig().getLte().getRan().getNeighborListInUse()
                .getLteNeighborListInUseLteCell();
        log.debug("Neighbor list size: {}", neighbourlist.size());

        for (int i = 0; i < neighbourlist.size(); i++) {

            String cell = neighbourlist.get(i).getAlias();
            int phy = neighbourlist.get(i).getPhyCellId();
            log.debug("cellID: {}", cell);
            log.debug("PCI: {}", phy);
            CellPciPair val1 = new CellPciPair();
            val1.setCellId(cell);
            val1.setPhysicalCellId(phy);
            log.debug(val1.toString());
            cluster.addEdge(val, val1);
            log.debug("cluster: {}", cluster.toString());
            String response = SdnrRestClient.getNbrList(neighbourlist.get(i).getAlias());
            log.debug("response: {}", response);

            ArrayList<Response> sdnrResponse = new ArrayList<>();
            JSONArray responseList = new JSONArray(response);
            for (int j = 0; j < responseList.length(); j++) {
                JSONObject resp = (JSONObject) responseList.get(j);
                Response responseObj = new Response();
                responseObj.setCellId(resp.getString("cellId"));
                responseObj.setPci(resp.getInt("pci"));
                sdnrResponse.add(responseObj);
            }

            log.debug("responselist :{}", sdnrResponse);

            for (int k = 0; k < sdnrResponse.size(); k++) {
                String cid = sdnrResponse.get(k).getCellId();
                int pci = sdnrResponse.get(k).getPci();
                CellPciPair val3 = new CellPciPair();
                val3.setCellId(cid);
                val3.setPhysicalCellId(pci);

                cluster.addEdge(val1, val3);
                log.debug("final cluster: {}", cluster);
            }

        }

        Map<CellPciPair, ArrayList<CellPciPair>> cellPciNeighbourMap = cluster.getCellPciNeighbourMap();
        JSONObject cellPciNeighbourJson = new JSONObject(cellPciNeighbourMap);
        String cellPciNeighbourString = cellPciNeighbourJson.toString();
        log.debug("cluster hahsmap to string : {}", cellPciNeighbourString);
        UUID clusterId = UUID.randomUUID();
        cluster.setGraphId(clusterId);

        details.setClusterId(clusterId.toString());
        details.setClusterInfo(cellPciNeighbourString);

        details.setChildThreadId(Thread.currentThread().getId());

        ClusterDetailsRepository clusterDetailsRepository = BeanUtil.getBean(ClusterDetailsRepository.class);
        clusterDetailsRepository.save(details);

        return cluster;

    }

    /**
     * Determines whether to trigger Oof or wait for notifications.
     */
    public void triggerOrWait(Graph cluster, String networkId) {
        // determine collision or confusion
        Map<String, ArrayList<Integer>> collisionConfusionResult;
        StateOof oof = new StateOof(childStatusUpdate, queue);

        collisionConfusionResult = detect.detectCollisionConfusion(cluster);

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
        if ((collisionSum >= configuration.getMinCollision()) && (confusionSum >= configuration.getMinConfusion())) {
            oof.triggerOof(collisionConfusionResult, cluster, networkId);

        } else {
            waitForNotification(collisionConfusionResult, cluster, networkId);

        }

    }

    /**
     * Waits for notifications.
     */
    public void waitForNotification(Map<String, ArrayList<Integer>> collisionConfusionResult, Graph cluster,
            String networkId) {

        StateOof oof = new StateOof(childStatusUpdate, queue);

        Map<String, ArrayList<Integer>> modifiedCollisionConfusion = null;

        FapServiceList newNotification;
        Graph modifiedCluster = null;
        Configuration configuration = Configuration.getInstance();

        ConfigPolicy config = ConfigPolicy.getInstance();
        int timer = (int) config.getConfig().get("PCI_NEIGHBOR_CHANGE_CLUSTER_TIMEOUT_IN_SECS");

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
                networkId = newNotification.getCellConfig().getLte().getRan().getNeighborListInUse()
                        .getLteNeighborListInUseLteCell().get(0).getPlmnid();
                modifiedCluster = clusterModification.clustermod(cluster, newNotification);

                // update cluster in DB
                Map<CellPciPair, ArrayList<CellPciPair>> cellPciNeighbourMap = modifiedCluster.getCellPciNeighbourMap();
                JSONObject cellPciNeighbourJson = new JSONObject(cellPciNeighbourMap);
                String cellPciNeighbourString = cellPciNeighbourJson.toString();
                UUID clusterId = cluster.getGraphId();
                ClusterDetailsRepository clusterDetailsRepository = BeanUtil.getBean(ClusterDetailsRepository.class);
                clusterDetailsRepository.updateCluster(cellPciNeighbourString, clusterId.toString());
                flag++;

            }
        }
        if (flag != 0) {

            modifiedCollisionConfusion = detect.detectCollisionConfusion(modifiedCluster);
            int collisionSum = 0;
            int confusionSum = 0;
            for (Map.Entry<String, ArrayList<Integer>> entry1 : modifiedCollisionConfusion.entrySet()) {

                ArrayList<Integer> arr;
                arr = entry1.getValue();
                collisionSum = collisionSum + arr.get(0);
                confusionSum = confusionSum + arr.get(1);
            }
            if ((collisionSum >= configuration.getMinCollision())
                    && (confusionSum >= configuration.getMinConfusion())) {
                oof.triggerOof(modifiedCollisionConfusion, modifiedCluster, networkId);

            }
        } else {
            oof.triggerOof(collisionConfusionResult, modifiedCluster, networkId);

        }

    }

}
