package com.id.cash.activity.feeds;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.widget.LinearLayout;

import com.id.cash.R;
import com.id.cash.activity.webview.CommonWebActivity;
import com.id.cash.bean.BonusPointTaskResultBean;
import com.id.cash.bean.BonusPointTaskUIBean;
import com.id.cash.bean.ClientFeedsBean;
import com.id.cash.bean.FeedsBean;
import com.id.cash.bean.ShareInfoBean;
import com.id.cash.common.ActivityManager;
import com.id.cash.common.LogUtil;
import com.id.cash.common.TrackUtil;
import com.id.cash.network.ApiActions;
import com.id.cash.presenter.BonusPointPresenter;
import com.id.cash.presenter.SharePresenter;
import com.id.cash.widget.DebouncingOnClickListener;
import com.id.cash.widget.PointAnimationDialog;

/**
 * Created by linchen on 2018/7/20.
 */
public class FeedsWebActivity extends CommonWebActivity {
    private static final String TAG_FEEDS_ID = "TAG_FEEDS_ID";

    private String feedsId;
    private ClientFeedsBean clientFeedsBeanCache;
    private LinearLayout llShareButton;
    private ShareInfoBean facebookShareInfoBean;

    // for showing FB share dialog
    private final SharePresenter sharePresenter = new SharePresenter(this);
    // for posting share result to server & showing animation (if necessary)
    private final BonusPointPresenter bonusPointPresenter = new BonusPointPresenter(this);

    @SuppressLint({"JavascriptInterface", "SetJavaScriptEnabled", "NewApi"})
    @Override
    public void bindViews() {
        super.bindViews();
        feedsId = getIntent().getStringExtra(TAG_FEEDS_ID);

        this.webView.addJavascriptInterface(getScriptInterface(), "UUang");

        llShareButton = findViewById(R.id.ll_feeds_share);
    }

    @Override
    protected void bindEvents() {
        super.bindEvents();

        llShareButton.setOnClickListener(new DebouncingOnClickListener() {
            @Override
            public void doClick(View v) {
                onFBShareClick();
            }
        });
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_feeds_web;
    }

    private void onFBShareClick() {
        if (TextUtils.isEmpty(feedsId)) {
            // not expected to reach here
            return;
        }

        if (facebookShareInfoBean == null) {
            TrackUtil.logEvent("feeds_detail_fb_share_nodata_click");
            // if share info data is not retrieved, fetch share info data
            // FEEDS_GET_SHARE_THEN_PERFORM_SHARE action in onApiResult will start share again
            presenter.getShareData(ApiActions.FEEDS_GET_SHARE_THEN_PERFORM_SHARE, feedsId);
        } else {
            TrackUtil.logEvent("feeds_detail_fb_share_click");
            shareFBImpl();
        }
    }

    private void shareFBImpl() {
        if (facebookShareInfoBean != null) {
            facebookShareInfoBean.setMethod(SharePresenter.ShareMethod.SHARING.toString());
            facebookShareInfoBean.setTaskTemplateId(BonusPointTaskUIBean.BonusPointTaskType.SHARE);

            // show FB share dialog
            sharePresenter.share(SharePresenter.ShareChannel.FACEBOOK, facebookShareInfoBean, this);
        }
    }

    // feeds interface: like/unlike
    private Object getScriptInterface() {
        return new Object() {
            @JavascriptInterface
            public String toggleLike() {
                if (!TextUtils.isEmpty(feedsId)) {
                    presenter.toggleLike(ApiActions.FEEDS_TOGGLE_LIKE, feedsId);
                }
                return null;
            }

            @JavascriptInterface
            public void getIsLiked() {
                if (clientFeedsBeanCache == null) {
                    presenter.getClientFeeds(ApiActions.FEEDS_CLIENT_FEEDS, feedsId);
                } else {
                    onApiResult(ApiActions.FEEDS_CLIENT_FEEDS, clientFeedsBeanCache);
                }
            }
        };
    }

    @Override
    public void onApiResult(String action, Object result) {
        super.onApiResult(action, result);

        if (ApiActions.FEEDS_TOGGLE_LIKE.equals(action) || ApiActions.FEEDS_CLIENT_FEEDS.equals(action)) {
            if (result != null) {
                clientFeedsBeanCache = (ClientFeedsBean) result;
                if (!isFinishing() && webView != null) {
                    try {
                        String template = "javascript:updateIsLiked(%s)";
                        webView.loadUrl(String.format(template, clientFeedsBeanCache.isLiked()));
                    } catch (Exception ex) {
                        LogUtil.e(ex);
                    }
                }

                if (ApiActions.FEEDS_TOGGLE_LIKE.equals(action) && clientFeedsBeanCache.isLiked()) {
                    // when toggle like successfully, fetch & update the like num
                    presenter.getFeeds(ApiActions.FEEDS_GET, feedsId);
                }
            }
        } else if (ApiActions.FEEDS_GET.equals(action)) {
            if (result != null) {
                FeedsBean feedsBean = (FeedsBean) result;
                try {
                    String template = "javascript:updateCount(%s, %s)";
                    webView.loadUrl(String.format(template, feedsBean.getLikeNum(), feedsBean.getSeenNum()));
                } catch (Exception ex) {
                    LogUtil.e(ex);
                }
            }
        } else if (ApiActions.FEEDS_GET_SHARE.equals(action) || ApiActions.FEEDS_GET_SHARE_THEN_PERFORM_SHARE.equals(action)) {
            if (result != null) {
                facebookShareInfoBean = (ShareInfoBean) result;
            }

            if (ApiActions.FEEDS_GET_SHARE_THEN_PERFORM_SHARE.equals(action)) {
                shareFBImpl();
            }
        } else if (ApiActions.BONUSPOINT_TASK_RESULT.equals(action)) {
            // sent by BonusPointPresenter.handleActivityResult (when FB share succeeds)
            bonusPointPresenter.showBonusPointTaskAnimation((BonusPointTaskResultBean) result);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (bonusPointPresenter != null) {
            bonusPointPresenter.handleActivityResult(requestCode, resultCode, data);
        }
    }

    public static void actionStart(Context context, String title, String feedsId, String link) {
        Intent intent = new Intent(context, FeedsWebActivity.class);
        intent.putExtra(TAG_TITLE, title);
        intent.putExtra(TAG_URL, link);

        if (!TextUtils.isEmpty(feedsId)) {
            intent.putExtra(TAG_FEEDS_ID, feedsId);
        }

        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
        context.startActivity(intent);
    }
}
