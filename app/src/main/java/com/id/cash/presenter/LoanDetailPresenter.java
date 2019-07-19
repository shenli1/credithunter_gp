package com.id.cash.presenter;

import com.id.cash.bean.ApiReturn;
import com.id.cash.bean.CashLoanProceduerBean;
import com.id.cash.bean.IgnoredDataBean;
import com.id.cash.module.starwin.common.base.IView;
import com.id.cash.network.ApiActions;
import com.id.cash.network.RestApiSubscriber;

/**
 * Created by linchen on 2018/5/21.
 */

public class LoanDetailPresenter extends BasePresenter<IView> {
    public LoanDetailPresenter(IView view) {
        super(view);
    }

    public void getLoanDetail(String action, String loanId) {
        view.getLoading().showLoading();
        register(api.getLoanDetail(loanId),
                new RestApiSubscriber() {
                    @Override
                    public void onFinish(String code, String message) {
                        if (view != null) {
                            view.getLoading().hideLoading();
                        }
                    }

                    @Override
                    public void onData(String code, String message, ApiReturn data) {
                        if (data != null && data.getData() != null) {
                            if (view != null) {
                                view.onApiResult(ApiActions.CASH_LOAN_DETAIL, data.getData());
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

    public void getLoanProcedure(String loanId) {
        register(api.getLoanProcedure(loanId),
                new RestApiSubscriber<CashLoanProceduerBean>() {
                    @Override
                    public void onFinish(String code, String message) {

                    }

                    @Override
                    public void onData(String code, String message, ApiReturn<CashLoanProceduerBean> data) {
                        if (data != null && data.getData() != null) {
                            view.onApiResult(ApiActions.CASH_LOAN_PROCEDURE, data.getData());
                        }
                    }

                    @Override
                    public void onError(String code, String message) {

                    }
                });
    }

    public void logApply(String loanId) {
        register(api.logApply(loanId),
                new RestApiSubscriber<IgnoredDataBean>() {
                    @Override
                    public void onFinish(String code, String message) {
                        // the response is ignored, no UI
                    }

                    @Override
                    public void onData(String code, String message, ApiReturn<IgnoredDataBean> data) {
                    }

                    @Override
                    public void onError(String code, String message) {
                    }
                });
    }
}
