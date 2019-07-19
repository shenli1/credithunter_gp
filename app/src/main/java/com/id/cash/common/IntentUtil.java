package com.id.cash.common;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

/**
 * Created by linchen on 2018/5/12.
 */

public class IntentUtil {
    public static <T> Intent intentWithJsonParameter(Context context, Class<T> classOfT, String parameterName, Object object) {
        Intent intent = new Intent(context, classOfT);
        String json = JsonUtil.stringify(object);
        intent.putExtra(parameterName, json);
        return intent;
    }

    public static <T> T getJsonParameter(Intent intent, String parameterName, Class<T> classOfT) {
        String json = intent.getStringExtra(parameterName);
        return JsonUtil.fromString(json, classOfT);
    }

    public static void startLauncher(Activity activity) {
        Intent intent = new Intent("android.intent.action.MAIN");
        intent.addCategory("android.intent.category.HOME");
        activity.startActivity(intent);
    }
}
