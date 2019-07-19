package com.id.cash.bean;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by linchen on 2018/6/5.
 */

// when user receives the bonus point
@JsonIgnoreProperties(ignoreUnknown = true)
public class BonusPointTaskResultBean {
    private String userId;
    private String taskTempletId;
    private String deviceId;
    private int bonusPoint;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getTaskTempletId() {
        return taskTempletId;
    }

    public void setTaskTempletId(String taskTempletId) {
        this.taskTempletId = taskTempletId;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public int getBonusPoint() {
        return bonusPoint;
    }

    public void setBonusPoint(int bonusPoint) {
        this.bonusPoint = bonusPoint;
    }

    @Override
    public String toString() {
        return "BonusPointTaskResultBean{" +
                "userId=" + userId +
                ", taskTempletId=" + taskTempletId +
                ", deviceId=" + deviceId +
                ", bonusPoint=" + bonusPoint +
                '}';
    }
}
