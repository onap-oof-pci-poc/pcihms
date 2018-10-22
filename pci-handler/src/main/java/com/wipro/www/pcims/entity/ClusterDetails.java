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
@Table(name = "CLUSTER_DETAILS")
public class ClusterDetails {

    @Id
    @Column(name = "CLUSTER_ID")
    private String clusterId;

    @Column(name = "CLUSTER_INFO")
    private String clusterInfo;

    @Column(name = "CHILD_THREAD_ID")
    private long childThreadId;

    public ClusterDetails() {

    }

    /**
     * Parameterised constructor.
     */
    public ClusterDetails(String clusterId, String clusterInfo, long childThreadId) {
        super();
        this.clusterId = clusterId;
        this.clusterInfo = clusterInfo;
        this.childThreadId = childThreadId;
    }

    public long getChildThreadId() {
        return childThreadId;
    }

    public void setChildThreadId(long childThreadId) {
        this.childThreadId = childThreadId;
    }

    public String getClusterId() {
        return clusterId;
    }

    public void setClusterId(String clusterId) {
        this.clusterId = clusterId;
    }

    public String getClusterInfo() {
        return clusterInfo;
    }

    public void setClusterInfo(String clusterInfo) {
        this.clusterInfo = clusterInfo;
    }

}
