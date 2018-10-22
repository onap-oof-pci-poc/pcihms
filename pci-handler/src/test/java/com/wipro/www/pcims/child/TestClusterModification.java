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

import static org.junit.Assert.assertNotEquals;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wipro.www.pcims.model.CellPciPair;
import com.wipro.www.pcims.model.FapServiceList;

import java.io.IOException;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

public class TestClusterModification {
    @Test
    public void testClusterModification() {

        CellPciPair cpPair = new CellPciPair();
        cpPair.setCellId("32");
        cpPair.setPhysicalCellId(26);

        CellPciPair cpPair1 = new CellPciPair();
        cpPair1.setCellId("25");
        cpPair1.setPhysicalCellId(23);

        CellPciPair cpPair2 = new CellPciPair();
        cpPair2.setCellId("42");
        cpPair2.setPhysicalCellId(12);

        CellPciPair cpPair3 = new CellPciPair();
        cpPair3.setCellId("56");
        cpPair3.setPhysicalCellId(200);

        CellPciPair cpPair4 = new CellPciPair();
        cpPair4.setCellId("21");
        cpPair4.setPhysicalCellId(6);

        CellPciPair cpPair5 = new CellPciPair();
        cpPair5.setCellId("24");
        cpPair5.setPhysicalCellId(5);

        CellPciPair cpPair6 = new CellPciPair();
        cpPair6.setCellId("38");
        cpPair6.setPhysicalCellId(126);

        CellPciPair cpPair7 = new CellPciPair();
        cpPair7.setCellId("67");
        cpPair7.setPhysicalCellId(300);

        CellPciPair cpPair8 = new CellPciPair();
        cpPair8.setCellId("69");
        cpPair8.setPhysicalCellId(129);

        CellPciPair cpPair9 = new CellPciPair();
        cpPair9.setCellId("78");
        cpPair9.setPhysicalCellId(147);

        ArrayList<CellPciPair> al = new ArrayList<CellPciPair>();
        al.add(cpPair1);
        al.add(cpPair2);
        al.add(cpPair3);

        ArrayList<CellPciPair> al1 = new ArrayList<CellPciPair>();
        al1.add(cpPair4);
        al1.add(cpPair5);
        al1.add(cpPair6);

        ArrayList<CellPciPair> al2 = new ArrayList<CellPciPair>();
        al2.add(cpPair7);
        al2.add(cpPair8);
        al2.add(cpPair9);

        Map<CellPciPair, ArrayList<CellPciPair>> map = new HashMap<CellPciPair, ArrayList<CellPciPair>>();

        map.put(cpPair, al);
        map.put(cpPair1, al1);
        map.put(cpPair2, al2);
        map.put(cpPair3, new ArrayList<CellPciPair>());
        map.put(cpPair4, new ArrayList<CellPciPair>());
        map.put(cpPair5, new ArrayList<CellPciPair>());
        map.put(cpPair6, new ArrayList<CellPciPair>());
        map.put(cpPair7, new ArrayList<CellPciPair>());
        map.put(cpPair8, new ArrayList<CellPciPair>());
        map.put(cpPair9, new ArrayList<CellPciPair>());
        Graph cluster = new Graph();

        cluster.setCellPciNeighbourMap(map);

        System.out.println("map before" + cluster.getCellPciNeighbourMap());

        String notif = "{  \n" + "\"alias\":\"Cell1\",\n" + "\"X0005b9Lte\":{  \n" + "\"phyCellIdInUse\":\"35\",\n"
                + "\"pnfName\":\"DU-1\"\n" + "},\n" + "\"CellConfig\":{  \n" + "\"LTE\":{  \n" + "\"RAN\":{  \n"
                + "\"CellIdentity\":\"25\",\n" + "\"NeighborListInUse\":{  \n" + "\"LTECellNumberOfEntries\":\"2\",\n"
                + "\"LTENeighborListInUseLTECell\":[  \n" + "{  \n" + "\"pnfName\":\"DU-2\",\n"
                + "\"enable\":\"true\",\n" + "\"alias\":\"Cell10\",\n" + "\"mustInclude\":\"true\",\n"
                + "\"plmnid\":\"123456\",\n" + "\"cid\":\"2\",\n" + "\"phyCellId\":\"22\",\n"
                + "\"blacklisted\":\"false\"\n" + "},\n" + "{  \n" + "\"pnfName\":\"DU-3\",\n"
                + "\"enable\":\"true\",\n" + "\"alias\":\"Cell15\",\n" + "\"mustInclude\":\"true\",\n"
                + "\"plmnid\":\"123456\",\n" + "\"cid\":\"5\",\n" + "\"phyCellId\":\"24\",\n"
                + "\"blacklisted\":\"false\"\n" + "}\n" + "]\n" + "}\n" + "}\n" + "}\n" + "}\n" + "}";

        ObjectMapper mapper = new ObjectMapper();
        FapServiceList fapser = new FapServiceList();
        try {
            fapser = mapper.readValue(notif, FapServiceList.class);
        } catch (JsonParseException e) {
            e.printStackTrace();
        } catch (JsonMappingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        CellPciPair cpPair11 = new CellPciPair();
        cpPair11.setCellId("2");
        cpPair11.setPhysicalCellId(22);

        CellPciPair cpPair12 = new CellPciPair();

        cpPair12.setCellId("5");
        cpPair12.setPhysicalCellId(24);

        ArrayList<CellPciPair> al4 = new ArrayList<CellPciPair>();

        al4.add(cpPair11);
        al4.add(cpPair12);

        Map<CellPciPair, ArrayList<CellPciPair>> newMap = new HashMap<CellPciPair, ArrayList<CellPciPair>>();
        newMap.put(cpPair, al);
        newMap.put(cpPair1, al4);
        newMap.put(cpPair2, al2);
        newMap.put(cpPair3, new ArrayList<CellPciPair>());
        newMap.put(cpPair4, new ArrayList<CellPciPair>());
        newMap.put(cpPair5, new ArrayList<CellPciPair>());
        newMap.put(cpPair6, new ArrayList<CellPciPair>());
        newMap.put(cpPair7, new ArrayList<CellPciPair>());
        newMap.put(cpPair8, new ArrayList<CellPciPair>());
        newMap.put(cpPair9, new ArrayList<CellPciPair>());
        newMap.put(cpPair11, new ArrayList<CellPciPair>());
        newMap.put(cpPair12, new ArrayList<CellPciPair>());

        Graph newCluster = new Graph();
        newCluster.setCellPciNeighbourMap(newMap);
        System.out.print("newCluster" + newCluster.getCellPciNeighbourMap());

        ClusterModification mod = new ClusterModification();
        try {
            // System.out.print("Cluster" + mod.clustermod(cluster,
            // fapser).getCellPciNeighbourMap().toString());

            assertNotEquals(newCluster, mod.clustermod(cluster, fapser));

        } catch (NullPointerException e) {
            e.printStackTrace();
        } catch (ConcurrentModificationException e) {
            System.out.println("Concureent execption" + e);

        }
    }

}
