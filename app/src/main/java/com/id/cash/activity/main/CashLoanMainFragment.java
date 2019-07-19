package com.id.cash.activity.main;

import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bigkoo.convenientbanner.ConvenientBanner;
import com.bigkoo.convenientbanner.holder.CBViewHolderCreator;
import com.bigkoo.convenientbanner.holder.Holder;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.id.cash.R;
import com.id.cash.activity.product.cashloan.CashLoanDetailActivity;
import com.id.cash.activity.product.cashloan.FilterActivity;
import com.id.cash.adapter.CashLoanAdapter;
import com.id.cash.adapter.FilterAdapter;
import com.id.cash.bean.BannerBean;
import com.id.cash.bean.CashLoanBean;
import com.id.cash.bean.FilterBean;
import com.id.cash.bean.ListResultBean;
import com.id.cash.common.ImageLoader;
import com.id.cash.common.NetworkUtil;
import com.id.cash.common.TrackUtil;
import com.id.cash.module.starwin.common.base.BaseFragment;
import com.id.cash.network.ApiActions;
import com.id.cash.presenter.CashLoanMainPresenter;
import com.id.cash.widget.MarqueeView;
import com.id.cash.widget.NetworkLoadingLayout;
import com.id.cash.widget.recyclerview.BaseRecyclerViewHolder;
import com.id.cash.widget.recyclerview.OnItemClickListener;
import com.id.cash.widget.recyclerview.RecyclerLoadMoreOnScrollListener;

/**
 * Created by linchen on 2018/6/1.
 */

public class CashLoanMainFragment extends BaseFragment<CashLoanMainPresenter> implements AdapterView.OnItemClickListener {
    private RecyclerView recyclerView;
    private CashLoanAdapter adapter;

    private SmartRefreshLayout layoutRefresh;
    private NetworkLoadingLayout networkLoadingLayout;
    private ConvenientBanner convenientBanner;
    private MarqueeView marqueeView;
    private LinearLayout llMarquee;

    private RecyclerView rvTagRecyclerView;
    private FilterAdapter filterAdapter;
    private FloatingActionButton fabGoTop;

    private List<BannerBean> bannerBeanList = new ArrayList<>();
    private List<FilterBean> filterBeanList = new ArrayList<>();
    private List<CashLoanBean> cashLoanBeanBeanList = new ArrayList<>();

    // refresh & loadmore control
    private boolean isRefresh = true;   // is refresh or load more
    private boolean isLoadingCashLoan = false;

    // pagination
    private int start = 1;

    // a flag to judge init action has finished
    private boolean isLazyLoaded;

    @Override
    public CashLoanMainPresenter createPresenter() {
        return new CashLoanMainPresenter(this);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_cashloan_main;
    }

    @Override
    protected void lazyLoad() {
        // according this judge, can prevent NRE
        if (!isPrepared || !isVisible) {
            return;
        }

        if (!isLazyLoaded) {
            isLazyLoaded = true;
            initializeAndLoadData();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (llMarquee != null && llMarquee.getVisibility() == View.VISIBLE) {
            marqueeView.startFlipping();
        }
        presenter.startMarqueeDataFetch();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (marqueeView != null) {
            marqueeView.stopFlipping();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        isLazyLoaded = false;
    }

    private void initializeAndLoadData() {
        bindViews();
        bindEvents();
        loadNew();
    }

    private void bindViews() {
        // header
        LayoutInflater inflater = LayoutInflater.from(myActivity);
        View header = inflater.inflate(R.layout.sub_cashloan_list_header, null, false);
        convenientBanner = header.findViewById(R.id.banner_cashloan_list);
        convenientBanner.setPageIndicator(new int[]{R.drawable.ic_banner_indicator, R.drawable.ic_banner_indicator_focused});
        View footer = inflater.inflate(R.layout.common_recycler_footer, null, false);
        llMarquee = header.findViewById(R.id.ll_marquee);
        marqueeView = header.findViewById(R.id.mqv_cashloan);

        // filters
        rvTagRecyclerView = header.findViewById(R.id.rv_cashloan_tags);
        if (filterAdapter == null) {
            filterAdapter = new FilterAdapter(myActivity);
        }
        rvTagRecyclerView.setAdapter(filterAdapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(myActivity);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        rvTagRecyclerView.setLayoutManager(layoutManager);

        // main list
        adapter = new CashLoanAdapter(myActivity);
        adapter.setHeaderView(header);
//        adapter.setFooterView(footer);
        recyclerView = view.findViewById(R.id.rv_cashloan_main);
        recyclerView.setLayoutManager(new LinearLayoutManager(myActivity));
        recyclerView.setAdapter(adapter);

        // refresh
        layoutRefresh = view.findViewById(R.id.layout_refresh);
        layoutRefresh.setEnableRefresh(true);
        networkLoadingLayout = view.findViewById(R.id.layout_cashloan_main_container);

        fabGoTop = view.findViewById(R.id.fab_go_top);
    }

    private void bindEvents() {
        adapter.setOnItemClickListener(this);
        filterAdapter.setOnItemClickListner(new OnItemClickListener<FilterBean>() {
            @Override
            public void onClick(BaseRecyclerViewHolder baseRecyclerViewHolder, View view, FilterBean filterBean) {
                if (filterBean != null) {
                    TrackUtil.logEvent("filter_click_" + filterBean.getName());
                    FilterActivity.actionStart(myActivity, filterBean.getId(), filterBean.getName());
                }
            }
        });

        networkLoadingLayout.setRefreshCallback(() -> {
            loadNew();
        });

        layoutRefresh.setOnRefreshLoadMoreListener(new OnRefreshLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                loadListDataMore();
            }

            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                loadNew();
                refreshLayout.finishRefresh(50);
            }
        });

        recyclerView.addOnScrollListener(new RecyclerLoadMoreOnScrollListener() {
            @Override
            protected void onMorePageDisplayed(boolean isMorePage) {
                showHideGoTopButton(isMorePage);
            }
        });
        fabGoTop.setOnClickListener((view) -> recyclerView.smoothScrollToPosition(0));
    }

