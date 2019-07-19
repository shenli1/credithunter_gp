package com.id.cash.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ScrollView;

/**
 * Created by linchen on 2018/5/15.
 */

public class ObservableScrollView extends ScrollView {
    private OnScrollChanged mOnScrollChanged;

    public interface OnScrollChanged {
        void onScroll(int l, int t, int oldl, int oldt);
    }

    public ObservableScrollView(Context context) {
        super(context);
    }

    public ObservableScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ObservableScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);

        if (this.mOnScrollChanged != null) {
            this.mOnScrollChanged.onScroll(l, t, oldl, oldt);
        }
    }

    public void setOnScrollChanged(OnScrollChanged onScrollChanged) {
        this.mOnScrollChanged = onScrollChanged;
    }
}
