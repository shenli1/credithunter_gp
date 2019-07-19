package com.id.cash.common;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import static android.view.inputmethod.InputMethodManager.HIDE_IMPLICIT_ONLY;

/**
 * Created by linchen on 2018/5/28.
 */

public class KeyBoardUtil {
    public static void hideKeyboard(Activity activity, View view) {
        ((InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE))
                .hideSoftInputFromWindow(view.getWindowToken(), HIDE_IMPLICIT_ONLY);
    }
}
