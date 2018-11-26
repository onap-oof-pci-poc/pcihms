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

public class FapService {

    @JsonProperty("alias")
    private String alias;

    @JsonProperty("X0005b9Lte")
    private X0005b9Lte x0005b9Lte;

    @JsonProperty("CellConfig")
    private CellConfig cellConfig;

    public FapService() {

    }

    /**
     * Parameterized constructor.
     */
    public FapService(String alias, X0005b9Lte x0005b9Lte, CellConfig cellConfig) {
        super();
        this.alias = alias;
        this.x0005b9Lte = x0005b9Lte;
        this.cellConfig = cellConfig;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public CellConfig getCellConfig() {
        return cellConfig;
    }

    public void setCellConfig(CellConfig cellConfig) {
        this.cellConfig = cellConfig;
    }

    public X0005b9Lte getX0005b9Lte() {
        return x0005b9Lte;
    }

    public void setX0005b9Lte(X0005b9Lte x0005b9Lte) {
        this.x0005b9Lte = x0005b9Lte;
    }

}
