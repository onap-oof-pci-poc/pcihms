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

public class LteNeighborListInUseLteCell {
    @JsonProperty("pnfName")
    private String pnfName;

    @JsonProperty("enable")
    private String enable;

    @JsonProperty("alias")
    private String alias;

    @JsonProperty("mustInclude")
    private String mustInclude;

    @JsonProperty("plmnid")
    private String plmnId;

    @JsonProperty("cid")
    private String cid;

    @JsonProperty("phyCellId")
    private int phyCellId;

    @JsonProperty("blacklisted")
    private String blacklisted;

    public LteNeighborListInUseLteCell() {

    }

    /**
     * Parameterized Constructor.
     */

    public LteNeighborListInUseLteCell(String pnfName, String enable, String alias, String mustInclude, String plmnId,
            String cid, int phyCellId, String blacklisted) {
        super();
        this.pnfName = pnfName;
        this.enable = enable;
        this.alias = alias;
        this.mustInclude = mustInclude;
        this.plmnId = plmnId;
        this.cid = cid;
        this.phyCellId = phyCellId;
        this.blacklisted = blacklisted;
    }

    public String getMustInclude() {
        return mustInclude;
    }

    public void setMustInclude(String mustInclude) {
        this.mustInclude = mustInclude;
    }

    public int getPhyCellId() {
        return phyCellId;
    }

    public void setPhyCellId(int phyCellId) {
        this.phyCellId = phyCellId;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getEnable() {
        return enable;
    }

    public void setEnable(String enable) {
        this.enable = enable;
    }

    public String getBlacklisted() {
        return blacklisted;
    }

    public void setBlacklisted(String blacklisted) {
        this.blacklisted = blacklisted;
    }

    public String getCid() {
        return cid;
    }

    public void setCid(String cid) {
        this.cid = cid;
    }

    public String getPnfName() {
        return pnfName;
    }

    public void setPnfName(String pnfName) {
        this.pnfName = pnfName;
    }

    public String getPlmnid() {
        return plmnId;
    }

    public void setPlmnid(String plmnId) {
        this.plmnId = plmnId;
    }

}
