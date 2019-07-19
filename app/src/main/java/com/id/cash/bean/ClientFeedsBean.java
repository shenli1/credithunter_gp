package com.id.cash.bean;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by linchen on 2018/7/20.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ClientFeedsBean {
    private String clientId;
    private String feedsId;
    private boolean liked;

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getFeedsId() {
        return feedsId;
    }

    public void setFeedsId(String feedsId) {
        this.feedsId = feedsId;
    }

    public boolean isLiked() {
        return liked;
    }

    public void setLiked(boolean liked) {
        this.liked = liked;
    }

    @Override
    public String toString() {
        return "ClientFeedsBean{" +
                "clientId='" + clientId + '\'' +
                ", feedsId='" + feedsId + '\'' +
                ", liked=" + liked +
                '}';
    }
}
