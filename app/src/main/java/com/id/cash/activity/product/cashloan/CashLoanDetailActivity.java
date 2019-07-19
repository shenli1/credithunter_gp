package com.id.cash.activity.product.cashloan;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.scwang.smartrefresh.layout.SmartRefreshLayout;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import com.id.cash.R;
import com.id.cash.activity.webview.CommonWebActivity;
import com.id.cash.adapter.CashLoanProcedureAdapter;
import com.id.cash.bean.CashLoanBean;
import com.id.cash.bean.CashLoanDetailBean;
import com.id.cash.bean.CashLoanExtensionBean;
import com.id.cash.bean.CashLoanProceduerBean;
import com.id.cash.bean.CashLoanProcedureStepBean;
import com.id.cash.bean.LauncherMode;
import com.id.cash.bean.PlatformBean;
import com.id.cash.common.CollapseUtil;
import com.id.cash.common.GooglePlayUtil;
import com.id.cash.common.ImageLoader;
import com.id.cash.common.StringUtil;
import com.id.cash.common.TrackUtil;
import com.id.cash.common.UrlUtil;
import com.id.cash.module.starwin.common.base.BaseActivity;
import com.id.cash.network.ApiActions;
import com.id.cash.presenter.LoanDetailPresenter;
import com.id.cash.widget.DebouncingOnClickListener;
import com.id.cash.widget.ObservableScrollView;
import com.id.cash.widget.SlidingTabBar;
import com.id.cash.widget.circle.CircleAngleAnimation;
import com.id.cash.widget.circle.RingChartView;


/**
 * Created by linchen on 2018/5/14.
 */
public class CashLoanDetailActivity extends BaseActivity<LoanDetailPresenter> implements ViewTreeObserver.OnScrollChangedListener {
    private static final String LOAN_ID_TAG = "LOAN_ID_TAG";

    ObservableScrollView scrollView;
    SlidingTabBar slidingTabBar;
    SmartRefreshLayout layoutRefresh;

    LinearLayout layoutLoanInfo;
    LinearLayout layoutLoanProcedure;

    ImageView ivIcon;
    TextView tvLoanName;
    TextView tvPassRate;
    TextView tvScore;
    TextView tvScoreTag;
    LinearLayout llScoreTag;

    TextView tvArea;

    ImageView icExpandIcon;
    TextView expandablePanel;

    RingChartView ringChartView;

    TextView tvQuota;
    TextView tvTotal;
    TextView tvChartQuota;
    TextView tvChartInterest;

    TextView tvTerm;

    TextView tvIssueTime;
    TextView tvLoanNotice;

    RecyclerView rvApply;
    RecyclerView rvAudit;

    TextView tvGoStore;

    // model
    private String loanId;
    private final InterestConfig interestConfig = new InterestConfig();

    private CashLoanProcedureAdapter cashLoanApplyProcedureAdapter;
    private CashLoanProcedureAdapter cashLoanAuditProcedureAdapter;
    private boolean isDescriptionPanelExpanded = false;
    private PlatformBean platformBean;

    public static void actionStart(Context context, String loanId) {
        actionStart(context, loanId, false);
    }

