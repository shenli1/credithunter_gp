package com.id.cash.module.starwin.sns;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.share.Sharer;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;

import com.id.cash.R;
import com.id.cash.bean.ShareResultBean;
import com.id.cash.bean.ShareResultType;
import com.id.cash.common.IntentUtil;
import com.id.cash.common.JsonUtil;
import com.id.cash.common.LogUtil;
import com.id.cash.common.TrackUtil;
import com.id.cash.module.starwin.common.base.BaseActivity;
import com.id.cash.presenter.BasePresenter;
import com.id.cash.presenter.SharePresenter;

public class FBShareActivity extends BaseActivity {
    ShareDialog shareDialog;
    CallbackManager callbackManager;

    private static final String SHARE_TAG = "SHARE_TAG";
    private static final String SHARE_RESULT_TAG = SharePresenter.SHARE_RESULT_TAG;
    private static final String SHARE_METHOD_TAG = "SHARE_METHOD_TAG";
    private static final String SHARE_TASK_TEMPLATE_ID_TAG = "SHARE_TASK_TEMPLATE_ID_TAG";
    private static final String REQUEST_CODE_TAG = "REQUEST_CODE_TAG";

    ShareContentDTO shareContentDTO;

    private int requestCode;
    private String shareMethod;
    private String shareTaskTemplateId;
    private boolean isCallbackInvoked = false;

    public static void actionStartForResult(Activity context,
                                            ShareContentDTO shareContentDTO,
                                            String method,  // inviting or sharing
                                            String taskTemplateId,
                                            int requestCode) {
        Intent intent = IntentUtil.intentWithJsonParameter(context, FBShareActivity.class, SHARE_TAG, shareContentDTO);
        intent.putExtra(REQUEST_CODE_TAG, requestCode);
        intent.putExtra(SHARE_METHOD_TAG, method);
        intent.putExtra(SHARE_TASK_TEMPLATE_ID_TAG, taskTemplateId);
        context.startActivityForResult(intent, requestCode);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        shareContentDTO = IntentUtil.getJsonParameter(getIntent(), SHARE_TAG, ShareContentDTO.class);
        requestCode = getIntent().getIntExtra(REQUEST_CODE_TAG, 0);
        shareMethod = getIntent().getStringExtra(SHARE_METHOD_TAG);
        shareTaskTemplateId = getIntent().getStringExtra(SHARE_TASK_TEMPLATE_ID_TAG);

        callbackManager = CallbackManager.Factory.create();
        shareDialog = new ShareDialog(this);
        // https://developers.facebook.com/support/bugs/890116221030179/
        // FB by design behavior: success is called if touching outside of the share dialog to cancel
        shareDialog.registerCallback(callbackManager, new FacebookCallback<Sharer.Result>() {
            @Override
            public void onSuccess(Sharer.Result result) {
                TrackUtil.logEvent("fb_share_dialog_success");
                LogUtil.i("FBShare: onSuccess");
//                RxBus.get().post(BusAction.SHARE_SUCCESS, new ShareSuccess());
                delayedFinish(true, ShareResultType.SUCCESS, null);
            }

            @Override
            public void onCancel() {
                TrackUtil.logEvent("fb_share_dialog_cancel");
                LogUtil.v("FBShare: onCancel");
//                RxBus.get().post(BusAction.SHARE_CANCEL, new ShareCancel());
                delayedFinish(true, ShareResultType.CANCEL, null);
            }

            @Override
            public void onError(FacebookException error) {
                TrackUtil.logEvent("fb_share_dialog_error_" + error.toString());
                LogUtil.e(error);
//                RxBus.get().post(BusAction.SHARE_ERROR,
//                        new ShareError().setMessage(error.getLocalizedMessage()));
                delayedFinish(true, ShareResultType.FAIL, error.getLocalizedMessage());
            }
        });

        String error = getString(R.string.share_wrong_parameter);
        if (shareContentDTO != null) {
            ShareLinkContent shareLinkContent = shareContentDTO.getFBShareLinkContent();
            if (shareLinkContent != null) {
                if (ShareDialog.canShow(ShareLinkContent.class)) {
                    shareDialog.show(shareLinkContent);
                    return;
                } else {
                    error = getString(R.string.share_not_allowed);
                }
            }
        }

        // if shareDialog does not show above, it falls down to this logic
        delayedFinish(true, ShareResultType.FAIL, error);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (callbackManager != null) {
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        shareDialog = null;
        callbackManager = null;

        TrackUtil.logEvent("fb_share_back_click");
        // if we have not notified the finish of the share flow
        // make sure we end the sure flow
        if (!this.isCallbackInvoked) {
            setShareResult(ShareResultType.CANCEL, null);
//            RxBus.get().post(BusAction.SHARE_CANCEL, new ShareCancel());
        }
    }

    @Override
    protected BasePresenter createPresenter() {
        return null;
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_share;
    }

    private void setShareResult(ShareResultType resultType, String errorMessage) {
        if (requestCode != 0) {
            ShareResultBean shareResultBean = new ShareResultBean();
            shareResultBean.setShareResultType(resultType);
            shareResultBean.setErrorMessage(errorMessage);
            shareResultBean.setChannel(SharePresenter.ShareChannel.FACEBOOK.toString());
            shareResultBean.setMethod(shareMethod);
            shareResultBean.setTemplateId(shareTaskTemplateId);
            Intent intent = new Intent();
            intent.putExtra(SHARE_RESULT_TAG, JsonUtil.stringify(shareResultBean));

            setResult(requestCode, intent);
        }
    }

    private void delayedFinish(boolean isCallbackInvoked, ShareResultType shareResult, String errorMessage) {
        new Handler(Looper.getMainLooper())
                .postDelayed(() -> {
                    if (!FBShareActivity.this.isFinishing()) {
                        setShareResult(shareResult, errorMessage);
                        // if Rx post was done, update the flag
                        if (isCallbackInvoked) {
                            this.isCallbackInvoked = true;
                        }
                        FBShareActivity.this.finish();
                    }
                }, 100);
    }
}
