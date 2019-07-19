package com.id.cash.common;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.text.TextUtils;

/**
 * Created by linchen on 2018/4/29.
 */

public class GooglePlayUtil {
    public static void openGooglePlay(Context context, String str) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        Uri parse = Uri.parse(str);
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Start market url: ");
        stringBuilder.append(parse);
        LogUtil.i(stringBuilder.toString());
        intent.setData(parse);
        if (isLaunchable(context, "com.android.vending")) {
            intent.setPackage("com.android.vending");
        }
        context.startActivity(intent);
    }

    public static String getMarketUrl(Context context, String queryString) {
        String protocol;
        StringBuilder marketStringBuilder = new StringBuilder("market://details?");
        marketStringBuilder.append(queryString);
        if (new Intent(Intent.ACTION_VIEW).setData(
                Uri.parse(marketStringBuilder.toString())).resolveActivity(context.getApplicationContext().getPackageManager()) != null) {
            protocol = "market://details?";
        } else {
            protocol = "https://play.google.com/store/apps/details?";
        }
        StringBuilder urlStringBuilder = new StringBuilder(protocol);
        urlStringBuilder.append(queryString);
        return urlStringBuilder.toString();
    }

    public static boolean isLaunchable(Context context, String packageName) {
        if (packageName == null || TextUtils.isEmpty(packageName)) {
            return false;
        }

        try {
            PackageManager pm = context.getPackageManager();
            return pm.getLaunchIntentForPackage(packageName) != null;
        } catch (Exception ex) {
            LogUtil.e(ex);
            return false;
        }
    }
}
