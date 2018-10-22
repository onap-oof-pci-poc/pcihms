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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;

public class WaitState implements PciState {

    private static WaitState instance;
    private Map<Long, String> childStatusUpdate = new HashMap<>();
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

    public synchronized void putChildStatus(Long childThreadId, String status) {
        childStatusUpdate.put(childThreadId, status);
    }

    public synchronized String getChildStatus(Long childThreadId) {
        return childStatusUpdate.get(childThreadId);
    }

    public synchronized void putSdnrNotification(String notification) {
        sdnrNotification.add(notification);
        log.debug("sdnrNotification size: {}", sdnrNotification.size());
    }

    @Override
    public void stateChange(PciContext pciContext) {
        log.debug("inside state change of wait state");
        while (childStatusUpdate.isEmpty() && sdnrNotification.isEmpty()) {
            log.debug("child update queue: {}", childStatusUpdate.size());
            log.debug("sdnr notif queue: {}", sdnrNotification.size());
        }
        if (!childStatusUpdate.isEmpty()) {
            log.debug("child status update received");
            Set childThreadId = childStatusUpdate.keySet();
            Iterator iterate = childThreadId.iterator();
            while (iterate.hasNext()) {
                pciContext.setChildThreadId((long) iterate.next());
                pciContext.setPciState(new ChildStatusUpdateState());
                pciContext.stateChange(pciContext);

            }
            childStatusUpdate.clear();

        }
        if (!sdnrNotification.isEmpty()) {
            log.debug("getting notification dmaap_notifications table");
            DmaapNotificationsRepository dmaapNotificationsRepository = BeanUtil
                    .getBean(DmaapNotificationsRepository.class);
            String notification = dmaapNotificationsRepository.getNotificationFromQueue();
            log.debug("notification from sdnr:{}", notification);
            pciContext.setSdnrNotification(notification);
            pciContext.setPciState(new SdnrNotificationHandlingState());
            pciContext.stateChange(pciContext);
        }

    }
}
