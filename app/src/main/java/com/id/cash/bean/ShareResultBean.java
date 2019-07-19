package com.id.cash.bean;

import android.support.annotation.StringRes;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;

/**
 * Created by linchen on 2018/5/30.
 */

@JsonIgnoreProperties(ignoreUnknown = true)
public class ShareResultBean implements Serializable {
    private ShareResultType shareResultType;
    private String errorMessage;
    private String channel; // share channel
    private String method; // share method
    private String templateId; // bonuspoint task template id

    public ShareResultType getShareResultType() {
        return shareResultType;
    }

    public void setShareResultType(ShareResultType shareResultType) {
        this.shareResultType = shareResultType;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getTemplateId() {
        return templateId;
    }

    public void setTemplateId(String templateId) {
        this.templateId = templateId;
    }

    @Override
    public String toString() {
        return "ShareResultBean{" +
                "shareResultType=" + shareResultType +
                ", errorMessage='" + errorMessage + '\'' +
                ", channel='" + channel + '\'' +
                ", method='" + method + '\'' +
                ", templateId='" + templateId + '\'' +
                '}';
    }
}
