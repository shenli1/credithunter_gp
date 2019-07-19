package com.id.cash.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.id.cash.R;
import com.id.cash.bean.CashLoanBean;
import com.id.cash.common.ImageLoader;
import com.id.cash.common.StringUtil;
import com.id.cash.widget.recyclerview.BaseRecyclerViewAdapter;
import com.id.cash.widget.recyclerview.BaseRecyclerViewHolder;

/**
 * Created by linchen on 2018/5/28.
 */

public class SearchCashLoanResultAdapter extends BaseRecyclerViewAdapter<CashLoanBean, BaseRecyclerViewHolder> {
    public SearchCashLoanResultAdapter(Context context) {
        super(context);
    }

    @Override
    public BaseRecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new BaseRecyclerViewHolder(inflate(parent, R.layout.item_search_cashloan));
    }

    protected void onBindViewData(BaseRecyclerViewHolder baseRecyclerViewHolder, int i, CashLoanBean bean) {
        ImageView iconView = baseRecyclerViewHolder.findViewById(R.id.iv_search_loan_icon);
        TextView tvLoanName = baseRecyclerViewHolder.findViewById(R.id.tv_search_loan_name);
        TextView tvMaxQuota = baseRecyclerViewHolder.findViewById(R.id.tv_search_max_quota);
        TextView tvInterest = baseRecyclerViewHolder.findViewById(R.id.tv_search_interest);
        TextView tvProcessTime = baseRecyclerViewHolder.findViewById(R.id.tv_search_process_time);
        TextView tvScore = baseRecyclerViewHolder.findViewById(R.id.tv_search_score_tag);
        TextView tvPassrate = baseRecyclerViewHolder.findViewById(R.id.tv_pass_rate);

        ImageLoader.loadWithDefaultIcon(this.context, bean.getIcon(), iconView);
        tvLoanName.setText(bean.getName());

        tvMaxQuota.setText(StringUtil.formatResourceString(this.context, R.string.search_max_quota_format,
                StringUtil.formatCurrency(bean.getMaxQuota())));

        String interest = StringUtil.formatInterestRate(bean.getInterestRate());
        String interestText = StringUtil.formatResourceString(this.context, R.string.search_interest_format, interest);
        tvInterest.setText(interestText);

        tvProcessTime.setText(
                StringUtil.formatResourceString(this.context, R.string.search_process_time_format, bean.getLoanTimeStr()));

        float score = bean.getScore() / 10.0f;
        tvScore.setText(StringUtil.oneDigit(score));

        if (tvPassrate != null) {
            String passRate = StringUtil.formatResourceString(context, R.string.cashloan_cell_pass_rate_format, "" + bean.getPassRate());
            tvPassrate.setText(passRate);
        }
    }
}
