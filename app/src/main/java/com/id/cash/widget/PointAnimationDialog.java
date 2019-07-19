package com.id.cash.widget;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import java.util.ArrayList;

import com.id.cash.R;

/**
 * Created by linchen on 2018/5/25.
 */

public class PointAnimationDialog {
    private Activity context;
    public Dialog dialog;

    private PointAnimationDialog() {
    }

    public PointAnimationDialog(Activity context) {
        this.context = context;
    }

    public PointAnimationDialog initialize(int point) {
        View view = LayoutInflater.from(this.context).inflate(R.layout.dialog_point_animation, null);
        dialog = new Dialog(context, R.style.PopupDialog);
        dialog.setContentView(view);
        dialog.setCanceledOnTouchOutside(false);
        Window window = dialog.getWindow();
        window.setGravity(Gravity.CENTER);
        WindowManager.LayoutParams attributes = window.getAttributes();
        attributes.x = 0;
        attributes.y = 0;
        window.setAttributes(attributes);

        configDialogContent(view, point);
        return this;
    }

    public void show() {
        if (!dialog.isShowing()) {
            dialog.show();
        }
    }

    private void configDialogContent(View view, int point) {
        TextView textView = view.findViewById(R.id.tv_point);
        textView.setText("" + point + " RP");

        final ObjectAnimator alphaIn = ObjectAnimator.ofFloat(textView, "alpha", 0f, 1f).setDuration(800);

        ObjectAnimator alphaOut = ObjectAnimator.ofFloat(textView, "alpha", 1f, 0f).setDuration(600);
        alphaOut.setStartDelay(1700);
        final AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playSequentially(new ArrayList<Animator>() {{
            add(alphaIn);
            add(alphaOut);
        }});
        animatorSet.start();

        new Handler(Looper.getMainLooper())
                .postDelayed(() -> {
                    animatorSet.cancel();
                    if (context != null && context.isFinishing()) {
                        return;
                    }
                    this.dialog.dismiss();
                }, 3100);
    }
}
