package com.id.cash.bean;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by linchen on 2018/6/4.
 */

@JsonIgnoreProperties(ignoreUnknown = true)
public class BonusPointTaskBean {
    private String templetId;
    private boolean status;

    public String getTempletId() {
        return templetId;
    }

    public void setTempletId(String templetId) {
        this.templetId = templetId;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "BonusPointTaskBean{" +
                "templetId='" + templetId + '\'' +
                ", status=" + status +
                '}';
    }
}
