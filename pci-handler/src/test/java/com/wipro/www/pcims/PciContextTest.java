package com.wipro.www.pcims;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class PciContextTest {

    @Test
    public void pciContextTest() {
        PciContext pciContext = new PciContext();
        pciContext.setChildThreadId(1);
        pciContext.setNotifToBeProcessed(true);
        pciContext.setSdnrNotification("notification");
        SdnrNotificationHandlingState pciState = new SdnrNotificationHandlingState();
        pciContext.setPciState(pciState);
        assertEquals(1, pciContext.getChildThreadId());
        assertTrue(pciContext.isNotifToBeProcessed());
        assertEquals("notification", pciContext.getSdnrNotification());
        assertEquals(pciState, pciContext.getPciState());

    }
}
