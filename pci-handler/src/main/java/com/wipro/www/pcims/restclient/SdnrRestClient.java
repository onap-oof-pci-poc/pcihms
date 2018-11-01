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

import java.sql.Time;
import java.text.SimpleDateFormat;

import org.json.JSONObject;

public class SdnrRestClient {

    private static final String DATETIMEFORMAT = "yyyy.MM.dd.HH.mm.ss";
    private static final String CELLID = "cellId=";

    private SdnrRestClient() {

    }

    /**
     * Method to get cell list from SDNR.
     */
    public static String getCellList(String networkId) {
        Configuration configuration = Configuration.getInstance();
        String ts = new SimpleDateFormat(DATETIMEFORMAT).format(new Time(System.currentTimeMillis()));
        String requestParams = "networkId=" + networkId + "&ts=" + ts;
        String requestUrl = configuration.getSdnrService() + "/SDNCConfigDBAPI/getCellList" + "?" + requestParams;
        return HttpRequester.sendGetRequest(requestUrl);
    }

    /**
     * Method to get neibhbour list from SDNR.
     */
    public static String getNbrList(String cellId) {
        Configuration configuration = Configuration.getInstance();
        String ts = new SimpleDateFormat(DATETIMEFORMAT).format(new Time(System.currentTimeMillis()));
        String requestParams = CELLID + cellId + "&ts=" + ts;
        String requestUrl = configuration.getSdnrService() + "/SDNCConfigDBAPI/getNbrList" + "?" + requestParams;
        return HttpRequester.sendGetRequest(requestUrl);

    }

    /**
     * Method to get PCI from SDNR.
     */
    public static String getPci(String cellId) {
        Configuration configuration = Configuration.getInstance();
        String ts = new SimpleDateFormat(DATETIMEFORMAT).format(new Time(System.currentTimeMillis()));
        String requestParams = CELLID + cellId + "&ts=" + ts;
        String requestUrl = configuration.getSdnrService() + "/SDNCConfigDBAPI/getPCI" + "?" + requestParams;
        return HttpRequester.sendGetRequest(requestUrl);
    }

    /**
     * Method to get PNF name from SDNR.
     */
    public static String getPnfName(String cellId) {
        Configuration configuration = Configuration.getInstance();
        String ts = new SimpleDateFormat(DATETIMEFORMAT).format(new Time(System.currentTimeMillis()));
        String requestParams = CELLID + cellId + "&ts=" + ts;
        String requestUrl = configuration.getSdnrService() + "/SDNCConfigDBAPI/getPnfName" + "?" + requestParams;
        String response = HttpRequester.sendGetRequest(requestUrl);
        JSONObject responseObject = new JSONObject(response);
        return responseObject.getString("pnfName");
    }

}
