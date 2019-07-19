package com.id.cash.presenter;

import com.trello.rxlifecycle2.LifecycleTransformer;

import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import com.id.cash.module.starwin.common.base.IView;
import com.id.cash.network.ApiService;
import com.id.cash.network.PortalApi;

/**
 * Created by linchen on 2018/5/21.
 */

public class BasePresenter<T extends IView> {
    public T view;
    protected PortalApi api = ApiService.getInstance().getPortalApi();
    protected CompositeDisposable disposables;

    BasePresenter(T view) {
        this.view = view;
    }

    protected void register(Observable observable, DisposableObserver subscriber) {
        if (disposables == null || disposables.isDisposed()) {
            disposables = new CompositeDisposable();
        }

        LifecycleTransformer lifecycleTransformer = view.getLifeCycle().doBindToLifecycle();
        if (lifecycleTransformer != null) {
            observable = observable.compose(lifecycleTransformer);
        }

        observable
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(subscriber);

        disposables.add(subscriber);
    }

    public void destroy() {
        this.view = null;
        unsubscribe();
    }

    public void unsubscribe() {
        if (disposables != null && !this.disposables.isDisposed()) {
            disposables.clear();
        }
    }
}
