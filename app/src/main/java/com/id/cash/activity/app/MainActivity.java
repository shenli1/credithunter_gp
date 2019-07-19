package com.id.cash.activity.app;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

//import com.google.firebase.iid.FirebaseInstanceId;
//import com.google.firebase.messaging.FirebaseMessaging;
import com.id.cash.R;
import com.id.cash.activity.product.cashloan.CashLoanDetailActivity;
import com.id.cash.common.DeviceUtil;
import com.id.cash.common.IntentUtil;
import com.id.cash.common.LogUtil;
import com.id.cash.common.TrackUtil;
import com.id.cash.module.starwin.common.BroadcastConstants;
import com.id.cash.module.starwin.common.base.BaseActivity;
import com.id.cash.network.ApiActions;
import com.id.cash.presenter.AppPresenter;
import com.id.cash.service.notification.NotificationBroadcastReceiver;

import java.util.UUID;

// this is actually the launcher activity
public class MainActivity extends BaseActivity<AppPresenter> {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TrackUtil.logEvent("launcher_page_view");

        handleIntent();
        init();

        initFCM();
        handleFCMNotification();

        LogUtil.i(DeviceUtil.getDevice().toString());

        // the first API call of the application to reduce server side concurrency problem
        // the concurrency problem may generate multiple client with the same unique id
        // this can be any API call (server side register device automatically)
        if(presenter!=null){
            presenter.registerDevice(ApiActions.REGISTER_DEVICE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        postDelayed(() -> {
                    MainTabActivity.actionStart(this);
                    finish();
                },
                1500);
    }

    @Override
    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        handleIntent();
    }

    // handle FCM & app linking
    private void handleIntent() {
        // ATTENTION: This was auto-generated to handle app links.
        Intent appLinkIntent = getIntent();
//        String appLinkAction = appLinkIntent.getAction();
        Uri appLinkData = appLinkIntent.getData();

        if (appLinkData != null) {
            String cashLoanId = appLinkData.getLastPathSegment();
            showPayDayLoan(cashLoanId);
        } else {
            handleFCMNotification();
        }
    }

    private void showPayDayLoan(String loanUUID) {
        Log.d("Loan Uri", loanUUID);

        try {
            UUID uuid = UUID.fromString(loanUUID);
            ;
            // uuid may be invalid
            if (uuid != null) {
                CashLoanDetailActivity.actionStart(this, loanUUID, true);
            }
        } catch (Exception ex) {
            LogUtil.e(ex);
        }
    }

    private void initFCM() {
//        FirebaseMessaging.getInstance().subscribeToTopic("all");
//        String token = FirebaseInstanceId.getInstance().getToken();
//        LogUtil.i("FCM token: " + token);
//
//        if (!TextUtils.isEmpty(token)&&presenter!=null) {
//            presenter.registerPushToken(token);
//        }
    }

    private void handleFCMNotification() {
        if (getIntent()!=null&&getIntent().getExtras() != null) {
            LogUtil.d("FCM notification data in MainActivity");
            String actionType = null;
            String productId = null;
            String linkTitle = null;
            String link = null;
            for (String key : getIntent().getExtras().keySet()) {
                Object value = getIntent().getExtras().get(key);
                LogUtil.i("key: " + key + "value: " + value);

                switch (key) {
                    case "action_type":
                        actionType = (String) value;
                        break;
                    case "product_id":
                        productId = (String) value;
                        break;
                    case "link_title":
                        linkTitle = (String) value;
                        break;
                    case "link":
                        link = (String) value;
                        break;
                    default:
                        break;
                }
            }

            if (!TextUtils.isEmpty(actionType)) {
                Intent intent = new Intent(this, NotificationBroadcastReceiver.class);
                intent.setAction(BroadcastConstants.PUSH_NOTIFICATION_RECEIVED);
                intent.putExtra("action_type", actionType);
                intent.putExtra("product_id", productId);
                intent.putExtra("link_title", linkTitle);
                intent.putExtra("link", link);
                sendBroadcast(intent);
            }
        }
    }

    @Override
    protected AppPresenter createPresenter() {
        return new AppPresenter(this);
    }

    @Override
    protected int getContentView() {
        return R.layout.launch_screen;
    }

    private void init() {
    }

    @Override
    protected int getStatusBarDrawable() {
        return R.color.transparent;
    }

    @Override
    public void onApiResult(String action, Object result) {
    }

    @Override
    public void onBackPressed() {
        IntentUtil.startLauncher(this);
    }
}
