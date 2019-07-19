package com.id.cash.widget.recyclerview;

import android.database.DataSetObservable;
import android.database.DataSetObserver;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListAdapter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.id.cash.adapter.CommonViewHolder;
import com.id.cash.common.LogUtil;

/**
 * Created by linchen on 2018/6/2.
 */

public abstract class BaseReloadableRecyclerAdapter<T> extends RecyclerView.Adapter<CommonViewHolder> implements ListAdapter {
    private View header;
    private View footer;
    private List<T> itemList;
    private AdapterView.OnItemClickListener onItemClickListener;
    private final int layoutId;
    private final DataSetObservable dataSetObservable = new DataSetObservable();

    private int itemsAnimated = -1;

    public BaseReloadableRecyclerAdapter(@LayoutRes int layoutId) {
        setHasStableIds(false);
        this.itemList = new ArrayList();
        this.layoutId = layoutId;
    }

    protected abstract void bindViewHolder(CommonViewHolder viewHolder, T bean, int position);

    private void animateItemChange(CommonViewHolder viewHolder, int position) {
        if (this.itemsAnimated < position) {
            viewHolder.itemView.setAlpha(0.0f);
            viewHolder.itemView.animate().alpha(1.0f).start();
            itemsAnimated = position;
        }
    }

    public BaseReloadableRecyclerAdapter<T> setItems(Collection<T> collection) {
        itemList.clear();
        itemList.addAll(collection);
        notifyDataSetChanged();
        notifyDataObservable();
        this.itemsAnimated = -1;
        return this;
    }

    public BaseReloadableRecyclerAdapter<T> refreshData(Collection<T> collection) {
        itemList.clear();
        itemList.addAll(collection);
        notifyDataSetChanged();
        notifyDataObservable();
        return this;
    }

    public BaseReloadableRecyclerAdapter<T> addData(Collection<T> collection) {
        itemList.addAll(collection);
        notifyDataSetChanged();
        notifyDataObservable();
        return this;
    }

    private void notifyDataObservable() {
        dataSetObservable.notifyChanged();
    }

    protected CommonViewHolder inflateViewHolder(ViewGroup parent, int viewType) {
        if (header != null && viewType == 0) {
            return new CommonViewHolder(header, onItemClickListener);
        }

        if (footer != null && viewType == 2) {
            return new CommonViewHolder(footer, onItemClickListener);
        }

        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        return new CommonViewHolder(layoutInflater.inflate(layoutId, parent, false),
                onItemClickListener);
    }

    public int getLayoutPosition(RecyclerView.ViewHolder viewHolder) {
        int layoutPosition = viewHolder.getLayoutPosition();
        return this.header == null ? layoutPosition : layoutPosition - 1;
    }

    public BaseReloadableRecyclerAdapter<T> setOnItemClickListener(AdapterView.OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
        return this;
    }

    public void setHeaderView(View view) {
        this.header = view;
        notifyItemInserted(0);
    }

    public void setFooterView(View view) {
        this.footer = view;
        notifyItemInserted(getItemCount() - 1);
    }

    private void bind(CommonViewHolder viewHolder, int position) {
        int viewType = getItemViewType(position);
        // not header & footer
        if (viewType != 0 && viewType != 2) {
            position = getLayoutPosition(viewHolder);
            bindViewHolder(viewHolder, itemList.get(position), position);
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        int viewType = getItemViewType(position);
        LogUtil.d("position: " + position + "  viewType: " + viewType);
        if (viewType == 0) {
            return header;
        } else if (viewType == 2) {
            return footer;
        }

        CommonViewHolder viewHolder;
        if (convertView != null) {
            viewHolder = (CommonViewHolder) convertView.getTag();
        } else {
            viewHolder = createViewHolder(parent, getItemViewType(position));
            convertView = viewHolder.itemView;
            convertView.setTag(viewHolder);
        }

        bindViewHolder(viewHolder, position);
        animateItemChange(viewHolder, position);
        return convertView;
    }

    @Override
    public Object getItem(int position) {
        if (header != null) {
            position--;
        }

        if (position >= 0 && position < itemList.size()) {
            return itemList.get(position);
        }
        return null;
    }

    // get item count without header/footer
    public int getRealItemCount() {
        return itemList.size();
    }

    @Override
    public int getCount() {
        return getItemCount();
    }

    @Override
    public int getItemCount() {
        int count = itemList.size();
        if (header != null) {
            count++;
        }

        if (footer != null) {
            count++;
        }
        return count;
    }

    @Override
    public int getItemViewType(int position) {
        if (this.header != null && position == 0) {
            return 0;
        } else if (this.footer != null && (position == (getCount() - 1))) {
            return 2;
        }

        return 1;
    }

    @Override
    public boolean areAllItemsEnabled() {
        return true;
    }

    @NonNull
    @Override
    public CommonViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return inflateViewHolder(parent, viewType);
    }

    @Override
    public void onBindViewHolder(@NonNull CommonViewHolder holder, int position) {
        bind(holder, position);
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public boolean isEnabled(int position) {
        return true;
    }

    @Override
    public boolean isEmpty() {
        return getCount() == 0;
    }

    @Override
    public void registerDataSetObserver(DataSetObserver dataSetObserver) {
        this.dataSetObservable.registerObserver(dataSetObserver);
    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver dataSetObserver) {
        this.dataSetObservable.unregisterObserver(dataSetObserver);
    }
}
