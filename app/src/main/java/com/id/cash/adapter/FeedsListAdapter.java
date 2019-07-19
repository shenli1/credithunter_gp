package com.id.cash.adapter;

import android.content.Context;
import android.widget.ImageView;
import android.widget.TextView;

import com.id.cash.R;
import com.id.cash.bean.FeedsBean;
import com.id.cash.common.ImageLoader;
import com.id.cash.widget.recyclerview.BaseReloadableRecyclerAdapter;

/**
 * Created by linchen on 2018/7/19.
 */
public class FeedsListAdapter extends BaseReloadableRecyclerAdapter<FeedsBean> {
    private Context context;

    public FeedsListAdapter(Context context) {
        super(R.layout.item_feeds);

        this.context = context;
    }

    @Override
    protected void bindViewHolder(CommonViewHolder viewHolder, FeedsBean bean, int position) {
        ImageView iconView = viewHolder.findViewById(R.id.iv_icon_feeds);
        TextView tvTitle = viewHolder.findViewById(R.id.tv_title_feeds);
        TextView tvType = viewHolder.findViewById(R.id.tv_type_feeds);
        TextView tvLook = viewHolder.findViewById(R.id.tv_look_feeds);
        TextView tvLike = viewHolder.findViewById(R.id.tv_like_feeds);

        ImageLoader.loadWithFeedsIcon(context, bean.getTitleImg(), iconView);
        tvTitle.setText(bean.getTitle());
        tvLook.setText(bean.getSeenNum());
        tvLike.setText(bean.getLikeNum());
        tvType.setText(bean.getType());
    }
}
