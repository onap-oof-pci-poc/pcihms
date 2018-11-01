package com.wipro.www.pcims.restclient;

import static org.junit.Assert.assertEquals;

import com.wipro.www.pcims.restclient.RequestInfo;
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;

public class RequestInfoTest {
    @Test
    public void requestInfoTest() {
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
        assertEquals(1, requestInfo.getNumSolutions());
        assertEquals(optimizers, requestInfo.getOptimizers());
        assertEquals("create", requestInfo.getRequestType());
        assertEquals("PCIHMS", requestInfo.getSourceId());
        assertEquals("3df7b0e9-26d1-4080-ba42-28e8a3139689", requestInfo.getTransactionId());
        assertEquals("e44a4165-3cf4-4362-89de-e2375eed97e7", requestInfo.getRequestId());
        assertEquals(60, requestInfo.getTimeout());
        assertEquals("", requestInfo.getCallbackUrl());
    }

}
