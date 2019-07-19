package com.id.cash.widget;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.id.cash.R;

/**
 * Created by linchen on 2018/5/28.
 */

public class NetworkLoadingLayout extends FrameLayout  {
    private View noDataView;
    private View networkErrorView;
    private View contentView;

    private TextView tvNetworkErrorDesc;
    private OnRefreshCallback refreshCallback;

    public enum Mode {
        CONTENT,
        NO_DATA,
        ERROR,
    }

    public interface OnRefreshCallback {
        void onRefreshClicked();
    }

    public NetworkLoadingLayout(@NonNull Context context) {
        this(context, null);
    }

    public NetworkLoadingLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, -1);
    }

    public NetworkLoadingLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        LayoutInflater inflater = LayoutInflater.from(getContext());
        noDataView = inflater.inflate(R.layout.sub_no_data, this, false);
        networkErrorView = inflater.inflate(R.layout.sub_network_error, this, false);
        addView(noDataView);
        addView(networkErrorView);
    }

    public void setMode(Mode mode) {
        switch (mode) {
            case ERROR:
                networkErrorView.setVisibility(VISIBLE);
                noDataView.setVisibility(INVISIBLE);
                contentView.setVisibility(INVISIBLE);
                break;
            case NO_DATA:
                networkErrorView.setVisibility(INVISIBLE);
                noDataView.setVisibility(VISIBLE);
                contentView.setVisibility(INVISIBLE);
                break;
            case CONTENT:
                networkErrorView.setVisibility(INVISIBLE);
                noDataView.setVisibility(INVISIBLE);
                contentView.setVisibility(VISIBLE);
                break;
        }
    }

    void onRefreshClick() {
        if (refreshCallback != null) {
            refreshCallback.onRefreshClicked();
        }
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        bindViews();
    }

    public OnRefreshCallback getRefreshCallback() {
        return refreshCallback;
    }

    public void setRefreshCallback(OnRefreshCallback refreshCallback) {
        this.refreshCallback = refreshCallback;
    }

    private void bindViews() {
        contentView = getChildAt(2);

        tvNetworkErrorDesc = findViewById(R.id.tv_network_error_desc);
        setMode(Mode.CONTENT);

        View btnNetworkReload = this.findViewById(R.id.tv_network_reload);
        btnNetworkReload.setOnClickListener(new DebouncingOnClickListener() {
            @Override
            public void doClick(View v) {
                onRefreshClick();
            }
        });

    }
}
