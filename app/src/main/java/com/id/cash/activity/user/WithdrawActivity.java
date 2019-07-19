package com.id.cash.activity.user;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.hwangjr.rxbus.RxBus;

import java.math.BigDecimal;

import com.id.cash.R;
import com.id.cash.bean.BankCardBean;
import com.id.cash.bean.UserBean;
import com.id.cash.common.LogUtil;
import com.id.cash.common.StringUtil;
import com.id.cash.common.TrackUtil;
import com.id.cash.module.starwin.common.base.BaseActivity;
import com.id.cash.module.starwin.common.bus.BusAction;
import com.id.cash.module.starwin.common.bus.WithdrawSuccessEvent;
import com.id.cash.network.ApiActions;
import com.id.cash.presenter.WithDrawPresenter;
import com.id.cash.widget.DebouncingOnClickListener;
import com.id.cash.widget.dialog.WithdrawConfirmDialog;

public class WithdrawActivity extends BaseActivity<WithDrawPresenter> {
    private TextView tvTotalBonusPoint;
    private EditText etUserName;
    private EditText etBankName;
    private EditText etBankCardNo;

    // caches the user's bonus point
    private UserBean userBean;

    public static void actionStart(final Context context) {
        actionStart(context, false);
    }

    public static void actionStart(final Context context, final boolean withNewTask) {
        Intent intent = new Intent(context, WithdrawActivity.class);

        if (withNewTask) {
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }

        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // load user total bonus amount & bankcard info
        loadData();
    }

    @Override
    protected void bindViews() {
        super.bindViews();

        etUserName = findViewById(R.id.et_withdraw_user_name);
        etBankName = findViewById(R.id.et_withdraw_bank_name);
        etBankCardNo = findViewById(R.id.et_withdraw_cardno);
        tvTotalBonusPoint = findViewById(R.id.tv_withdraw_total_bonuspoint);

        TextView tvTitle = findViewById(R.id.tv_actionbar_title);
        tvTitle.setText(R.string.withdraw_result_title);
    }

    @Override
    protected void bindEvents() {
        super.bindEvents();

        View btnWithdraw = findViewById(R.id.tv_withdraw_button);
        btnWithdraw.setOnClickListener(new DebouncingOnClickListener() {
            @Override
            public void doClick(View v) {
                onWithdrawClicked();
            }
        });
    }

    @Override
    protected WithDrawPresenter createPresenter() {
        return new WithDrawPresenter(this);
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_withdraw;
    }

    public void onWithdrawClicked() {
        TrackUtil.logEvent("withdraw_submit_click");
        String userName = etUserName.getText().toString();
        String bankName = etBankName.getText().toString();
        String bankNo = etBankCardNo.getText().toString();

        BankCardBean bankInfo = new BankCardBean();
        bankInfo.setUserName(userName.trim());
        bankInfo.setBankName(bankName.trim());
        bankInfo.setBankAccount(bankNo.trim());

        presenter.submitBankCardInfo(ApiActions.WITHDRAW_REFRESH_BANK_CARD_INFO, bankInfo);
    }

    private void loadData() {
        // retrieve user bankcard info & total bonuspoint info
        presenter.loadBankcardSummaryInfo(ApiActions.BANKCARD_INFO_SUMMARY);
    }

    @Override
    public void onApiResult(String action, Object result) {
        if (ApiActions.BANKCARD_INFO_SUMMARY.equals(action)) {
            onBankcardSummaryInfo((WithDrawPresenter.BankcardInfoSummaryBean) result);
        } else if (ApiActions.WITHDRAW_REFRESH_BANK_CARD_INFO.equals(action)) {
            // 1. update bankcard info (succeed)
            if (result != null) {
                // 2. withdraw confirm dialog
                BankCardBean bankCardBean = (BankCardBean) result;
                if (userBean != null) {
                    WithdrawConfirmDialog withdrawConfirmDialog = new WithdrawConfirmDialog(this, bankCardBean, userBean.getTotalPoint());
                    withdrawConfirmDialog
                            .show();
                    withdrawConfirmDialog.setOnWithdrawClickListener((dialog) -> {
                        // 3. on confirm to submit withdraw
                        presenter.submitWithdraw(ApiActions.SUBMIT_WITHDRAW, bankCardBean, userBean.getTotalPoint());
                    });
                } else {
                    LogUtil.e("withdraw with null userbean");
                }
            } else {
                LogUtil.e("withdraw with null bankcard bean");
            }
        } else if (ApiActions.SUBMIT_WITHDRAW.equals(action)) {
            // 4. show the withdraw result
            WithdrawSubmitResultActivity.actionStart(this);
            RxBus.get().post(BusAction.WITHDRAW_SUCCESS, new WithdrawSuccessEvent());
            finish();
        }
    }

    private void onBankcardSummaryInfo(WithDrawPresenter.BankcardInfoSummaryBean summaryBean) {
        if (summaryBean != null) {
            // populate user's total bonus point
            UserBean userBean = summaryBean.getUserBean();
            if (userBean != null) {
                this.userBean = userBean;
                tvTotalBonusPoint.setText("RP. " + StringUtil.formatCurrency(new BigDecimal(userBean.getTotalPoint())));
            }

            // populate bankcard info
            BankCardBean bankCardBean = summaryBean.getBankCardBean();
            if (bankCardBean != null) {
                if (!TextUtils.isEmpty(bankCardBean.getUserName())) {
                    etUserName.setText(bankCardBean.getUserName().trim());
                }
                if (!TextUtils.isEmpty(bankCardBean.getBankName())) {
                    etBankName.setText(bankCardBean.getBankName().trim());
                }
                if (!TextUtils.isEmpty(bankCardBean.getBankAccount())) {
                    etBankCardNo.setText(bankCardBean.getBankAccount().trim());
                }
            }
        }
    }
}
