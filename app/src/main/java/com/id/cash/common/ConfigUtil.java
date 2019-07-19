package com.id.cash.common;

import android.content.pm.ApplicationInfo;

import com.id.cash.MainApplication;

/**
 * Created by linchen on 2018/5/21.
 */

public class ConfigUtil {
    public static boolean isApkInDebug() {
        try {
            ApplicationInfo info = MainApplication.getContext().getApplicationInfo();
            return (info.flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
