package com.id.cash.widget.recyclerview;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import static android.support.v7.widget.RecyclerView.SCROLL_STATE_IDLE;

/**
 * Created by linchen on 2018/6/4.
 */

public abstract class RecyclerLoadMoreOnScrollListener extends RecyclerView.OnScrollListener {
    private int lastVisibleItemPosition;

    abstract protected void onMorePageDisplayed(boolean isMorePage);

    @Override
    public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
        super.onScrollStateChanged(recyclerView, newState);

        onMorePageDisplayed(lastVisibleItemPosition >= 7);
    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);

        LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
        lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition();
    }
}
