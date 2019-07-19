package com.id.cash.bean;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by linchen on 2018/5/29.
 */

@JsonIgnoreProperties(ignoreUnknown = true)
public class InviteReferralBean {
    private String mobile;
    private int point;

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public int getPoint() {
        return point;
    }

    public void setPoint(int point) {
        this.point = point;
    }

    @Override
    public String toString() {
        return "InviteReferralBean{" +
                "mobile='" + mobile + '\'' +
                ", point=" + point +
                '}';
    }
}
