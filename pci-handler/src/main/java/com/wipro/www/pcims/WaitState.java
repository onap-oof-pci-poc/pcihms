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

import com.wipro.www.pcims.dao.DmaapNotificationsRepository;
import com.wipro.www.pcims.utils.BeanUtil;

import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;

public class WaitState implements PciState {

    private static WaitState instance;
    private List<String> sdnrNotification = new ArrayList<>();
    private static final Logger log = org.slf4j.LoggerFactory.getLogger(WaitState.class);

    protected WaitState() {

    }

    /**
     * singleton.
     */
    public static WaitState getInstance() {
        if (instance == null) {
            return new WaitState();
        }
        return instance;
    }

    public void putSdnrNotification(String notification) {
        sdnrNotification.add(notification);
        log.debug("sdnrNotification size: {}", sdnrNotification.size());
    }

    @Override
    public void stateChange(PciContext pciContext) {
        log.debug("inside state change of wait state");

        while (pciContext.getChildStatusUpdate().isEmpty() && !pciContext.getNewNotification().getNewNotif()) {
        }

        List<String> childStatus = pciContext.getChildStatusUpdate().poll();
        if (childStatus != null) {
            Long threadId = Long.parseLong(childStatus.get(0));
            log.debug("threadId: {}", threadId);
            log.debug("childStatus: {}", childStatus.get(1));
            pciContext.setChildThreadId(threadId);
            pciContext.addChildStatus(threadId, childStatus.get(1));
            pciContext.setPciState(new ChildStatusUpdateState());
            pciContext.stateChange(pciContext);
        }

        DmaapNotificationsRepository dmaapNotificationsRepository = BeanUtil
                .getBean(DmaapNotificationsRepository.class);
        String notification = dmaapNotificationsRepository.getNotificationFromQueue();
        if (notification != null) {
            log.debug("notification from sdnr:{}", notification);
            pciContext.setSdnrNotification(notification);
            pciContext.setPciState(new SdnrNotificationHandlingState());
            pciContext.stateChange(pciContext);
        } else {
            pciContext.getNewNotification().setNewNotif(false);
            log.debug("setting new notification to false");
            pciContext.setPciState(this);
            pciContext.stateChange(pciContext);
        }

    }
}
