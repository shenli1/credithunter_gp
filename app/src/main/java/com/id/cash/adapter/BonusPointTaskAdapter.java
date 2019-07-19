package com.id.cash.adapter;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.HashSet;

import com.id.cash.R;
import com.id.cash.bean.BonusPointTaskUIBean;
import com.id.cash.widget.recyclerview.BaseReloadableRecyclerAdapter;

/**
 * Created by linchen on 2018/6/4.
 */

public class BonusPointTaskAdapter extends BaseReloadableRecyclerAdapter<BonusPointTaskUIBean> {
    private Context context;

    public BonusPointTaskAdapter(Context context) {
        super(R.layout.item_bonuspoint_task);

        this.context = context;
    }

    @Override
    protected void bindViewHolder(CommonViewHolder viewHolder, BonusPointTaskUIBean bean, int position) {
        BonusPointTaskUIBean uiBean = BonusPointTaskUIBean.bonusPointTaskTypes.get(bean.getTemplateId());

        ImageView iconView = viewHolder.findViewById(R.id.iv_bonuspoint_task_icon);
        ImageView stampView = viewHolder.findViewById(R.id.iv_bonuspoint_task_stamp);
        ImageView countImageView = viewHolder.findViewById(R.id.iv_bonuspoint_task_count);
        ImageView arrowImageView = viewHolder.findViewById(R.id.iv_cell_arrow);

        TextView tvTaskName = viewHolder.findViewById(R.id.tv_bonuspoint_task_title);
        TextView tvDesc = viewHolder.findViewById(R.id.tv_bonuspoint_task_desc);

        iconView.setImageDrawable(context.getResources().getDrawable(uiBean.getIcon()));
        countImageView.setImageDrawable(context.getResources().getDrawable(uiBean.getType()));
        tvTaskName.setText(context.getResources().getString(uiBean.getTitle()));
        tvDesc.setText(context.getResources().getString(uiBean.getDesc()));

        if (bean.isStatus()) {
            stampView.setVisibility(View.VISIBLE);
        } else {
            stampView.setVisibility(View.GONE);
        }

        HashSet<String> arrowedTasks = new HashSet<String>() {
            {
                add(BonusPointTaskUIBean.BonusPointTaskType.SHARE);
                add(BonusPointTaskUIBean.BonusPointTaskType.INVITE);
            }
        };
        if (arrowedTasks.contains(bean.getTemplateId())) {
            arrowImageView.setVisibility(View.VISIBLE);
        } else {
            arrowImageView.setVisibility(View.GONE);
        }
    }
}
