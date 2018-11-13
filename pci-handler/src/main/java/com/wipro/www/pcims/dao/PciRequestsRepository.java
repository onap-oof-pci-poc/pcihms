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

import com.wipro.www.pcims.entity.PciRequests;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public interface PciRequestsRepository extends CrudRepository<PciRequests, String> {

    @Query(nativeQuery = true, value = "SELECT child_thread_id FROM pci_requests WHERE transaction_id = ?1")
    public long getChildThreadMapping(String transactionId);

    @Modifying
    @Query(nativeQuery = true, value = "DELETE FROM pci_requests WHERE child_thread_id = ?1")
    public void deleteByChildThreadId(Long threadId);

}
