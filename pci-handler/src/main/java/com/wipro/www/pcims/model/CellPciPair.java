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

public class CellPciPair {
    @JsonProperty("cellId")
    private String cellId;

    @JsonProperty("physicalCellId")
    private int physicalCellId;

    public CellPciPair() {

    }

    /**
     * Parameterized constructor.
     */
    public CellPciPair(String cellId, int physicalCellId) {
        super();
        this.cellId = cellId;
        this.physicalCellId = physicalCellId;
    }

    public String getCellId() {
        return cellId;
    }

    public void setCellId(String cellId) {
        this.cellId = cellId;
    }

    public int getPhysicalCellId() {
        return physicalCellId;
    }

    public void setPhysicalCellId(int physicalCellId) {
        this.physicalCellId = physicalCellId;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = (prime * result) + ((cellId == null) ? 0 : cellId.hashCode());
        result = (prime * result) + physicalCellId;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;

        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        CellPciPair other = (CellPciPair) obj;
        if (cellId == null) {
            if (other.cellId != null) {
                return false;
            }
        } else if (!cellId.equals(other.cellId)) {
            return false;
        }
        if (physicalCellId != other.physicalCellId) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "CellPciPair [cellId=" + cellId + ", physicalCellId=" + physicalCellId + "]";
    }

}
