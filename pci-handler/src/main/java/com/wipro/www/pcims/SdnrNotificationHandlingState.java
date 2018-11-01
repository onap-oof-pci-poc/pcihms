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

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wipro.www.pcims.child.ChildThread;
import com.wipro.www.pcims.entity.ClusterDetails;
import com.wipro.www.pcims.model.CellPciPair;
import com.wipro.www.pcims.model.FapServiceList;
import com.wipro.www.pcims.model.Notification;
import com.wipro.www.pcims.utils.BeanUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.slf4j.Logger;

public class SdnrNotificationHandlingState implements PciState {
    private static Map<Long, ChildThread> childThreadMap = new HashMap<>();
    private static final Logger log = org.slf4j.LoggerFactory.getLogger(SdnrNotificationHandlingState.class);

    @Override
    public void stateChange(PciContext pciContext) {
        // logic to determine if notif to be processed
        log.debug("inside statechange of sdnr notif state");
        String clusterId = null;
        String notification = pciContext.getSdnrNotification();
        Notification notificationObject;
        try {

            Map<CellPciPair, ArrayList<CellPciPair>> cellPciNeighbourMap;
            ObjectMapper mapper = new ObjectMapper();
            notificationObject = mapper.readValue(notification, Notification.class);
            log.debug("notificationObject{}", notificationObject);

            List<FapServiceList> serviceList = notificationObject.getPayload().getRadioAccess().getFapServiceList();
            for (FapServiceList list : serviceList) {
                String cellId = list.getCellConfig().getLte().getRan().getCellIdentity();
                log.debug("cellId:{}", cellId);
                log.debug("inside for loop");

                ClusterDetailsComponent clusterDetailsComponent = new ClusterDetailsComponent();
                List<ClusterDetails> allClusterDetails = clusterDetailsComponent.getClusterDetails();
                for (ClusterDetails clusterDetail : allClusterDetails) {
                    cellPciNeighbourMap = mapper.readValue(clusterDetail.getClusterInfo(),
                            new TypeReference<HashMap<CellPciPair, ArrayList<CellPciPair>>>() {
                            });
                    Set keys = cellPciNeighbourMap.keySet();
                    Iterator traverse = keys.iterator();
                    while (traverse.hasNext()) {
                        CellPciPair key = (CellPciPair) traverse.next();
                        String currentCellId = key.getCellId();
                        if (cellId.equals(currentCellId)) {

                            log.debug("cell id matched in the cluster");

                            clusterId = clusterDetail.getClusterId();
                            long childThreadId = clusterDetailsComponent.getChildThread(clusterId);
                            String childStatus = pciContext.getChildStatus(childThreadId);
                            if (childStatus.equals("triggeredOof")) {
                                pciContext.setNotifToBeProcessed(false);
                                log.debug("notification is to be buffered");
                                log.debug("buffering notification");

                                BufferNotificationComponent bufferNotifComponent = new BufferNotificationComponent();
                                String serviceListString = mapper.writeValueAsString(list);
                                bufferNotifComponent.bufferNotification(serviceListString, clusterId);
                                pciContext.setPciState(new BufferNotificationState());
                                pciContext.stateChange(pciContext);
                            } else {
                                pciContext.setNotifToBeProcessed(true);
                                log.debug("childThreadId:{}", childThreadId);
                                // forward the notification to that child thread id for processing
                                // get the child thread instance from hashmap and put the notif in its queue
                                childThreadMap.get(childThreadId).putInQueue(list);
                            }

                        }
                    }
                }

                if (clusterId == null) {
                    log.debug("creating new child:");
                    BlockingQueue<FapServiceList> queue = new LinkedBlockingQueue<>();

                    ChildThread child = new ChildThread(pciContext.getChildStatusUpdate(), queue);
                    queue.put(list);
                    MainThreadComponent mainThreadComponent = BeanUtil.getBean(MainThreadComponent.class);
                    mainThreadComponent.getPool().execute(child);
                    pciContext.setPciState(new ChildTriggeringState());
                    pciContext.stateChange(pciContext);
                }

            }

        } catch (Exception e) {
            log.error("caught in sdnr notif handling state{}", e);
        }

    }

    public static void addChildThreadMap(Long childThreadId, ChildThread child) {
        childThreadMap.put(childThreadId, child);
    }

    public static Map<Long, ChildThread> getChildThreadMap() {
        return childThreadMap;
    }

}
