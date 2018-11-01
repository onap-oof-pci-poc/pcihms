package com.wipro.www.pcims.restclient;

import static org.junit.Assert.assertEquals;

import com.wipro.www.pcims.restclient.PolicyRequestBody;
import org.junit.Test;

public class PolicyRequestBodyTest {
    @Test
    public void policyRequestBodyTest() {
        PolicyRequestBody policyRequestBody = new PolicyRequestBody();
        policyRequestBody.setConfigName("PCIMS_CONFIG_POLICY");
        policyRequestBody.setPolicyName("com.PCIMS_CONFIG_POLICY");
        policyRequestBody.setRequestId("60fe7fe6-2649-4f6c-8468-30eb03fd0527");
        assertEquals("PCIMS_CONFIG_POLICY", policyRequestBody.getConfigName());
        assertEquals("com.PCIMS_CONFIG_POLICY", policyRequestBody.getPolicyName());
        assertEquals("60fe7fe6-2649-4f6c-8468-30eb03fd0527", policyRequestBody.getRequestId());

    }

}
