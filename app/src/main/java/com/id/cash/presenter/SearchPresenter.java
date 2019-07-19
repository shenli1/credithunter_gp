package com.id.cash.presenter;

import java.util.List;

import com.id.cash.bean.ApiReturn;
import com.id.cash.bean.CashLoanBean;
import com.id.cash.bean.ListResultBean;
import com.id.cash.common.Preference;
import com.id.cash.module.starwin.common.base.IView;
import com.id.cash.network.RestApiSubscriber;

/**
 * Created by linchen on 2018/5/26.
 */

public class SearchPresenter extends BasePresenter<IView> {
    public SearchPresenter(IView view) {
        super(view);
    }

    public void loadHistory(String action) {
        List<String> searchHistory = Preference.getInstance().getSearchHistory();
        if (view != null) {
            view.onApiResult(action, searchHistory);
        }
    }

    public void loadSearch(String action, String keyword) {
        Preference.getInstance().addToSearchHistory(keyword);

        if (view != null) {
            view.getLoading().showLoading();
            register(api.search(keyword),
                    new RestApiSubscriber<ListResultBean<CashLoanBean>>() {
                        @Override
                        public void onFinish(String code, String message) {
                            // the response is ignored, no UI
                            if (view != null) {
                                view.getLoading().hideLoading();
                            }
                        }

                        @Override
                        public void onData(String code, String message, ApiReturn<ListResultBean<CashLoanBean>> data) {
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

    public void loadFilter(String action, String filterId, String current, String size) {
        if (view != null) {
            view.getLoading().showLoading();
            register(api.filter(filterId, current, size),
                    new RestApiSubscriber<ListResultBean<CashLoanBean>>() {
                        @Override
                        public void onFinish(String code, String message) {
                            // the response is ignored, no UI
                            if (view != null) {
                                view.getLoading().hideLoading();
                            }
                        }

                        @Override
                        public void onData(String code, String message, ApiReturn<ListResultBean<CashLoanBean>> data) {
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

    public void clearHistory(String action) {
        Preference.getInstance().clearSearchHistory();
        List<String> searchHistory = Preference.getInstance().getSearchHistory();
        if (view != null) {
            view.onApiResult(action, searchHistory);
        }
    }
}
