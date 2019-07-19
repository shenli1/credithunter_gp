package com.id.cash.bean;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by linchen on 2018/4/30.
 */

@JsonIgnoreProperties(ignoreUnknown = true)
public class AppVersionBean {
    private String title;
    private String url;
    private int versionCode;
    private String versionName;
    private String content;
    private int updateType;

    public String getTitle() {
        return title;
    }

    public AppVersionBean setTitle(String title) {
        this.title = title;
        return this;
    }

    public String getUrl() {
        return url;
    }

    public AppVersionBean setUrl(String url) {
        this.url = url;
        return this;
    }

    public int getVersionCode() {
        return versionCode;
    }

    public AppVersionBean setVersionCode(int versionCode) {
        this.versionCode = versionCode;
        return this;
    }

    public String getVersionName() {
        return versionName;
    }

    public AppVersionBean setVersionName(String versionName) {
        this.versionName = versionName;
        return this;
    }

    public String getContent() {
        return content;
    }

    public AppVersionBean setContent(String content) {
        this.content = content;
        return this;
    }

    public int getUpdateType() {
        return updateType;
    }

    public AppVersionBean setUpdateType(int updateType) {
        this.updateType = updateType;
        return this;
    }
}
