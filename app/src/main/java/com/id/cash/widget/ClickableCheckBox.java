package com.id.cash.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.AppCompatCheckBox;
import android.util.AttributeSet;

import com.id.cash.R;

public class ClickableCheckBox extends AppCompatCheckBox {
    private boolean isCheckAllowed = true;
    Drawable notAllowedDrawable;
    Drawable allowedDrawable;

    public ClickableCheckBox(Context context) {
        this(context, null);
    }

    public ClickableCheckBox(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.checkboxStyle);
    }

    public ClickableCheckBox(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ClickableCheckBox);
        try {
            notAllowedDrawable = typedArray.getDrawable(R.styleable.ClickableCheckBox_ccbNotAllowedBackground);
            allowedDrawable = typedArray.getDrawable(R.styleable.ClickableCheckBox_allowedBackground);

            setButtonDrawable(allowedDrawable);
        } finally {
            if (typedArray != null) {
                typedArray.recycle();
            }
        }
    }

    public boolean isCheckAllowed() {
        return isCheckAllowed;
    }

    public void setCheckAllowed(boolean checkAllowed) {
        isCheckAllowed = checkAllowed;

        setButtonDrawable(checkAllowed ? allowedDrawable : notAllowedDrawable);
    }

    @Override
    public void setChecked(boolean checked) {
        if (isCheckAllowed) {
            super.setChecked(checked);
        }
    }
}
