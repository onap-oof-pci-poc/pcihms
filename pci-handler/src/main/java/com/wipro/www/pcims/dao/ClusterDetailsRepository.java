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

import com.wipro.www.pcims.entity.ClusterDetails;
import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClusterDetailsRepository extends CrudRepository<ClusterDetails, String> {

    @Query(nativeQuery = true, value = "UPDATE CLUSTER_DETAILS SET clusterInfo=?1  WHERE clusterId = ?2")
    public void updateCluster(String clusterInfo, String clusterId);

    @Query(nativeQuery = true, value = "SELECT * FROM cluster_details")
    public List<ClusterDetails> getAllClusterDetails();

    @Query(nativeQuery = true, value = "SELECT child_thread_id FROM cluster_details WHERE cluster_id = ?1")
    public long getChildThreadForCluster(String clusterId);

    @Query(nativeQuery = true, value = "SELECT cluster_id FROM cluster_details WHERE child_thread_id = ?1")
    public String getClusterIdForChildThread(long childThreadId);

}
