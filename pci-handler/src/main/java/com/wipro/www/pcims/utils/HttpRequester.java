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

package com.wipro.www.pcims.utils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpRequester {

    private static final String ACCEPT = "Accept";
    private static final String JSON = "application/json";
    private static final String CONTENT = "Content-Type";
    private static final String UTF = "UTF-8";
    private static final String FAILMSG = "Post failed";
    private static final String AUTH = "Authorization";
    private static Logger log = LoggerFactory.getLogger(HttpRequester.class);

    public static class MyHostnameVerifier implements HostnameVerifier {

        @Override
        public boolean verify(String hostname, SSLSession session) {
            // verification of hostname is switched off
            log.debug("Hostname: {}", hostname);
            return true;
        }
    }

    /**
     * Send Post Request.
     */
    public static String sendPostRequest(String requestUrl, String requestBody) {
        String response = "";

        try {
            URL url = new URL(requestUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setRequestMethod("POST");
            connection.setRequestProperty(ACCEPT, JSON);
            connection.setRequestProperty(CONTENT, JSON);
            OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream(), UTF);
            writer.write(requestBody);
            writer.close();
            BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String temp;
            int responseCode = connection.getResponseCode();
            log.debug("response code: {}", responseCode);
            response = br.readLine();
            while ((temp = br.readLine()) != null) {
                response = response.concat(temp);
            }
            br.close();
            connection.disconnect();

            if (response == null) {
                response = String.valueOf(responseCode);
            }

        } catch (Exception e) {
            response = FAILMSG;
        }

        return response;
    }

    /**
     * Send Post Request to policy.
     */
    public static String sendPostToPolicy(String requestUrl, String requestBody) {
        String response = "";
        log.debug("inside post to policy");

        try {
            URL url = new URL(requestUrl);

            HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
            connection.setHostnameVerifier(new MyHostnameVerifier());
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setRequestMethod("POST");
            connection.setRequestProperty(ACCEPT, JSON);
            connection.setRequestProperty(CONTENT, JSON);
            connection.setRequestProperty("ClientAuth", "cHl0aG9uOnRlc3Q=");
            connection.setRequestProperty("Environment", "TEST");
            connection.setRequestProperty(AUTH, "Basic dGVzdHBkcDphbHBoYTEyMw==");
            OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream(), UTF);
            writer.write(requestBody);
            writer.close();
            BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String temp;
            int responseCode = connection.getResponseCode();
            log.debug("response code: {}", responseCode);
            response = br.readLine();
            while ((temp = br.readLine()) != null) {
                response = response.concat(temp);
            }
            br.close();
            connection.disconnect();

        } catch (Exception e) {
            log.debug("Exception during post to policy: {}", e);
            response = FAILMSG;
        }

        return response;
    }

    /**
     * Send Get Request.
     */
    public static String sendGetRequest(String requestUrl) {
        String response = "";
        int returnCode = 0;

        try {
            URL url = new URL(requestUrl);
            HttpURLConnection connection = null;
            connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.setRequestMethod("GET");
            connection.setRequestProperty(ACCEPT, JSON);
            returnCode = connection.getResponseCode();
            InputStream connectionIn = null;
            if (returnCode == 200) {
                connectionIn = connection.getInputStream();
                BufferedReader buffer = new BufferedReader(new InputStreamReader(connectionIn));
                String inputLine;
                while ((inputLine = buffer.readLine()) != null) {
                    response = response.concat(inputLine);
                }
                buffer.close();
            }

            else {
                response = "";
                log.debug("return code: {}", returnCode);
            }
        } catch (Exception e) {
            log.debug("Get failed,Exception : {}", e);
            response = "";
        }
        return response;

    }

    /**
     * Send Get Request to SDNR.
     */
    public String sendGetRequest(String requestUrl, String requestBody) {
        String response;
        try {
            URL url = new URL(requestUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setRequestMethod("POST");
            connection.setRequestProperty(ACCEPT, "text/plain");
            connection.setRequestProperty(CONTENT, JSON);
            connection.setRequestProperty(AUTH, "Basic SW5mcmFQb3J0YWxDbGllbnQ6cGFzc3dvcmQxJA==");
            OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream(), UTF);
            writer.write(requestBody);
            writer.close();
            BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String temp;
            response = br.readLine();
            while ((temp = br.readLine()) != null) {
                response = response.concat(temp);
            }
            br.close();
            connection.disconnect();

        } catch (Exception e) {
            response = FAILMSG;
        }

        return response;
    }

    /**
     * Send Post Request.
     */
    public String sendPostRequest1(String requestUrl, String requestBody) {
        String response;
        try {
            URL url = new URL(requestUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setRequestMethod("POST");
            connection.setRequestProperty(ACCEPT, JSON);
            connection.setRequestProperty(CONTENT, JSON);
            connection.setRequestProperty(AUTH, "Basic SW5mcmFQb3J0YWxDbGllbnQ6cGFzc3dvcmQxJA==");
            connection.setRequestProperty("Content-Length", "" + requestBody.length());
            OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream(), UTF);
            writer.write(requestBody);
            writer.close();
            BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String temp;
            response = br.readLine();
            while ((temp = br.readLine()) != null) {
                response = response.concat(temp);
            }
            br.close();
            connection.disconnect();

        } catch (Exception e) {
            response = FAILMSG;
        }

        return response;
    }

}
