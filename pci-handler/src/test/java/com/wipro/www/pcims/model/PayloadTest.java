package com.wipro.www.pcims.model;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;

import org.junit.Test;

public class PayloadTest {

    @Test
    public void payloadTest() {
        Common common = new Common("cell1");

        Rf rf = new Rf("22");

        Ran ran = new Ran(rf, common);

        Lte lte = new Lte(ran);

        CellConfig cellConfig = new CellConfig(lte);

        FapService fapService = new FapService("cell6", cellConfig);

        Data data = new Data(fapService);

        Configurations config = new Configurations(data, "pnf2");
        ArrayList<Configurations> al = new ArrayList<>();
        al.add(config);

        Payload payload = new Payload(al);

        assertEquals("pnf2", payload.getConfiguration().get(0).getPnfName());

        assertEquals("cell6", payload.getConfiguration().get(0).getData().getFapservice().getAlias());

    }

}
