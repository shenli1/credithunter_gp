package com.id.cash.activity.other;

import android.app.Activity;

import com.id.cash.R;
import com.id.cash.activity.webview.CommonWebActivity;
import com.id.cash.network.H5Urls;

/**
 * Created by linchen on 2018/5/26.
 */

public class CommonWebActivities {
    public static void showPointQA(Activity activity) {
        CommonWebActivity.actionStart(activity,
                activity.getString(R.string.point_faq),
                H5Urls.getQA());
    }

    public static void showPrivacyPolicy(Activity activity) {
        CommonWebActivity.actionStart(activity,
                activity.getString(R.string.privacy_policy),
                H5Urls.getPrivacyPolicy());
    }

    public static void showServiceAgreement(Activity activity) {
        CommonWebActivity.actionStart(activity,
                activity.getString(R.string.service_agreement),
                H5Urls.getUserPolicy());
    }
}
