package com.id.cash.common;

import android.app.Activity;

//import com.google.firebase.analytics.FirebaseAnalytics;

import com.id.cash.MainApplication;

/**
 * Created by linchen on 2018/5/23.
 */

public class TrackUtil {
    public static void logEvent(String event) {
        event = event.replaceAll("\\s+","");

        LogUtil.v("tracking: " + event);
//        FirebaseAnalytics.getInstance(MainApplication.getContext()).logEvent(event, null);
    }

    public static void setCurrentScreen(Activity activity, String screenName) {
//        FirebaseAnalytics.getInstance(MainApplication.getContext()).setCurrentScreen(activity, screenName, null);
    }
}