    private void loadNew() {
        if (NetworkUtil.isNetworkAvailable(this.myActivity)) {
            loadBannerData();
            loadListDataNew();
            loadFilters();
            return;
        }

        hideLoading();
        showNetworkError();
    }

    private void loadBannerData() {
        presenter.loadBanner(ApiActions.BANNERS);
    }

    private void loadFilters() {
        presenter.loadFilters(ApiActions.FILTERS);
    }

    private void beforeLoadingList() {
        if (isRefresh) {
            start = 1;
            showLoading();
        } else {
            start = start + 1;
            if (cashLoanBeanBeanList.size() == 0) {
                showLoading();
            }
        }
    }

    private void loadListDataNew() {
        isRefresh = true;
        loadDataImpl();
    }

    private void loadListDataMore() {
        isRefresh = false;
        loadDataImpl();
    }

    private void loadDataImpl() {
        if (!isLoadingCashLoan) {
            isLoadingCashLoan = true;
            beforeLoadingList();    // show loading
            presenter.loadCashLoanList(ApiActions.CASHLOAN_LIST_MAIN, start);
        }
    }

    private void showNetworkError() {
        networkLoadingLayout.setMode(NetworkLoadingLayout.Mode.ERROR);
        showHideGoTopButton(false);
    }

    private void showEmptyData() {
        networkLoadingLayout.setMode(NetworkLoadingLayout.Mode.NO_DATA);
        showHideGoTopButton(false);
    }

    private void showContent() {
        networkLoadingLayout.setMode(NetworkLoadingLayout.Mode.CONTENT);
    }

    private void showHideGoTopButton(boolean isShow) {
        fabGoTop.setVisibility(isShow ? View.VISIBLE : View.GONE);
    }

