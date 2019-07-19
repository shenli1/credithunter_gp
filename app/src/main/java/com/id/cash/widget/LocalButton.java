package com.id.cash.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatButton;
import android.util.AttributeSet;
import android.view.View;

import com.id.cash.R;

/**
 * Created by linchen on 2018/4/26.
 */

public class LocalButton extends AppCompatButton {
    private String actionName = "no name set";
    private boolean isRestrictDoubleClick = true;

    public LocalButton(Context context, AttributeSet attrSet) {
        super(context, attrSet);

        if (attrSet != null) {
            TypedArray attrs = context.obtainStyledAttributes(attrSet, R.styleable.LocalButton);
            if (attrs.hasValue(R.styleable.LocalTextView_tActionName)) {
                this.actionName = attrs.getString(R.styleable.LocalTextView_tActionName);
            }

            isRestrictDoubleClick = attrs.getBoolean(R.styleable.LocalButton_restrictClick, true);
            attrs.recycle();
        }
    }

    @Override
    public void setOnClickListener(@Nullable OnClickListener l) {
        super.setOnClickListener(l);
    }

}
