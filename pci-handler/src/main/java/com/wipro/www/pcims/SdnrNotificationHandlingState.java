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

package com.wipro.www.pcims;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wipro.www.pcims.child.ChildThread;
import com.wipro.www.pcims.child.Graph;
import com.wipro.www.pcims.dao.CellInfoRepository;
import com.wipro.www.pcims.dao.ClusterDetailsRepository;
import com.wipro.www.pcims.entity.CellInfo;
import com.wipro.www.pcims.entity.ClusterDetails;
import com.wipro.www.pcims.model.CellPciPair;
import com.wipro.www.pcims.model.FapServiceList;
import com.wipro.www.pcims.model.LteNeighborListInUseLteCell;
import com.wipro.www.pcims.model.Notification;
import com.wipro.www.pcims.model.Response;
import com.wipro.www.pcims.model.ThreadId;
import com.wipro.www.pcims.restclient.SdnrRestClient;
import com.wipro.www.pcims.utils.BeanUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;

public class SdnrNotificationHandlingState implements PciState {
    private static Map<Long, ChildThread> childThreadMap = new HashMap<>();
    private static final Logger log = org.slf4j.LoggerFactory.getLogger(SdnrNotificationHandlingState.class);

    @Override
    public void stateChange(PciContext pciContext) {
        // logic to determine if notif to be processed
        log.debug("inside statechange of sdnr notif state");
        String notification = pciContext.getSdnrNotification();
        Notification notificationObject;
        try {

            ObjectMapper mapper = new ObjectMapper();
            notificationObject = mapper.readValue(notification, Notification.class);
            log.debug("notificationObject{}", notificationObject);

            List<FapServiceList> serviceList = notificationObject.getPayload().getRadioAccess().getFapServiceList();
            for (FapServiceList fapService : serviceList) {
                String cellId = fapService.getCellConfig().getLte().getRan().getCellIdentity();
                log.debug("cellId:{}", cellId);
                log.debug("inside for loop");

                List<ClusterDetails> clusterDetails = getAllClusters();

                ClusterDetails clusterDetail = getClusterForNotification(fapService, clusterDetails);

                if (clusterDetail == null) {
                    // form the cluster
                    Graph cluster = createCluster(fapService);
                    // save to db
                    UUID clusterId = UUID.randomUUID();
                    // create the child thread
                    log.debug("creating new child");
                    BlockingQueue<FapServiceList> queue = new LinkedBlockingQueue<>();
                    ThreadId threadId = new ThreadId();
                    threadId.setChildThreadId(0);
                    ChildThread child = new ChildThread(pciContext.getChildStatusUpdate(), cluster, queue,
                            clusterId.toString(), threadId);
                    queue.put(fapService);
                    MainThreadComponent mainThreadComponent = BeanUtil.getBean(MainThreadComponent.class);
                    mainThreadComponent.getPool().execute(child);
                    try {
                        synchronized (threadId) {
                            while (threadId.getChildThreadId() == 0) {
                                threadId.wait();
                            }
                        }
                    } catch (InterruptedException e) {

                        log.error("ChildThread queue error {}", e);
                        Thread.currentThread().interrupt();
                    }
                    saveCluster(cluster, clusterId, threadId.getChildThreadId());
                    addChildThreadMap(threadId.getChildThreadId(), child);
                    pciContext.addChildStatus(threadId.getChildThreadId(), "processingNotifications");

                }

                else {
                    if (isOofTriggeredForCluster(pciContext, clusterDetail)) {
                        pciContext.setNotifToBeProcessed(false);
                        bufferNotification(fapService, clusterDetail.getClusterId());
                    } else {
                        pciContext.setNotifToBeProcessed(true);
                        log.debug("childThreadId:{}", clusterDetail.getChildThreadId());
                        childThreadMap.get(clusterDetail.getChildThreadId()).putInQueue(fapService);
                    }
                }
            }
        } catch (Exception e) {
            log.error("caught in sdnr notif handling state{}", e);
        }

        WaitState waitState = WaitState.getInstance();
        pciContext.setPciState(waitState);
        pciContext.stateChange(pciContext);
    }

