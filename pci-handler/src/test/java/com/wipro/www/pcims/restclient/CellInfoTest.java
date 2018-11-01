package com.wipro.www.pcims.restclient;

import static org.junit.Assert.assertEquals;

import com.wipro.www.pcims.restclient.CellIdList;
import com.wipro.www.pcims.restclient.CellInfo;
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;

public class CellInfoTest {
    @Test
    public void cellInfoTest() {
        CellIdList cellIdList = new CellIdList();
        List<CellIdList> cellIdLists = new ArrayList<CellIdList>();
        cellIdList.setCellId("cell1");
        cellIdLists.add(cellIdList);

        CellInfo cellInfo = new CellInfo();
        cellInfo.setNetworkId("NTWK001");
        cellInfo.setCellIdList(cellIdLists);
        assertEquals("NTWK001", cellInfo.getNetworkId());
        assertEquals(cellIdLists, cellInfo.getCellIdList());

    }
}
