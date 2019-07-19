package com.id.cash.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.id.cash.R;

/**
 * Created by linchen on 2018/5/29.
 */

public class InviteShareButton extends LinearLayout {
    private ImageView ivIcon;
    private TextView tvTitle;
    private ImageView ivCheck;

    private Drawable icon;
    private Drawable iconSelected;

    public InviteShareButton(Context context) {
        this(context, null);
    }

    public InviteShareButton(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public InviteShareButton(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        setFocusable(true);
        setClickable(true);

        LayoutInflater inflater = LayoutInflater.from(context);
        inflater.inflate(R.layout.sub_share_button, this, true);

        ivIcon = findViewById(R.id.iv_share_button_icon);
        tvTitle = findViewById(R.id.tv_share_button_title);
        ivCheck = findViewById(R.id.iv_share_button_select);

        String title = "";
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.InviteShareButton);
        if (typedArray != null) {
            try {
                icon = typedArray.getDrawable(R.styleable.InviteShareButton_isbIcon);
                iconSelected = typedArray.getDrawable(R.styleable.InviteShareButton_isbIconSelected);
                title = typedArray.getString(R.styleable.InviteShareButton_isbTitle);

                ivIcon.setImageDrawable(icon);
                tvTitle.setText(title);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                typedArray.recycle();
            }
        }

        bindEvent();
    }

    void bindEvent() {
        setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                if (action == MotionEvent.ACTION_DOWN) {
                    updateUI(true);
                } else if (action == MotionEvent.ACTION_UP) {
                    v.performClick();
                    updateUI(false);
                }
                return false;
            }
        });
    }

    private void updateUI(boolean pressed) {
        if (pressed) {
            ivIcon.setImageDrawable(iconSelected);
            ivCheck.setVisibility(VISIBLE);
            tvTitle.setTextColor(getResources().getColor(R.color.button_text_highlight));
        } else {
            ivIcon.setImageDrawable(icon);
            ivCheck.setVisibility(INVISIBLE);
            tvTitle.setTextColor(getResources().getColor(R.color.text_white));
        }
    }
}
