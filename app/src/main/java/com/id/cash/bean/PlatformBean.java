package com.id.cash.bean;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;


/**
 * Created by linchen on 2018/5/21.
 */

@JsonIgnoreProperties(ignoreUnknown = true)
public class PlatformBean {
    private String name;
    private String webSite;
    private String description;
    private LauncherMode launcherMode;
    private String launcherUrl;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getWebSite() {
        return webSite;
    }

    public void setWebSite(String webSite) {
        this.webSite = webSite;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LauncherMode getLauncherMode() {
        return launcherMode;
    }

    public void setLauncherMode(LauncherMode launcherMode) {
        this.launcherMode = launcherMode;
    }

    public String getLauncherUrl() {
        return launcherUrl;
    }

    public void setLauncherUrl(String launcherUrl) {
        this.launcherUrl = launcherUrl;
    }

    @Override
    public String toString() {
        return "PlatformBean{" +
                "name='" + name + '\'' +
                ", webSite='" + webSite + '\'' +
                ", description='" + description + '\'' +
                ", launcherMode=" + launcherMode +
                ", launcherUrl='" + launcherUrl + '\'' +
                '}';
    }
}
