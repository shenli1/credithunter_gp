package com.id.cash.common;

/**
 * Created by linchen on 2018/4/29.
 */

public class StackInfoUtil {
    public static StackTraceElement getStackTraceElement() {
        StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
        int length = stackTraceElements.length;
        if (length >= 5) {
            return stackTraceElements[4];
        } else {
            return stackTraceElements[length - 1];
        }
    }
}
