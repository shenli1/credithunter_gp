package com.id.cash.bean;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ApiEndPointSetBean implements Serializable {
    private String h5Base;
    private String apiBase;

    public String getH5Base() {
        return h5Base;
    }

    public void setH5Base(String h5Base) {
        this.h5Base = h5Base;
    }

    public String getApiBase() {
        return apiBase;
    }

    public void setApiBase(String apiBase) {
        this.apiBase = apiBase;
    }

    @Override
    public String toString() {
        return "ApiEndPointSetBean{" +
                "h5Base='" + h5Base + '\'' +
                ", apiBase='" + apiBase + '\'' +
                '}';
    }
}
