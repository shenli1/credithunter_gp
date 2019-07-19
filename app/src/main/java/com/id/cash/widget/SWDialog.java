package com.id.cash.widget;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.PixelFormat;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;

/**
 * Created by linchen on 2018/4/26.
 */

public class SWDialog implements DialogInterface {
    private View view;
    private WindowManager.LayoutParams layoutParams;
    private boolean isShowing = false;
    private WindowManager windowManager;
    private DialogInterface.OnDismissListener onDismissListener;

    public SWDialog(Context ctx) {
        this.windowManager = (WindowManager) ctx.getSystemService(Context.WINDOW_SERVICE);
    }

    public SWDialog setView(View view) {
        this.view = view;
        return this;
    }

    public SWDialog setLayoutParams(WindowManager.LayoutParams layoutParams) {
        this.layoutParams = layoutParams;
        return this;
    }

    public OnDismissListener getOnDismissListener() {
        return onDismissListener;
    }

    public void setOnDismissListener(OnDismissListener onDismissListener) {
        this.onDismissListener = onDismissListener;
    }

    void show(int x, int y, int gravity) {
        if (isShowing) {
            dismiss();
        }

        if (layoutParams == null) {
            layoutParams = new WindowManager.LayoutParams(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);
            layoutParams.flags = WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION |
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS |
                    WindowManager.LayoutParams.FLAG_DIM_BEHIND |
                    WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD;
            layoutParams.type = WindowManager.LayoutParams.TYPE_APPLICATION;
            layoutParams.dimAmount = 0.5f;
            layoutParams.format = PixelFormat.TRANSLUCENT;
        }

        layoutParams.gravity = gravity;
        if (gravity == Gravity.NO_GRAVITY) {
            layoutParams.x = x;
            layoutParams.y = y;
        }

        windowManager.addView(view, layoutParams);
        isShowing = true;
    }

    @Override
    public void cancel() {
    }

    @Override
    public void dismiss() {
        if (isShowing) {
            if (onDismissListener != null) {
                onDismissListener.onDismiss(this);
                ;
            }
            isShowing = false;
            windowManager.removeView(view);
        }
    }
}
