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

package com.wipro.www.pcims;

import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MainThread implements Runnable {
    private static Logger log = LoggerFactory.getLogger(MainThread.class);

    private NewNotification newNotification;

    @Override
    public void run() {
        log.debug("Starting pci context");
        PciContext pciContext = new PciContext(new LinkedBlockingQueue<List<String>>(), newNotification);
        log.debug("initializing pci state to wait state");
        WaitState waitState = WaitState.getInstance();
        pciContext.setPciState(waitState);
        pciContext.stateChange(pciContext);
    }

    public MainThread(NewNotification newNotification) {
        super();
        this.newNotification = newNotification;
    }

}
