package com.id.cash.module.starwin.common.base;

import android.app.IntentService;
import android.content.Context;

import com.id.cash.MainApplication;
import com.id.cash.presenter.BasePresenter;

/**
 * Created by linchen on 2018/6/8.
 */

public abstract class BaseIntentService<T extends BasePresenter> extends IntentService implements IView {
    protected Context context;
    protected T presenter;

    public abstract T createPresenter();

    public BaseIntentService(String name) {
        super(name);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        context = MainApplication.getContext();
        presenter = createPresenter();
    }
}
