package com.id.cash.presenter;

import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;

import java.util.Arrays;
import java.util.HashMap;

import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.functions.BiFunction;
import com.id.cash.bean.ApiReturn;
import com.id.cash.bean.BonusPointBean;
import com.id.cash.bean.BonusPointTaskBean;
import com.id.cash.bean.BonusPointTaskResultBean;
import com.id.cash.bean.ShareInfoBean;
import com.id.cash.bean.ShareResultBean;
import com.id.cash.bean.ShareResultType;
import com.id.cash.bean.UserBean;
import com.id.cash.common.ActivityManager;
import com.id.cash.common.IntentUtil;
import com.id.cash.common.LogUtil;
import com.id.cash.module.starwin.common.base.IView;
import com.id.cash.network.ApiActions;
import com.id.cash.network.RestApiSubscriber;
import com.id.cash.widget.PointAnimationDialog;

import static com.id.cash.presenter.SharePresenter.SHARE_REQUEST_CODE;
import static com.id.cash.presenter.SharePresenter.SHARE_RESULT_TAG;

/**
 * Created by linchen on 2018/5/28.
 */

public class BonusPointPresenter extends BasePresenter<IView> {
    public BonusPointPresenter(IView view) {
        super(view);
    }

    public void getBonusPointList(String action) {
        view.getLoading().showLoading();
        register(api.getBonusPointList(),
                new RestApiSubscriber<BonusPointBean[]>() {
                    @Override
                    public void onFinish(String code, String message) {
                        if (view != null) {
                            view.getLoading().hideLoading();
                        }
                    }

                    @Override
                    public void onData(String code, String message, ApiReturn<BonusPointBean[]> data) {
                        if (data != null && data.getData() != null) {
                            if (view != null) {
                                view.onApiResult(ApiActions.BONUSPOINT_LIST, data.getData());
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

    public class BonusPointSummaryBean {
        private UserBean userBean;
        private BonusPointTaskBean[] tasks;
        private ShareInfoBean shareInfoBean;

        public BonusPointSummaryBean(UserBean userBean, BonusPointTaskBean[] tasks, ShareInfoBean shareInfoBean) {
            this.userBean = userBean;
            this.tasks = tasks;
            this.shareInfoBean = shareInfoBean;
        }

        public UserBean getUserBean() {
            return userBean;
        }

        public void setUserBean(UserBean userBean) {
            this.userBean = userBean;
        }

        public BonusPointTaskBean[] getTasks() {
            return tasks;
        }

        public void setTasks(BonusPointTaskBean[] tasks) {
            this.tasks = tasks;
        }

        public ShareInfoBean getShareInfoBean() {
            return shareInfoBean;
        }

        public void setShareInfoBean(ShareInfoBean shareInfoBean) {
            this.shareInfoBean = shareInfoBean;
        }

        @Override
        public String toString() {
            return "BonusPointSummaryBean{" +
                    "userBean=" + userBean +
                    ", tasks=" + Arrays.toString(tasks) +
                    ", shareInfoBean=" + shareInfoBean +
                    '}';
        }
    }

    public void getBonusPointSummary(String action) {
        view.getLoading().showLoading();

        register(Observable.zip(api.getUserInfo(),
                api.getBonusPointTaskExecuteStatus(),
                api.getShareInfo(SharePresenter.ShareChannel.FACEBOOK.toString(), SharePresenter.ShareMethod.SHARING.toString()),
                (userApiReturn, taskApiReturn, shareInfoBeanApiReturn) -> {
                    ApiReturn<BonusPointSummaryBean> fakeApiReturn = new ApiReturn<>();
                    BonusPointSummaryBean data = new BonusPointSummaryBean(userApiReturn.getData(), taskApiReturn.getData(), shareInfoBeanApiReturn.getData());
                    fakeApiReturn.setData(data);
                    return fakeApiReturn;
                }),
                new RestApiSubscriber<BonusPointSummaryBean>() {
                    @Override
                    public void onFinish(String code, String message) {
                        if (view != null) {
                            view.getLoading().hideLoading();
                        }
                    }

                    @Override
                    public void onData(String code, String message, ApiReturn<BonusPointSummaryBean> data) {
                        if (view != null) {
                            view.onApiResult(action, data.getData());
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

    public void postTaskResult(String action, String taskTemplateId, String method) {
        if (TextUtils.isEmpty(taskTemplateId) || TextUtils.isEmpty(method)) {
            return;
        }

        view.getLoading().showLoading();
        register(api.postTaskResult(taskTemplateId, method),
                new RestApiSubscriber<BonusPointTaskResultBean>() {
                    @Override
                    public void onFinish(String code, String message) {
                        if (view != null) {
                            view.getLoading().hideLoading();
                        }
                    }

                    @Override
                    public void onData(String code, String message, ApiReturn<BonusPointTaskResultBean> data) {
                        if (view != null) {
                            view.onApiResult(action, data.getData());
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

    // post share result to server & responds with an animation (if needed)
    public void handleActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == SHARE_REQUEST_CODE) {
            if (data != null) {
                // FBShareActivity started by SharePresent.share in BonusPointMainFragment
                ShareResultBean shareResultBean = IntentUtil.getJsonParameter(data, SHARE_RESULT_TAG, ShareResultBean.class);
                if (shareResultBean != null) {
                    if (shareResultBean.getShareResultType() == ShareResultType.SUCCESS) {
                        postTaskResult(ApiActions.BONUSPOINT_TASK_RESULT,
                                shareResultBean.getTemplateId(),
                                shareResultBean.getMethod());
                    } else if (shareResultBean.getShareResultType() == ShareResultType.FAIL &&
                            !TextUtils.isEmpty(shareResultBean.getErrorMessage())) {
                        // even if there is an error message, there is no need to show it in share result
                        // showError(null, null, shareResultBean.getErrorMessage());
                    }
                }
            }
        }
    }

    public void showBonusPointTaskAnimation(BonusPointTaskResultBean bonusPointTaskResultBean) {
        LogUtil.d("showBonusPointTaskAnimation: " + bonusPointTaskResultBean);
        if (bonusPointTaskResultBean != null && bonusPointTaskResultBean.getBonusPoint() > 0) {
            Activity activity = getTopActivity();
            if (activity != null) {
                PointAnimationDialog pointAnimationDialog = new PointAnimationDialog(activity);
                pointAnimationDialog.initialize(bonusPointTaskResultBean.getBonusPoint());
                pointAnimationDialog.show();
            }
        }
    }

    private Activity getTopActivity() {
        Activity activity = ActivityManager.getInstance().getTopActivity();
        if (activity == null || activity.isFinishing()) {
            return null;
        } else if (activity.equals(view)) {
            // only when the top activity is this.view, show the animation
            return activity;
        }

        return null;
    }
}
