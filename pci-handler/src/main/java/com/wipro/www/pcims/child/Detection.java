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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;

public class Detection {

    private static final Logger log = org.slf4j.LoggerFactory.getLogger(Detection.class);

    /**
     * Returns a map with key as cellid and its value is a list of its collision and
     * confusion.
     */

    public Map<String, ArrayList<Integer>> detectCollisionConfusion(Graph cluster) {

        Map<CellPciPair, ArrayList<CellPciPair>> clusterMap = cluster.getCellPciNeighbourMap();
        HashMap<String, ArrayList<Integer>> hash = new HashMap<>();

        for (Map.Entry<CellPciPair, ArrayList<CellPciPair>> entry : clusterMap.entrySet()) {
            int collisionCount = 0;
            int confusionCount = 0;
            CellPciPair val = entry.getKey();
            String cellId = val.getCellId();
            int pci = val.getPhysicalCellId();
            ArrayList<CellPciPair> arr;
            // getting colision and confusion count
            ArrayList<Integer> counts = new ArrayList<>();
            // gets the value for the key
            arr = entry.getValue();
            if (!arr.isEmpty()) {
                for (int i = 0; i < arr.size(); i++) {
                    if (pci == arr.get(i).getPhysicalCellId()) {
                        collisionCount++;
                    }

                }
                counts.add(collisionCount);

                for (int j = 0; j < arr.size(); j++) {
                    for (int k = j + 1; k < arr.size(); k++) {
                        if (arr.get(j).getPhysicalCellId() == arr.get(k).getPhysicalCellId()) {
                            confusionCount++;

                        }
                    }

                }
                counts.add(confusionCount);
                log.debug("count {}", counts.toString());

            }
            hash.put(cellId, counts);

        }
        log.debug("collison and confusion map {}", hash);

        return hash;
    }

}
