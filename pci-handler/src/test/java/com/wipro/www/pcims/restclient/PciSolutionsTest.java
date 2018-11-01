package com.wipro.www.pcims.restclient;

import static org.junit.Assert.assertEquals;

import com.wipro.www.pcims.restclient.PciSolution;
import org.junit.Test;

public class PciSolutionsTest {
    @Test
    public void pciSolutionsTest() {
        PciSolution pciSolutions = new PciSolution();
        pciSolutions.setCellId("EXP001");
        pciSolutions.setPci(101);
        assertEquals("EXP001", pciSolutions.getCellId());
        assertEquals(101, pciSolutions.getPci());
    }

}
