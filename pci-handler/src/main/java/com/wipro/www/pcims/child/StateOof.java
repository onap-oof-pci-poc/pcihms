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

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wipro.www.pcims.ConfigPolicy;
import com.wipro.www.pcims.Configuration;
import com.wipro.www.pcims.dao.PciRequestsRepository;
import com.wipro.www.pcims.dmaap.PolicyDmaapClient;
import com.wipro.www.pcims.entity.PciRequests;
import com.wipro.www.pcims.model.Aai;
import com.wipro.www.pcims.model.CellConfig;
import com.wipro.www.pcims.model.CellPciPair;
import com.wipro.www.pcims.model.Common;
import com.wipro.www.pcims.model.Configurations;
import com.wipro.www.pcims.model.Data;
import com.wipro.www.pcims.model.FapService;
import com.wipro.www.pcims.model.FapServiceList;
import com.wipro.www.pcims.model.Lte;
import com.wipro.www.pcims.model.Payload;
import com.wipro.www.pcims.model.PolicyNotification;
import com.wipro.www.pcims.model.Ran;
import com.wipro.www.pcims.model.Rf;
import com.wipro.www.pcims.restclient.AsyncResponseBody;
import com.wipro.www.pcims.restclient.CellIdList;
import com.wipro.www.pcims.restclient.OofRestClient;
import com.wipro.www.pcims.restclient.PciSolution;
import com.wipro.www.pcims.restclient.SdnrRestClient;
import com.wipro.www.pcims.restclient.Solution;
import com.wipro.www.pcims.utils.BeanUtil;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;

import org.slf4j.Logger;

public class StateOof {
    private static final Logger log = org.slf4j.LoggerFactory.getLogger(StateOof.class);
    private BlockingQueue<List<String>> childStatusUpdate;
    private BlockingQueue<FapServiceList> queue;

    public StateOof() {

    }

    /**
     * Parameterized Constructor.
     *
     */
    public StateOof(BlockingQueue<List<String>> childStatusUpdate, BlockingQueue<FapServiceList> queue) {
        super();
        this.childStatusUpdate = childStatusUpdate;
        this.queue = queue;
    }

    /**
     * Triggers OOF.
     */

