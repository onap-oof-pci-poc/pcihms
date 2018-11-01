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

package com.wipro.www.pcims.restclient;

import java.util.ArrayList;
import java.util.List;

public class Solution {
    String startTime = null;
    String finishTime = null;
    String networkId = null;
    List<PciSolution> pciSolutions = new ArrayList<>();

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getFinishTime() {
        return finishTime;
    }

    public void setFinishTime(String finishTime) {
        this.finishTime = finishTime;
    }

    public String getNetworkId() {
        return networkId;
    }

    public void setNetworkId(String networkId) {
        this.networkId = networkId;
    }

    public List<PciSolution> getPciSolutions() {
        return pciSolutions;
    }

    /**
     * Sets PciSolutions.
     */
    public void setPciSolutions(List<PciSolution> pciSolutions) {

        this.pciSolutions = pciSolutions;

    }

    @Override
    public String toString() {
        return "Solutions [startTime=" + startTime + ", finishTime=" + finishTime + ", networkId=" + networkId
                + ", pciSolutions=" + pciSolutions + "]";
    }
}
