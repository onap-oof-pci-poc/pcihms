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

import com.wipro.www.pcims.Configuration;
import com.wipro.www.pcims.utils.HttpRequester;

public class SdnrRestClient {
    private SdnrRestClient() {

    }

    /**
     * Method to get cell list from SDNR.
     */
    public static String getCellList() {
        Configuration configuration = Configuration.getInstance();
        String requestUrl = configuration.getSdnrService() + "/databaseAPI/getCellList";
        return HttpRequester.sendGetRequest(requestUrl);
    }

    /**
     * Method to get neibhbour list from SDNR.
     */
    public static String getNbrList() {
        Configuration configuration = Configuration.getInstance();
        String requestUrl = configuration.getSdnrService() + "/databaseAPI/getNbrList";
        return HttpRequester.sendGetRequest(requestUrl);

    }

    /**
     * Method to get PCI from SDNR.
     */
    public static String getPci() {
        Configuration configuration = Configuration.getInstance();
        String requestUrl = configuration.getSdnrService() + "/databaseAPI/getPCI";
        return HttpRequester.sendGetRequest(requestUrl);
    }

    /**
     * Method to get PNF name from SDNR.
     */
    public static String getPnfName() {
        Configuration configuration = Configuration.getInstance();
        String requestUrl = configuration.getSdnrService() + "/databaseAPI/getPnfName";
        return HttpRequester.sendGetRequest(requestUrl);
    }

}
