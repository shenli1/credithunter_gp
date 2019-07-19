package com.id.cash.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.ViewGroup;
import android.widget.TextView;

import com.id.cash.R;
import com.id.cash.bean.FilterBean;
import com.id.cash.widget.recyclerview.BaseRecyclerViewAdapter;
import com.id.cash.widget.recyclerview.BaseRecyclerViewHolder;

/**
 * Created by linchen on 2018/6/3.
 */

public class FilterAdapter extends BaseRecyclerViewAdapter<FilterBean, BaseRecyclerViewHolder> {
    public FilterAdapter(Context context) {
        super(context);
    }

    @NonNull
    @Override
    public BaseRecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new BaseRecyclerViewHolder(inflate(parent, R.layout.item_filter_tag));
    }

    protected void onBindViewData(BaseRecyclerViewHolder baseRecyclerViewHolder, int i, FilterBean bean) {
        TextView filterName = baseRecyclerViewHolder.findViewById(R.id.tv_filter_name);
        filterName.setText(bean.getName());
    }
}
