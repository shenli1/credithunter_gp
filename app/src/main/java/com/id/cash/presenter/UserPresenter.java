package com.id.cash.presenter;

import android.support.annotation.NonNull;

import com.id.cash.bean.ApiReturn;
import com.id.cash.bean.UserBean;
import com.id.cash.common.Preference;
import com.id.cash.module.starwin.common.base.IView;
import com.id.cash.network.RestApiSubscriber;

/**
 * Created by linchen on 2018/6/1.
 */

public class UserPresenter extends BasePresenter<IView> {
    public UserPresenter(IView view) {
        super(view);
    }

    public void loginByAccountKitAuthCode(String action, @NonNull String authorizationCode) {
        if (view != null) {
            view.getLoading().showLoading();
        }

        // 1. login token 2. use the token to get user info
        register(api.loginByAccountKitAuthCode(authorizationCode)
                        .flatMap((tokenApiReturn) -> {
                            // 1. save token
                            // 2. get user info
                            Preference.getInstance().setToken(tokenApiReturn.getData());
                            return api.getUserInfo();
                        }),
                new RestApiSubscriber<UserBean>() {
                    @Override
                    public void onFinish(String code, String message) {
                        if (view != null) {
                            view.getLoading().hideLoading();
                        }
                    }

                    @Override
                    public void onData(String code, String message, ApiReturn<UserBean> data) {
                        if (view != null) {
                            Preference.getInstance().setUser(data.getData());
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

    public void logout(String action) {
        if (view != null) {
            view.getLoading().showLoading();
        }
        register(api.logout(),
                // logout should succeed in all condition
                new RestApiSubscriber<Boolean>(false) {
                    @Override
                    public void onFinish(String code, String message) {
                        // logout should succeed in all condition
                        // hide loading
                        if (view != null) {
                            view.getLoading().hideLoading();
                            view.onApiResult(action, null);
                        }
                    }

                    @Override
                    public void onData(String code, String message, ApiReturn<Boolean> data) {
                    }

                    @Override
                    public void onError(String code, String message) {
                        // hide loading
                        if (view != null) {
                            view.getLoading().hideLoading();
                            view.onApiResult(action, null);
                        }
                    }
                });
    }

    public void getUserInfo(String action) {
        register(api.getUserInfo(),
                new RestApiSubscriber<UserBean>() {
                    @Override
                    public void onFinish(String code, String message) {
                        if (view != null && view.getLoading() != null) {
                            view.getLoading().hideLoading();
                        }
                    }

                    @Override
                    public void onData(String code, String message, ApiReturn<UserBean> data) {
                        if (view != null) {
                            // save user bean
                            Preference.getInstance().setUser(data.getData());
                            view.onApiResult(action, data.getData());
                        }
                    }

                    @Override
                    public void onError(String code, String message) {
                        if (view != null && view.getLoading() != null) {
                            view.getLoading().showError(action, code, message);
                        }
                    }
                });
    }

}
