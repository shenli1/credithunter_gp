package com.id.cash.module.starwin.accountkit;

import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;

import com.facebook.accountkit.AccountKitError;
import com.facebook.accountkit.AccountKitLoginResult;
import com.facebook.accountkit.internal.AccountKitController;
import com.facebook.accountkit.ui.AccountKitActivity;
import com.facebook.accountkit.ui.AccountKitConfiguration;
import com.facebook.accountkit.ui.LoginType;

import com.id.cash.BuildConfig;
import com.id.cash.MainApplication;
import com.id.cash.activity.user.WithdrawActivity;
import com.id.cash.bean.TokenBean;
import com.id.cash.bean.UserBean;
import com.id.cash.common.LogUtil;
import com.id.cash.common.Preference;
import com.id.cash.common.TrackUtil;
import com.id.cash.module.starwin.common.base.ILifeCycle;
import com.id.cash.module.starwin.common.base.ILoading;
import com.id.cash.module.starwin.common.base.IView;
import com.id.cash.module.starwin.common.bus.BusAction;
import com.id.cash.network.ApiActions;
import com.id.cash.presenter.UserPresenter;

/**
 * Created by linchen on 2018/4/20.
 */

public class LoginManager implements IView {
    private static int LOGIN_REQUEST_CODE = 99;
    private static boolean isLoginShown;
    private IView viewDelegate;

    // the next action that will be taking after login
    private String loginNextAction;
    private UserPresenter userPresenter = new UserPresenter(this);

    private LoginManager() {
    }

    public LoginManager(IView view) {
        this.viewDelegate = view;
    }

    public boolean isLoggedIn() {
        if(Preference.getInstance()==null)return false;
        TokenBean token = Preference.getInstance().getToken();
        return token != null && !TextUtils.isEmpty(token.getUserId()) && !TextUtils.isEmpty(token.getToken());
    }

    public void phoneLogin(Activity activity, String loginNextAction) {
        if (!isLoginShown) {
            this.loginNextAction = loginNextAction;
            isLoginShown = true;
            final Intent intent = new Intent(activity, AccountKitActivity.class);

            String[] smsWhiteList = BuildConfig.ACCOUNTKIT_COUNTRY_WHITE_LIST;
            AccountKitConfiguration.AccountKitConfigurationBuilder configurationBuilder =
                    new AccountKitConfiguration.AccountKitConfigurationBuilder(
                            LoginType.PHONE, AccountKitActivity.ResponseType.CODE)
                            .setFacebookNotificationsEnabled(true)
                            .setSMSWhitelist(smsWhiteList)
                            .setDefaultCountryCode("ID");
            intent.putExtra(
                    AccountKitActivity.ACCOUNT_KIT_ACTIVITY_CONFIGURATION,
                    configurationBuilder.build());
            activity.startActivityForResult(intent, LOGIN_REQUEST_CODE);
        }
    }

    public void logout() {
        if(userPresenter==null)return;
        userPresenter.logout(ApiActions.LOGOUT);
    }

    public void logoutClearData() {
        if(Preference.getInstance()==null)return;
        Preference.getInstance().setToken(null);
        Preference.getInstance().setUser(null);
        AccountKitController.logOut();
    }

    public boolean handleActivityResult(final int requestCode, final int resultCode, final Intent data) {
        if (requestCode == LOGIN_REQUEST_CODE) { // confirm that this response matches your request
            isLoginShown = false;

            if (data != null) {
                AccountKitLoginResult loginResult = data.getParcelableExtra(AccountKitLoginResult.RESULT_KEY);
                if(loginResult==null)return false;
                if (loginResult.getError() != null) {
                    AccountKitError error = loginResult.getError();
                    LogUtil.e(error.toString());

                    TrackUtil.logEvent("login_mgr_accountkit_fail_" + error.toString());
                    if (viewDelegate != null && viewDelegate.getLoading() != null) {
                        viewDelegate.getLoading().showError(ApiActions.LOGIN_TOKEN, null, error.getUserFacingMessage());
                    }
                    userPresenter.logout(ApiActions.LOGOUT);
                } else if (loginResult.wasCancelled()) {
                    TrackUtil.logEvent("login_mgr_accountkit_cancel");
                    userPresenter.logout(ApiActions.LOGOUT);
                } else {
                    TrackUtil.logEvent("login_mgr_accountkit_success");
                    // 1. get authorization code
                    String authorizationCode = loginResult.getAuthorizationCode();
                    // 2. ask for token
                    userPresenter.loginByAccountKitAuthCode(ApiActions.LOGIN_TOKEN, authorizationCode);
                }
            }
        }
        return true;
    }

    @Override
    public void onApiResult(String action, Object result) {
        if (ApiActions.LOGIN_TOKEN.equals(action)) {
            UserBean userBean = (UserBean) result;
            TrackUtil.logEvent("login_mgr_user_fetch_success");
            if (userBean != null) {
                Preference.getInstance().setUser(userBean);

                viewDelegate.onApiResult(action, userBean);
                // perform next action
                // TODO temporarily put next action here, can be more generic in the future
                if (BusAction.SHOW_WITHDRAW.equals(loginNextAction)) {
                    WithdrawActivity.actionStart(MainApplication.getContext(), true);
                }
            }
        } else if (ApiActions.LOGOUT.equals(action)) {
            logoutClearData();
            viewDelegate.onApiResult(action, result);
        }
    }

    @Override
    public ILoading getLoading() {
        return viewDelegate.getLoading();
    }

    @Override
    public ILifeCycle getLifeCycle() {
        return viewDelegate.getLifeCycle();
    }

    public interface AccountKitLoginResultListener {
        void onSuccess(String authorizationCode);

        void onCancel();

        void onError(String message);
    }
}
