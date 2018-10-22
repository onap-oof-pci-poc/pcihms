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
import com.wipro.www.pcims.Configuration;
import com.wipro.www.pcims.WaitState;
import com.wipro.www.pcims.dao.PciRequestsRepository;
import com.wipro.www.pcims.dmaap.PolicyDmaapClient;
import com.wipro.www.pcims.entity.PciRequests;
import com.wipro.www.pcims.model.Configurations;
import com.wipro.www.pcims.model.FapServiceList;
import com.wipro.www.pcims.model.Payload;
import com.wipro.www.pcims.restclient.AsyncResponseBody;
import com.wipro.www.pcims.restclient.CellIdList;
import com.wipro.www.pcims.restclient.OofRestClient;
import com.wipro.www.pcims.restclient.PciSolutions;
import com.wipro.www.pcims.restclient.Solutions;
import com.wipro.www.pcims.utils.BeanUtil;
import com.wipro.www.pcims.utils.HttpRequester;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.json.JSONObject;
import org.slf4j.Logger;

public class StateOof {
    private static final Logger log = org.slf4j.LoggerFactory.getLogger(StateOof.class);

    private HttpRequester httpRequester = new HttpRequester();
    ArrayList<String> cellIds = new ArrayList<>();

    /**
     * Triggers OOF.
     */