    public static void actionStart(Context context, String loanId, boolean isFromNotification) {
        Intent intent = new Intent(context, CashLoanDetailActivity.class);
        intent.putExtra(LOAN_ID_TAG, loanId);

        if (isFromNotification) {
            intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS | Intent.FLAG_RECEIVER_FOREGROUND);
        }
        context.startActivity(intent);
    }

    @Override
    protected LoanDetailPresenter createPresenter() {
        return new LoanDetailPresenter(this);
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_cashloan_detail;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        loanId = getIntent().getStringExtra(LOAN_ID_TAG);
        // initial state is collapsed
        CollapseUtil.collapse(expandablePanel);

        init();

        interestConfig.setListener(new OnInterestConfigChangeListener() {
            @Override
            public void onChange(BigDecimal interest, BigDecimal quota, int days) {
                // update UI
                CashLoanDetailActivity thisActivity = CashLoanDetailActivity.this;
                int intInterest = interest.intValue();
                int intQuota = quota.intValue();

                thisActivity.tvQuota.setText("Rp " + intQuota);
                thisActivity.tvChartQuota.setText("Rp " + intQuota);
                thisActivity.tvTotal.setText("Rp " + (intInterest + intQuota));
                thisActivity.tvChartInterest.setText("Rp " + intInterest);
                thisActivity.tvTerm.setText("" + days + " Days");

                ringChartView.reset();
                BigDecimal total = interest.add(quota);
                float percent = quota.divide(total, 3, RoundingMode.HALF_DOWN).floatValue();
                CircleAngleAnimation animation = new CircleAngleAnimation(ringChartView, percent);
                animation.setDuration(1000);
                ringChartView.startAnimation(animation);
            }
        });
    }

    @Override
    protected void bindViews() {
        super.bindViews();

        scrollView = findViewById(R.id.scrollview_cashloan_detail);
        slidingTabBar = findViewById(R.id.slt_cash_loan_detail);
        layoutRefresh = findViewById(R.id.layout_refresh);
        layoutLoanInfo = findViewById(R.id.loan_info);
        layoutLoanProcedure = findViewById(R.id.loan_procedure);

        ivIcon = findViewById(R.id.iv_loan_icon);
        tvLoanName = findViewById(R.id.tv_loan_name);
        tvPassRate = findViewById(R.id.tv_loan_pass_rate);
        tvScore = findViewById(R.id.tv_loan_score);
        tvScoreTag = findViewById(R.id.tv_loan_score_tag);
        llScoreTag = findViewById(R.id.score_tag);

        tvArea = findViewById(R.id.tv_area);

        icExpandIcon = findViewById(R.id.iv_expand_collapse_icon);
        expandablePanel = findViewById(R.id.panel_expandable);

        ringChartView = findViewById(R.id.rcv_interest_fee_percent);

        tvQuota = findViewById(R.id.tv_loan_quota);
        tvTotal = findViewById(R.id.tv_total_value);
        tvChartQuota = findViewById(R.id.tv_loan_chart_quota);
        tvChartInterest = findViewById(R.id.tv_loan_chart_interest);

        tvTerm = findViewById(R.id.tv_loan_term);

        tvIssueTime = findViewById(R.id.tv_issue_time);
        tvLoanNotice = findViewById(R.id.tv_loan_notice);

        rvApply = findViewById(R.id.rv_apply);
        rvAudit = findViewById(R.id.rv_audit);

        tvGoStore = findViewById(R.id.tv_go_store);
    }

    @Override
    protected void bindEvents() {
        super.bindEvents();

        View expandableBar = findViewById(R.id.bar_expandable);
        expandableBar.setOnClickListener(new DebouncingOnClickListener() {
            @Override
            public void doClick(View v) {
                onExpandDescription();
            }
        });

        View btnAddQuota = findViewById(R.id.btn_add_quota);
        btnAddQuota.setOnClickListener(new DebouncingOnClickListener() {
            @Override
            public void doClick(View v) {
                onAddQuota();
            }
        });

        View btnReduceQuota = findViewById(R.id.btn_reduce_quota);
        btnReduceQuota.setOnClickListener(new DebouncingOnClickListener() {
            @Override
            public void doClick(View v) {
                onReduceQuota();
            }
        });

        View btnAddTerm = findViewById(R.id.btn_add_term);
        btnAddTerm.setOnClickListener(new DebouncingOnClickListener() {
            @Override
            public void doClick(View v) {
                onAddTerm();
            }
        });

        View btnReduceTerm = findViewById(R.id.btn_reduce_term);
        btnReduceTerm.setOnClickListener(new DebouncingOnClickListener() {
            @Override
            public void doClick(View v) {
                onReduceTerm();
            }
        });

        View btnGoStore = findViewById(R.id.tv_go_store);
        btnGoStore.setOnClickListener(new DebouncingOnClickListener() {
            @Override
            public void doClick(View v) {
                onGoStore();
            }
        });
    }

    void onExpandDescription() {
        TrackUtil.logEvent("detail_expand_desc_click");
        if (isDescriptionPanelExpanded) {
            int duration = CollapseUtil.collapse(expandablePanel);
            CollapseUtil.rotate(icExpandIcon, duration);
        } else {
            int duration = CollapseUtil.expand(expandablePanel);
            CollapseUtil.rotate(icExpandIcon, duration);
        }

        isDescriptionPanelExpanded = !isDescriptionPanelExpanded;
    }

    void onAddQuota() {
        TrackUtil.logEvent("detail_add_quota_click");
        interestConfig.adjustQuota(true);
    }

    void onReduceQuota() {
        TrackUtil.logEvent("detail_reduce_quota_click");
        interestConfig.adjustQuota(false);
    }

    void onAddTerm() {
        TrackUtil.logEvent("detail_add_term_click");
        interestConfig.adjustTerm(true);
    }

    void onReduceTerm() {
        TrackUtil.logEvent("detail_reduce_term_click");
        interestConfig.adjustTerm(false);
    }

    void onGoStore() {
        TrackUtil.logEvent("detail_goto_store_click");
        if (platformBean != null && platformBean.getLauncherUrl() != null) {
            // 1. log apply
            presenter.logApply(loanId);
            String url = platformBean.getLauncherUrl();
            // 2. go to store
            if (LauncherMode.STORE == platformBean.getLauncherMode()) {
                if (!UrlUtil.isGooglePlayLink(url)) {
                    CommonWebActivity.actionStart(this,
                            platformBean.getName(), url);
                    return;
                }
                // 2.1 if jump directly
                TrackUtil.logEvent("detail_open_gp_by_gp");
                GooglePlayUtil.openGooglePlay(this, url);
            } else {
                TrackUtil.logEvent("detail_open_gp_by_web");
                // open browser directly
                // we do not have the permission, browser is a better choice
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                if (browserIntent.resolveActivity(getPackageManager()) != null) {
                    startActivity(browserIntent);
                }
            }
        }
    }

    private void setCashLoanBean(CashLoanBean cashLoanBean) {
        if (cashLoanBean == null) {
            return;
        }

        if (!TextUtils.isEmpty(cashLoanBean.getName())) {
            tvLoanName.setText(cashLoanBean.getName());
        }

        float passRate = 5 * cashLoanBean.getPassRate() / 100.0f;
        tvPassRate.setText(StringUtil.oneDigit(passRate));

        float score = cashLoanBean.getScore() / 10.0f;
        tvScore.setText(StringUtil.oneDigit(score));
        llScoreTag.setVisibility(View.VISIBLE);
        tvScoreTag.setText(StringUtil.oneDigit(score));
    }

    private void setCashLoanExtensionBean(CashLoanExtensionBean cashLoanExtensionBean) {
        if (cashLoanExtensionBean == null) {
            return;
        }

        ImageLoader.loadWithDefaultIcon(this, cashLoanExtensionBean.getIcon(), ivIcon, 8);

        if (!TextUtils.isEmpty(cashLoanExtensionBean.getAreas())) {
            tvArea.setText(cashLoanExtensionBean.getAreas());
            tvArea.setVisibility(View.VISIBLE);
        }

        if (!TextUtils.isEmpty(cashLoanExtensionBean.getReviewDescription())) {
            expandablePanel.setText(cashLoanExtensionBean.getReviewDescription());
        }
    }

    private void setPlatformBean(PlatformBean platformBean) {
        this.platformBean = platformBean;

        if (platformBean != null) {
            tvGoStore.setVisibility(View.VISIBLE);
        }
    }

    private void onCashLoanDetail(CashLoanDetailBean cashLoanDetailBean) {
        CashLoanBean cashLoanBean = cashLoanDetailBean.getLoan();
        CashLoanExtensionBean cashLoanExtensionBean = cashLoanDetailBean.getLoanExtension();
        PlatformBean platformBean = cashLoanDetailBean.getPlatform();

        // top loan info
        setCashLoanBean(cashLoanBean);
        setCashLoanExtensionBean(cashLoanExtensionBean);
        setPlatformBean(platformBean);

        // loan animated ring chart
        interestConfig.setCashLoanBean(cashLoanBean);

        // issue time & notice
        if (!TextUtils.isEmpty(cashLoanBean.getLoanTimeStr())) {
            tvIssueTime.setVisibility(View.VISIBLE);
            tvIssueTime.setText("Waktu pinjaman tercepat: " + cashLoanBean.getLoanTimeStr());
        }

        if (!TextUtils.isEmpty(cashLoanBean.getNotice())) {
            tvLoanNotice.setVisibility(View.VISIBLE);
            tvLoanNotice.setText(cashLoanBean.getNotice());
        }
    }

    private void onCashLoanProcedure(CashLoanProceduerBean cashLoanProceduerBean) {
        // load apply procedure
        ArrayList<CashLoanProcedureStepBean> applyStepList = cashLoanProceduerBean.getApply();
        if (applyStepList != null && applyStepList.size() > 0) {
            rvApply.setVisibility(View.VISIBLE);

            cashLoanApplyProcedureAdapter.setItemList(applyStepList);
        } else {
            rvApply.setVisibility(View.GONE);
        }

        // load audit procedure
        ArrayList<CashLoanProcedureStepBean> auditStepList = cashLoanProceduerBean.getAudit();
        if (auditStepList != null && auditStepList.size() > 0) {
            rvAudit.setVisibility(View.VISIBLE);

            cashLoanAuditProcedureAdapter.setItemList(auditStepList);
        } else {
            rvAudit.setVisibility(View.GONE);
        }
    }

    @Override
    public void showError(String action, String code, String message) {
        super.showError(action, code, message);

        if (ApiActions.CASH_LOAN_DETAIL.equals(action)) {
            // we didn't get the cash loan bean, we need to exit
            postDelayed(() -> finish(), 1000);
        }
    }

    @Override
    public void onApiResult(String action, Object result) {
        if (ApiActions.CASH_LOAN_DETAIL.equals(action)) {
            onCashLoanDetail((CashLoanDetailBean) result);
        } else if (ApiActions.CASH_LOAN_PROCEDURE.equals(action)) {
            onCashLoanProcedure((CashLoanProceduerBean) result);
        }
    }

    private void loadData() {
        // load loan info
        presenter.getLoanDetail(ApiActions.CASH_LOAN_DETAIL, loanId);

        // load flow
        presenter.getLoanProcedure(loanId);
    }

    private void init() {
        // config the audit process list
        cashLoanApplyProcedureAdapter = new CashLoanProcedureAdapter(this);
        this.rvApply.setAdapter(cashLoanApplyProcedureAdapter);
        rvApply.setLayoutManager(new LinearLayoutManager(this));
        rvApply.setNestedScrollingEnabled(true);
        rvApply.setFocusable(false);

        // config the apply process list
        cashLoanAuditProcedureAdapter = new CashLoanProcedureAdapter(this);
        this.rvAudit.setAdapter(cashLoanAuditProcedureAdapter);
        rvAudit.setLayoutManager(new LinearLayoutManager(this));
        rvAudit.setNestedScrollingEnabled(false);
        rvAudit.setFocusable(false);

        // config sliding tab bar
        List<String> titles = new ArrayList<String>() {
            {
                add(getString(R.string.loan_tab_detail));
                add(getString(R.string.loan_tab_flow));
            }
        };

        List<View> views = new ArrayList<View>() {
            {
                add(layoutLoanInfo);
                add(layoutLoanProcedure);
            }
        };
        slidingTabBar.setScrollView(scrollView, this, titles, views);

        loadData();

        // config refresh
        layoutRefresh.setOnRefreshListener((refreshLayout) -> {
            this.loadData();
            refreshLayout.finishRefresh(50);
        });
    }

    @Override
    public void onScrollChanged() {
//        scrollView.getLocationOnScreen();
    }

    private class InterestConfig {
        BigDecimal quota = BigDecimal.ZERO;
        BigDecimal interestRate = BigDecimal.ZERO;
        int days = 0;
        CashLoanBean cashLoanBean;

        private OnInterestConfigChangeListener listener;

        public InterestConfig setListener(OnInterestConfigChangeListener listener) {
            this.listener = listener;
            return this;
        }

        void adjustQuota(boolean isAdd) {
            BigDecimal resultQuota;
            if (cashLoanBean == null) {
                return;
            }

            BigDecimal quotaStep = cashLoanBean.getQuotaStep();
            if (quotaStep == null || quotaStep.equals(BigDecimal.ZERO)) {
                return;
            }

            if (isAdd) {
                resultQuota = quota.add(quotaStep);
            } else {
                resultQuota = quota.subtract(quotaStep);
            }

            if ((resultQuota.intValue() <= cashLoanBean.getMaxQuota().intValue()) &&
                    resultQuota.intValue() >= cashLoanBean.getMinQuota().intValue()) {
                this.setQuota(resultQuota);
                updateUI();
            }
        }

        void adjustTerm(boolean isAdd) {
            if (cashLoanBean == null) {
                return;
            }

            int resultDays;
            int termStep = cashLoanBean.getTermStep();

            if (termStep == 0) {
                return;
            }

            if (isAdd) {
                resultDays = this.days + termStep;
            } else {
                resultDays = this.days - termStep;
            }

            if (resultDays >= cashLoanBean.getMinTerm() &&
                    resultDays <= cashLoanBean.getMaxTerm()) {
                this.setDays(resultDays);
                updateUI();
            }
        }

        private void setQuota(BigDecimal quota) {
            if (quota != null && !quota.equals(this.quota)) {
                this.quota = quota;
            }
        }

        private void setDays(int days) {
            if (days != this.days) {
                this.days = days;
            }
        }

        private void setInterestRate(BigDecimal interestRate) {
            if (interestRate != null && !interestRate.equals(this.interestRate)) {
                this.interestRate = interestRate;
            }
        }

        private void setCashLoanBean(CashLoanBean cashLoanBean) {
            this.cashLoanBean = cashLoanBean;
            setQuota(cashLoanBean.getMinQuota());
            setDays(cashLoanBean.getMinTerm());
            setInterestRate(cashLoanBean.getInterestRate());

            updateUI();
        }

        private void updateUI() {
            BigDecimal interest = calculateInterest();

            // update UI
            if (listener != null) {
                listener.onChange(interest, quota, days);
            }
        }

        BigDecimal calculateInterest() {
            return quota.multiply(interestRate).multiply(new BigDecimal(days));
        }
    }

    public interface OnInterestConfigChangeListener {
        void onChange(BigDecimal interest, BigDecimal quota, int days);
    }
}
