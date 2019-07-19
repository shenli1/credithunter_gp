package com.id.cash.presenter;

import android.app.Activity;
import android.content.Intent;
import android.widget.Toast;

import com.id.cash.R;
import com.id.cash.bean.ApiReturn;
import com.id.cash.bean.InviteReferralBean;
import com.id.cash.bean.ShareInfoBean;
import com.id.cash.common.LogUtil;
import com.id.cash.module.starwin.common.base.IView;
import com.id.cash.module.starwin.sns.FBShareActivity;
import com.id.cash.module.starwin.sns.ShareContentDTO;
import com.id.cash.network.RestApiSubscriber;

/**
 * Created by linchen on 2018/5/29.
 */

public class SharePresenter extends BasePresenter<IView> {
    public static final int SHARE_REQUEST_CODE = 1000;
    public static final String SHARE_RESULT_TAG = "SHARE_RESULT_TAG";

    public enum ShareChannel {
        WHATSAPP("WHATSAPP"),
        FACEBOOK("FACEBOOK"),
        OTHER("LAINNYA");

        private final String value;

        ShareChannel(String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return value;
        }
    }

    public enum ShareMethod {
        INVITING("INVITING"),
        SHARING("SHARING");

        private final String value;

        ShareMethod(String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return value;
        }
    }

    public class ShareAction {
        private ShareChannel shareChannel;
        private ShareInfoBean shareInfoBean;

        public ShareChannel getShareChannel() {
            return shareChannel;
        }

        public void setShareChannel(ShareChannel shareChannel) {
            this.shareChannel = shareChannel;
        }

        public ShareInfoBean getShareInfoBean() {
            return shareInfoBean;
        }

        public void setShareInfoBean(ShareInfoBean shareInfoBean) {
            this.shareInfoBean = shareInfoBean;
        }

        @Override
        public String toString() {
            return "ShareAction{" +
                    "shareChannel=" + shareChannel +
                    ", shareInfoBean=" + shareInfoBean +
                    '}';
        }
    }

    public SharePresenter(IView view) {
        super(view);
    }

    public void loadInviteList(String action) {
        if (view != null) {
            view.getLoading().showLoading();
            register(api.getInviteList(),
                    new RestApiSubscriber<InviteReferralBean[]>() {
                        @Override
                        public void onFinish(String code, String message) {
                            // the response is ignored, no UI
                            if (view != null) {
                                view.getLoading().hideLoading();
                            }
                        }

                        @Override
                        public void onData(String code, String message, ApiReturn<InviteReferralBean[]> data) {
                            if (view != null) {
                                if (data != null) {
                                    view.onApiResult(action, data.getData());
                                }
                            }
                        }

                        @Override
                        public void onError(String code, String message) {
                            if (view != null) {
                                view.getLoading().showError(action, code, message);
                            }
                        }
                    });
        }
    }

    public void getShareData(String action, ShareChannel channel, ShareMethod method) {
        view.getLoading().showLoading();
        register(api.getShareInfo(channel.toString(), method.toString()),
                new RestApiSubscriber<ShareInfoBean>() {
                    @Override
                    public void onFinish(String code, String message) {
                        // the response is ignored, no UI
                        if (view != null) {
                            view.getLoading().hideLoading();
                        }
                    }

                    @Override
                    public void onData(String code, String message, ApiReturn<ShareInfoBean> data) {
                        // the response is ignored, no UI
                        if (view != null) {
                            if (data.getData() != null) {
                                ShareAction shareAction = new ShareAction();
                                shareAction.setShareChannel(channel);
                                shareAction.setShareInfoBean(data.getData());
                                view.onApiResult(action, shareAction);
                            } else {
                                LogUtil.e("empty share info returned by server");
                            }
                        }
                    }

                    @Override
                    public void onError(String code, String message) {
                        if (view != null) {
                            view.getLoading().showError(action, code, message);
                        }
                    }
                }
        );
    }

    public void share(ShareChannel channel, ShareInfoBean bean, Activity activity) {
        bean.setChannel(channel.toString());
        if (ShareChannel.WHATSAPP == channel) {
            shareToWhatsApp(bean, activity);
        } else if (ShareChannel.FACEBOOK == channel) {
            shareToFacebook(bean, activity);
        } else if (ShareChannel.OTHER == channel) {
            shareToOther(bean, activity);
        }
    }

    // facebook has callback, but what's app & system share panel does not
    private void shareToFacebook(ShareInfoBean shareInfoBean, Activity activity) {
        FBShareActivity.actionStartForResult(activity,
                new ShareContentDTO().setLink(shareInfoBean.getLink()),
                shareInfoBean.getMethod(),
                shareInfoBean.getTaskTemplateId(),
                SHARE_REQUEST_CODE);
    }

    private void shareToWhatsApp(ShareInfoBean shareInfoBean, Activity activity) {
        Intent sendIntent = new Intent(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, shareInfoBean.getText());
        sendIntent.setType("text/plain");
        sendIntent.setPackage("com.whatsapp");
        try {
            activity.startActivity(sendIntent);
        } catch (Exception e) {
            LogUtil.e(e);
            Toast.makeText(activity, activity.getString(R.string.invite_share_whatsapp_fail), Toast.LENGTH_SHORT).show();
        }
    }

    private void shareToOther(ShareInfoBean shareInfoBean, Activity activity) {
        Intent sendIntent = new Intent(Intent.ACTION_SEND);
        sendIntent.setType("text/plain");
        sendIntent.putExtra(Intent.EXTRA_TEXT, shareInfoBean.getText());

        try {
            activity.startActivity(Intent.createChooser(sendIntent, activity.getString(R.string.invite_share_share_other_title)));
        } catch (Exception e) {
            LogUtil.e(e);
            Toast.makeText(activity, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}
