package com.id.cash.activity.webview;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.id.cash.R;
import com.id.cash.common.GooglePlayUtil;
import com.id.cash.common.LogUtil;
import com.id.cash.common.NetworkUtil;
import com.id.cash.common.UrlUtil;
import com.id.cash.module.starwin.common.base.BaseActivity;
import com.id.cash.presenter.FeedsPresenter;
import com.id.cash.widget.ProgressDialog;

/**
 * Created by linchen on 2018/4/29.
 */
@SuppressLint({"SetJavaScriptEnabled", "JavascriptInterface"})
public abstract class BaseWebActivity extends BaseActivity<FeedsPresenter> {

    protected ProgressDialog progressDialog;
    private boolean shouldFinishOnResume = false;
    private String url;
    private String title;
    protected WebView webView;

    public abstract void onReceivedTitle(String title);

    public abstract void onLoadUrl();

    @Override
    protected FeedsPresenter createPresenter() {
        return new FeedsPresenter(this);
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_common_web;
    }


    private void handleGooglePlay(String url) {
        webView.setVisibility(View.VISIBLE);
        LogUtil.i("handle url: " + url);

        if (!url.contains(UrlUtil.GP_URL_PREFIX)) {
            if (url.contains(UrlUtil.GP_MARKET_PREFIX)) {
                webView.setVisibility(View.GONE);
            } else {
                return;
            }
        }
        GooglePlayUtil.openGooglePlay(this,
                GooglePlayUtil.getMarketUrl(this, UrlUtil.getQueryString(url)));
        shouldFinishOnResume = true;
    }

    @SuppressLint({"JavascriptInterface", "SetJavaScriptEnabled", "NewApi"})
    private void loadWebViewImpl() {
        if (NetworkUtil.isNetworkAvailable(this)) {
            this.webView.loadUrl(this.url);
        }
    }

    protected void loadGooglePlay(WebView webView, String title, String url) {
        this.webView = webView;
        this.url = url;
        this.title = title;

        loadWebViewImpl();
    }

    protected void loadContent(WebView webView, String title, String content) {
        this.webView = webView;
        this.title = title;

        webView.loadData(content, "text/html; charset=utf-8", "UTF-8");
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        progressDialog = ProgressDialog.getInstance(this);
        progressDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    BaseWebActivity.this.finish();
                    return true;
                }

                return false;
            }
        });
        progressDialog.setCancelable(false);

        onLoadUrl();
    }

    protected void bindViews() {
        webView = findViewById(R.id.webview_common_web);
        webView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode != KeyEvent.KEYCODE_BACK || !BaseWebActivity.this.webView.canGoBack()) {
                    return false;
                }
                BaseWebActivity.this.webView.goBack();
                return true;
            }
        });

        CookieManager cookieManager = CookieManager.getInstance();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            CookieManager.setAcceptFileSchemeCookies(true);
            cookieManager.setAcceptThirdPartyCookies(this.webView, true);
        } else {
            cookieManager.setAcceptCookie(true);
        }

        WebSettings webSettings = webView.getSettings();
        webSettings.setDefaultTextEncodingName("utf-8");
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setUseWideViewPort(true);
        webSettings.setNeedInitialFocus(true);
        webSettings.setSupportZoom(false);
        webSettings.setLoadsImagesAutomatically(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            webSettings.setMixedContentMode(0);
        }
        webSettings.setJavaScriptEnabled(true);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        webSettings.setRenderPriority(WebSettings.RenderPriority.HIGH);
        webSettings.setEnableSmoothTransition(true);
        webSettings.setAllowFileAccess(true);
        webSettings.setSaveFormData(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setAppCacheMaxSize(8388608);
        webSettings.setAppCachePath(this.getApplicationContext().getCacheDir().getAbsolutePath());
        webSettings.setAppCacheEnabled(true);

        // TODO: user agent & version code
//        StringBuilder stringBuilder = new StringBuilder();
//        stringBuilder.append(Build.MODEL);
//        stringBuilder.append("/Android ");
//        stringBuilder.append(Build.VERSION.RELEASE);
//        stringBuilder.append("/");
//        stringBuilder.append(webSettings.getUserAgentString());
//        String b = b(stringBuilder.toString());
//        StringBuilder stringBuilder2 = new StringBuilder();
//        stringBuilder2.append("userAgent: ");
//        stringBuilder2.append(b);
//        LogUtil.a(stringBuilder2.toString());
//        webSettings.setUserAgentString(b);
        if (NetworkUtil.isNetworkAvailable(this)) {
            webSettings.setCacheMode(WebSettings.LOAD_DEFAULT);
        } else {
            webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        }
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView webView, int i) {
                if (i < 50) {
                    BaseWebActivity.this.showLoading();
                    return;
                }
                if (i >= 50) {
                    BaseWebActivity.this.hideLoading();
                }
            }

            @Override
            public void onReceivedTitle(WebView webView, String title) {
                if (TextUtils.isEmpty(BaseWebActivity.this.title)) {
                    BaseWebActivity.this.onReceivedTitle(title);
                }
            }
        });
        this.webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView webView, String url) {
                LogUtil.i("completed loading: " + url);
            }

            @Override
            public void onPageStarted(WebView webView, String url, Bitmap bitmap) {
                LogUtil.i("start loading: " + url);
            }

            @Override
            public void onReceivedError(WebView webView, WebResourceRequest webResourceRequest, WebResourceError webResourceError) {
                LogUtil.w("error loading: " + url + "\n" + webResourceError.getDescription());
                super.onReceivedError(webView, webResourceRequest, webResourceError);
            }

            @Override
            public WebResourceResponse shouldInterceptRequest(WebView webView, String url) {
                BaseWebActivity.this.handleGooglePlay(url);
                return super.shouldInterceptRequest(webView, url);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView webView, String url) {
                BaseWebActivity.this.handleGooglePlay(url);
                return false;
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        try {
            if (isFinishing() && this.webView != null) {
                // low frequence crash inside of webview:
                // android.webkit.WebViewClassic.clearHistory
                webView.clearHistory();
                webView.clearFormData();
            }
            getCacheDir().delete();
            progressDialog.setOnKeyListener(null);
        } catch (Exception ex) {
            LogUtil.e(ex);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (this.shouldFinishOnResume) {
            this.shouldFinishOnResume = false;
            finish();
        }
    }
}
