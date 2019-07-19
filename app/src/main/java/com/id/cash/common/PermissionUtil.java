package com.id.cash.common;

import android.app.Activity;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

public class PermissionUtil {
    public static boolean checkAndRequestPermission(Activity activity, String permission, int requestCode) {
        if (ContextCompat.checkSelfPermission(activity, permission) == 0) {
            return true;
        }
        ActivityCompat.requestPermissions(activity, new String[]{permission}, requestCode);
        return false;
    }
}
