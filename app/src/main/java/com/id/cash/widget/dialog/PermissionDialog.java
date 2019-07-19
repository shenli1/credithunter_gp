package com.id.cash.widget.dialog;

import android.app.Activity;
import android.view.View;
import android.widget.TextView;

import com.id.cash.R;
import com.id.cash.common.TrackUtil;

/**
 * Created by linchen on 2018/6/9.
 */

public class PermissionDialog extends DialogBase {

    @Override
    int getContentView() {
        return R.layout.dialog_permission;
    }

    public PermissionDialog(Activity activity) {
        super(activity);
        dialog.setCancelable(false);
    }

    @Override
    protected View bindViews() {
        View view = super.bindViews();
        TextView tv = view.findViewById(R.id.tv_withdraw_prompt);
        String appName = activity.getString(R.string.app_name);
        String str = String.format(activity.getString(R.string.permission_dialog_content_format), appName, appName);
        tv.setText(str);
        return view;
    }

    @Override
    protected void bindEvents() {
        super.bindEvents();
    }

    @Override
    public void show() {
        super.show();
    }

    @Override
    protected void onCancel() {
        super.onCancel();
        TrackUtil.logEvent("permission_cancel_click");
    }

    @Override
    protected void onOk() {
        super.onOk();
        TrackUtil.logEvent("permission_ok_click");
    }
}
