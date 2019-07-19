package com.id.cash.widget;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.id.cash.R;

import static android.view.Gravity.AXIS_SPECIFIED;
import static android.view.Gravity.CENTER_VERTICAL;

/**
 * Created by linchen on 2018/4/29.
 */

public class ProgressDialog extends Dialog {
    public static ProgressDialog getInstance(final Context context) {
        ProgressDialog instance = new ProgressDialog(context, R.style.ProgressDialog);
        instance.getWindow().getAttributes().gravity = AXIS_SPECIFIED + CENTER_VERTICAL;
        instance.setCanceledOnTouchOutside(false);
        return instance;
    }

    private ProgressDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);

        initialize(context);
    }

    public void setMessage(String message) {
        TextView textView = findViewById(R.id.tv_loading_message);
        TextView errorTextView = findViewById(R.id.tv_error_message);

        if (textView != null) {
            if (TextUtils.isEmpty(message)) {
                message = getContext().getString(R.string.default_loading_message);
            }
            textView.setText(message);

            textView.setVisibility(View.VISIBLE);
        }
        if (errorTextView != null) {
            errorTextView.setVisibility(View.GONE);
        }
    }

    public void setErrorMessage(String message) {
        if (TextUtils.isEmpty(message)) {
            return;
        }
        TextView textView = findViewById(R.id.tv_loading_message);
        TextView errorTextView = findViewById(R.id.tv_error_message);

        if (textView != null) {
            textView.setVisibility(View.GONE);
        }
        if (errorTextView != null) {
            errorTextView.setText(message);
            errorTextView.setVisibility(View.VISIBLE);
        }

        // error message disappears after some time
        new Handler(Looper.getMainLooper())
                .postDelayed(() -> {
                    if (this.isShowing()) {
                        this.dismiss();
                    }
                }, 3000);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
    }

    private void initialize(final Context context) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.common_progress, null, false);
        setContentView(view);
        setMessage(null);
    }
}


