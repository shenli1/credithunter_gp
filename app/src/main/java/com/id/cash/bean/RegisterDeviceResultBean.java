package com.id.cash.bean;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by linchen on 2018/5/31.
 */

@JsonIgnoreProperties(ignoreUnknown = true)
public class RegisterDeviceResultBean {
    private String udid;

    public String getUdid() {
        return udid;
    }

    public void setUdid(String udid) {
        this.udid = udid;
    }

    @Override
    public String toString() {
        return "RegisterDeviceResultBean{" +
                "udid='" + udid + '\'' +
                '}';
    }
}
