package com.id.cash.widget.recyclerview;

import android.view.View;

/**
 * Created by linchen on 2018/5/19.
 */

public interface OnItemClickListener<T> {
    void onClick(BaseRecyclerViewHolder baseRecyclerViewHolder, View view, T t);
}
