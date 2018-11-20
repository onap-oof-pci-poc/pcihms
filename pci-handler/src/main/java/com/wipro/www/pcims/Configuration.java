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

package com.wipro.www.pcims;

import java.util.List;

public class Configuration {

    private static Configuration instance = null;
    private List<Topic> topics;
    private String sdnrTopic;
    private String policyTopic;
    private List<String> servers;
    private String managerApiKey;
    private String managerSecretKey;
    private String pcimsApiKey;
    private String pcimsSecretKey;
    private String cg;
    private String cid;
    private int pollingInterval;
    private int pollingTimeout;
    private int minCollision;
    private int minConfusion;
    private String sdnrService;
    private String policyService;
    private String oofService;
    private String sourceId;
    private String policyName;
    private String configName;
    private String callbackUrl;
    private List<String> optimizers;
    private int numSolutions;
    private int bufferTime;
    private int maximumClusters;

    public int getMaximumClusters() {
        return maximumClusters;
    }

    public void setMaximumClusters(int maximumClusters) {
        this.maximumClusters = maximumClusters;
    }

    protected Configuration() {

    }

    /**
     * Get instance of class.
     */
    public static Configuration getInstance() {
        if (instance == null) {
            instance = new Configuration();
        }
        return instance;
    }

    public List<String> getServers() {
        return servers;
    }

    public void setServers(List<String> servers) {
        this.servers = servers;
    }

    public String getManagerApiKey() {
        return managerApiKey;
    }

    public void setManagerApiKey(String managerApiKey) {
        this.managerApiKey = managerApiKey;
    }

    public String getManagerSecretKey() {
        return managerSecretKey;
    }

    public void setManagerSecretKey(String managerSecretKey) {
        this.managerSecretKey = managerSecretKey;
    }

    public String getPcimsApiKey() {
        return pcimsApiKey;
    }

    public void setPcimsApiKey(String pcimsApiKey) {
        this.pcimsApiKey = pcimsApiKey;
    }

    public String getPcimsSecretKey() {
        return pcimsSecretKey;
    }

    public void setPcimsSecretKey(String pcimsSecretKey) {
        this.pcimsSecretKey = pcimsSecretKey;
    }

    public String getCg() {
        return cg;
    }

    public void setCg(String cg) {
        this.cg = cg;
    }

    public String getCid() {
        return cid;
    }

    public void setCid(String cid) {
        this.cid = cid;
    }

    public int getPollingInterval() {
        return pollingInterval;
    }

    public void setPollingInterval(int pollingInterval) {
        this.pollingInterval = pollingInterval;
    }

    public int getPollingTimeout() {
        return pollingTimeout;
    }

    public void setPollingTimeout(int pollingTimeout) {
        this.pollingTimeout = pollingTimeout;
    }

    public int getMinCollision() {
        return minCollision;
    }

    public void setMinCollision(int minCollision) {
        this.minCollision = minCollision;
    }

    public int getMinConfusion() {
        return minConfusion;
    }

    public void setMinConfusion(int minConfusion) {
        this.minConfusion = minConfusion;
    }

    public String getSdnrService() {
        return sdnrService;
    }

    public void setSdnrService(String sdnrService) {
        this.sdnrService = sdnrService;
    }

    public String getPolicyService() {
        return policyService;
    }

    public void setPolicyService(String policyService) {
        this.policyService = policyService;
    }

    public String getOofService() {
        return oofService;
    }

    public void setOofService(String oofService) {
        this.oofService = oofService;
    }

    public String getSourceId() {
        return sourceId;
    }

    public void setSourceId(String sourceId) {
        this.sourceId = sourceId;
    }

    public String getPolicyName() {
        return policyName;
    }

    public void setPolicyName(String policyName) {
        this.policyName = policyName;
    }

    public String getConfigName() {
        return configName;
    }

    public void setConfigName(String configName) {
        this.configName = configName;
    }

    public String getCallbackUrl() {
        return callbackUrl;
    }

    public void setCallbackUrl(String callbackUrl) {
        this.callbackUrl = callbackUrl;
    }

    public List<String> getOptimizers() {
        return optimizers;
    }

    public void setOptimizers(List<String> optimizers) {
        this.optimizers = optimizers;
    }

    public int getNumSolutions() {
        return numSolutions;
    }

    public void setNumSolutions(int numSolutions) {
        this.numSolutions = numSolutions;
    }

    public int getBufferTime() {
        return bufferTime;
    }

    public void setBufferTime(int bufferTime) {
        this.bufferTime = bufferTime;
    }

    public List<Topic> getTopics() {
        return topics;
    }

    public void setTopics(List<Topic> topics) {
        this.topics = topics;
    }

    public String getSdnrTopic() {
        return sdnrTopic;
    }

    public void setSdnrTopic(String sdnrTopic) {
        this.sdnrTopic = sdnrTopic;
    }

    public String getPolicyTopic() {
        return policyTopic;
    }

    public void setPolicyTopic(String policyTopic) {
        this.policyTopic = policyTopic;
    }

    @Override
    public String toString() {
        return "Configuration [topics=" + topics + ", sdnrTopic=" + sdnrTopic + ", policyTopic=" + policyTopic
                + ", servers=" + servers + ", managerApiKey=" + managerApiKey + ", managerSecretKey=" + managerSecretKey
                + ", pcimsApiKey=" + pcimsApiKey + ", pcimsSecretKey=" + pcimsSecretKey + ", cg=" + cg + ", cid=" + cid
                + ", pollingInterval=" + pollingInterval + ", pollingTimeout=" + pollingTimeout + ", minCollision="
                + minCollision + ", minConfusion=" + minConfusion + ", sdnrService=" + sdnrService + ", policyService="
                + policyService + ", oofService=" + oofService + ", sourceId=" + sourceId + ", policyName=" + policyName
                + ", configName=" + configName + ", callbackUrl=" + callbackUrl + ", optimizers=" + optimizers
                + ", numSolutions=" + numSolutions + ", bufferTime=" + bufferTime + ", maximumClusters="
                + maximumClusters + "]";
    }

}
