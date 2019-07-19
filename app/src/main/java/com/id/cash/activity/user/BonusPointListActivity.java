package com.id.cash.activity.user;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.scwang.smartrefresh.layout.SmartRefreshLayout;

import com.id.cash.R;
import com.id.cash.adapter.BonusPointAdapter;
import com.id.cash.bean.BonusPointBean;
import com.id.cash.module.starwin.common.base.BaseActivity;
import com.id.cash.network.ApiActions;
import com.id.cash.presenter.BonusPointPresenter;
import com.id.cash.widget.NetworkLoadingLayout;

/**
 * Created by linchen on 2018/5/28.
 */

public class BonusPointListActivity extends BaseActivity<BonusPointPresenter> implements NetworkLoadingLayout.OnRefreshCallback {
    private RecyclerView recyclerView;
    private BonusPointAdapter adapter;
    private NetworkLoadingLayout layoutContainer;
    private SmartRefreshLayout refreshLayout;

    public static void actionStart(Activity activity) {
        Intent intent = new Intent(activity, BonusPointListActivity.class);
        activity.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        activity.startActivity(intent);
    }

    @Override
    protected BonusPointPresenter createPresenter() {
        return new BonusPointPresenter(this);
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_bonuspoint_list;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        loadData();
    }

    @Override
    protected void bindViews() {
        super.bindViews();

        tvTitle.setText(getString(R.string.bonuspoint_list_title));
        layoutContainer = findViewById(R.id.layout_list_container);
        recyclerView = findViewById(R.id.rv_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        refreshLayout = findViewById(R.id.layout_refresh);

        adapter = new BonusPointAdapter(this);
        recyclerView.setAdapter(adapter);

        layoutContainer.setMode(NetworkLoadingLayout.Mode.CONTENT);
    }

    @Override
    protected void bindEvents() {
        super.bindEvents();
        layoutContainer.setRefreshCallback(this);
        refreshLayout.setOnRefreshListener((refreshLayout) -> {
            loadData();
            refreshLayout.finishRefresh(50);
        });
    }

    private void loadData() {
        presenter.getBonusPointList(ApiActions.BONUSPOINT_LIST);
    }

    @Override
    public void onApiResult(String action, Object result) {
        if (ApiActions.BONUSPOINT_LIST.equals(action)) {
            onBonusPointList((BonusPointBean[])result);
        }
    }

    @Override
    public void onRefreshClicked() {
        loadData();
    }

    private void onBonusPointList(BonusPointBean[] result) {
        if (result != null) {
            adapter.setItemList(result);
        }
        if (adapter.getItemCount() == 0) {
            layoutContainer.setMode(NetworkLoadingLayout.Mode.NO_DATA);
        }
    }
}
