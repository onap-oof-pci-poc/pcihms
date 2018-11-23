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

package com.wipro.www.pcims.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public class RadioAccess {

    @JsonProperty("FAPServiceNumberOfEntries")
    private String fapServiceNumberOfEntries;

    @JsonProperty("FAPServiceList")
    private List<FapServiceList> fapServiceList;

    public RadioAccess() {

    }

    /**
     * Parameterized Constructor.
     */

    public RadioAccess(String fapServiceNumberOfEntries, List<FapServiceList> fapServiceList) {
        super();
        this.fapServiceNumberOfEntries = fapServiceNumberOfEntries;
        this.fapServiceList = fapServiceList;
    }

    public String getFapServiceNumberOfEntries() {
        return fapServiceNumberOfEntries;
    }

    public void setFapServiceNumberOfEntries(String fapServiceNumberOfEntries) {
        this.fapServiceNumberOfEntries = fapServiceNumberOfEntries;
    }

    public List<FapServiceList> getFapServiceList() {
        return fapServiceList;
    }

    public void setFapServiceList(List<FapServiceList> fapServiceList) {
        this.fapServiceList = fapServiceList;
    }

}
