package com.id.cash.adapter;

import android.content.Context;
import android.widget.ImageView;
import android.widget.TextView;

import com.id.cash.R;
import com.id.cash.bean.CashLoanBean;
import com.id.cash.common.ImageLoader;
import com.id.cash.common.StringUtil;
import com.id.cash.widget.recyclerview.BaseReloadableRecyclerAdapter;

/**
 * Created by linchen on 2018/6/2.
 */

public class CashLoanAdapter extends BaseReloadableRecyclerAdapter<CashLoanBean> {
    private Context context;

    public CashLoanAdapter(Context context) {
        super(R.layout.item_cashloan);

        this.context = context;
    }

    @Override
    protected void bindViewHolder(CommonViewHolder viewHolder, CashLoanBean bean, int position) {
        ImageView iconView = viewHolder.findViewById(R.id.iv_loan_icon);
        TextView tvLoanName = viewHolder.findViewById(R.id.tv_loan_name);
        TextView tvScore = viewHolder.findViewById(R.id.tv_score);
        TextView tvLoanIntro = viewHolder.findViewById(R.id.tv_loan_intro);
        TextView tvQuota = viewHolder.findViewById(R.id.tv_loan_quota);
        TextView tvInterest = viewHolder.findViewById(R.id.tv_interest_rate);
        TextView tvTime = viewHolder.findViewById(R.id.tv_time);
        TextView tvPassrate = viewHolder.findViewById(R.id.tv_pass_rate);

        ImageLoader.loadWithDefaultIcon(context, bean.getIcon(), iconView);
        tvLoanName.setText(bean.getName());
        tvScore.setText(StringUtil.oneDigit(bean.getScore() / 10.0f));
        tvLoanIntro.setText(bean.getReviewDescription());

        String amount = StringUtil.formatResourceString(context, R.string.common_money_with_unit_format, StringUtil.formatCurrency(bean.getMaxQuota()));
        tvQuota.setText(amount);

        if (tvInterest != null) {
            String interest = StringUtil.formatResourceString(context, R.string.cashloan_cell_interest_format, StringUtil.formatInterestRate(bean.getInterestRate()));
            tvInterest.setText(interest);
        }

        if (tvTime != null) {
            tvTime.setText(bean.getLoanTimeStr());
        }

        if (tvPassrate != null) {
            String passRate = StringUtil.formatResourceString(context, R.string.cashloan_cell_pass_rate_format, "" + bean.getPassRate());
            tvPassrate.setText(passRate);
        }
    }
}
