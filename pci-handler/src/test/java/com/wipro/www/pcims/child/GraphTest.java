package com.wipro.www.pcims.child;

import static org.junit.Assert.assertNotEquals;

import com.wipro.www.pcims.model.CellPciPair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.slf4j.Logger;

public class GraphTest {
    private static final Logger log = org.slf4j.LoggerFactory.getLogger(GraphTest.class);

    @Test
    public void graphTest() {

        CellPciPair cpPair1 = new CellPciPair();
        cpPair1.setCellId("25");
        cpPair1.setPhysicalCellId(32);

        CellPciPair cpPair2 = new CellPciPair();
        cpPair2.setCellId("29");
        cpPair2.setPhysicalCellId(209);

        Graph graph = new Graph();

        graph.addEdge(cpPair1, cpPair2);

        Map<CellPciPair, ArrayList<CellPciPair>> map = new HashMap<>();

        log.debug("graph {}", graph.getCellPciNeighbourMap());
        System.out.println("graph" + graph.getCellPciNeighbourMap());
        assertNotEquals(map, graph.getCellPciNeighbourMap());

    }

}
