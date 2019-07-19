package com.id.cash.bean;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by linchen on 2018/6/2.
 */

@JsonIgnoreProperties(ignoreUnknown = true)
public class BannerBean {
    private String type;
    private String location;
    private String link;
    private String imgUrl;

    public interface BannerType {
        String PRODUCT = "PRODUCT";
    }

    public interface BannerLocation {
        String BANNER = "BANNER";
        String SPLASH = "SPLASH";
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    @Override
    public String toString() {
        return "BannerBean{" +
                "type='" + type + '\'' +
                ", location='" + location + '\'' +
                ", link='" + link + '\'' +
                ", imgUrl='" + imgUrl + '\'' +
                '}';
    }
}
