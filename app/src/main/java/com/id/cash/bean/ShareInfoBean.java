package com.id.cash.bean;

import android.support.annotation.StringRes;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by linchen on 2018/5/30.
 */

@JsonIgnoreProperties(ignoreUnknown = true)
public class ShareInfoBean {
    private String id;
    private String title;
    private String img;
    private String link;
    private String text;

    // below 2 fields are manually patched for piggy back the info
    private String method;
    private String channel;
    private String taskTemplateId;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public String getTaskTemplateId() {
        return taskTemplateId;
    }

    public void setTaskTemplateId(String taskTemplateId) {
        this.taskTemplateId = taskTemplateId;
    }

    @Override
    public String toString() {
        return "ShareInfoBean{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", img='" + img + '\'' +
                ", link='" + link + '\'' +
                ", text='" + text + '\'' +
                ", method='" + method + '\'' +
                ", channel='" + channel + '\'' +
                ", taskTemplateId='" + taskTemplateId + '\'' +
                '}';
    }
}