    public void triggerOof(Map<String, ArrayList<Integer>> result, Graph cluster, String networkId) {
        ObjectMapper objectMapper = new ObjectMapper();
        // check for 0 collision and 0 confusion
        ArrayList<CellIdList> cellidList = new ArrayList<>();
        CellIdList cells = new CellIdList();

        for (Map.Entry<String, ArrayList<Integer>> entry : result.entrySet()) {
            String key = entry.getKey();
            ArrayList<Integer> arr;
            arr = entry.getValue();
            Set<Integer> set = new HashSet<>(arr);
            if (((set.size() == 1) && !set.contains(0)) || (set.size() != 1)) {
                cellIds.add(key);

            }

        }

        for (int l = 0; l < cellIds.size(); l++) {
            cells.setCellId(cellIds.get(l));
            cellidList.add(cells);
        }
        log.debug("the cells triggering the oof are {}", cellIds);

        UUID transactionId = UUID.randomUUID();

        Configuration config = Configuration.getInstance();
        int numSolutions = config.getNumSolutions();
        List<String> optimizers = config.getOptimizers();

        String oofResponse = OofRestClient.queryOof(numSolutions, transactionId.toString(), "create", cellidList,
                networkId, optimizers);

        WaitState waitState = WaitState.getInstance();
        waitState.putChildStatus(ChildThread.childThreadId, "Triggered Oof Waiting for response");

        // Store Request details in Database

        log.debug("Synchronous Response {}", oofResponse);

        PciRequests pciRequest = new PciRequests();

        long childThreadId = ChildThread.childThreadId;
        pciRequest.setTransactionId(transactionId.toString());
        pciRequest.setChildThreadId(childThreadId);
        PciRequestsRepository pciRequestsRepository = BeanUtil.getBean(PciRequestsRepository.class);
        pciRequestsRepository.save(pciRequest);

        try {
            synchronized (ChildThread.asynchronousResponse) {
                while (ChildThread.asynchronousResponse.isEmpty()) {
                    wait();
                }
            }
        } catch (InterruptedException e) {

            log.error("ChildThread queue error {}", e);
            Thread.currentThread().interrupt();
        }
        AsyncResponseBody async = null;
        async = ChildThread.asynchronousResponse.poll();

        List<Solutions> solution;
        solution = async.getSolutions();
        for (int j = 0; j < solution.size(); j++) {
            List<PciSolutions> pciSolutions;
            pciSolutions = solution.get(j).getPciSolutions();
            String temp1 = "";
            for (int k = 0; k < pciSolutions.size(); k++) {
                String cell = pciSolutions.get(k).getCellId();
                int pci = pciSolutions.get(k).getPci();
                Date date = new Date();
                long time = date.getTime();
                Timestamp ts = new Timestamp(time);

                String body = "{\n\t\"cellId\":" + cell + " ,\n\t\"ts\": " + ts + " \"2\"\n}";
                String response = httpRequester.sendGetRequest("http://:/SdncConfigDBAPI/getPnfName", body);

                JSONObject obj = new JSONObject(response);
                String pnf = obj.getString("pnfName");
                if (j != (pciSolutions.size() - 1)) {
                    temp1 = temp1 + "{\n" + "   \"pnf-name\":\"" + pnf + "\",\n" + "   \"data\":{\n"
                            + "      \"FAPService\":{\n" + "         \"alias\":\"Network1\",\n"
                            + "         \"CellConfig\":{\n" + "            \"LTE\":{\n" + "               \"RAN\":{\n"
                            + "                  \"Common\":{\n" + "                     \"CellIdentity\":\"" + cell
                            + "\"\n" + "                  },\n" + "                  \"RF\":{\n"
                            + "                     \"PhyCellID\":\"" + pci + "\"\n" + "                  }\n"
                            + "               }\n" + "            }\n" + "         }\n" + "      }\n" + "   }\n" + "},";
                } else {
                    temp1 = temp1 + "{\n" + "   \"pnf-name\":\"" + pnf + "\",\n" + "   \"data\":{\n"
                            + "      \"FAPService\":{\n" + "         \"alias\":\"Network1\",\n"
                            + "         \"CellConfig\":{\n" + "            \"LTE\":{\n" + "               \"RAN\":{\n"
                            + "                  \"Common\":{\n" + "                     \"CellIdentity\":\"" + cell
                            + "\"\n" + "                  },\n" + "                  \"RF\":{\n"
                            + "                     \"PhyCellID\":\"" + pci + "\"\n" + "                  }\n"
                            + "               }\n" + "            }\n" + "         }\n" + "      }\n" + "   }\n" + "}";
                }

            }
            String formpay = "{\"Paload\":{\n" + "\"Configurations\":[" + temp1 + "]\n" + "}\n" + "}";
            Payload payload = null;
            try {
                payload = objectMapper.readValue(formpay, Payload.class);
            } catch (JsonParseException e) {
                log.error("JsonParse {}", e);
            } catch (JsonMappingException e) {
                log.error("JsonMappingException is {}", e);
            } catch (IOException e) {
                log.error("IOException is {}", e);
            }

            ArrayList<Configurations> configuration;
            Map<String, ArrayList<Configurations>> sort = new HashMap<>();
            configuration = (ArrayList<Configurations>) payload.getConfiguration();
            for (int h = 0; h < configuration.size(); h++) {
                String pnfName = configuration.get(h).getPnfName();
                if (sort.containsKey(pnfName)) {
                    ArrayList<Configurations> retrieveOldList;
                    retrieveOldList = sort.get(pnfName);
                    retrieveOldList.add(configuration.get(h));
                    sort.put(pnfName, retrieveOldList);
                } else {
                    ArrayList<Configurations> newList = new ArrayList<>();
                    newList.add(configuration.get(h));
                    sort.put(pnfName, newList);
                }

            }
            log.debug("Sorted Pnfs map {}", sort);

            for (Map.Entry<String, ArrayList<Configurations>> entry : sort.entrySet()) {
                ArrayList<Configurations> arr;
                arr = entry.getValue();

                ///// send DMaaP msg to Policy
                String notif = "{\"closedLoopControlName\":\"ControlLoop-vPCI-fb41f388-a5f2-11e8-98d0-\n"
                        + "529269fb1459\",\n" + "\"closedLoopAlarmStart\":1510187409180,\n"
                        + "\"closedLoopEventClient\":\"microservice.PCI\",\n" + "\"closedLoopEventStatus\":\"ONSET\",\n"
                        + "\"requestID\":\"9d2d790e-a5f0-11e8-98d0-529269fb1459\",\n" + "\"AAI\":{},\n"
                        + "\"from\":\"PCIMS\",\n" + "\"version\":\"1.0.2\",\n" + "\"Action\":\"ModifyConfig\",\n"
                        + "\"Payload\":{\n" + "\"Configurations\":[" + arr + "]\n" + "}\n" + "}\n" + "\n" + "\n" + "\n"
                        + "\n" + "";
                PolicyDmaapClient policy = new PolicyDmaapClient();
                boolean msg = policy.sendNotificationToPolicy(notif);
                if (msg == true) {
                    waitState.putChildStatus(ChildThread.childThreadId, "Success");
                } else {
                    log.debug("Notification Failed {}", msg);
                }

            }

        }

        // bufferTimer

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

            log.debug("Timer has run for 10 seconds {}", difference);

            while (!ChildThread.queue.isEmpty()) {

                FapServiceList fapService;
                ClusterModification clusterModification = new ClusterModification();
                ClusterFormation form = new ClusterFormation();
                Graph latestCluster;
                fapService = ChildThread.queue.poll();
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
