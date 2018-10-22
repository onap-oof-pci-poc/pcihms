/*******************************************************************************
 * ============LICENSE_START=======================================================
 * pcims
 *  ================================================================================
 *  Copyright (C) 2018 Wipro Limited.
 *  ==============================================================================
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 *   ============LICENSE_END=========================================================
 ******************************************************************************/

package com.wipro.www.pcims.model;

import static org.junit.Assert.assertNotEquals;

import org.junit.Test;

public class NotificationTest {

    @Test
    public void notificationTest() {

        Notification notif = new Notification();
        // Payload payload = new Payload();

        notif.setRequestId("9d2d790e-a5f0-11e8-98d0-529269fb1459");
        notif.setAai("");
        notif.setAction("NeighborListModified");
        notif.setFrom("SDNR");
        notif.setVersion("1.0.2");
        notif.setPayload(null);
        assertNotEquals("159", notif.getRequestId());

    }

}
