package com.id.cash.network;

import android.text.TextUtils;

import com.hwangjr.rxbus.RxBus;

import java.util.HashSet;
import java.util.Set;

import io.reactivex.observers.DisposableObserver;
import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.HttpException;
import retrofit2.Response;
import retrofit2.converter.jackson.JacksonConverterFactory;
import com.id.cash.bean.ApiErrorType;
import com.id.cash.bean.ApiReturn;
import com.id.cash.bean.BonusPointTaskResultBean;
import com.id.cash.bean.IgnoredDataBean;
import com.id.cash.common.AppUpgradeManager;
import com.id.cash.common.LogUtil;
import com.id.cash.module.starwin.accountkit.LoginEvent;
import com.id.cash.module.starwin.common.bus.BusAction;

import static com.id.cash.module.starwin.common.bus.BusAction.LOGIN;

/**
 * Created by linchen on 2018/5/21.
 */

public abstract class RestApiSubscriber<T> extends DisposableObserver<ApiReturn<T>> {
    public abstract void onFinish(String code, String message);

    public abstract void onData(String code, String message, ApiReturn<T> data);

    public abstract void onError(String code, String message);

    private boolean shouldShowLogin = true;

    public RestApiSubscriber() {
        this(true);
    }

    public RestApiSubscriber(boolean shouldShowLogin) {
        this.shouldShowLogin = shouldShowLogin;
    }

    public void onResult(ApiReturn<T> apiReturn) {
        try {
            String code = apiReturn.getCode();
            String message = apiReturn.getMessage();
            String status = apiReturn.getStatus();
            // null is sent by android code, see the usage of BonusPointSummaryBean
            if (status == null || ApiReturn.STATUS_OK.equals(status)) {
                onData(code, apiReturn.getMessage(), apiReturn);
                onFinish(code, message);

                BonusPointTaskResultBean dailyActiveBean = apiReturn.getDailyActive();
                if (dailyActiveBean != null && dailyActiveBean.getBonusPoint() > 0) {
                    RxBus.get().post(BusAction.SHOW_BONUSPOINT_ANIMATION, dailyActiveBean);
                }

                // handle optional update (if appVersionBean is piggy back)
                AppUpgradeManager.getInstance().check(apiReturn.getAppVersion());
            } else {
                onError(code, message);
            }
        } catch (Throwable e) {
            onError(null, e.getLocalizedMessage());
            // do not crash if user's onData bails
            LogUtil.e(e);
        }
    }

    @Override
    public void onNext(ApiReturn<T> apiReturn) {
        onResult(apiReturn);
    }

    @Override
    public void onError(Throwable t) {
        if (t instanceof HttpException) {
            try {
                // handle known http exception: 401: unauthorized
                HttpException exception = (HttpException) t;
                if (handleKnownHttpException(exception)) {
                    return;
                }

                Response response = exception.response();
                ResponseBody errorBody = response.errorBody();
                if (errorBody != null) {
                    JacksonConverterFactory factory = JacksonConverterFactory.create();
                    Converter<ResponseBody, ?> converter =
                            factory.responseBodyConverter(ApiReturn.class, null, null);
                    ApiReturn<IgnoredDataBean> ret = (ApiReturn<IgnoredDataBean>) converter.convert(errorBody);
                    if (ret != null) {
                        handleKnownError(ret);
                        onError(ret.getCode(), ret.getMessage());
                        return;
                    }
                }
            } catch (Exception ex) {
                LogUtil.e(ex);
            }
        }

        // handle network exception
        if (t != null) {
            String message = t.getMessage();
            // on some rare unknown occasions, t.getMessage is null, which causes crash
            if (message != null && (message.contains("java.net.ConnectException") ||
                    message.contains("java.net.SocketTimeoutException") ||
                    message.contains("unexpected end of stream") ||
                    message.contains("Failed to connect to") ||
                    message.contains("stream was reset") ||
                    message.contains("Unable to resolve host"))) {
                onError("", "Kegagalan koneksi jaringan");
                return;
            }

            if (message != null) {
                onError("", message);
            } else {
                onError("", t.toString());
            }

            LogUtil.e(t);
        }
    }

    private boolean handleKnownHttpException(HttpException ex) {
        if (ex.code() == 401) {
            if (shouldShowLogin) {
                RxBus.get().post(LOGIN, new LoginEvent());
            }
            onError(null, ex.getLocalizedMessage());
            return true;
        }
        return false;
    }

    private void handleKnownError(ApiReturn apiReturn) {
        String code = apiReturn.getCode();
        if (TextUtils.isEmpty(code)) {
            return;
        }

        if (ApiErrorType.VERSION_NEED_UPDATE.equals(code)) {
            AppUpgradeManager.getInstance().check(apiReturn.getAppVersion());
            return;
        }

        // handle user/token related error
        Set<String> tokenErrors = new HashSet<String>() {{
            add(ApiErrorType.TOKEN_NOT_EXIST_OR_EXPIRED);
            add(ApiErrorType.USER_ID_NOT_EXIST);
        }};

        if (shouldShowLogin && tokenErrors.contains(code)) {
            RxBus.get().post(LOGIN, new LoginEvent());
        }
    }

    @Override
    public void onComplete() {

    }
}
