package com.wipro.www.pcims.restclient;

import static org.junit.Assert.assertEquals;

import com.wipro.www.pcims.restclient.PciSolution;
import com.wipro.www.pcims.restclient.Solution;
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;

public class SolutionsTest {

    @Test
    public void solutionsTest() {

        PciSolution pciSolutions = new PciSolution();
        pciSolutions.setCellId("EXP001");
        pciSolutions.setPci(101);
        List<PciSolution> pciSolutionsList = new ArrayList<PciSolution>();
        pciSolutionsList.add(pciSolutions);
        Solution solutions = new Solution();
        solutions.setFinishTime("2018-10-01T00:40+01.00");
        solutions.setNetworkId("EXP001");
        solutions.setPciSolutions(pciSolutionsList);
        solutions.setStartTime("2018-10-01T00:30+01:00");
        assertEquals("2018-10-01T00:40+01.00", solutions.getFinishTime());
        assertEquals("EXP001", solutions.getNetworkId());
        assertEquals(pciSolutionsList, solutions.getPciSolutions());
        assertEquals("2018-10-01T00:30+01:00", solutions.getStartTime());

    }

}
