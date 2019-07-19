package com.id.cash.common;

import android.app.Activity;

import java.util.Stack;

/**
 * Created by linchen on 2018/5/16.
 */

public class ActivityManager {
    private static Stack<Activity> activityStack;
    private static ActivityManager instance;
    private static Activity currentTopActivity;

    public static ActivityManager getInstance() {
        if (instance == null) {
            instance = new ActivityManager();
        }
        return instance;
    }

    public void setTopActivity(Activity activity) {
        currentTopActivity = activity;
    }

//    public void push(final Activity activity) {
//        if (activityStack == null) {
//            activityStack = new Stack<Activity>();
//        }
//        activityStack.add(activity);
//    }

//    public void finish(final Activity activity) {
//        if (activity != null) {
//            activityStack.remove(activity);
//
//            if (!activity.isFinishing()) {
//                activity.finish();
//            }
//        }
//    }

    public Activity getTopActivity() {
        return currentTopActivity;
    }

//    public void killAll() {
//        if (activityStack != null) {
//            for (int i = 0; i < activityStack.size(); i++) {
//                if (activityStack.get(i) != null) {
//
//                    if (!activityStack.get(i).isFinishing()) {
//                        activityStack.get(i).finish();
//                    }
//                }
//            }
//            activityStack.clear();
//        }
//    }
}
