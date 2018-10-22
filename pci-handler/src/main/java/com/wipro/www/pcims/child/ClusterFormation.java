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

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wipro.www.pcims.ConfigPolicy;
import com.wipro.www.pcims.Configuration;
import com.wipro.www.pcims.dao.ClusterDetailsRepository;
import com.wipro.www.pcims.entity.ClusterDetails;

import com.wipro.www.pcims.model.CellPciPair;
import com.wipro.www.pcims.model.FapServiceList;
import com.wipro.www.pcims.model.LteNeighborListInUseLteCell;
import com.wipro.www.pcims.model.Response;
import com.wipro.www.pcims.model.SdnrResponse;
import com.wipro.www.pcims.utils.BeanUtil;
import com.wipro.www.pcims.utils.HttpRequester;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;

import org.json.JSONObject;
import org.slf4j.Logger;

public class ClusterFormation {

    private static final Logger log = org.slf4j.LoggerFactory.getLogger(ClusterFormation.class);

    private ObjectMapper mapper;
    private Graph cluster;
    private HttpRequester httpRequester;
    private ClusterModification clusterModification;
    private Detection detect;
    Properties confProp;
    ClusterDetails details = new ClusterDetails();

    /**
     * Returns a new Cluster for a given notification.
     */
    public Graph clusterForm(FapServiceList fapser) {

        int phycellId = fapser.getX0005b9Lte().getPhyCellIdInUse();
        String cellId = fapser.getCellConfig().getLte().getRan().getCellIdentity();
        CellPciPair val = new CellPciPair();
        val.setCellId(cellId);
        val.setPhysicalCellId(phycellId);
        List<LteNeighborListInUseLteCell> neighbourlist;
        neighbourlist = fapser.getCellConfig().getLte().getRan().getNeighborListInUse()
                .getLteNeighborListInUseLteCell();

        for (int i = 0; i < neighbourlist.size(); i++) {

            String cell = neighbourlist.get(i).getCid();
            int phy = neighbourlist.get(i).getPhyCellId();
            CellPciPair val1 = new CellPciPair();
            val1.setCellId(cell);
            val1.setPhysicalCellId(phy);
            cluster.addEdge(val, val1);

            Date date = new Date();
            long time = date.getTime();
            Timestamp ts = new Timestamp(time);
            String requestBody = "{\n\t\"cellId\":" + neighbourlist.get(1).getCid() + " ,\n\t\"ts\": " + ts
                    + " \"2\"\n}";
            String response = httpRequester.sendGetRequest("http://:/SdncConfigDBAPI/getNbrList", requestBody);
            log.debug("response: {}", response);
            SdnrResponse sdnrResponsejson = null;
            try {
                sdnrResponsejson = mapper.readValue(response, SdnrResponse.class);
            } catch (JsonParseException e) {
                log.error("JsonParseException {}", e);
            } catch (JsonMappingException e) {
                log.error("JsonMappingException {}", e);
            } catch (IOException e) {
                log.error("IOException {}", e);
            }

            List<Response> responselist = new ArrayList<>();
            try {
                responselist = sdnrResponsejson.getResponse();
            } catch (NullPointerException e) {
                log.error("NullPointerException {}", e);
            }

            log.debug("responselist :{}", responselist);

            for (int k = 0; k < responselist.size(); k++) {
                String cid = responselist.get(k).getCellId();
                int pci = responselist.get(k).getPhysicalCellId();
                CellPciPair val3 = new CellPciPair();
                val3.setCellId(cid);
                val3.setPhysicalCellId(pci);

                cluster.addEdge(val1, val3);
                log.debug("final cluster: {}", cluster);
            }

            Map<CellPciPair, ArrayList<CellPciPair>> cellPciNeighbourMap = cluster.getCellPciNeighbourMap();
            JSONObject cellPciNeighbourJson = new JSONObject(cellPciNeighbourMap);
            String cellPciNeighbourString = cellPciNeighbourJson.toString();

            log.debug("cluster hahsmap to string : {}", cellPciNeighbourString);

            UUID clusterId = UUID.randomUUID();
            cluster.setGraphId(clusterId);

            details.setClusterId(clusterId.toString());
            details.setClusterInfo(cellPciNeighbourString);

            ClusterDetailsRepository clusterDetailsRepository = BeanUtil.getBean(ClusterDetailsRepository.class);
            clusterDetailsRepository.save(details);

        }

        return cluster;

    }

    /**
     * Determines whether to trigger Oof or wait for notifications.
     */
    public void triggerOrWait(Graph cluster, String networkId) {
        // determine collision or confusion
        StateOof oof;
        Map<String, ArrayList<Integer>> collisionConfusionResult;
        Map<String, ArrayList<Integer>> modifiedCollisionConfusion = null;

        FapServiceList newNotification;
        Graph modifiedCluster = null;
        oof = new StateOof();

        collisionConfusionResult = detect.detectCollisionConfusion(cluster);

        Configuration configuration = Configuration.getInstance();

        for (Map.Entry<String, ArrayList<Integer>> entry : collisionConfusionResult.entrySet()) {
            ArrayList<Integer> arr;
            arr = entry.getValue();
            // check for 0 collision and confusion

            if ((arr.get(0) >= configuration.getMinCollision()) && (arr.get(1) >= configuration.getMinConfusion())) {

                oof.triggerOof(collisionConfusionResult, cluster, networkId);
                break;

            }
        }

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
            if (difference < (timer * 1000)) {
                if (ChildThread.queue.isEmpty()) {
                    continue;
                }
                UUID clusterId = cluster.getGraphId();

                newNotification = ChildThread.queue.poll();
                String network = newNotification.getCellConfig().getLte().getRan().getNeighborListInUse()
                        .getLteNeighborListInUseLteCell().get(0).getPlmnid();
                modifiedCluster = clusterModification.clustermod(cluster, newNotification);

                // update cluster in DB
                Map<CellPciPair, ArrayList<CellPciPair>> cellPciNeighbourMap = modifiedCluster.getCellPciNeighbourMap();
                JSONObject cellPciNeighbourJson = new JSONObject(cellPciNeighbourMap);
                String cellPciNeighbourString = cellPciNeighbourJson.toString();

                ClusterDetailsRepository clusterDetailsRepository = BeanUtil.getBean(ClusterDetailsRepository.class);
                clusterDetailsRepository.updateCluster(cellPciNeighbourString, clusterId.toString());

                modifiedCollisionConfusion = detect.detectCollisionConfusion(modifiedCluster);
                for (Map.Entry<String, ArrayList<Integer>> entry1 : modifiedCollisionConfusion.entrySet()) {

                    ArrayList<Integer> arr1;
                    arr1 = entry1.getValue();
                    if ((arr1.get(0) >= configuration.getMinCollision())
                            && (arr1.get(1) >= configuration.getMinConfusion())) {
                        oof.triggerOof(modifiedCollisionConfusion, modifiedCluster, network);
                        flag++;
                        break;
                    } else {
                        continue;
                    }

                }

            }
        }
        if (flag == 0) {
            oof.triggerOof(modifiedCollisionConfusion, modifiedCluster, networkId);

        }

    }

}
