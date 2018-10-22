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

import com.wipro.www.pcims.dao.BufferedNotificationsRepository;
import com.wipro.www.pcims.entity.BufferedNotifications;
import com.wipro.www.pcims.utils.BeanUtil;
import java.util.List;

public class BufferNotificationComponent {

    /**
     * Buffers notification along with cluster id in the database.
     */
    public void bufferNotification(String notification, String clusterId) {
        BufferedNotifications bufferedNotifications = new BufferedNotifications();
        bufferedNotifications.setNotification(notification);
        bufferedNotifications.setClusterId(clusterId);
        BufferedNotificationsRepository bufferedNotificationsRepository = BeanUtil
                .getBean(BufferedNotificationsRepository.class);
        bufferedNotificationsRepository.save(bufferedNotifications);

    }

    /**
     * Retrieves buffered notification from the database.
     */
    public List<String> getBufferedNotification(String clusterId) {
        BufferedNotificationsRepository bufferedNotificationsRepository = BeanUtil
                .getBean(BufferedNotificationsRepository.class);
        return bufferedNotificationsRepository.getNotificationsFromQueue(clusterId);

    }

    /**
     * Retrieves clusterid from the database.
     */
    public String getClusterId(String notification) {
        BufferedNotificationsRepository bufferedNotificationsRepository = BeanUtil
                .getBean(BufferedNotificationsRepository.class);
        return bufferedNotificationsRepository.getClusterIdForNotification(notification);
    }

}
