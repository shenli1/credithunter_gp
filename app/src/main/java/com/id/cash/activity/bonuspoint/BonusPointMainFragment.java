package com.id.cash.activity.bonuspoint;


import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;

import com.hwangjr.rxbus.RxBus;
import com.hwangjr.rxbus.annotation.Subscribe;
import com.hwangjr.rxbus.annotation.Tag;
import com.hwangjr.rxbus.thread.EventThread;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.util.ArrayList;

import com.id.cash.R;
import com.id.cash.activity.app.MainTabActivity;
import com.id.cash.activity.app.PrivacyPolicyActivity;
import com.id.cash.activity.user.InviteResultActivity;
import com.id.cash.activity.user.WithdrawActivity;
import com.id.cash.adapter.BonusPointTaskAdapter;
import com.id.cash.bean.BonusPointTaskBean;
import com.id.cash.bean.BonusPointTaskUIBean;
import com.id.cash.bean.ShareInfoBean;
import com.id.cash.common.LogUtil;
import com.id.cash.common.PrivacyPolicyUtil;
import com.id.cash.common.StringUtil;
import com.id.cash.common.TrackUtil;
import com.id.cash.module.starwin.accountkit.LoginManager;
import com.id.cash.module.starwin.common.base.BaseFragment;
import com.id.cash.module.starwin.common.bus.BusAction;
import com.id.cash.module.starwin.common.bus.WithdrawSuccessEvent;
import com.id.cash.network.ApiActions;
import com.id.cash.presenter.BonusPointPresenter;
import com.id.cash.presenter.SharePresenter;
import com.id.cash.widget.DebouncingOnClickListener;
import com.id.cash.widget.NetworkLoadingLayout;

import static com.id.cash.activity.app.MainTabActivity.PRIVACY_POLICY_RESULT_CODE;

/**
 * Created by linchen on 2018/6/4.
 */

public class BonusPointMainFragment extends BaseFragment<BonusPointPresenter> implements AdapterView.OnItemClickListener {
    // UI
    private View header;
    private SmartRefreshLayout layoutRefresh;
    private NetworkLoadingLayout networkLoadingLayout;
    private BonusPointTaskAdapter adapter;
    private RecyclerView recyclerView;
    private TextView tvTotalBonusPoint;
    private View noUserWarning;

    private ArrayList<BonusPointTaskUIBean> bonusPointTaskBeanList = new ArrayList<>();
    private ShareInfoBean facebookShareInfoBean;

    private SharePresenter sharePresenter;

    private LoginManager loginManager = new LoginManager(this);

