package com.id.cash.activity.product.cashloan;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.scwang.smartrefresh.layout.SmartRefreshLayout;

import com.id.cash.R;
import com.id.cash.adapter.SearchCashLoanResultAdapter;
import com.id.cash.bean.CashLoanBean;
import com.id.cash.bean.ListResultBean;
import com.id.cash.common.TrackUtil;
import com.id.cash.module.starwin.common.base.BaseFragment;
import com.id.cash.network.ApiActions;
import com.id.cash.presenter.SearchPresenter;
import com.id.cash.widget.NetworkLoadingLayout;
import com.id.cash.widget.recyclerview.BaseRecyclerViewHolder;
import com.id.cash.widget.recyclerview.OnItemClickListener;

/**
 * Created by linchen on 2018/5/28.
 */

public class SearchResultFragment extends BaseFragment<SearchPresenter> {
    private RecyclerView recyclerView;
    private SearchCashLoanResultAdapter adapter;
    private SmartRefreshLayout layoutRefresh;
    private NetworkLoadingLayout searchResultContainer;
    private NetworkLoadingLayout.OnRefreshCallback refreshCallback;

    @Override
    public SearchPresenter createPresenter() {
        return new SearchPresenter(this);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_search_result;
    }

    @Override
    protected void lazyLoad() {
        // according this judge, can prevent NRE
        if (!isPrepared || !isVisible) {
            return;
        }

        bindViews();
    }

    private void bindViews() {
        recyclerView = view.findViewById(R.id.rv_search_result);
        layoutRefresh = view.findViewById(R.id.layout_refresh);
        // we do not support load more or load refresh
        layoutRefresh.setEnableRefresh(false);
        searchResultContainer = view.findViewById(R.id.layout_search_result_container);

        adapter = new SearchCashLoanResultAdapter(myActivity);
        adapter.setOnItemClickListner(new OnItemClickListener<CashLoanBean>() {
            @Override
            public void onClick(BaseRecyclerViewHolder baseRecyclerViewHolder, View view, CashLoanBean cashLoanBean) {
                TrackUtil.logEvent("search_list_item_click");
                if (cashLoanBean != null) {
                    CashLoanDetailActivity.actionStart(myActivity, cashLoanBean.getUuid());
                }
            }
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(myActivity));
        recyclerView.setAdapter(adapter);

        searchResultContainer.setRefreshCallback(() -> {
            if (refreshCallback != null) {
                refreshCallback.onRefreshClicked();
            }
        });
    }

    public void search(boolean isRefresh, String keyword) {
        presenter.loadSearch(ApiActions.SEARCH, keyword);
        searchResultContainer.setMode(NetworkLoadingLayout.Mode.CONTENT);
    }

    public void filter(String filterId) {
        presenter.loadFilter(ApiActions.SEARCH, filterId, "1", "50");
        searchResultContainer.setMode(NetworkLoadingLayout.Mode.CONTENT);
    }

    public void setRefreshCallback(NetworkLoadingLayout.OnRefreshCallback refreshCallback) {
        this.refreshCallback = refreshCallback;
    }

    @Override
    public void onApiResult(String action, Object result) {
        if (ApiActions.SEARCH.equals(action)) {
            onSearchResult((ListResultBean<CashLoanBean>) result);
            layoutRefresh.finishRefresh();
        }
    }

    private void onSearchResult(ListResultBean<CashLoanBean> result) {
        // show no data UI when no data
        if (result != null) {
            adapter.setItemList(result.getRecords());
        }
        if (adapter.getItemCount() == 0) {
            searchResultContainer.setMode(NetworkLoadingLayout.Mode.NO_DATA);
        }
    }
}
