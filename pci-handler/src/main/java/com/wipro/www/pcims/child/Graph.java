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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wipro.www.pcims.model.CellNeighbourList;
import com.wipro.www.pcims.model.CellPciPair;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import java.util.concurrent.ConcurrentHashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;

public class Graph {
    private static final Logger log = org.slf4j.LoggerFactory.getLogger(Graph.class);

    // symbol table: key = string vertex, value = set of neighboring vertices
    private Map<CellPciPair, ArrayList<CellPciPair>> cellPciNeighbourMap;
    private UUID graphId;

    /**
     * Parameterized constructor.
     */
    @SuppressWarnings("unchecked")
    public Graph(String clusterInfo) {
        JSONArray cells = new JSONArray(clusterInfo);

        Map<CellPciPair, ArrayList<CellPciPair>> cellMap = new HashMap<>();
        for (int i = 0; i < cells.length(); i++) {
            JSONObject cell = (JSONObject) cells.get(i);
            CellPciPair cellPciPair = new CellPciPair(cell.getString("cellId"), cell.getInt("physicalCellId"));
            ObjectMapper mapper = new ObjectMapper();
            ArrayList<CellPciPair> neighbours = new ArrayList<>();
            try {
                neighbours = mapper.readValue(cell.getString("neighbours"), ArrayList.class);
            } catch (JSONException | IOException e) {
                log.debug("Error parsing json: {}", e);
            }
            cellMap.put(cellPciPair, neighbours);

        }

        this.cellPciNeighbourMap = cellMap;
    }

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
        int oldPci = oldPair.getPhysicalCellId();
        int newPci = newPair.getPhysicalCellId();

        if (oldPci != newPci) {

            this.cellPciNeighbourMap.put(newPair, this.cellPciNeighbourMap.get(oldPair));
            this.cellPciNeighbourMap.remove(oldPair);

        }
        for (Map.Entry<CellPciPair, ArrayList<CellPciPair>> entry : this.cellPciNeighbourMap.entrySet()) {

            ArrayList<CellPciPair> al = entry.getValue();
            for (int i = 0; i < al.size(); i++) {
                int pci = al.get(i).getPhysicalCellId();
                if (pci != newPci) {
                    if (al.contains(oldPair)) {
                        al.remove(oldPair);
                        al.add(newPair);
                    }
                }
            }
        }
        log.debug("Final Map {}", cellPciNeighbourMap);

    }

    @Override
    public String toString() {
        return "Graph [cellPciNeighbourMap=" + cellPciNeighbourMap + ", graphId=" + graphId + "]";
    }

    /**
     * Convert Graph into a json.
     */
    public String getPciNeighbourJson() {

        List<CellNeighbourList> cells = new ArrayList<>();

        for (CellPciPair key : cellPciNeighbourMap.keySet()) {
            JSONArray neighbours = new JSONArray(cellPciNeighbourMap.get(key));
            CellNeighbourList cell = new CellNeighbourList(key.getCellId(), key.getPhysicalCellId(),
                    neighbours.toString());
            cells.add(cell);
        }
        ObjectMapper mapper = new ObjectMapper();
        String pciNeighbourJson = "";
        try {
            pciNeighbourJson = mapper.writeValueAsString(cells);
        } catch (JsonProcessingException e) {
            log.debug("Error while processing json: {}", e);
        }
        return pciNeighbourJson;
    }

}
