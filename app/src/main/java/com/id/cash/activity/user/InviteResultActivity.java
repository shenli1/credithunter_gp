package com.id.cash.activity.user;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.scwang.smartrefresh.layout.SmartRefreshLayout;

import com.id.cash.R;
import com.id.cash.adapter.InviteReferralAdapter;
import com.id.cash.bean.InviteReferralBean;
import com.id.cash.common.TrackUtil;
import com.id.cash.module.starwin.common.base.BaseActivity;
import com.id.cash.network.ApiActions;
import com.id.cash.presenter.SharePresenter;
import com.id.cash.widget.DebouncingOnClickListener;

/**
 * Created by linchen on 2018/5/29.
 */

public class InviteResultActivity extends BaseActivity<SharePresenter> {
    private InviteReferralAdapter adapter;
    private SmartRefreshLayout refreshLayout;
    private TextView tvTotalHead;
    private TextView tvTotalBonusPoint;

    public static void actionStart(Activity activity) {
        Intent intent = new Intent(activity, InviteResultActivity.class);
        activity.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        activity.startActivity(intent);
    }

    @Override
    protected SharePresenter createPresenter() {
        return new SharePresenter(this);
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_invite_result;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        loadData();
    }

    @Override
    protected void bindViews() {
        super.bindViews();
        RecyclerView recyclerView = findViewById(R.id.rv_invite_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        refreshLayout = findViewById(R.id.layout_refresh);

        adapter = new InviteReferralAdapter(this);
        recyclerView.setAdapter(adapter);

        tvTotalHead = findViewById(R.id.tv_invite_person_count);
        tvTotalBonusPoint = findViewById(R.id.tv_invite_bonuspoint_count);
    }

    @Override
    protected void bindEvents() {
        super.bindEvents();

        refreshLayout.setOnRefreshListener((refreshLayout) -> {
            loadData();
            refreshLayout.finishRefresh(50);
        });

        View btnInvite = findViewById(R.id.tv_invite);
        btnInvite.setOnClickListener(new DebouncingOnClickListener() {
            @Override
            public void doClick(View v) {
                onInviteClick();
            }
        });
    }

    private void loadData() {
        presenter.loadInviteList(ApiActions.INVITE_LIST);
    }

    @Override
    protected int getStatusBarDrawable() {
        return 0;
    }

    void onInviteClick() {
        TrackUtil.logEvent("invite_result_invite_click");
        InviteShareActivity.actionStart(this);
    }

    @Override
    public void onApiResult(String action, Object result) {
        if (ApiActions.INVITE_LIST.equals(action)) {
            onInviteList((InviteReferralBean[])result);
        }
    }

    private void onInviteList(InviteReferralBean[] result) {
        if (result != null) {
            adapter.setItemList(result);

            // calculate the total invite count
            if (result.length > 0) {
                tvTotalHead.setText(String.valueOf(result.length));
                tvTotalBonusPoint.setText(String.valueOf(result.length * result[0].getPoint()));
            }
        }
    }
}
