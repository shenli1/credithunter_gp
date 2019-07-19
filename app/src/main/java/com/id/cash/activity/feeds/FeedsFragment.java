package com.id.cash.activity.feeds;

import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;

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
import com.id.cash.activity.product.cashloan.FilterActivity;
import com.id.cash.adapter.FeedsListAdapter;
import com.id.cash.adapter.FilterAdapter;
import com.id.cash.bean.FeedsBannerBean;
import com.id.cash.bean.FeedsBean;
import com.id.cash.bean.FilterBean;
import com.id.cash.bean.ListResultBean;
import com.id.cash.common.ImageLoader;
import com.id.cash.common.NetworkUtil;
import com.id.cash.common.TrackUtil;
import com.id.cash.module.starwin.common.base.BaseFragment;
import com.id.cash.network.ApiActions;
import com.id.cash.presenter.FeedsPresenter;
import com.id.cash.widget.NetworkLoadingLayout;
import com.id.cash.widget.recyclerview.BaseRecyclerViewHolder;
import com.id.cash.widget.recyclerview.OnItemClickListener;
import com.id.cash.widget.recyclerview.RecyclerLoadMoreOnScrollListener;

/**
 * Created by linchen on 2018/7/19.
 */
public class FeedsFragment extends BaseFragment<FeedsPresenter> implements AdapterView.OnItemClickListener {
    private RecyclerView recyclerView;
    private FeedsListAdapter adapter;

    private SmartRefreshLayout layoutRefresh;
    private NetworkLoadingLayout networkLoadingLayout;
    private ConvenientBanner convenientBanner;

    private FloatingActionButton fabGoTop;

    private RecyclerView rvTagRecyclerView;
    private FilterAdapter filterAdapter;

    private List<FilterBean> filterBeanList = new ArrayList<>();
    private List<FeedsBannerBean> bannerBeanList = new ArrayList<>();
    private List<FeedsBean> feedsBeanList = new ArrayList<>();

    // refresh & loadmore control
    private boolean isRefresh = true;   // is refresh or load more
    private boolean isLoadingFeeds = false;

    // pagination
    private int start = 1;

    // a flag to judge init action has finished
    private boolean isLazyLoaded;

    @Override
    public FeedsPresenter createPresenter() {
        return new FeedsPresenter(this);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_feeds_list;
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
        View header = inflater.inflate(R.layout.sub_feeds_list_header, null, false);
        convenientBanner = header.findViewById(R.id.banner_feeds_list);
        convenientBanner.setPageIndicator(new int[]{R.drawable.ic_banner_indicator, R.drawable.ic_banner_indicator_focused});
        View footer = inflater.inflate(R.layout.common_recycler_footer, null, false);

        // filters
        rvTagRecyclerView = header.findViewById(R.id.rv_cashloan_tags);
        if (filterAdapter == null) {
            filterAdapter = new FilterAdapter(myActivity);
        }
        rvTagRecyclerView.setAdapter(filterAdapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(myActivity);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        rvTagRecyclerView.setLayoutManager(layoutManager);

        // feeds list
        adapter = new FeedsListAdapter(myActivity);
        adapter.setHeaderView(header);
//        adapter.setFooterView(footer);
        recyclerView = view.findViewById(R.id.rv_feedslist_main);
        recyclerView.setLayoutManager(new LinearLayoutManager(myActivity));
        recyclerView.setAdapter(adapter);

        // refresh
        layoutRefresh = view.findViewById(R.id.layout_refresh);
        layoutRefresh.setEnableRefresh(true);
        networkLoadingLayout = view.findViewById(R.id.layout_feedslist_main_container);

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

        networkLoadingLayout.setRefreshCallback(() -> loadNew());

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
        presenter.loadBanner(ApiActions.FEEDS_BANNER);
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
            if (feedsBeanList.size() == 0) {
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
        if (!isLoadingFeeds) {
            isLoadingFeeds = true;
            beforeLoadingList();    // show loading
            presenter.loadFeedsList(ApiActions.FEEDS_LIST, start);
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
        TrackUtil.logEvent("feeds_banner_click_" + position);
        if (bannerBeanList.size() > 0 && position >= 0 && position < bannerBeanList.size()) {
            FeedsBannerBean bean = bannerBeanList.get(position);
            if (bean != null) {
                FeedsWebActivity.actionStart(getContext(), bean.getType(), bean.getFeedsId(), bean.getLink());
            }
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        TrackUtil.logEvent("feeds_list_item_click");
        if (feedsBeanList != null && feedsBeanList.size() > 0) {
            // position is not index to the bean list, because of header
            FeedsBean bean = (FeedsBean) adapter.getItem(position);
            if (bean != null) {
                FeedsWebActivity.actionStart(getContext(), bean.getType(), bean.getId(), bean.getLink());
            }
        }
    }

    @Override
    public void onApiResult(String action, Object result) {
        if (ApiActions.FEEDS_LIST.equals(action)) {
            onFeedsList((ListResultBean<FeedsBean>) result);
            isLoadingFeeds = false;
        } else if (ApiActions.FILTERS.equals(action)) {
            if (result != null) {
                ListResultBean<FilterBean> filterResultBean = (ListResultBean<FilterBean>) result;
                filterBeanList = Arrays.asList(filterResultBean.getRecords());
            }
            updateFilterUI();
        } else if (ApiActions.FEEDS_BANNER.equals(action)) {
            if (result != null) {
                bannerBeanList = Arrays.asList((FeedsBannerBean[]) result);
                updateBannerUI();
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

    private void updateBannerUI() {
        if (bannerBeanList == null || bannerBeanList.size() == 0) {
            convenientBanner.setVisibility(View.GONE);
        } else {
            convenientBanner.setVisibility(View.VISIBLE);
            ArrayList<String> list = new ArrayList<>();
            for (FeedsBannerBean bannerBean : bannerBeanList) {
                list.add(bannerBean.getTitleImg());
            }
            convenientBanner.setPages(new CBViewHolderCreator() {
                @Override
                public Holder createHolder(View itemView) {
                    return new FeedsFragment.BannerImageHolderView(itemView);
                }

                @Override
                public int getLayoutId() {
                    return R.layout.item_banner;
                }
            }, list);
            convenientBanner.setOnItemClickListener((position) -> onBannerClick(position));

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

    @Override
    public void showError(String action, String code, String message) {
        if (ApiActions.FEEDS_BANNER.equals(action)) {
            // TODO show error on banner?
        }
        if (ApiActions.FEEDS_LIST.equals(action)) {
            isLoadingFeeds = false;
            super.showError(action, code, message);

            layoutRefresh.finishLoadMore();
            layoutRefresh.finishRefresh();
        }
    }

    private void onFeedsList(ListResultBean<FeedsBean> result) {
        hideLoading();
        // show no data UI when no data
        if (isRefresh) {
            feedsBeanList.clear();
            feedsBeanList.addAll(Arrays.asList(result.getRecords()));
            adapter.setItems(feedsBeanList);
            layoutRefresh.finishRefresh();

            // re-allow load more data
            layoutRefresh.setNoMoreData(false);
        } else {
            List<FeedsBean> list = Arrays.asList(result.getRecords());
            feedsBeanList.addAll(list);
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