    private void onBannerClick(int position) {
        TrackUtil.logEvent("banner_click_" + position);
        if (bannerBeanList.size() > 0 && position >= 0 && position < bannerBeanList.size()) {
            BannerBean bean = bannerBeanList.get(position);
            if (BannerBean.BannerType.PRODUCT.equals(bean.getType()) &&
                    !TextUtils.isEmpty(bean.getLink())) {
                CashLoanDetailActivity.actionStart(myActivity, bean.getLink());
            }
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        TrackUtil.logEvent("main_list_item_click");
        if (cashLoanBeanBeanList != null && cashLoanBeanBeanList.size() > 0) {
            // position is not index to the bean list, because of header
            CashLoanBean bean = (CashLoanBean) adapter.getItem(position);
            if (bean != null) {
                CashLoanDetailActivity.actionStart(myActivity, bean.getUuid());
            }
        }
    }

    private List<String> notices;

    @Override
    public void onApiResult(String action, Object result) {
        if (ApiActions.CASHLOAN_LIST_MAIN.equals(action)) {
            onCashLoanList((ListResultBean<CashLoanBean>) result);
            isLoadingCashLoan = false;
        } else if (ApiActions.BANNERS.equals(action)) {
            if (result != null) {
                // filter banner bean
                BannerBean[] bannerBeans = (BannerBean[]) result;
                bannerBeanList = new ArrayList<>();
                for (BannerBean bannerBean : bannerBeans) {
                    if (BannerBean.BannerLocation.BANNER.equals(bannerBean.getLocation())) {
                        bannerBeanList.add(bannerBean);
                    }
                }

                updateBannerUI();
            }
        } else if (ApiActions.FILTERS.equals(action)) {
            if (result != null) {
                ListResultBean<FilterBean> filterResultBean = (ListResultBean<FilterBean>) result;
                filterBeanList = Arrays.asList(filterResultBean.getRecords());
            }
            updateFilterUI();
        } else if (ApiActions.MARQUEE.equals(action)) {
            postDelayed(() -> {
                if (marqueeView != null) {
                    if (notices == null) {
                        notices = (List<String>) result;
                        marqueeView.startWithList(notices);
                    } else {
                        notices = (List<String>) result;
                        marqueeView.reloadWithList(notices);
                    }
                }
                if (llMarquee != null) {
                    llMarquee.setVisibility(View.VISIBLE);
                }
            }, 0);
        }
    }

    private void updateBannerUI() {
        if (bannerBeanList == null || bannerBeanList.size() == 0) {
            convenientBanner.setVisibility(View.GONE);
        } else {
            convenientBanner.setVisibility(View.VISIBLE);
            ArrayList<String> list = new ArrayList<>();
            for (BannerBean bannerBean : bannerBeanList) {
                list.add(bannerBean.getImgUrl());
            }
            convenientBanner.setPages(new CBViewHolderCreator() {
                @Override
                public Holder createHolder(View itemView) {
                    return new BannerImageHolderView(itemView);
                }

                @Override
                public int getLayoutId() {
                    return R.layout.item_banner;
                }
            }, list);
            convenientBanner.setOnItemClickListener(new com.bigkoo.convenientbanner.listener.OnItemClickListener() {
                @Override
                public void onItemClick(int position) {
                    onBannerClick(position);
                }
            });

            if (list.size() <= 1) {
                convenientBanner.setPointViewVisible(false)
                        .setCanLoop(false);
            } else {
                convenientBanner.setPointViewVisible(true)
                        .setCanLoop(true)
                        .startTurning(4000);
            }
        }
    }

    private void updateFilterUI() {
        if (filterBeanList == null || filterBeanList.size() == 0) {
            rvTagRecyclerView.setVisibility(View.GONE);
        } else {
            rvTagRecyclerView.setVisibility(View.VISIBLE);
            filterAdapter.setItemList(filterBeanList);
        }
    }

    @Override
    public void showError(String action, String code, String message) {
        if (ApiActions.BANNERS.equals(action)) {
            // TODO show error on banner?
        }
        if (ApiActions.FILTERS.equals(action)) {
            // TODO show error on filters?
        }
        if (ApiActions.CASHLOAN_LIST_MAIN.equals(action)) {
            isLoadingCashLoan = false;
            super.showError(action, code, message);

            layoutRefresh.finishLoadMore();
            layoutRefresh.finishRefresh();
        }
    }

    private void onCashLoanList(ListResultBean<CashLoanBean> result) {
        hideLoading();
        // show no data UI when no data
        if (isRefresh) {
            cashLoanBeanBeanList.clear();
            cashLoanBeanBeanList.addAll(Arrays.asList(result.getRecords()));
            adapter.setItems(cashLoanBeanBeanList);
            layoutRefresh.finishRefresh();

            // re-allow load more data
            layoutRefresh.setNoMoreData(false);
        } else {
            List<CashLoanBean> list = Arrays.asList(result.getRecords());
            cashLoanBeanBeanList.addAll(list);
            adapter.addData(list);

            if (list.isEmpty()) {
                layoutRefresh.finishLoadMoreWithNoMoreData();
            } else {
                layoutRefresh.finishLoadMore();
            }
        }

        if (adapter.getRealItemCount() == 0) {
            showEmptyData();
        } else {
            showContent();
        }
    }

    public class BannerImageHolderView extends Holder<String> {
        private ImageView imageView;

        BannerImageHolderView(View itemView) {
            super(itemView);
        }

        @Override
        protected void initView(View itemView) {
            imageView = itemView.findViewById(R.id.iv_banner);
        }

        @Override
        public void updateUI(String data) {
            ImageLoader.loadWithBannerPlaceHolder(getContext(), data, imageView);
        }
    }
}
