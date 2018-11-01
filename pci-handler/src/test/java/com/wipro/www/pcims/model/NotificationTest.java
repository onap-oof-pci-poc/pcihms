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

package com.wipro.www.pcims.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.ArrayList;

import org.junit.Test;

public class NotificationTest {

    @Test
    public void notificationTest() {

        Notification notif = new Notification();
        LteNeighborListInUseLteCell lteNeighborListInUseLteCell = new LteNeighborListInUseLteCell("pnf1", "true",
                "Cell10", "true", "123456", "5", 22, "false");

        ArrayList<LteNeighborListInUseLteCell> list = new ArrayList<>();
        list.add(lteNeighborListInUseLteCell);

        NeighborListInUse neighborListInUse = new NeighborListInUse(list, "1");

        NotificationRan notificationRan = new NotificationRan(neighborListInUse, "Cell25");
        NotificationLte notificationLte = new NotificationLte(notificationRan);
        NotificationCellConfig notificationCell = new NotificationCellConfig(notificationLte);
        X0005b9Lte lte = new X0005b9Lte(126, "pnf2");
        FapServiceList fap = new FapServiceList("Cell1", lte, notificationCell);

        ArrayList<FapServiceList> al = new ArrayList<>();
        al.add(fap);

        RadioAccess radioAccess = new RadioAccess("1", al);
        NotificationPayload payload = new NotificationPayload(radioAccess);

        notif.setRequestId("9d2d790e-a5f0-11e8-98d0-529269fb1459");
        notif.setAai("{}");
        notif.setAction("NeighborListModified");
        notif.setFrom("SDNR");
        notif.setVersion("1.0.2");
        notif.setPayload(payload);
        assertNotEquals("159", notif.getRequestId());

        String test = "{\n" + "  \"requestID\": \"9d2d790e-a5f0-11e8-98d0-529269fb1459\",\n" + "  \"AAI\": {},\n"
                + "  \"from\": \"SDNR\",\n" + "  \"version\": \"1.0.2\",\n"
                + "  \"Action\": \"NeighborListModified\",\n" + "  \"Payload\": {\n" + "\n" + "   \"RadioAccess\":{  \n"
                + "      \"FAPServiceNumberOfEntries\":\"2\",\n" + "      \"FAPServiceList\":[  \n" + "         {  \n"
                + "            \"alias\":\"Cell1\",\n" + "            \"X0005b9Lte\":{  \n"
                + "               \"phyCellIdInUse\":\"35\",\n" + "               \"pnfName\":\"DU-1\"\n"
                + "            },\n" + "            \"CellConfig\":{  \n" + "               \"LTE\":{  \n"
                + "                  \"RAN\":{  \n" + "                     \"CellIdentity\":\"Cell1\",\n"
                + "                     \"NeighborListInUse\":{  \n"
                + "                        \"LTECellNumberOfEntries\":\"2\",\n"
                + "                        \"LTENeighborListInUseLTECell\":[  \n" + "                           {  \n"
                + "                              \"pnfName\":\"DU-2\",\n"
                + "                              \"enable\":\"true\",\n"
                + "                              \"alias\":\"Cell10\",\n"
                + "                              \"mustInclude\":\"true\",\n"
                + "                              \"plmnid\":\"123456\",\n"
                + "                              \"cid\":\"2\",\n"
                + "                              \"phyCellId\":\"22\",\n"
                + "                              \"blacklisted\":\"false\"\n" + "                           },\n"
                + "                           {  \n" + "                              \"pnfName\":\"DU-3\",\n"
                + "                              \"enable\":\"true\",\n"
                + "                              \"alias\":\"Cell15\",\n"
                + "                              \"mustInclude\":\"true\",\n"
                + "                              \"plmnid\":\"123456\",\n"
                + "                              \"cid\":\"5\",\n"
                + "                              \"phyCellId\":\"24\",\n"
                + "                              \"blacklisted\":\"false\"\n" + "                           }\n"
                + "                        ]\n" + "                     }\n" + "                  }\n"
                + "               }\n" + "            }\n" + "         }\n" + "         ]\n" + "   }\n" + "}\n" + "}";

        ObjectMapper mapper = new ObjectMapper();
        Notification notif1 = new Notification();
        try {
            notif1 = mapper.readValue(test, Notification.class);
        } catch (JsonParseException e) {
            e.printStackTrace();
        } catch (JsonMappingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        assertNotEquals(notif, notif1);
        assertEquals(notif.getAction(), notif1.getAction());
        assertEquals(notif.getAai().toString(), notif1.getAai().toString());

    }

}