    private boolean shouldReload = false;
    private boolean isLazyLoaded;
    private boolean isBusRegistered = false;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_bonuspoint_main;
    }

    @Override
    public BonusPointPresenter createPresenter() {
        return new BonusPointPresenter(this);
    }

    @Override
    protected void lazyLoad() {
        // according this judge, can prevent NRE
        if (!isPrepared || !isVisible) {
            return;
        }

        if (!isLazyLoaded) {
            isLazyLoaded = true;

            sharePresenter = new SharePresenter(this);
            initializeAndLoadData();

            try {
                if (!isBusRegistered) {
                    RxBus.get().register(this);
                }
                isBusRegistered = true;
            } catch (Exception ex) {
                LogUtil.e(ex);
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        // check login
        updateUserUI();

        if (shouldReload) {
            shouldReload = false;
            loadNew();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        isLazyLoaded = false;

        try {
            if (isBusRegistered) {
                RxBus.get().unregister(this);
                isBusRegistered = false;
            }
        } catch (Exception ex) {
            LogUtil.e(ex);
        }
    }

    private void initializeAndLoadData() {
        bindViews();
        bindEvents();
        loadNew();
        updateUserUI();
    }

    private void bindViews() {
        header = LayoutInflater.from(myActivity).inflate(R.layout.sub_bonuspoint_main_header, null, false);

        adapter = new BonusPointTaskAdapter(myActivity);
        adapter.setHeaderView(header);
//        View footer  = LayoutInflater.from(myActivity).inflate(R.layout.common_recycler_footer, null, false);
//        adapter.setFooterView(footer);

        noUserWarning = header.findViewById(R.id.ll_bonuspoint_guest_warning);
        tvTotalBonusPoint = header.findViewById(R.id.tv_bonuspoint_amount);

        recyclerView = view.findViewById(R.id.rv_bonuspoint_main);
        recyclerView.setLayoutManager(new LinearLayoutManager(myActivity));
        recyclerView.setAdapter(adapter);

        // refresh
        layoutRefresh = view.findViewById(R.id.layout_refresh);
        layoutRefresh.setEnableRefresh(true);
        networkLoadingLayout = view.findViewById(R.id.layout_networking_container);
    }

    private void bindEvents() {
        networkLoadingLayout.setRefreshCallback(() -> {
            loadNew();
        });

        layoutRefresh.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                loadNew();
                refreshLayout.finishRefresh(50);
            }
        });

        adapter.setOnItemClickListener(this);

        View btnWithDraw = header.findViewById(R.id.tv_bonuspoint_withdraw);
        btnWithDraw.setOnClickListener(new DebouncingOnClickListener() {
            @Override
            public void doClick(View v) {
                onWithdrawClicked();
            }
        });
    }

    private void loadNew() {
        presenter.getBonusPointSummary(ApiActions.BONUSPOINT_SUMMARY);
    }

    private void onBonusPointTasks(BonusPointTaskBean[] taskBeans) {
        if (taskBeans != null) {
            ArrayList<BonusPointTaskUIBean> filtered = new ArrayList<>();
            for (BonusPointTaskBean bean : taskBeans) {
                if (bean.getTempletId().equals("10004") || bean.getTempletId().equals("10003")) {
                    continue; // 售出代码隐藏部分奖励
                }
                BonusPointTaskUIBean uiBean = BonusPointTaskUIBean.bonusPointTaskTypes.get(bean.getTempletId());
                if (uiBean != null) {
                    uiBean.setStatus(bean.isStatus());
                    filtered.add(uiBean);
                }
            }

            bonusPointTaskBeanList.clear();
            bonusPointTaskBeanList.addAll(filtered);

            adapter.setItems(bonusPointTaskBeanList);
        }
    }

    public void updateUserUI() {
        if (loginManager.isLoggedIn()) {
            if (noUserWarning != null) {
                noUserWarning.setVisibility(View.GONE);
            }
            if (tvTotalBonusPoint != null) {
                tvTotalBonusPoint.setTextColor(getResources().getColor(R.color.dark_text1));
            }
        } else {
            if (noUserWarning != null) {
                noUserWarning.setVisibility(View.VISIBLE);
            }
            if (tvTotalBonusPoint != null) {
                tvTotalBonusPoint.setTextColor(getResources().getColor(R.color.button_title_disabled));
            }
        }
    }

    private void onFBShareClick() {
        if (facebookShareInfoBean == null) {
            TrackUtil.logEvent("point_main_list_fb_share_nodata_click");
            // if share info data is not retrieved, fetch share info data
            // BONUSPOINT_SHARE_FB action in onApiResult will start share again
            presenter.getBonusPointSummary(ApiActions.BONUSPOINT_SHARE_FB);
        } else {
            TrackUtil.logEvent("point_main_list_fb_share_click");
            facebookShareInfoBean.setMethod(SharePresenter.ShareMethod.SHARING.toString());
            facebookShareInfoBean.setTaskTemplateId(BonusPointTaskUIBean.BonusPointTaskType.SHARE);
            sharePresenter.share(SharePresenter.ShareChannel.FACEBOOK, facebookShareInfoBean, myActivity);
        }
    }

    private void onInviteClick() {
        InviteResultActivity.actionStart(myActivity);
    }

    // 1. if the user want to participate the privacy task (collect SMS, etc)
    // 2. and if the user has not accepted privacy policy,
    // show user the privacy policy activity
    private void onPrivacyTaskClick() {
        // !PrivacyPolicyUtil.shouldShowPrivacyPolicyDialog: means user has not accepted the privacy policy
        if (!PrivacyPolicyUtil.shouldCollectPrivacyData()) {
            // show privacy policy
            PrivacyPolicyActivity.actionStartForResult(myActivity, PRIVACY_POLICY_RESULT_CODE);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (bonusPointTaskBeanList != null && bonusPointTaskBeanList.size() > 0) {
            BonusPointTaskUIBean bean = (BonusPointTaskUIBean) adapter.getItem(position);
            if (bean != null) {
                if (BonusPointTaskUIBean.BonusPointTaskType.SHARE.equals(bean.getTemplateId())) {
                    onFBShareClick();
                } else if (BonusPointTaskUIBean.BonusPointTaskType.INVITE.equals(bean.getTemplateId())) {
                    TrackUtil.logEvent("point_main_list_invite_click");
                    onInviteClick();
                } else if (BonusPointTaskUIBean.BonusPointTaskType.BORROWED.equals(bean.getTemplateId())) {
                    TrackUtil.logEvent("point_main_list_borrow_click");
                    onPrivacyTaskClick();
                } else if (BonusPointTaskUIBean.BonusPointTaskType.REGISTERED.equals(bean.getTemplateId())) {
                    TrackUtil.logEvent("point_main_list_register_click");
                    onPrivacyTaskClick();
                } else if (BonusPointTaskUIBean.BonusPointTaskType.SIGN.equals(bean.getTemplateId())) {
                    TrackUtil.logEvent("point_main_list_login_click");
                }
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Subscribe(
        thread = EventThread.IO,
        tags = {@Tag(BusAction.WITHDRAW_SUCCESS)})
    public void onWithdrawSuccess(WithdrawSuccessEvent withdrawSuccessEvent) {
        shouldReload = true;
    }

    void onWithdrawClicked() {
        TrackUtil.logEvent("point_main_withdraw_click");

        if (loginManager.isLoggedIn()) {
            WithdrawActivity.actionStart(myActivity);
        } else {
            showError(null, null, "Silakan login sebelum menarik");
            if (myActivity instanceof MainTabActivity) {
                ((MainTabActivity) myActivity).getLoginManager().phoneLogin(myActivity, BusAction.SHOW_WITHDRAW);
            }
        }
    }

    @Override
    public void onApiResult(String action, Object result) {
        if (ApiActions.BONUSPOINT_SUMMARY.equals(action) || ApiActions.BONUSPOINT_SHARE_FB.equals(action)) {
            BonusPointPresenter.BonusPointSummaryBean summaryBean = (BonusPointPresenter.BonusPointSummaryBean) result;
            facebookShareInfoBean = summaryBean.getShareInfoBean();
            // update total point
            if (summaryBean.getUserBean() != null) {
                tvTotalBonusPoint.setText(
                    StringUtil.formatResourceString(getContext(),
                        R.string.common_money_with_unit_format,
                        String.valueOf(summaryBean.getUserBean().getTotalPoint())));
            }

            // update tasks
            onBonusPointTasks(summaryBean.getTasks());

            // perform share on share click action
            if (ApiActions.BONUSPOINT_SHARE_FB.equals(action)) {
                onFBShareClick();
            }
        }
    }
}
