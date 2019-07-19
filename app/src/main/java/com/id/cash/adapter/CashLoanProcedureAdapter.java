package com.id.cash.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.id.cash.R;
import com.id.cash.bean.CashLoanProcedureStepBean;
import com.id.cash.common.ImageLoader;
import com.id.cash.widget.DashedLineView;
import com.id.cash.widget.recyclerview.BaseRecyclerViewAdapter;
import com.id.cash.widget.recyclerview.BaseRecyclerViewHolder;

/**
 * Created by linchen on 2018/5/19.
 */

public class CashLoanProcedureAdapter extends BaseRecyclerViewAdapter<CashLoanProcedureStepBean, BaseRecyclerViewHolder> {
    public CashLoanProcedureAdapter(Context context) {
        super(context);
    }

    @Override
    public BaseRecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new BaseRecyclerViewHolder(inflate(parent, R.layout.item_step_audit_process));
    }

    protected void onBindViewData(BaseRecyclerViewHolder baseRecyclerViewHolder, int i, CashLoanProcedureStepBean bean) {
        boolean isLast = i == (itemList.size() - 1);

        ImageView imageView = baseRecyclerViewHolder.findViewById(R.id.iv_step_audit_icon);
        TextView titleText = baseRecyclerViewHolder.findViewById(R.id.tv_step_audit_title);
        TextView descText = baseRecyclerViewHolder.findViewById(R.id.tv_step_audit_desc);
        DashedLineView dashedLineView = baseRecyclerViewHolder.findViewById(R.id.dash_step);

        ImageLoader.loadWithDefaultIcon(this.context, bean.getIcon(), imageView);
        titleText.setText(bean.getTitle());
        descText.setText(bean.getDescription());

        dashedLineView.setVisibility(isLast ? View.INVISIBLE : View.VISIBLE);
    }
}
