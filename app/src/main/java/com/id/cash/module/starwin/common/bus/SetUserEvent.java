package com.id.cash.module.starwin.common.bus;

import com.id.cash.bean.UserBean;

/**
 * Created by linchen on 2018/5/23.
 */

public class SetUserEvent {
    private UserBean userBean;
    private boolean isLogout;

    public UserBean getUserBean() {
        return userBean;
    }

    public void setUserBean(UserBean userBean) {
        this.userBean = userBean;
    }

    public boolean isLogout() {
        return isLogout;
    }

    public SetUserEvent setLogout(boolean logout) {
        isLogout = logout;
        return this;
    }

    @Override
    public String toString() {
        return "SetUserEvent{" +
                "userBean=" + userBean +
                ", isLogout=" + isLogout +
                '}';
    }
}
