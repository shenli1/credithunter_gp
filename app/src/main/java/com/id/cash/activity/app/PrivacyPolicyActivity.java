package com.id.cash.activity.app;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.id.cash.BuildConfig;
import com.id.cash.R;
import com.id.cash.common.PrivacyPolicyUtil;
import com.id.cash.common.TrackUtil;
import com.id.cash.module.starwin.common.base.BaseActivity;
import com.id.cash.network.H5Urls;
import com.id.cash.presenter.AppPresenter;
import com.id.cash.widget.ClickableCheckBox;
import com.id.cash.widget.dialog.DialogBase;
import com.id.cash.widget.dialog.PermissionDialog;

/**
 * Created by linchen on 2018/4/26.
 */

public class PrivacyPolicyActivity extends BaseActivity<AppPresenter> {

    public static final String RESULT_TAG = "RESULT_TAG";

    private ImageView loadingPlaceHolder;
    private ClickableCheckBox cb;
    private Button agreeButton;
    private WebView webView;

    // if user has not finished reading the privacy policy, do not enable ok button
    private int requestCode;
    private static final int DEFAULT_REQUEST_CODE = 1001;

    @Override
    protected AppPresenter createPresenter() {
        return null;
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_privacy_policy;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        tvTitle.setText(getString(R.string.privacy_policy));

        cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    cb.setSelected(true);
                    agreeButton.setSelected(false);
                } else {
                    cb.setSelected(false);
                    agreeButton.setSelected(true);
                }
            }
        });

        cb.setOnClickListener((v) -> {
            if (v instanceof ClickableCheckBox) {
                if (!((ClickableCheckBox)v).isCheckAllowed()) {
                    // check not allowed (user has not fully read the policy),
                    // ask the user to read the entire document to proceed
                    Toast.makeText(PrivacyPolicyActivity.this, getString(R.string.read_the_document_to_proceed), Toast.LENGTH_SHORT).show();
                }
            }
        });

        // when selected is true, the button looks disabled
        agreeButton.setSelected(true);
        agreeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!cb.isChecked()) {
                    Toast.makeText(PrivacyPolicyActivity.this, getString(R.string.check_the_checkbox), Toast.LENGTH_SHORT).show();
                } else {
                    privacyPolicyResult(true);
                }
            }
        });

        webView.getSettings().setAppCachePath(this.getCacheDir().getAbsolutePath());
        webView.getSettings().setAppCacheEnabled(true);
        webView.getSettings().setDisplayZoomControls(true);
        webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setJavaScriptEnabled(false);
        webView.getSettings().setSupportZoom(false);
        webView.getSettings().setMinimumFontSize(14);
        webView.setScrollBarStyle(View.SCROLLBARS_OUTSIDE_INSET);
        webView.setVerticalScrollBarEnabled(true);
        webView.setOverScrollMode(View.OVER_SCROLL_ALWAYS);
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                // show privacy policy after fully loaded
                if (newProgress > 90 && loadingPlaceHolder.getVisibility() == View.VISIBLE) {
                    loadingPlaceHolder.setVisibility(View.GONE);
                }
            }
        });
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);

                webView.setVisibility(View.VISIBLE);
                loadingPlaceHolder.setVisibility(View.GONE);
            }
        });
        webView.loadUrl(H5Urls.getPrivacyPolicy());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            cb.setCheckAllowed(false);
            webView.setOnScrollChangeListener((v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
                int contentHeight = Math.round(webView.getContentHeight() * webView.getScale() - webView.getHeight());
                if ((v.getScrollBarSize() + scrollY) >= (contentHeight - 20)) {
                    cb.setCheckAllowed(true);
                }
            });
        }
    }

    @Override
    protected void bindViews() {
        super.bindViews();

        loadingPlaceHolder = findViewById(R.id.iv_loading_placeholder);
        cb = findViewById(R.id.cb_agree);
        agreeButton = findViewById(R.id.btn_agree);
        webView = findViewById(R.id.wv_privacy_policy);
    }

    @Override
    public void onBackPressed() {
        privacyPolicyResult(false);
        TrackUtil.logEvent("privacy_back_click");
        super.onBackPressed();
    }

    private void privacyPolicyResult(boolean isAgree) {
        // 1. save result
        PrivacyPolicyUtil.setPrivacyPolicy(isAgree);
        if (isAgree) {
            // 1. display PrivacyPolicyActivity
            // 2. if user accepts privacy policy
            // 3. show permission
            // 4. save user's decision
            PermissionDialog permissionDialog = new PermissionDialog(this);
            permissionDialog.setOnButtonClickedListener(new DialogBase.OnButtonClickedListener() {
                @Override
                public void onOkClicked(DialogBase dialog) {
                    TrackUtil.logEvent("privacy_agree_click");
                    setResultAndFinish(true);
                }

                @Override
                public void onCancelClicked(DialogBase dialog) {
                    TrackUtil.logEvent("privacy_reject_click");
                    setResultAndFinish(false);
                }
            });
            permissionDialog.show();
        } else {
            setResultAndFinish(false);
        }
    }

    private void setResultAndFinish(boolean isAgree) {
        PrivacyPolicyUtil.setPrivacyPolicy(isAgree);

        // notify MainTabActivity
        Intent intent = new Intent();
        intent.putExtra(RESULT_TAG, isAgree);
        setResult(RESULT_OK, intent);

        finish();
    }

    public static void actionStartForResult(final Activity context, int requestCode) {
        Intent intent = new Intent(context, PrivacyPolicyActivity.class);
        context.startActivityForResult(intent, requestCode);
    }
}
