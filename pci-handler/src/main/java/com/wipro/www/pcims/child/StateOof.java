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
import com.wipro.www.pcims.dao.CellInfoRepository;
import com.wipro.www.pcims.dao.PciRequestsRepository;
import com.wipro.www.pcims.dmaap.PolicyDmaapClient;
import com.wipro.www.pcims.entity.CellInfo;
import com.wipro.www.pcims.entity.PciRequests;
import com.wipro.www.pcims.exceptions.ConfigDbNotFoundException;
import com.wipro.www.pcims.exceptions.OofNotFoundException;
import com.wipro.www.pcims.model.CellConfig;
import com.wipro.www.pcims.model.CellPciPair;
import com.wipro.www.pcims.model.Common;
import com.wipro.www.pcims.model.Configurations;
import com.wipro.www.pcims.model.Data;
import com.wipro.www.pcims.model.FapService;
import com.wipro.www.pcims.model.Lte;
import com.wipro.www.pcims.model.Payload;
import com.wipro.www.pcims.model.PolicyNotification;
import com.wipro.www.pcims.model.Ran;
import com.wipro.www.pcims.model.X0005b9Lte;
import com.wipro.www.pcims.restclient.AsyncResponseBody;
import com.wipro.www.pcims.restclient.OofRestClient;
import com.wipro.www.pcims.restclient.PciSolution;
import com.wipro.www.pcims.restclient.SdnrRestClient;
import com.wipro.www.pcims.restclient.Solution;
import com.wipro.www.pcims.utils.BeanUtil;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;

import org.slf4j.Logger;

public class StateOof {
    private static final Logger log = org.slf4j.LoggerFactory.getLogger(StateOof.class);
    private BlockingQueue<List<String>> childStatusUpdate;

    public StateOof() {

    }

    /**
     * Parameterized Constructor.
     *
     */
    public StateOof(BlockingQueue<List<String>> childStatusUpdate) {
        super();
        this.childStatusUpdate = childStatusUpdate;
    }

    /**
     * Triggers OOF.
     * @throws OofNotFoundException  when trigger oof fails
     */
    public void triggerOof(Map<String, ArrayList<Integer>> result, String networkId) throws OofNotFoundException {
        // check for 0 collision and 0 confusion
        ArrayList<String> cellidList = new ArrayList<>();
        ArrayList<String> cellIds = new ArrayList<>();

        for (Map.Entry<String, ArrayList<Integer>> entry : result.entrySet()) {
            String key = entry.getKey();
            ArrayList<Integer> arr;
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
            cellidList.add(cell);
        }
        log.debug("the cells triggering the oof are {}", cellidList);

        UUID transactionId = UUID.randomUUID();

        Configuration config = Configuration.getInstance();
        int numSolutions = config.getNumSolutions();
        List<String> optimizers = config.getOptimizers();

        String oofResponse = OofRestClient.queryOof(numSolutions, transactionId.toString(), "create", cellidList,
                networkId, optimizers);
        log.debug("Synchronous Response {}", oofResponse);

        List<String> childStatus = new ArrayList<>();
        childStatus.add(Long.toString(Thread.currentThread().getId()));
        childStatus.add("triggeredOof");
        try {
            childStatusUpdate.put(childStatus);
        } catch (InterruptedException e1) {
            log.debug("Interrupted execption {}", e1);
            Thread.currentThread().interrupt();

        }

        // Store Request details in Database

        PciRequests pciRequest = new PciRequests();

        long childThreadId = Thread.currentThread().getId();
        pciRequest.setTransactionId(transactionId.toString());
        pciRequest.setChildThreadId(childThreadId);
        PciRequestsRepository pciRequestsRepository = BeanUtil.getBean(PciRequestsRepository.class);
        pciRequestsRepository.save(pciRequest);

        while (!ChildThread.getResponseMap().containsKey(childThreadId)) {

        }

        AsyncResponseBody asynResponseBody = ChildThread.getResponseMap().get(childThreadId);

        try {
            sendToPolicy(asynResponseBody, networkId);
        } catch (ConfigDbNotFoundException e1) {
            log.debug("Config DB is unreachable: {}", e1);
        }

        pciRequestsRepository = BeanUtil.getBean(PciRequestsRepository.class);
        pciRequestsRepository.deleteByChildThreadId(childThreadId);

        childStatus = new ArrayList<>();
        childStatus.add(Long.toString(Thread.currentThread().getId()));
        childStatus.add("success");
        try {
            childStatusUpdate.put(childStatus);
        } catch (InterruptedException e) {
            log.debug("InterruptedException {}", e);
            Thread.currentThread().interrupt();

        }

    }

