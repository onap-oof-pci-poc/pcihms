package com.wipro.www.pcims.child;

import com.wipro.www.pcims.model.CellPciPair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

public class TestDetection {
    @Test
    public void testDetection() {

        CellPciPair cpPair = new CellPciPair();
        cpPair.setCellId("32");
        cpPair.setPhysicalCellId(26);

        CellPciPair cpPair1 = new CellPciPair();
        cpPair1.setCellId("25");
        cpPair1.setPhysicalCellId(23);

        CellPciPair cpPair2 = new CellPciPair();
        cpPair2.setCellId("42");
        cpPair2.setPhysicalCellId(26);

        CellPciPair cpPair3 = new CellPciPair();
        cpPair3.setCellId("56");
        cpPair3.setPhysicalCellId(200);

        CellPciPair cpPair4 = new CellPciPair();
        cpPair4.setCellId("21");
        cpPair4.setPhysicalCellId(5);

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

        System.out.println("mapsssssss" + cluster.getCellPciNeighbourMap());
        Detection detect = new Detection();
        detect.detectCollisionConfusion(cluster);
        System.out.println("result" + detect.detectCollisionConfusion(cluster));

    }

}
