package com.id.cash.widget;

import android.view.View;

/**
 * Created by linchen on 2018/6/8.
 */

public abstract class DebouncingOnClickListener implements View.OnClickListener {
    static boolean enabled = true;

    private static final Runnable ENABLE_AGAIN = new Runnable() {
        @Override public void run() {
            enabled = true;
        }
    };

    @Override public final void onClick(View v) {
        if (enabled) {
            enabled = false;
            v.post(ENABLE_AGAIN);
            doClick(v);
        }
    }

    public abstract void doClick(View v);
}
