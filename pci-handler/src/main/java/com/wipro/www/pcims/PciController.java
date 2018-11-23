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

import com.wipro.www.pcims.child.ChildThread;
import com.wipro.www.pcims.restclient.AsyncResponseBody;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PciController {
    private static final Logger log = org.slf4j.LoggerFactory.getLogger(PciController.class);

    @Autowired
    PciRequestsComponent pciRequestsComponent;

    @RequestMapping(value = "/callbackUrl", method = RequestMethod.POST)
    String callBackUrl(@RequestBody AsyncResponseBody callback) {
        log.debug("received request to callback url");
        String async = callback.toString();
        log.debug("AsyncResponseBody{}", async);

        String transactionId = callback.getTransactionId();
        log.debug("transaction id {}", transactionId);

        long childThreadId = pciRequestsComponent.getChildThread(transactionId);
        log.debug("childThreadId {}", childThreadId);

        ChildThread.putResponse(childThreadId, callback);
        return "Forwarded to child thread";

    }

}
