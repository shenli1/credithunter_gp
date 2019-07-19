package com.id.cash.presenter;

import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.text.TextUtils;

import java.util.HashMap;

import io.reactivex.Flowable;
import io.reactivex.Observable;
import com.id.cash.MainApplication;
import com.id.cash.R;
import com.id.cash.bean.ApiReturn;
import com.id.cash.bean.BankCardBean;
import com.id.cash.bean.SubmitWithdrawResultBean;
import com.id.cash.bean.UserBean;
import com.id.cash.module.starwin.common.base.IView;
import com.id.cash.network.ApiActions;
import com.id.cash.network.RestApiSubscriber;

/**
 * Created by liubin on 2018/5/31.
 */

public class WithDrawPresenter extends BasePresenter<IView> {
    public WithDrawPresenter(IView view) {
        super(view);
    }

    public void loadBankcardSummaryInfo(String action) {
        view.getLoading().showLoading();
        register(Observable.zip(
                api.getUserInfo(),
                api.getGetBankCardInfo(),
                (userBeanApiReturn, bankCardBeanApiReturn) -> {
                    ApiReturn<BankcardInfoSummaryBean> fakeApiReturn = new ApiReturn<>();
                    BankcardInfoSummaryBean bean = new BankcardInfoSummaryBean(userBeanApiReturn.getData(), bankCardBeanApiReturn.getData());
                    fakeApiReturn.setData(bean);
                    return fakeApiReturn;
                }),
                new RestApiSubscriber<BankcardInfoSummaryBean>() {
                    @Override
                    public void onFinish(String code, String message) {
                        if (view != null) {
                            view.getLoading().hideLoading();
                        }
                    }

                    @Override
                    public void onData(String code, String message, ApiReturn<BankcardInfoSummaryBean> data) {
                        if (data != null && data.getData() != null) {
                            if (view != null) {
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
                }
        );
    }

    private @StringRes
    int validateWithdrawInfo(@NonNull BankCardBean bankCardBean) {
        int errMessageId = 0;
        if (TextUtils.isEmpty(bankCardBean.getUserName())) {
            errMessageId = R.string.withdraw_user_name_hint;
        } else if (TextUtils.isEmpty(bankCardBean.getBankName())) {
            errMessageId = R.string.withdraw_bank_name_hint;
        } else if (TextUtils.isEmpty(bankCardBean.getBankAccount())) {
            errMessageId = R.string.withdraw_bankcard_no_hint;
        }

        return errMessageId;
    }

    public void submitBankCardInfo(String action, BankCardBean bankCardBean) {
        view.getLoading().showLoading();

        @StringRes int errMessageId = validateWithdrawInfo(bankCardBean);
        if (errMessageId == 0) {
            register(api.submitBankCardInfo(bankCardBean.getUserName(), bankCardBean.getBankName(), bankCardBean.getBankAccount()),
                    new RestApiSubscriber<BankCardBean>() {
                        @Override
                        public void onFinish(String code, String message) {
                            if (view != null) {
                                view.getLoading().hideLoading();
                            }
                        }

                        @Override
                        public void onData(String code, String message, ApiReturn<BankCardBean> data) {
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
                    }
            );
        } else {
            view.getLoading().showError(null, null, MainApplication.getContext().getString(errMessageId));
        }
    }

    public void submitWithdraw(String action, BankCardBean bankCardBean, long amount) {
        view.getLoading().showLoading();

        register(api.submitWithdraw(bankCardBean.getId(), String.valueOf(amount)),
                new RestApiSubscriber<SubmitWithdrawResultBean>() {
                    @Override
                    public void onFinish(String code, String message) {
                        if (view != null) {
                            view.getLoading().hideLoading();
                        }
                    }

                    @Override
                    public void onData(String code, String message, ApiReturn<SubmitWithdrawResultBean> data) {
                        if (view != null) {
                            view.onApiResult(action, data);
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

    public class BankcardInfoSummaryBean {
        private UserBean userBean;
        private BankCardBean bankCardBean;

        public BankcardInfoSummaryBean(UserBean userBean, BankCardBean bankCardBean) {
            this.userBean = userBean;
            this.bankCardBean = bankCardBean;
        }

        public UserBean getUserBean() {
            return userBean;
        }

        public void setUserBean(UserBean userBean) {
            this.userBean = userBean;
        }

        public BankCardBean getBankCardBean() {
            return bankCardBean;
        }

        public void setBankCardBean(BankCardBean bankCardBean) {
            this.bankCardBean = bankCardBean;
        }

        @Override
        public String toString() {
            return "BankcardInfoSummaryBean{" +
                    "userBean=" + userBean +
                    ", bankCardBean=" + bankCardBean +
                    '}';
        }
    }
}
