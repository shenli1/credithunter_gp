package com.id.cash.common;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.util.Log;

import java.io.PrintWriter;
import java.io.StringWriter;

import com.id.cash.BuildConfig;

/**
 * Created by linchen on 2018/4/29.
 */

public class LogUtil {
    private static final int VERBOSE = 0x20;
    private static final int DEBUG = 0x30;
    private static final int INFO = 0x40;
    private static final int WARN = 0x50;
    private static final int ERROR = 0x60;

    private static int currentLogLevel = VERBOSE;
    private static boolean shouldLog = true;

    static void init(Context context) {
        shouldLog = isDebugMode(context);
    }

    public static boolean isDebugMode(Context context) {
        return BuildConfig.DEBUG || (context.getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0;
    }

    public static void d(String messsage) {
        if (shouldLog && currentLogLevel <= DEBUG) {
            StackTraceElement stackTraceElement = StackInfoUtil.getStackTraceElement();
            String tag = getTag(stackTraceElement);
            Log.d(tag, messsage);
        }
    }

    public static void i(String messsage) {
        if (shouldLog && currentLogLevel <= INFO) {
            StackTraceElement stackTraceElement = StackInfoUtil.getStackTraceElement();
            String tag = getTag(stackTraceElement);
            Log.i(tag, messsage);
        }
    }

    public static void e(String messsage) {
        if (shouldLog && currentLogLevel <= ERROR) {
            StackTraceElement stackTraceElement = StackInfoUtil.getStackTraceElement();
            String tag = getTag(stackTraceElement);
            Log.e(tag, messsage);
        }
    }

    public static void e(Throwable ex) {
        if (shouldLog && currentLogLevel <= ERROR) {
            StackTraceElement stackTraceElement = StackInfoUtil.getStackTraceElement();
            String tag = getTag(stackTraceElement);
            Log.e(tag, Log.getStackTraceString(ex));
        }
    }

    public static void v(String messsage) {
        if (shouldLog && currentLogLevel <= VERBOSE) {
            StackTraceElement stackTraceElement = StackInfoUtil.getStackTraceElement();
            String tag = getTag(stackTraceElement);
            Log.v(tag, messsage);
        }
    }

    public static void w(String messsage) {
        if (shouldLog && currentLogLevel <= WARN) {
            StackTraceElement stackTraceElement = StackInfoUtil.getStackTraceElement();
            String tag = getTag(stackTraceElement);
            Log.w(tag, messsage);
        }
    }

    private static String getTag(StackTraceElement stackTraceElement) {
        String className = stackTraceElement.getClassName();
        className = className.substring(className.lastIndexOf(".") + 1);
        return String.format("%s.%s(L:%d)", className, stackTraceElement.getMethodName(),
                stackTraceElement.getLineNumber());
    }

}
