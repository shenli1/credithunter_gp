package com.id.cash.common;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by linchen on 2018/4/29.
 */

public class NetworkUtil {
    public static boolean isNetworkAvailable(final Context context) {
        if (context == null) {
            return false;
        }

        NetworkInfo networkInfo = ((ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isAvailable();
    }
}
