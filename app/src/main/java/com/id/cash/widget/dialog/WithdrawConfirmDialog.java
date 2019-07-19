package com.id.cash.widget.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.view.View;
import android.widget.TextView;

import java.math.BigDecimal;

import com.id.cash.R;
import com.id.cash.bean.BankCardBean;
import com.id.cash.common.StringUtil;
import com.id.cash.common.TrackUtil;

/**
 * Created by liubin on 2018/6/5.
 */

public class WithdrawConfirmDialog extends DialogBase {
    private Activity activity;
    public Dialog dialog;
    private TextView tvPrompt;

    private long amount;
    private BankCardBean bankCardBean;
    private OnWithdrawClickListener onWithdrawClickListener;

    public interface OnWithdrawClickListener {
        void onConfirmClick(WithdrawConfirmDialog dialog);
    }

    public WithdrawConfirmDialog(Activity activity, BankCardBean bankCardBean, long amount) {
        super(activity);
        this.activity = activity;
        this.amount = amount;
        this.bankCardBean = bankCardBean;

        String prompt = String.format(activity.getString(R.string.withdraw_confirm_format),
                "Rp. " + StringUtil.formatCurrency(new BigDecimal(amount)),
                bankCardBean.getBankName(),
                bankCardBean.getBankAccount());
        tvPrompt.setText(prompt);
    }

    @Override
    int getContentView() {
        return R.layout.withdraw_confirm_dialog;
    }

    @Override
    protected View bindViews() {
        View view = super.bindViews();
        tvPrompt = view.findViewById(R.id.tv_withdraw_prompt);
        return view;
    }

    @Override
    protected void bindEvents() {
        super.bindEvents();
    }

    @Override
    protected boolean shouldCancelOnTouchOutside() {
        return true;
    }

    public void setOnWithdrawClickListener(OnWithdrawClickListener onWithdrawClickListener) {
        this.onWithdrawClickListener = onWithdrawClickListener;
    }

    @Override
    public void onCancel() {
        super.onCancel();
        TrackUtil.logEvent("withdraw_confirm_cancel_click");
    }

    @Override
    protected void onOk() {
        super.onCancel();
        TrackUtil.logEvent("withdraw_confirm_ok_click");

        if (onWithdrawClickListener != null) {
            onWithdrawClickListener.onConfirmClick(this);
        }
    }
}
