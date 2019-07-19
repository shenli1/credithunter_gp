package com.id.cash.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import com.id.cash.R;

/**
 * Created by linchen on 2018/5/16.
 */

public class RefreshHeaderLayout extends LinearLayout {

    public RefreshHeaderLayout(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);

        LayoutInflater.from(context).inflate(R.layout.common_refresh_header, this);
    }
}
