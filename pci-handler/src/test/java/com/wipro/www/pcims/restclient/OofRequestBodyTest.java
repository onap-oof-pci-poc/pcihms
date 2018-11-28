package com.wipro.www.pcims.restclient;

import static org.junit.Assert.assertEquals;

import com.wipro.www.pcims.restclient.CellInfo;
import com.wipro.www.pcims.restclient.OofRequestBody;
import com.wipro.www.pcims.restclient.RequestInfo;
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;

public class OofRequestBodyTest {
    @Test
    public void oofRequestBodyTest() {

        List<String> cellIdLists = new ArrayList<>();
        cellIdLists.add("cell1");
        CellInfo cellInfo = new CellInfo();
        cellInfo.setNetworkId("NTWK001");
        cellInfo.setCellIdList(cellIdLists);
        RequestInfo requestInfo = new RequestInfo();
        List<String> optimizers = new ArrayList<String>();
        optimizers.add("PCI");
        requestInfo.setCallbackUrl("");
        requestInfo.setNumSolutions(1);
        requestInfo.setOptimizers(optimizers);
        requestInfo.setRequestId("e44a4165-3cf4-4362-89de-e2375eed97e7");
        requestInfo.setRequestType("create");
        requestInfo.setSourceId("PCIHMS");
        requestInfo.setTimeout(60);
        requestInfo.setTransactionId("3df7b0e9-26d1-4080-ba42-28e8a3139689");

        OofRequestBody oofRequestBody = new OofRequestBody();
        oofRequestBody.setCellInfo(cellInfo);
        oofRequestBody.setRequestInfo(requestInfo);
        assertEquals(requestInfo, oofRequestBody.getRequestInfo());
        assertEquals(cellInfo, oofRequestBody.getCellInfo());
    }
}
