package com.id.cash.activity.user;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.id.cash.R;
import com.id.cash.common.TrackUtil;
import com.id.cash.module.starwin.common.base.BaseActivity;
import com.id.cash.presenter.AppPresenter;
import com.id.cash.widget.DebouncingOnClickListener;

public class WithdrawSubmitResultActivity extends BaseActivity<AppPresenter> {
    public static void actionStart(final Context context) {
        Intent intent = new Intent(context, WithdrawSubmitResultActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected AppPresenter createPresenter() {
        return new AppPresenter(this);
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_withdraw_submit_result;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void bindViews() {
        super.bindViews();

        // set title
        TextView tvTitle = findViewById(R.id.tv_actionbar_title);
        tvTitle.setText(R.string.withdraw_result_title);
    }

    @Override
    protected void bindEvents() {
        super.bindEvents();

        View view = findViewById(R.id.tv_withdraw_result_ok);
        view.setOnClickListener(new DebouncingOnClickListener() {
            @Override
            public void doClick(View v) {
                TrackUtil.logEvent("withdraw_result_ok_click");
                finish();
            }
        });
    }

    @Override
    public void onApiResult(String action, Object result) {
    }
}
