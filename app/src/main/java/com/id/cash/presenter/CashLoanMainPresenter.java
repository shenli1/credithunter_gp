package com.id.cash.presenter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import com.id.cash.bean.ApiReturn;
import com.id.cash.bean.BannerBean;
import com.id.cash.bean.CashLoanBean;
import com.id.cash.bean.FilterBean;
import com.id.cash.bean.ListResultBean;
import com.id.cash.module.starwin.common.base.IView;
import com.id.cash.module.starwin.harvester.InstalledAppManager;
import com.id.cash.network.ApiActions;
import com.id.cash.network.RestApiSubscriber;

/**
 * Created by linchen on 2018/6/1.
 */

public class CashLoanMainPresenter extends BasePresenter<IView> {
    public CashLoanMainPresenter(IView view) {
        super(view);
    }

    public void loadBanner(String action) {
        register(api.getBanner(),
                new RestApiSubscriber<BannerBean[]>() {
                    @Override
                    public void onFinish(String code, String message) {
                    }

                    @Override
                    public void onData(String code, String message, ApiReturn<BannerBean[]> data) {
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
    }

    public void loadFilters(String action) {
        register(api.getFilters(0, 100, "priority"),
                new RestApiSubscriber<ListResultBean<FilterBean>>() {
                    @Override
                    public void onFinish(String code, String message) {
                    }

                    @Override
                    public void onData(String code, String message, ApiReturn<ListResultBean<FilterBean>> data) {
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

    //  1. fetch the supported app list
    //  2. from the app list, check which ones are installed
    private Observable<ArrayList<String>> installedPackageObservable() {
        // 2. if no cache:
        return api.getPackageList()
                // i. fetch the supported app list
                .map((apiReturn -> apiReturn.getData()))
                // ii. from the app list, check which ones are installed
                .flatMap((cashLoanBeans) -> InstalledAppManager.appList(cashLoanBeans))
                .map((cashLoanBeans) -> {
                    ArrayList<String> uuids = new ArrayList<>();
                    for (CashLoanBean bean : cashLoanBeans) {
                        uuids.add(bean.getUuid());
                    }
                    return uuids;
                });
    }

    public void loadCashLoanList(String action, int start) {
        view.getLoading().showLoading();
        Observable<ApiReturn<ListResultBean<CashLoanBean>>> observable = installedPackageObservable()
                .flatMap((uuids) -> {
                    HashMap<String, Object> body = new HashMap<String, Object>() {{
                        put("uuids", uuids);
                    }};
                    return api.getCustomizedCashLoanList(start, 10, "priority", body);
                });

        register(observable,
                new RestApiSubscriber<ListResultBean<CashLoanBean>>() {
                    @Override
                    public void onFinish(String code, String message) {
                    }

                    @Override
                    public void onData(String code, String message, ApiReturn<ListResultBean<CashLoanBean>> data) {
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

    private RestApiSubscriber<String[]> subscriber;

    public void startMarqueeDataFetch() {
        // fetch every 5 mins
        // delay 2 seconds to avoid initial loading stress
        Observable<ApiReturn<String[]>> timedCall = Observable.interval(2, 300, TimeUnit.SECONDS)
                .flatMap((v) -> api.getLatestEvents());

        if (subscriber != null) {
            subscriber.dispose();
        }
        subscriber =
                new RestApiSubscriber<String[]>() {
                    @Override
                    public void onFinish(String code, String message) {

                    }

                    @Override
                    public void onData(String code, String message, ApiReturn<String[]> data) {
                        if (view != null) {
                            view.onApiResult(ApiActions.MARQUEE, Arrays.asList(data.getData()));
                        }
                    }

                    @Override
                    public void onError(String code, String message) {
                    }
                };
        register(timedCall, subscriber);

    }
}
