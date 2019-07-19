package com.id.cash.widget.dialog;

import android.app.Activity;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.id.cash.BuildConfig;
import com.id.cash.R;
import com.id.cash.activity.app.CloseAppActivity;
import com.id.cash.bean.AppVersionBean;
import com.id.cash.common.DownloadApkUtil;
import com.id.cash.common.GooglePlayUtil;
import com.id.cash.common.Preference;
import com.id.cash.common.TrackUtil;
import com.id.cash.common.UrlUtil;

/**
 * Created by linchen on 2018/4/30.
 */

public class UpgradeDialog extends DialogBase {
    TextView tvUpgradeDesc;

    private AppVersionBean appVersionBean;

    public UpgradeDialog(Activity activity, AppVersionBean appVersionBean) {
        super(activity);

        // configure the dialog
        this.appVersionBean = appVersionBean;

        String upgradeContent = appVersionBean.getContent();
        if (!TextUtils.isEmpty(upgradeContent)) {
            tvUpgradeDesc.setText(upgradeContent);
        }

        if (appVersionBean.getUpdateType() != 0) {
            dialog.setCancelable(false);
            // for force update, cancel means exit app
            ((TextView) btnCancel).setText(R.string.upgrade_dialog_exit);
        }
    }

    @Override
    int getContentView() {
        return R.layout.dialog_app_upgrade;
    }

    @Override
    protected View bindViews() {
        View view = super.bindViews();

        tvUpgradeDesc = view.findViewById(R.id.tv_upgrade_dialog_content);
        return view;
    }

    @Override
    public void show() {
        super.show();
        Preference.getInstance().setLastUpgradePromptVersion(appVersionBean.getVersionCode());
    }

    @Override
    protected void onCancel() {
        super.onCancel();
        TrackUtil.logEvent("upgrade_cancel_click");
        if (this.appVersionBean.getUpdateType() != 0) {
            killApp();
        }
    }

    @Override
    protected void onOk() {
        super.onOk();
        TrackUtil.logEvent("upgrade_ok_click");
        String url = appVersionBean.getUrl();
        // 1. open by google play
        if (url.contains("https://play.google.com/store/apps/details")) {
            GooglePlayUtil.openGooglePlay(activity,
                    GooglePlayUtil.getMarketUrl(activity, UrlUtil.getQueryString(url)));
        } else {
            // open by download manager
            DownloadApkUtil.downloadApk(activity, url, BuildConfig.APK_NAME, activity.getString(R.string.app_name));
        }
    }

    private void killApp() {
        // kill all activities, otherwise, the system will restore the crash
//        ActivityManager.getInstance().killAll();
//        Process.killProcess(Process.myPid());
//        System.exit(0);
        CloseAppActivity.actionStart(activity);
    }
}