    public void triggerOof(Map<String, ArrayList<Integer>> result, Graph cluster, String networkId) {
        // check for 0 collision and 0 confusion
        ArrayList<CellIdList> cellidList = new ArrayList<>();
        ArrayList<String> cellIds = new ArrayList<>();

        for (Map.Entry<String, ArrayList<Integer>> entry : result.entrySet()) {
            String key = entry.getKey();
            ArrayList<Integer> arr = new ArrayList<>();
            arr = entry.getValue();
            if (!arr.isEmpty()) {
                Set<Integer> set = new HashSet<>(arr);
                if (((set.size() == 1) && !set.contains(0)) || (set.size() != 1)) {
                    cellIds.add(key);

                }
            }

        }

        for (String cell : cellIds) {
            log.debug("cellidList entries: {}", cell);
            CellIdList cells = new CellIdList();
            cells.setCellId(cell);
            cellidList.add(cells);
        }
        log.debug("the cells triggering the oof are {}", cellidList);

        UUID transactionId = UUID.randomUUID();

        Configuration config = Configuration.getInstance();
        int numSolutions = config.getNumSolutions();
        List<String> optimizers = config.getOptimizers();

        String oofResponse = OofRestClient.queryOof(numSolutions, transactionId.toString(), "create", cellidList,
                networkId, optimizers);
        log.debug("Synchronous Response {}", oofResponse);

        // Store Request details in Database

        PciRequests pciRequest = new PciRequests();

        long childThreadId = Thread.currentThread().getId();
        pciRequest.setTransactionId(transactionId.toString());
        pciRequest.setChildThreadId(childThreadId);
        PciRequestsRepository pciRequestsRepository = BeanUtil.getBean(PciRequestsRepository.class);
        pciRequestsRepository.save(pciRequest);

        try {
            synchronized (ChildThread.asynchronousResponse) {
                while (ChildThread.asynchronousResponse.isEmpty()) {
                    ChildThread.asynchronousResponse.wait();
                }
            }
        } catch (InterruptedException e) {

            log.error("ChildThread queue error {}", e);
            Thread.currentThread().interrupt();
        }

        sendToPolicy(networkId);

        synchronized (queue) {

            try {
                while (queue.isEmpty()) {
                    wait();
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                log.debug("Exception while waiting for buffered notifications {}", e);
            }

        }

        bufferNotification(cluster);

    }

    /**
     * Sends Dmaap notification to Policy.
     */
    private void sendToPolicy(String networkId) {

        List<String> childStatus = new ArrayList<>();
        childStatus.add(Long.toString(Thread.currentThread().getId()));
        childStatus.add("Triggered Oof");
        try {
            childStatusUpdate.put(childStatus);
        } catch (InterruptedException e1) {
            log.debug("Interrupted execption {}", e1);
            Thread.currentThread().interrupt();

        }
        AsyncResponseBody async = null;
        async = ChildThread.asynchronousResponse.poll();

        List<Solution> solutions;
        solutions = async.getSolutions();

        Map<String, List<CellPciPair>> pnfs = getPnfs(solutions);

        for (Map.Entry<String, List<CellPciPair>> entry : pnfs.entrySet()) {
            String pnfName = entry.getKey();
            List<CellPciPair> cellPciPairs = entry.getValue();

            String notification = getNotificationString(pnfName, cellPciPairs, networkId);

            PolicyDmaapClient policy = new PolicyDmaapClient();
            boolean status = policy.sendNotificationToPolicy(notification);
            log.debug("sent Message: {}", status);
            if (status) {
                childStatus = new ArrayList<>();
                childStatus.add(Long.toString(Thread.currentThread().getId()));
                childStatus.add("Success");
                try {
                    childStatusUpdate.put(childStatus);
                } catch (InterruptedException e) {
                    log.debug("InterruptedException {}", e);
                    Thread.currentThread().interrupt();

                }

            } else {
                log.debug("Sending notification to policy failed");
            }

        }
    }

    private String getNotificationString(String pnfName, List<CellPciPair> cellPciPairs, String networkId) {
        ArrayList<Configurations> configurations = new ArrayList<>();
        for (CellPciPair cellPciPair : cellPciPairs) {
            String cellId = cellPciPair.getCellId();
            String pci = Integer.toString(cellPciPair.getPhysicalCellId());
            Configurations configuration = new Configurations(new Data(
                    new FapService(networkId, new CellConfig(new Lte(new Ran(new Rf(pci), new Common(cellId)))))),
                    pnfName);
            configurations.add(configuration);
        }

        Payload payload = new Payload(configurations);

        PolicyNotification policyNotification = new PolicyNotification();
        ConfigPolicy configPolicy = ConfigPolicy.getInstance();
        String closedLoopControlName = (String) configPolicy.getConfig().get("PCI_MODCONFIG_POLICY_NAME");
        policyNotification.setClosedLoopControlName(closedLoopControlName);
        policyNotification.setPayload(payload);
        policyNotification.setAai(new Aai());

        ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(Include.NON_NULL);
        String notification = "";
        try {
            notification = mapper.writeValueAsString(policyNotification);
        } catch (JsonProcessingException e1) {
            log.debug("JSON processing exception: {}", e1);
        }
        return notification;
    }

    private Map<String, List<CellPciPair>> getPnfs(List<Solution> solutions) {

        Map<String, List<CellPciPair>> pnfs = new HashMap<>();

        for (Solution solution : solutions) {
            List<PciSolution> pciSolutions = solution.getPciSolutions();
            for (PciSolution pciSolution : pciSolutions) {
                String cellId = pciSolution.getCellId();
                int pci = pciSolution.getPci();
                String pnfName = SdnrRestClient.getPnfName(cellId);
                if (pnfs.containsKey(pnfName)) {
                    pnfs.get(pnfName).add(new CellPciPair(cellId, pci));
                } else {
                    List<CellPciPair> cellPciPairs = new ArrayList<>();
                    cellPciPairs.add(new CellPciPair(cellId, pci));
                    pnfs.put(pnfName, cellPciPairs);
                }
            }

        }
        return pnfs;
    }

    /**
     * Buffer Notification.
     */
    public void bufferNotification(Graph cluster) {

        // Processing Buffered notifications

        Configuration config = Configuration.getInstance();

        int bufferTime = config.getBufferTime();

        Timestamp currentTime = new Timestamp(System.currentTimeMillis());
        log.debug("Current time {}", currentTime);

        Timestamp laterTime = new Timestamp(System.currentTimeMillis());
        log.debug("Later time {}", laterTime);

        long difference = laterTime.getTime() - currentTime.getTime();
        while (difference < bufferTime) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                log.error("InterruptedException {}", e);
                Thread.currentThread().interrupt();

            }
            laterTime = new Timestamp(System.currentTimeMillis());
            difference = laterTime.getTime() - currentTime.getTime();

            log.debug("Timer has run for  seconds {}", difference);

            if (!queue.isEmpty()) {

                FapServiceList fapService;
                ClusterModification clusterModification = new ClusterModification();
                ClusterFormation form = new ClusterFormation();
                Graph latestCluster;
                fapService = queue.poll();
                latestCluster = clusterModification.clustermod(cluster, fapService);
                String network = fapService.getCellConfig().getLte().getRan().getNeighborListInUse()
                        .getLteNeighborListInUseLteCell().get(0).getPlmnid();
                try {
                    form.triggerOrWait(latestCluster, network);
                } catch (Exception e) {
                    log.error("IOException {}", e);
                }

            }

        }

    }

}
