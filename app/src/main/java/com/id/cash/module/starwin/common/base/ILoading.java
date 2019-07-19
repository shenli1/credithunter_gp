package com.id.cash.module.starwin.common.base;

/**
 * Created by linchen on 2018/5/28.
 */

public interface ILoading {
    void showLoading();
    void hideLoading();
    void showError(String action, String code, String message);
}
