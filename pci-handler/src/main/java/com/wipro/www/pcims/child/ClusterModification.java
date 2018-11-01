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

import com.wipro.www.pcims.model.CellPciPair;

import com.wipro.www.pcims.model.FapServiceList;
import com.wipro.www.pcims.model.LteNeighborListInUseLteCell;
import java.util.ArrayList;
import java.util.Map;

import org.slf4j.Logger;

public class ClusterModification {
    private static final Logger log = org.slf4j.LoggerFactory.getLogger(ClusterModification.class);

    /**
     * Forms a modified cluster for the existing cluster.
     */

    public Graph clustermod(Graph cluster, FapServiceList fapser) {

        int phycellId = fapser.getX0005b9Lte().getPhyCellIdInUse();
        String cellId = fapser.getCellConfig().getLte().getRan().getCellIdentity();
        CellPciPair mainCellPciPair = new CellPciPair();
        mainCellPciPair.setCellId(cellId);
        mainCellPciPair.setPhysicalCellId(phycellId);
        ArrayList<LteNeighborListInUseLteCell> newNeighbourList;
        newNeighbourList = fapser.getCellConfig().getLte().getRan().getNeighborListInUse()
                .getLteNeighborListInUseLteCell();

        Map<CellPciPair, ArrayList<CellPciPair>> clusterMap;
        clusterMap = cluster.getCellPciNeighbourMap();

        // coe

        ArrayList<CellPciPair> tempCellPair = new ArrayList<CellPciPair>();
        for (Map.Entry<CellPciPair, ArrayList<CellPciPair>> entry : clusterMap.entrySet()) {
            CellPciPair oldClusterKeys = entry.getKey();
            tempCellPair.add(oldClusterKeys);
        }

        for (CellPciPair entry : tempCellPair) {
            String cell = entry.getCellId();
            int physicalCell = entry.getPhysicalCellId();
            CellPciPair mapVal = new CellPciPair();
            mapVal.setCellId(cell);
            mapVal.setPhysicalCellId(physicalCell);

            if (cellId.equals(cell)) {

                // removes the old neighbours and adds new neighbours for that cell
                cluster.updateVertex(mapVal, mainCellPciPair);

            }

        }

        /////// update cluster with new pci values for the same cell

        if (clusterMap.containsKey(mainCellPciPair)) {
            ArrayList<CellPciPair> oldClusterArray;
            oldClusterArray = clusterMap.get(mainCellPciPair);
            oldClusterArray.clear();

            for (int i = 0; i < newNeighbourList.size(); i++) {
                String cid = newNeighbourList.get(i).getAlias();
                int phy = newNeighbourList.get(i).getPhyCellId();
                CellPciPair val2 = new CellPciPair();
                val2.setCellId(cid);
                val2.setPhysicalCellId(phy);
                cluster.addEdge(mainCellPciPair, val2);
            }

        }

        for (CellPciPair entry : tempCellPair) {
            String cell = entry.getCellId();
            int physicalCell = entry.getPhysicalCellId();
            CellPciPair mapVal = new CellPciPair();
            mapVal.setCellId(cell);
            mapVal.setPhysicalCellId(physicalCell);
            for (int j = 0; j < newNeighbourList.size(); j++) {
                String cid1 = newNeighbourList.get(j).getAlias();
                int phy1 = newNeighbourList.get(j).getPhyCellId();
                CellPciPair val3 = new CellPciPair();
                val3.setCellId(cid1);
                val3.setPhysicalCellId(phy1);

                if (cid1.equals(cell)) {

                    // removes the old neighbours and adds new neighbours for that cell
                    cluster.updateVertex(mapVal, val3);

                }

            }
        }

        for (int j = 0; j < newNeighbourList.size(); j++) {
            String cid1 = newNeighbourList.get(j).getAlias();
            int phy1 = newNeighbourList.get(j).getPhyCellId();
            CellPciPair val3 = new CellPciPair();
            val3.setCellId(cid1);
            val3.setPhysicalCellId(phy1);
            if (clusterMap.containsKey(val3)) {
                cluster.addEdge(mainCellPciPair, val3);
            }

        }

        for (int k = 0; k < newNeighbourList.size(); k++) {
            String cid2 = newNeighbourList.get(k).getAlias();
            int phy2 = newNeighbourList.get(k).getPhyCellId();
            CellPciPair val5 = new CellPciPair();
            val5.setCellId(cid2);
            val5.setPhysicalCellId(phy2);
            cluster.addEdge(mainCellPciPair, val5);
        }

        log.debug("Modified Cluster {}", cluster);

        return cluster;
    }

}