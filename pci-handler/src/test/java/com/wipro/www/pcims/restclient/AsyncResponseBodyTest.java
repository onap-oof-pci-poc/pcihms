package com.wipro.www.pcims.restclient;

import com.wipro.www.pcims.restclient.AsyncResponseBody;
import com.wipro.www.pcims.restclient.PciSolution;
import com.wipro.www.pcims.restclient.Solution;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class AsyncResponseBodyTest {

    @Test
    public void asyncResponseBodyTest() {
        AsyncResponseBody asyncResponseBody = new AsyncResponseBody();
        asyncResponseBody.setRequestId("e44a4165-3cf4-4362-89de-e2375eed97e7");
        asyncResponseBody.setRequestStatus("completed");
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
        ArrayList<Solution> solutionsList = new ArrayList<Solution>();
        solutionsList.add(solutions);
        asyncResponseBody.setSolutions(solutionsList);
        asyncResponseBody.setStatusMessage("success");
        asyncResponseBody.setTransactionId("3df7b0e9-26d1-4080-ba42-28e8a3139689");
    }
}
