package com.id.cash.activity.product.cashloan;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;

import com.id.cash.R;
import com.id.cash.common.TrackUtil;
import com.id.cash.module.starwin.common.base.BaseActivity;
import com.id.cash.module.starwin.common.base.BaseFragment;
import com.id.cash.presenter.SearchPresenter;
import com.id.cash.widget.NetworkLoadingLayout;

/**
 * Created by linchen on 2018/5/28.
 */

public class FilterActivity extends BaseActivity<SearchPresenter> implements NetworkLoadingLayout.OnRefreshCallback, BaseFragment.OnFragmentCreateViewListener {
    private static final String FILTER_ID_TAG = "FILTER_ID_TAG";
    private static final String FILTER_NAME_TAG = "FILTER_NAME_TAG";
    private SearchResultFragment searchResultFragment;
    private String filterId;
    private String filterName;
    private boolean isLoaded;

    public static void actionStart(Activity activity, String filterId, String filterName) {
        Intent intent = new Intent(activity, FilterActivity.class);
        intent.putExtra(FILTER_ID_TAG, filterId);
        intent.putExtra(FILTER_NAME_TAG, filterName);
        activity.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        activity.startActivity(intent);
    }

    @Override
    protected SearchPresenter createPresenter() {
        return new SearchPresenter(this);
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_filter;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        filterId = getIntent().getStringExtra(FILTER_ID_TAG);
        filterName = getIntent().getStringExtra(FILTER_NAME_TAG);

        super.onCreate(savedInstanceState);

        if (!TextUtils.isEmpty(filterName)) {
            // firebase event can't contain space
            TrackUtil.logEvent(filterName.replaceAll("\\s+/g", ""));
        }
    }

    @Override
    protected void bindViews() {
        super.bindViews();

        if (!TextUtils.isEmpty(filterName)) {
            // firebase event can't contain space
            tvTitle.setText(filterName);
        }

        searchResultFragment = new SearchResultFragment();
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.add(R.id.layout_filter_content, searchResultFragment);
        transaction.commit();

        searchResultFragment.setUserVisibleHint(true);
    }

    @Override
    protected void bindEvents() {
        super.bindEvents();
        searchResultFragment.setRefreshCallback(this);
        searchResultFragment.setOnFragmentCreateViewListener(this);
    }

    private void filter() {
        if (!TextUtils.isEmpty(filterId) && searchResultFragment != null) {
            searchResultFragment.filter(filterId);
        }
    }

    @Override
    public void onFragmentViewCreated(boolean isVisible) {
        if (isVisible && !isLoaded) {
            isLoaded = true;
            filter();
        }
    }

    @Override
    public void onRefreshClicked() {
        filter();
    }

    @Override
    public void onApiResult(String action, Object result) {
        // API handled by search fragment
    }
}
