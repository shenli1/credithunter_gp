package com.id.cash.module.starwin.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.id.cash.common.LogUtil;
import com.id.cash.common.Preference;

/**
 * Created by linchen on 2018/5/2.
 */

public class InstallReferrerReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle extras = intent.getExtras();
        if (extras != null) {
            String referrer = extras.getString("referrer");
            LogUtil.d("install referrer: " + referrer);

            // install referrer is saved in shared prefs, because RN engine may not be started yet
            // the install referrer is exported as RN constants & js side handles the post
            Preference.getInstance().setInstallReferrer(referrer);
        }
    }
}
