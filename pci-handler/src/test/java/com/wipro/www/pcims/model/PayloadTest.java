package com.wipro.www.pcims.model;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;

import org.junit.Test;

public class PayloadTest {

    @Test
    public void payloadTest() {
        Common common = new Common("cell1");

        Ran ran = new Ran(common);

        Lte lte = new Lte(ran);

        CellConfig cellConfig = new CellConfig(lte);

        X0005b9Lte x0005b9Lte = new X0005b9Lte(0, "pnf2");

        FapService fapService = new FapService("cell6", x0005b9Lte, cellConfig);

        Data data = new Data(fapService);

        Configurations config = new Configurations(data);
        ArrayList<Configurations> al = new ArrayList<>();
        al.add(config);

        Payload payload = new Payload(al);

        assertEquals("pnf2", payload.getConfiguration().get(0).getData().getFapservice().getX0005b9Lte().getPnfName());

        assertEquals("cell6", payload.getConfiguration().get(0).getData().getFapservice().getAlias());

    }

}