    /**
     * Sends Dmaap notification to Policy.
     *
     * @throws ConfigDbNotFoundException
     *             when config db is unreachable
     */
    private void sendToPolicy(AsyncResponseBody async, String networkId) throws ConfigDbNotFoundException {

        if (log.isDebugEnabled()) {
            log.debug(async.toString());
        }

        List<Solution> solutions;
        solutions = async.getSolutions();

        Map<String, List<CellPciPair>> pnfs = getPnfs(solutions);

        for (Map.Entry<String, List<CellPciPair>> entry : pnfs.entrySet()) {
            String pnfName = entry.getKey();
            List<CellPciPair> cellPciPairs = entry.getValue();

            String notification = getNotificationString(pnfName, cellPciPairs, networkId);
            log.debug("Policy Notification: {}", notification);
            PolicyDmaapClient policy = new PolicyDmaapClient();
            boolean status = policy.sendNotificationToPolicy(notification);
            log.debug("sent Message: {}", status);
            if (status) {
                log.debug("Message sent to policy");
            } else {
                log.debug("Sending notification to policy failed");
            }

        }
    }

    private String getNotificationString(String pnfName, List<CellPciPair> cellPciPairs, String networkId) {
        ArrayList<Configurations> configurations = new ArrayList<>();
        for (CellPciPair cellPciPair : cellPciPairs) {
            String cellId = cellPciPair.getCellId();
            int pci = cellPciPair.getPhysicalCellId();
            Configurations configuration = new Configurations(new Data(new FapService(cellId,
                    new X0005b9Lte(pci, pnfName), new CellConfig(new Lte(new Ran(new Common(cellId)))))));
            configurations.add(configuration);
        }

        Payload payload = new Payload(configurations);
        ObjectMapper mapper = new ObjectMapper();
        String payloadString = "";
        try {
            payloadString = mapper.writeValueAsString(payload);
        } catch (JsonProcessingException e) {
            log.debug("JSON processing exception: {}", e);
        }
        PolicyNotification policyNotification = new PolicyNotification();
        ConfigPolicy configPolicy = ConfigPolicy.getInstance();
        String closedLoopControlName = (String) configPolicy.getConfig().get("PCI_MODCONFIG_POLICY_NAME");
        policyNotification.setClosedLoopControlName(closedLoopControlName);
        policyNotification.setPayload(payloadString);

        mapper.setSerializationInclusion(Include.NON_NULL);
        String notification = "";
        try {
            notification = mapper.writeValueAsString(policyNotification);
        } catch (JsonProcessingException e1) {
            log.debug("JSON processing exception: {}", e1);
        }
        return notification;
    }

    private Map<String, List<CellPciPair>> getPnfs(List<Solution> solutions) throws ConfigDbNotFoundException {

        Map<String, List<CellPciPair>> pnfs = new HashMap<>();

        for (Solution solution : solutions) {
            List<PciSolution> pciSolutions = solution.getPciSolutions();
            for (PciSolution pciSolution : pciSolutions) {
                String cellId = pciSolution.getCellId();
                int pci = pciSolution.getPci();

                String pnfName = "";
                CellInfoRepository cellInfoRepository = BeanUtil.getBean(CellInfoRepository.class);
                Optional<CellInfo> cellInfo = cellInfoRepository.findById(cellId);
                if (cellInfo.isPresent()) {
                    pnfName = cellInfo.get().getPnfName();
                } else {
                    pnfName = SdnrRestClient.getPnfName(cellId);
                    cellInfoRepository.save(new CellInfo(cellId, pnfName));
                }
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

}
