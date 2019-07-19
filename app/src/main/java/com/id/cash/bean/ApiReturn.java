package com.id.cash.bean;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by linchen on 2018/5/21.
 */

@JsonIgnoreProperties(ignoreUnknown = true)
public class ApiReturn<T> {
    public final static String STATUS_OK = "200";

    private String code;
    private String message;
    private String status;
    private T data;
    private BonusPointTaskResultBean dailyActive;
    @JsonProperty("appVersion")
    private AppVersionBean appVersion;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public BonusPointTaskResultBean getDailyActive() {
        return dailyActive;
    }

    public void setDailyActive(BonusPointTaskResultBean dailyActive) {
        this.dailyActive = dailyActive;
    }

    public AppVersionBean getAppVersion() {
        return appVersion;
    }

    public void setAppVersion(AppVersionBean appVersion) {
        this.appVersion = appVersion;
    }

    @Override
    public String toString() {
        return "ApiReturn{" +
                "code='" + code + '\'' +
                ", message='" + message + '\'' +
                ", status='" + status + '\'' +
                ", data=" + data +
                ", dailyActive=" + dailyActive +
                ", appVersion=" + appVersion +
                '}';
    }
}
