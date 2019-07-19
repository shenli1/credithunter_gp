package com.id.cash.widget.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.support.annotation.LayoutRes;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.id.cash.R;
import com.id.cash.widget.DebouncingOnClickListener;

/**
 * Created by linchen on 2018/6/9.
 */

public abstract class DialogBase {
    protected Activity activity;
    public Dialog dialog;

    abstract @LayoutRes
    int getContentView();

    protected View btnCancel;
    protected View btnOk;

    private OnButtonClickedListener onButtonClickedListener;

    public interface OnButtonClickedListener {
        void onOkClicked(DialogBase dialog);
        void onCancelClicked(DialogBase dialog);
    }

    // hide the constructor
    private DialogBase() {
    }

    protected DialogBase(Activity activity) {
        this.activity = activity;

        bindViews();
        bindEvents();
    }

    protected View bindViews() {
        View view = LayoutInflater.from(this.activity).inflate(getContentView(), null);
        btnCancel = view.findViewById(R.id.btn_cancel);
        btnOk = view.findViewById(R.id.btn_ok);

        dialog = new Dialog(activity, R.style.PopupDialog_BottomIn);
        dialog.setContentView(view);
        dialog.setCanceledOnTouchOutside(shouldCancelOnTouchOutside());
        Window window = dialog.getWindow();
        window.setGravity(Gravity.CENTER);
        WindowManager.LayoutParams attributes = window.getAttributes();
        attributes.x = 0;
        attributes.y = 0;
        window.setAttributes(attributes);

        return view;
    }

    protected void bindEvents() {
        if (btnCancel != null) {
            btnCancel.setOnClickListener(new DebouncingOnClickListener() {
                @Override
                public void doClick(View v) {
                    onCancel();
                }
            });
        }

        if (btnOk != null) {
            btnOk.setOnClickListener(new DebouncingOnClickListener() {
                @Override
                public void doClick(View v) {
                    onOk();
                }
            });
        }
    }

    public void show() {
        if (!dialog.isShowing()) {
            dialog.show();
        }
    }

    public void setOnButtonClickedListener(OnButtonClickedListener onButtonClickedListener) {
        this.onButtonClickedListener = onButtonClickedListener;
    }

    protected boolean shouldCancelOnTouchOutside() {
        return false;
    }

    protected void onCancel() {
        dialog.dismiss();

        if (onButtonClickedListener != null) {
            onButtonClickedListener.onCancelClicked(this);
        }
    }

    protected void onOk() {
        dialog.dismiss();

        if (onButtonClickedListener != null) {
            onButtonClickedListener.onOkClicked(this);
        }
    }
}
