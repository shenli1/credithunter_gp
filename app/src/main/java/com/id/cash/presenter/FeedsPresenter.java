package com.id.cash.presenter;

import com.id.cash.bean.ApiReturn;
import com.id.cash.bean.ClientFeedsBean;
import com.id.cash.bean.FeedsBannerBean;
import com.id.cash.bean.FeedsBean;
import com.id.cash.bean.FilterBean;
import com.id.cash.bean.ListResultBean;
import com.id.cash.module.starwin.common.base.IView;
import com.id.cash.network.RestApiSubscriber;

/**
 * Created by linchen on 2018/7/19.
 */
public class FeedsPresenter extends BasePresenter<IView> {
    public FeedsPresenter(IView view) {
        super(view);
    }

    public void loadBanner(String action) {
        register(api.getFeedsBanner(),
                new RestApiSubscriber<FeedsBannerBean[]>() {
                    @Override
                    public void onFinish(String code, String message) {
                    }

                    @Override
                    public void onData(String code, String message, ApiReturn<FeedsBannerBean[]> data) {
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


    public void loadFeedsList(String action, int start) {
        view.getLoading().showLoading();

        register(api.getFeedsList(start, 10),
                new RestApiSubscriber<ListResultBean<FeedsBean>>() {
                    @Override
                    public void onFinish(String code, String message) {
                        if (view != null) {
                            view.getLoading().hideLoading();
                        }
                    }

                    @Override
                    public void onData(String code, String message, ApiReturn<ListResultBean<FeedsBean>> data) {
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


    public void getFeeds(String action, String feedsId) {
        register(api.getFeeds(feedsId),
                new RestApiSubscriber<ListResultBean<FeedsBean>>() {
                    @Override
                    public void onFinish(String code, String message) {
                    }

                    @Override
                    public void onData(String code, String message, ApiReturn<ListResultBean<FeedsBean>> data) {
                        if (view != null) {
                            view.onApiResult(action, data.getData());
                        }
                    }

                    @Override
                    public void onError(String code, String message) {
                    }
                });
    }

    public void toggleLike(String action, String feedsId) {
        register(api.toggleLike(feedsId),
                new RestApiSubscriber<ClientFeedsBean>() {
                    @Override
                    public void onFinish(String code, String message) {

                    }

                    @Override
                    public void onData(String code, String message, ApiReturn<ClientFeedsBean> data) {
                        view.onApiResult(action, data.getData());
                    }

                    @Override
                    public void onError(String code, String message) {
                    }
                });
    }

    public void getClientFeeds(String action, String feedsId) {
        register(api.getClientFeeds(feedsId),
                new RestApiSubscriber<ClientFeedsBean>() {
                    @Override
                    public void onFinish(String code, String message) {

                    }

                    @Override
                    public void onData(String code, String message, ApiReturn<ClientFeedsBean> data) {
                        view.onApiResult(action, data.getData());
                    }

                    @Override
                    public void onError(String code, String message) {
                    }
                });
    }

    public void getShareData(String action, String feedsId) {
        view.getLoading().showLoading();

        register(api.getFeedsShareInfo(SharePresenter.ShareChannel.FACEBOOK.toString(),
                SharePresenter.ShareMethod.SHARING.toString(),
                feedsId),
                new RestApiSubscriber<ClientFeedsBean>() {
                    @Override
                    public void onFinish(String code, String message) {
                        if (view != null) {
                            view.getLoading().hideLoading();
                        }
                    }

                    @Override
                    public void onData(String code, String message, ApiReturn<ClientFeedsBean> data) {
                        view.onApiResult(action, data.getData());
                    }

                    @Override
                    public void onError(String code, String message) {
                        if (view != null) {
                            view.getLoading().showError(action, code, message);
                        }
                    }
                });
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
}
