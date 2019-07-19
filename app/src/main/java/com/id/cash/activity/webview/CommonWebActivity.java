package com.id.cash.activity.webview;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.widget.TextView;

import com.id.cash.R;
import com.id.cash.common.LogUtil;

/**
 * Created by linchen on 2018/4/29.
 */

public class CommonWebActivity extends BaseWebActivity {
    public static final String TAG_TITLE = "TAG_TITLE";
    public static final String TAG_URL = "TAG_URL";

    private String url;
    protected String title;

    TextView tvTitle;

    protected void initTitle() {
        tvTitle.setText(TextUtils.isEmpty(title) ? "" : title);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void bindViews() {
        super.bindViews();
        tvTitle = findViewById(R.id.tv_actionbar_title);

        title = getIntent().getStringExtra(TAG_TITLE);
        url = getIntent().getStringExtra(TAG_URL);

        initTitle();
    }

    @Override
    protected void onDestroy() {
        if (!isFinishing()) {
            webView.removeAllViews();
            webView.destroy();
        }

        progressDialog.dismiss();
        super.onDestroy();
    }

    @Override
    public void onReceivedTitle(String title) {

    }

    @Override
    public void onLoadUrl() {
        loadGooglePlay(webView, title, url);
    }

    public static void actionStart(Context context, String title, String url) {
        actionStart(context, title, url, null, null);
    }

    public static void actionStart(Context context, String title, String url, String feedsId, String content) {
        LogUtil.i("starting web: " + url);
        Intent intent = new Intent(context, CommonWebActivity.class);
        intent.putExtra(TAG_TITLE, title);
        intent.putExtra(TAG_URL, url);

        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
        context.startActivity(intent);
    }

}
