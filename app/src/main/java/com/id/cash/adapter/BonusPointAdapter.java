package com.id.cash.adapter;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.TextView;

import com.id.cash.R;
import com.id.cash.bean.BonusPointBean;
import com.id.cash.common.StringUtil;
import com.id.cash.widget.recyclerview.BaseRecyclerViewAdapter;
import com.id.cash.widget.recyclerview.BaseRecyclerViewHolder;

/**
 * Created by linchen on 2018/5/28.
 */

public class BonusPointAdapter extends BaseRecyclerViewAdapter<BonusPointBean, BaseRecyclerViewHolder> {
    public BonusPointAdapter(Context context) {
        super(context);
    }

    @Override
    public BaseRecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new BaseRecyclerViewHolder(inflate(parent, R.layout.item_bonuspoint));
    }

    protected void onBindViewData(BaseRecyclerViewHolder baseRecyclerViewHolder, int i, BonusPointBean bean) {
        TextView tvSource = baseRecyclerViewHolder.findViewById(R.id.tv_bonuspoint_source);
        TextView tvTime = baseRecyclerViewHolder.findViewById(R.id.tv_bonuspoint_time);
        TextView tvAmountChange = baseRecyclerViewHolder.findViewById(R.id.tv_bonuspoint_change);

        tvSource.setText(bean.getSource());
        tvTime.setText(StringUtil.formatServerTimeStamp(bean.getCreateTime()));
        tvAmountChange.setText(StringUtil.formatWithSign(bean.getPoint()));
    }
}

