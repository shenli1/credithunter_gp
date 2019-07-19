package com.id.cash.widget.recyclerview;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by linchen on 2018/5/19.
 */

public abstract class BaseRecyclerViewAdapter<T, VH extends BaseRecyclerViewAdapter.SparseArrayViewHolder> extends RecyclerView.Adapter<VH> {
    protected OnItemClickListener onItemClickListener;
    protected OnItemLongClickListener onItemLongClickListener;
    protected List<T> itemList = new ArrayList<>();
    protected Context context;

    public static class SparseArrayViewHolder extends RecyclerView.ViewHolder {
        private final SparseArray<View> viewSparseArray = new SparseArray<>();

        public SparseArrayViewHolder(View view) {
            super(view);
        }

        @SuppressWarnings("unchecked")
        public <T extends View> T findViewById(int i) {
            T view = (T) this.viewSparseArray.get(i);
            if (view != null) {
                return view;
            }
            T viewById = this.itemView.findViewById(i);
            this.viewSparseArray.put(i, viewById);
            return viewById;
        }
    }

    public BaseRecyclerViewAdapter(Context context) {
        this.context = context;
    }

    protected View inflate(ViewGroup viewGroup, int i) {
        return inflate(viewGroup, i, false);
    }

    protected View inflate(ViewGroup viewGroup, int i, boolean attachToRoot) {
        return LayoutInflater.from(viewGroup.getContext()).inflate(i, viewGroup, attachToRoot);
    }

    public T get(int i) {
        return this.itemList.get(i);
    }

    public void clear() {
        if (this.itemList != null && this.itemList.size() > 0) {
            this.itemList.clear();
        }
    }

    @Override
    public final void onBindViewHolder(VH vh, int i) {
        T item = get(i);
        onBindViewData(vh, i, item);
        bindViewHolder(vh, item);
    }

    protected abstract void onBindViewData(VH vh, int i, T t);

    protected final void bindViewHolder(final VH vh, final T t) {
        if (this.onItemClickListener != null) {
            vh.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    BaseRecyclerViewAdapter.this.onItemClickListener.onClick((BaseRecyclerViewHolder) vh, view, t);
                }
            });
        }
        if (this.onItemLongClickListener != null) {
            vh.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    BaseRecyclerViewAdapter.this.onItemLongClickListener.onLongClick(view, t);
                    return true;
                }
            });
        }
    }

    public void setOnItemClickListner(OnItemClickListener<T> onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public void setItemList(List<T> list) {
        this.itemList = list;
        notifyDataSetChanged();
    }

    public void setItemList(T[] arr) {
        List<T> list;
        if (arr != null) {
            list = new ArrayList<>(Arrays.asList(arr));
        } else {
            list = new ArrayList<>();
        }
        this.setItemList(list);
    }

    public int getItemCount() {
        return this.itemList == null ? 0 : this.itemList.size();
    }
}
