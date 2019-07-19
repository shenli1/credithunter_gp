package com.id.cash.bean;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Arrays;

@JsonIgnoreProperties(ignoreUnknown = true)
public class GatewayBean {
    private String[] gateways;
    private ApiEndPointSetBean[] endPoints;

    public String[] getGateways() {
        return gateways;
    }

    public void setGateways(String[] gateways) {
        this.gateways = gateways;
    }

    public ApiEndPointSetBean[] getEndPoints() {
        return endPoints;
    }

    public void setEndPoints(ApiEndPointSetBean[] endPoints) {
        this.endPoints = endPoints;
    }

    @Override
    public String toString() {
        return "GatewayBean{" +
                "gateways=" + Arrays.toString(gateways) +
                ", endPoints=" + Arrays.toString(endPoints) +
                '}';
    }
}
