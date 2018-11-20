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

package com.wipro.www.pcims.restclient;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wipro.www.pcims.ConfigPolicy;
import com.wipro.www.pcims.Configuration;
import com.wipro.www.pcims.utils.HttpRequester;
import java.util.List;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OofRestClient {
    private static Logger log = LoggerFactory.getLogger(OofRestClient.class);

    private OofRestClient() {

    }

    /**
     * rest client that pci uses to query the OOF for pci solutions.
     */

    public static String queryOof(int numSolutions, String transactionId, String requestType,
            List<CellIdList> cellIdList, String networkId, List<String> optimizers) {
        log.debug("inside queryoof");

        String response = "";
        Configuration configuration = Configuration.getInstance();
        try {
            UUID requestUuid = UUID.randomUUID();
            String requestId = requestUuid.toString();
            String callbackUrl = configuration.getCallbackUrl();
            RequestInfo requestInfo = new RequestInfo();
            requestInfo.setTransactionId(transactionId);
            requestInfo.setRequestId(requestId);
            requestInfo.setCallbackUrl(callbackUrl);
            String sourceId = configuration.getSourceId();
            requestInfo.setSourceId(sourceId);
            requestInfo.setRequestType(requestType);
            requestInfo.setNumSolutions(numSolutions);
            requestInfo.setOptimizers(optimizers);
            ConfigPolicy config = ConfigPolicy.getInstance();
            int timeout = 60;
            try {
                timeout = (int) config.getConfig().get("PCI_NEIGHBOR_CHANGE_CLUSTER_TIMEOUT_IN_SECS");
            } catch (NullPointerException e) {
                log.debug("No config policy available. Using default timeout 60 sec");
            }
            requestInfo.setTimeout(timeout);
            CellInfo cellInfo = new CellInfo();
            cellInfo.setCellIdList(cellIdList);
            cellInfo.setNetworkId(networkId);
            OofRequestBody oofRequestBody = new OofRequestBody();
            oofRequestBody.setRequestInfo(requestInfo);
            oofRequestBody.setCellInfo(cellInfo);

            ObjectMapper mapper = new ObjectMapper();
            String requestBody = mapper.writeValueAsString(oofRequestBody);
            log.debug("requestBody{}", requestBody);

            String requestUrl = configuration.getOofService() + "/api/oof/v1/pci";
            log.debug("requestUrl{}", requestUrl);

            response = HttpRequester.sendPostRequest(requestUrl, requestBody);
            log.debug("response{}", response);

            return response;
        } catch (JsonProcessingException e) {
            log.debug("exception{}", e);

        }
        return response;
    }
}
