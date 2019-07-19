package com.id.cash.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.ViewGroup;
import android.widget.TextView;

import com.id.cash.R;
import com.id.cash.bean.InviteReferralBean;
import com.id.cash.common.StringUtil;
import com.id.cash.widget.recyclerview.BaseRecyclerViewAdapter;
import com.id.cash.widget.recyclerview.BaseRecyclerViewHolder;

/**
 * Created by linchen on 2018/5/29.
 */

public class InviteReferralAdapter extends BaseRecyclerViewAdapter<InviteReferralBean, BaseRecyclerViewHolder> {
    public InviteReferralAdapter(Context context) {
        super(context);
    }

    @Override
    public BaseRecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new BaseRecyclerViewHolder(inflate(parent, R.layout.item_invite_referral));
    }

    protected void onBindViewData(BaseRecyclerViewHolder baseRecyclerViewHolder, int i, InviteReferralBean bean) {
        TextView tvMobile = baseRecyclerViewHolder.findViewById(R.id.tv_invite_mobile);
        TextView tvAmountChange = baseRecyclerViewHolder.findViewById(R.id.tv_invite_bonuspoint);

        String people = TextUtils.isEmpty(bean.getMobile()) ? context.getString(R.string.invite_anonymous) : bean.getMobile();
        tvMobile.setText(people);
        tvAmountChange.setText(StringUtil.formatWithSign(bean.getPoint()));
    }
}

