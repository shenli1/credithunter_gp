package com.id.cash.widget.recyclerview;

import android.view.View;

/**
 * Created by linchen on 2018/5/19.
 */

public interface OnItemLongClickListener<T> {
    void onLongClick(View view, T t);
}
