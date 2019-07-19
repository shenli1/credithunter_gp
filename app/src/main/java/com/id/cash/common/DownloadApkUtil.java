package com.id.cash.common;

import android.app.DownloadManager;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatTextView;
import android.text.TextUtils;
import android.util.Log;

import java.io.File;

import com.id.cash.R;

import static android.os.Environment.MEDIA_UNMOUNTED;

/**
 * Created by linchen on 2018/4/30.
 */

public class DownloadApkUtil {
    private static final String DOWNLOAD_MANAGER_PACKAGE_NAME = "com.android.provider.downloads";

    /*
    if download manager is enabled, use download manager to download
    if download manager is disabled, use browser to download
     */
    // TOOD: check if this is possible: http://www.cnblogs.com/liyiran/p/6393813.html
    public static void downloadApk(final Context context, String url, String name, String title) {
        if (TextUtils.isEmpty(url)) {
            return;
        }

        LogUtil.i("downloading: " + url);
        if (resolveDownloadManager(context)) {
            try {
                DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
                DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
                request.setVisibleInDownloadsUi(true);
                request.setTitle(title);

                if (Environment.getExternalStorageState().equals(MEDIA_UNMOUNTED)) {
                    String apkPath = Environment.getExternalStorageDirectory().getAbsolutePath() +
                            File.separator + name;
                    new File(apkPath).delete();
                    request.setDestinationUri(Uri.fromFile(new File(apkPath)));
                    downloadManager.enqueue(request);
                } else {
                    LogUtil.w("No external storage");
                }
            } catch (Exception ex) {
                downloadByBrowser(context, url);
            }
        } else {
            downloadByBrowser(context, url);
        }
    }

    private static void downloadByBrowser(Context context, String url) {
        Intent intent = new Intent("android.intent.action.VIEW", Uri.parse(url));
        intent.addFlags(Intent.FLAG_RECEIVER_FOREGROUND);
        context.startActivity(intent);
    }

    private static boolean resolveDownloadManager(Context context) {
        try {
            int state = context.getPackageManager()
                    .getApplicationEnabledSetting(DOWNLOAD_MANAGER_PACKAGE_NAME);

            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN_MR2) {
                return !(state == PackageManager.COMPONENT_ENABLED_STATE_DISABLED ||
                        state == PackageManager.COMPONENT_ENABLED_STATE_DISABLED_USER
                        || state == PackageManager.COMPONENT_ENABLED_STATE_DISABLED_UNTIL_USED);
            } else {
                return !(state == PackageManager.COMPONENT_ENABLED_STATE_DISABLED ||
                        state == PackageManager.COMPONENT_ENABLED_STATE_DISABLED_USER);
            }
        } catch (Exception ex) {
            LogUtil.w(ex.toString());
            return false;
        }
    }

//    private static AlertDialog createDialog(final Context context) {
//        return new AlertDialog.Builder(context)
//                .setMessage(R.string.upgrade_dialog_enable_download_manager)
//                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        enableDownloadManager(context);
//                    }
//                })
//                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        dialog.dismiss();
//                    }
//                })
//                .setCancelable(true)
//                .create();
//    }

    /**
     * Start activity to Settings to enable DownloadManager.
     */
    private static void enableDownloadManager(Context context) {
        try {
            //Open the specific App Info page:
            Intent intent = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            intent.setData(Uri.parse("package:" + DOWNLOAD_MANAGER_PACKAGE_NAME));
            context.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();

            //Open the generic Apps page:
            Intent intent = new Intent(android.provider.Settings.ACTION_MANAGE_APPLICATIONS_SETTINGS);
            context.startActivity(intent);
        }
    }
}
