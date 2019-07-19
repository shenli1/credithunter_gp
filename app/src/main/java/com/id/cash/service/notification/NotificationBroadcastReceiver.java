package com.id.cash.service.notification;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.id.cash.activity.app.MainActivity;
import com.id.cash.activity.app.MainTabActivity;
import com.id.cash.activity.product.cashloan.CashLoanDetailActivity;
import com.id.cash.activity.webview.CommonWebActivity;
import com.id.cash.common.TrackUtil;
import com.id.cash.module.starwin.common.BroadcastConstants;

/**
 * Created by linchen on 2018/5/24.
 */

public class NotificationBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (BroadcastConstants.PUSH_NOTIFICATION_RECEIVED.equals(intent.getAction())) {
//            String pushId = intent.getStringExtra("push_id");
//            String pushType = intent.getStringExtra("push_type");
            String actionType = intent.getStringExtra("action_type");
            String productId = intent.getStringExtra("product_id");
            String linkTitle = intent.getStringExtra("link_title");
            String link = intent.getStringExtra("link");

            handleAction(context, actionType, productId, linkTitle, link);
        }
    }

    // actionType is: PushActionType
    private void handleAction(Context context, String actionType, String productId, String linkTitle, String link) {
        TrackUtil.logEvent("push_notification_clicked");
        if (PushActionType.OPEN_PRODUCT.equals(actionType)) {
            MainTabActivity.actionStart(context);
            CashLoanDetailActivity.actionStart(context, productId, true);
            return;
        } else if (PushActionType.OPEN_LINK.equals(actionType)) {
            if (!TextUtils.isEmpty(link)) {
                MainTabActivity.actionStart(context);
                CommonWebActivity.actionStart(context, linkTitle, link);
                return;
            }
        }

        MainTabActivity.actionStart(context);
    }
}
