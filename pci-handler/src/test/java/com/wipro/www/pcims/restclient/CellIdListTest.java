package com.wipro.www.pcims.restclient;

import static org.junit.Assert.assertEquals;

import com.wipro.www.pcims.restclient.CellIdList;
import org.junit.Test;

public class CellIdListTest {
    @Test
    public void cellIdListTest() {
        CellIdList cellIdList = new CellIdList();
        cellIdList.setCellId("cell1");
        assertEquals("cell1", cellIdList.getCellId());

    }
}
