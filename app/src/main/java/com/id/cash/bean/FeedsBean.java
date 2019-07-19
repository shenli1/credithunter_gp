package com.id.cash.bean;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by linchen on 2018/7/19.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class FeedsBean {
    private String id;
    private String title;
    private String titleImg;
    private String seenNum;
    private String likeNum;
    private String type;
    private String link;

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

    public String getTitleImg() {
        return titleImg;
    }

    public void setTitleImg(String titleImg) {
        this.titleImg = titleImg;
    }

    public String getSeenNum() {
        return seenNum;
    }

    public void setSeenNum(String seenNum) {
        this.seenNum = seenNum;
    }

    public String getLikeNum() {
        return likeNum;
    }

    public void setLikeNum(String likeNum) {
        this.likeNum = likeNum;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    @Override
    public String toString() {
        return "FeedsBean{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", titleImg='" + titleImg + '\'' +
                ", seenNum='" + seenNum + '\'' +
                ", likeNum='" + likeNum + '\'' +
                ", type='" + type + '\'' +
                ", link='" + link + '\'' +
                '}';
    }
}
