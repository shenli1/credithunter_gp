package com.id.cash.bean;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;

/**
 * Created by linchen on 2018/6/1.
 */

@JsonIgnoreProperties(ignoreUnknown = true)
public class UserBean implements Serializable {
    private String id;
    private String mobile;
    private long totalPoint;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public long getTotalPoint() {
        return totalPoint;
    }

    public void setTotalPoint(long totalPoint) {
        this.totalPoint = totalPoint;
    }

    @Override
    public String toString() {
        return "UserBean{" +
                "id='" + id + '\'' +
                ", mobile='" + mobile + '\'' +
                ", totalPoint=" + totalPoint +
                '}';
    }
}
