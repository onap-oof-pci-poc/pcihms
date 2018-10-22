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

package com.wipro.www.pcims.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "PCI_REQUESTS")
public class PciRequests {

    @Id
    @Column(name = "TRANSACTION_ID")
    private String transactionId;

    @Column(name = "CHILD_THREAD_ID")
    private long childThreadId;

    public PciRequests() {

    }

    /**
     * Parameterised constructor.
     */
    public PciRequests(String transactionId, long childThreadId) {
        super();
        this.transactionId = transactionId;
        this.childThreadId = childThreadId;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public long getChildThreadId() {
        return childThreadId;
    }

    public void setChildThreadId(long childThreadId) {
        this.childThreadId = childThreadId;
    }

}
