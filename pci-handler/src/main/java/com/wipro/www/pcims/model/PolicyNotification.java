package com.wipro.www.pcims.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.UUID;

public class PolicyNotification {

    private String closedLoopControlName;
    private long closedLoopAlarmStart;
    private String closedLoopEventClient;
    private String closedLoopEventStatus;
    private String requestId;

    @JsonProperty("AAI")
    private Aai aai;

    private String from;
    private String version;

    @JsonProperty("Action")
    private String action;
    private String payload;

    /**
     * constructor.
     */
    public PolicyNotification() {
        this.requestId = UUID.randomUUID().toString();
        this.closedLoopEventClient = "microservice.PCI";
        this.closedLoopEventStatus = "ONSET";
        this.closedLoopAlarmStart = System.currentTimeMillis();
        this.from = "PCIMS";
        this.version = "1.0.2";
        this.action = "ModifyConfig";

    }

    public long getClosedLoopAlarmStart() {
        return closedLoopAlarmStart;
    }

    public void setClosedLoopAlarmStart(long closedLoopAlarmStart) {
        this.closedLoopAlarmStart = closedLoopAlarmStart;
    }

    public String getClosedLoopEventClient() {
        return closedLoopEventClient;
    }

    public void setClosedLoopEventClient(String closedLoopEventClient) {
        this.closedLoopEventClient = closedLoopEventClient;
    }

    public String getClosedLoopEventStatus() {
        return closedLoopEventStatus;
    }

    public void setClosedLoopEventStatus(String closedLoopEventStatus) {
        this.closedLoopEventStatus = closedLoopEventStatus;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getClosedLoopControlName() {
        return closedLoopControlName;
    }

    public void setClosedLoopControlName(String closedLoopControlName) {
        this.closedLoopControlName = closedLoopControlName;
    }

    public Aai getAai() {
        return aai;
    }

    public void setAai(Aai aai) {
        this.aai = aai;
    }

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }

    @Override
    public String toString() {
        return "PolicyNotification [closedLoopControlName=" + closedLoopControlName + ", closedLoopAlarmStart="
                + closedLoopAlarmStart + ", closedLoopEventClient=" + closedLoopEventClient + ", closedLoopEventStatus="
                + closedLoopEventStatus + ", requestId=" + requestId + ", aai=" + aai + ", from=" + from + ", version="
                + version + ", action=" + action + ", payload=" + payload + "]";
    }

}
