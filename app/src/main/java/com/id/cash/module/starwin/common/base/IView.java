package com.id.cash.module.starwin.common.base;

/**
 * Created by linchen on 2018/5/12.
 */

public interface IView {
    ILoading getLoading();

    ILifeCycle getLifeCycle();

    void onApiResult(String action, Object result);
}
