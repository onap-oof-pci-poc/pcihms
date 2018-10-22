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

package com.wipro.www.pcims.dao;

import com.wipro.www.pcims.entity.DmaapNotifications;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DmaapNotificationsRepository extends CrudRepository<DmaapNotifications, String> {

    @Query(nativeQuery = true,
            value = "DELETE FROM dmaap_notifications "
                    + "WHERE notification = ( SELECT notification FROM dmaap_notifications ORDER BY "
                    + "created_at FOR UPDATE SKIP LOCKED LIMIT 1 ) RETURNING notification;")

    public String getNotificationFromQueue();

}
