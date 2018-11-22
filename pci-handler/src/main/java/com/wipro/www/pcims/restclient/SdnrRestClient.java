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
import com.wipro.www.pcims.model.CellPciPair;
import com.wipro.www.pcims.utils.HttpRequester;

import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

public class SdnrRestClient {

    private static final String DATETIMEFORMAT = "yyyy-MM-dd HH:mm:ss";

    private SdnrRestClient() {

    }

    /**
     * Method to get cell list from SDNR.
     */
    public static String getCellList(String networkId) {
        Configuration configuration = Configuration.getInstance();
        String ts = new SimpleDateFormat(DATETIMEFORMAT).format(new Time(System.currentTimeMillis()));
        String requestUrl = configuration.getSdnrService() + "/SDNCConfigDBAPI/getCellList" + "/" + networkId + "/"
                + ts;
        return HttpRequester.sendGetRequest(requestUrl);
    }

    /**
     * Method to get neibhbour list from SDNR.
     */
    public static List<CellPciPair> getNbrList(String cellId) {
        Configuration configuration = Configuration.getInstance();
        String ts = new SimpleDateFormat(DATETIMEFORMAT).format(new Time(System.currentTimeMillis()));
        String requestUrl = configuration.getSdnrService() + "/SDNCConfigDBAPI/getNbrList" + "/" + cellId + "/" + ts;
        String response = HttpRequester.sendGetRequest(requestUrl);
        List<CellPciPair> nbrList = new ArrayList<>();
        JSONArray nbrListObj = new JSONArray(response);
        for (int i = 0; i < nbrListObj.length(); i++) {
            JSONObject cellObj = nbrListObj.getJSONObject(i);
            CellPciPair cell = new CellPciPair(cellObj.getString("cellId"), cellObj.getInt("pciValue"));
            nbrList.add(cell);
        }

        return nbrList;
    }

    /**
     * Method to get PCI from SDNR.
     */
    public static int getPci(String cellId) {
        Configuration configuration = Configuration.getInstance();
        String ts = new SimpleDateFormat(DATETIMEFORMAT).format(new Time(System.currentTimeMillis()));
        String requestUrl = configuration.getSdnrService() + "/SDNCConfigDBAPI/getPCI" + "/" + cellId + "/" + ts;
        String response = HttpRequester.sendGetRequest(requestUrl);
        JSONObject respObj = new JSONObject(response);
        return respObj.getInt("value");
    }

    /**
     * Method to get PNF name from SDNR.
     */
    public static String getPnfName(String cellId) {
        Configuration configuration = Configuration.getInstance();
        String ts = new SimpleDateFormat(DATETIMEFORMAT).format(new Time(System.currentTimeMillis()));
        String requestUrl = configuration.getSdnrService() + "/SDNCConfigDBAPI/getPnfName" + "/" + cellId + "/" + ts;
        String response = HttpRequester.sendGetRequest(requestUrl);
        JSONObject responseObject = new JSONObject(response);
        return responseObject.getString("value");
    }

}
