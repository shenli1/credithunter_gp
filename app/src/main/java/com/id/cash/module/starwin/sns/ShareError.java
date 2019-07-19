package com.id.cash.module.starwin.sns;

/**
 * Created by linchen on 2018/5/12.
 */

public class ShareError {
    private String message;

    public String getMessage() {
        return message;
    }

    public ShareError setMessage(String message) {
        this.message = message;
        return this;
    }

    @Override
    public String toString() {
        return "ShareError{" +
                "message='" + message + '\'' +
                '}';
    }
}