    private String saveCluster(Graph cluster, UUID clusterId, Long threadId) {

        String cellPciNeighbourString = cluster.getPciNeighbourJson();

        log.debug("cluster hahsmap to string : {}", cellPciNeighbourString);
        cluster.setGraphId(clusterId);

        ClusterDetails details = new ClusterDetails();
        details.setClusterId(clusterId.toString());
        details.setClusterInfo(cellPciNeighbourString);
        details.setChildThreadId(threadId);

        ClusterDetailsRepository clusterDetailsRepository = BeanUtil.getBean(ClusterDetailsRepository.class);
        clusterDetailsRepository.save(details);

        return clusterId.toString();
    }

    private Graph createCluster(FapServiceList fapService) {

        Graph cluster = new Graph();
        log.debug("cluster formation started");
        int phycellId = fapService.getX0005b9Lte().getPhyCellIdInUse();
        String cellId = fapService.getCellConfig().getLte().getRan().getCellIdentity();

        CellInfoRepository cellInfoRepository = BeanUtil.getBean(CellInfoRepository.class);
        cellInfoRepository.save(new CellInfo(cellId, fapService.getX0005b9Lte().getPnfName()));

        CellPciPair val = new CellPciPair();
        val.setCellId(cellId);
        val.setPhysicalCellId(phycellId);
        List<LteNeighborListInUseLteCell> neighbourlist;
        neighbourlist = fapService.getCellConfig().getLte().getRan().getNeighborListInUse()
                .getLteNeighborListInUseLteCell();
        log.debug("Neighbor list size: {}", neighbourlist.size());

        for (int i = 0; i < neighbourlist.size(); i++) {
            String cell = neighbourlist.get(i).getAlias();
            int phy = neighbourlist.get(i).getPhyCellId();

            cellInfoRepository.save(new CellInfo(cell, neighbourlist.get(i).getPnfName()));

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
            }
        }

        log.debug("final cluster: {}", cluster);
        return cluster;
    }

    private void bufferNotification(FapServiceList fapService, String clusterId) {

        ObjectMapper mapper = new ObjectMapper();
        BufferNotificationComponent bufferNotifComponent = new BufferNotificationComponent();
        String serviceListString = "";
        try {
            serviceListString = mapper.writeValueAsString(fapService);
        } catch (JsonProcessingException e) {
            log.debug("JSON processing exception: {}", e);
        }
        bufferNotifComponent.bufferNotification(serviceListString, clusterId);

    }

    private boolean isOofTriggeredForCluster(PciContext pciContext, ClusterDetails clusterDetail) {
        Long childThreadId = clusterDetail.getChildThreadId();
        String childStatus = pciContext.getChildStatus(childThreadId);
        return childStatus.equals("triggeredOof");

    }

    private ClusterDetails getClusterForNotification(FapServiceList fapService, List<ClusterDetails> clusterDetails) {

        String cellId = fapService.getCellConfig().getLte().getRan().getCellIdentity();
        Map<CellPciPair, ArrayList<CellPciPair>> cellPciNeighbourMap = new HashMap<>();

        for (ClusterDetails clusterDetail : clusterDetails) {
            Graph cluster = new Graph(clusterDetail.getClusterInfo());
            cellPciNeighbourMap = cluster.getCellPciNeighbourMap();
            Set<CellPciPair> keys = cellPciNeighbourMap.keySet();
            Iterator<CellPciPair> traverse = keys.iterator();
            while (traverse.hasNext()) {
                CellPciPair key = traverse.next();
                String currentCellId = key.getCellId();
                if (cellId.equals(currentCellId)) {
                    return clusterDetail;
                }
            }
        }

        return null;
    }

    private List<ClusterDetails> getAllClusters() {
        ClusterDetailsComponent clusterDetailsComponent = new ClusterDetailsComponent();
        return clusterDetailsComponent.getClusterDetails();
    }

    public static void addChildThreadMap(Long childThreadId, ChildThread child) {
        childThreadMap.put(childThreadId, child);
    }

    public static Map<Long, ChildThread> getChildThreadMap() {
        return childThreadMap;
    }

}
