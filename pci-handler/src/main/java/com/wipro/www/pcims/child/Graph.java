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
import java.util.Map;
import java.util.UUID;

import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;

public class Graph {
    private static final Logger log = org.slf4j.LoggerFactory.getLogger(Graph.class);

    // symbol table: key = string vertex, value = set of neighboring vertices
    private Map<CellPciPair, ArrayList<CellPciPair>> cellPciNeighbourMap;
    private UUID graphId;

    public UUID getGraphId() {
        return graphId;
    }

    public void setGraphId(UUID graphId) {
        this.graphId = graphId;
    }

    public Map<CellPciPair, ArrayList<CellPciPair>> getCellPciNeighbourMap() {
        return cellPciNeighbourMap;
    }

    public void setCellPciNeighbourMap(Map<CellPciPair, ArrayList<CellPciPair>> cellPciNeighbourMap) {
        this.cellPciNeighbourMap = cellPciNeighbourMap;
    }

    /**
     * Initializes an empty graph with no vertices or edges.
     */
    public Graph() {
        this.cellPciNeighbourMap = new ConcurrentHashMap<>();
    }

    // throw an exception if v is not a vertex
    private void validateVertex(CellPciPair start) {
        if (!hasVertex(start)) {
            throw new IllegalArgumentException(start + " is not a vertex");
        }
    }

    /**
     * Adds the edge v-w to this graph (if it is not already an edge).
     */
    public void addEdge(CellPciPair start, CellPciPair end) {
        if (!hasVertex(start)) {
            addVertex(start);
        }
        if (!hasVertex(end)) {
            addVertex(end);
        }
        if (!hasEdge(start, end)) {
            this.cellPciNeighbourMap.get(start).add(end);
        }
    }

    /**
     * Adds vertex v to this graph (if it is not already a vertex).
     */
    public void addVertex(CellPciPair start) {
        if (!hasVertex(start)) {
            this.cellPciNeighbourMap.put(start, new ArrayList<CellPciPair>());
        }
    }

    /**
     * Returns the set of vertices adjacent to v in this graph.
     */
    public Iterable<CellPciPair> adjacentTo(CellPciPair start) {
        validateVertex(start);
        return this.cellPciNeighbourMap.get(start);
    }

    /**
     * Returns true if v is a vertex in this graph.
     */
    public boolean hasVertex(CellPciPair start) {
        return this.cellPciNeighbourMap.containsKey(start);
    }

    /**
     * Returns true if v-w is an edge in this graph.
     */
    public boolean hasEdge(CellPciPair start, CellPciPair end) {
        validateVertex(start);
        validateVertex(end);
        return this.cellPciNeighbourMap.get(start).contains(end);
    }

    /**
     * Updates Vertex.
     */
    public void updateVertex(CellPciPair oldPair, CellPciPair newPair) {
        log.debug("mapsss {}" + this.cellPciNeighbourMap);
        String cell1 = oldPair.getCellId();
        String cell2 = newPair.getCellId();
        log.debug("equal:" + cell1.equals(cell2));
        log.debug("Oldpair {}" + oldPair);
        log.debug("NewPair " + newPair);
        log.debug("condition before if {}" + this.cellPciNeighbourMap.get(oldPair));
        log.debug("Contains oldPair: " + cellPciNeighbourMap.containsKey(oldPair));
        log.debug("cell1 =1 cell2? " + cell1.equals(cell2));

        if (cell1.equals(cell2)) {

            log.debug("old" + this.cellPciNeighbourMap.get(oldPair));
            log.debug("condition {}" + this.cellPciNeighbourMap.get(oldPair));

            this.cellPciNeighbourMap.put(newPair, this.cellPciNeighbourMap.get(oldPair));
            this.cellPciNeighbourMap.remove(oldPair);

        }
        log.debug("map after updating {}" + cellPciNeighbourMap);
        for (Map.Entry<CellPciPair, ArrayList<CellPciPair>> entry : this.cellPciNeighbourMap.entrySet()) {

            ArrayList<CellPciPair> al = entry.getValue();
            for (int i = 0; i < al.size(); i++) {
                String cell3 = al.get(i).getCellId();
                if (cell3.equals(cell1)) {
                    if (al.contains(oldPair)) {
                        al.remove(oldPair);
                        al.add(newPair);
                    }
                    this.cellPciNeighbourMap.put(entry.getKey(), al);
                }
            }
        }
        log.debug("Final Map" + cellPciNeighbourMap);

    }

    @Override
    public String toString() {
        return "Graph [cellPciNeighbourMap=" + cellPciNeighbourMap + ", graphId=" + graphId + "]";
    }

}
