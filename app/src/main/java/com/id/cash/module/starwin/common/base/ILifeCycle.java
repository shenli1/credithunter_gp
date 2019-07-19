package com.id.cash.module.starwin.common.base;

import com.trello.rxlifecycle2.LifecycleTransformer;

/**
 * Created by linchen on 2018/5/28.
 */

public interface ILifeCycle {
    <T> LifecycleTransformer<T> doBindToLifecycle();
}
