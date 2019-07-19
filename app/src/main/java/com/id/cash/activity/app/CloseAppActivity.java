package com.id.cash.activity.app;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Process;
import android.support.annotation.Nullable;

/**
 * Created by linchen on 2018/6/6.
 */

// based on the following SO:
// https://stackoverflow.com/questions/2092951/how-to-close-android-application
public class CloseAppActivity extends Activity {
    public static void actionStart(Activity activity) {
        Intent intent = new Intent(activity, CloseAppActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addCategory(Intent.CATEGORY_HOME);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        Process.killProcess(Process.myPid());
        System.exit(0);
    }
}
