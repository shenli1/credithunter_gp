package com.id.cash.activity.app;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.widget.TextView;

import com.id.cash.BuildConfig;
import com.id.cash.R;
import com.id.cash.common.LogUtil;
import com.id.cash.common.TrackUtil;
import com.id.cash.module.starwin.common.base.BaseActivity;
import com.id.cash.presenter.AppPresenter;

/**
 * Created by linchen on 2018/6/20.
 */
public class AboutUsActivity extends BaseActivity<AppPresenter> {

    @Override
    protected AppPresenter createPresenter() {
        return null;
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_aboutus;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void bindViews() {
        super.bindViews();

        TextView tvTitle = findViewById(R.id.tv_actionbar_title);
        tvTitle.setText(R.string.about_us);

        TextView tvVersion = findViewById(R.id.tv_app_version);
        String versionName = "";
        try {
            Context context = this;
            PackageInfo pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            versionName = pInfo.versionName;
        } catch (Exception e) {
            LogUtil.e(e.toString());
        }

        if (!TextUtils.isEmpty(versionName)) {
            tvVersion.setText(getString(R.string.app_name) + " " + versionName);
        }
    }

    public static void actionStart(final Context context) {
        Intent intent = new Intent(context, AboutUsActivity.class);
        context.startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        TrackUtil.logEvent("aboutus_back_click");
        super.onBackPressed();
    }
}
