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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wipro.www.pcims.dmaap.DmaapClient;
import com.wipro.www.pcims.restclient.PolicyRestClient;
import com.wipro.www.pcims.utils.FileIo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Application {

    @Autowired
    DmaapClient dmaapClient;

    @Autowired
    MainThreadComponent mainThreadComponent;

    private static Logger log = LoggerFactory.getLogger(Application.class);

    /**
     * Main method where the pci context is initially set.
     */
    public static void main(String[] args) {
        SpringApplication.run(Application.class);

    }

    /**
     * initialization.
     */
    @PostConstruct
    void init() {
        getConfig();
        fetchIntialConfigFromPolicy();
        NewNotification newNotification = new NewNotification(false);
        dmaapClient.initClient(newNotification);
        mainThreadComponent.init(newNotification);
    }

    /**
     * Gets configuration from policy.
     */
    @SuppressWarnings("unchecked")
    private void fetchIntialConfigFromPolicy() {
        log.debug("fetch initial config from policy");
        String configPolicyResponseJson = PolicyRestClient.fetchConfigFromPolicy();
        if (configPolicyResponseJson.equals("Post failed")) {
            log.debug("cannot fetch config from policy");
            return;
        }
        ObjectMapper mapper = new ObjectMapper();
        List<HashMap<String, Object>> configPolicyResponse = new ArrayList<>();
        try {
            configPolicyResponse = mapper.readValue(configPolicyResponseJson, List.class);
        } catch (IOException e) {
            log.debug("exception during parsing response from policy", e);
        }
        String configPolicyJson = null;
        if (configPolicyResponse != null) {
            configPolicyJson = (String) configPolicyResponse.get(0).get("config");
        } else {
            return;
        }
        Map<String, Object> configPolicyMap = new HashMap<>();
        try {
            configPolicyMap = mapper.readValue(configPolicyJson, HashMap.class);
        } catch (IOException e) {
            log.debug("exception during parsing config body from policy", e);
        }
        ConfigPolicy configPolicy = ConfigPolicy.getInstance();
        configPolicy.setConfig(configPolicyMap);
        if (log.isDebugEnabled()) {
            log.debug(configPolicy.toString());
        }
    }

    /**
     * Gets config from config.json.
     *
     */
    private void getConfig() {
        log.debug("getting initial config");
        String configJson = FileIo.readFromFile("/etc/config.json");
        ObjectMapper mapper = new ObjectMapper();
        Configuration configuration = Configuration.getInstance();
        try {
            mapper.readerForUpdating(configuration).readValue(configJson);
            if (log.isDebugEnabled()) {
                log.debug(configuration.toString());
            }
        } catch (IOException e) {
            log.debug("exception during parsing configuration", e);
        }
    }
}
