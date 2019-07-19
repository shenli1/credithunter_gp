package com.id.cash.adapter;

import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.AdapterView;

/**
 * Created by linchen on 2018/6/2.
 */

public class CommonViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    private final AdapterView.OnItemClickListener onItemClickListener;

    public CommonViewHolder(View itemView, AdapterView.OnItemClickListener onItemClickListener) {
        super(itemView);
        this.onItemClickListener = onItemClickListener;
        itemView.setOnClickListener(this);
    }

    public <T extends View> T findViewById(@IdRes int i) {
        return i == 0 ? null : itemView.findViewById(i);
    }

    @Override
    public void onClick(View view) {
        if (onItemClickListener != null) {
            int position = getAdapterPosition();
            if (position >= 0) {
                onItemClickListener.onItemClick(null, view, position, getItemId());
            }
        }
    }
}
