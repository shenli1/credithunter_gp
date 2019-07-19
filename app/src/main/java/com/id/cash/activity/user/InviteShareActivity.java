package com.id.cash.activity.user;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.id.cash.R;
import com.id.cash.bean.ShareResultBean;
import com.id.cash.common.IntentUtil;
import com.id.cash.common.TrackUtil;
import com.id.cash.module.starwin.common.base.BaseActivity;
import com.id.cash.network.ApiActions;
import com.id.cash.presenter.SharePresenter;
import com.id.cash.widget.DebouncingOnClickListener;
import com.id.cash.widget.InviteShareButton;

import static com.id.cash.presenter.SharePresenter.SHARE_RESULT_TAG;

/**
 * Created by linchen on 2018/5/29.
 */

public class InviteShareActivity extends BaseActivity<SharePresenter> {
    private InviteShareButton isbWhatsApp;
    private InviteShareButton isbFacebook;
    private InviteShareButton isbMore;

    public static void actionStart(Activity activity) {
        Intent intent = new Intent(activity, InviteShareActivity.class);
        activity.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        activity.startActivity(intent);
    }

    @Override
    protected SharePresenter createPresenter() {
        return new SharePresenter(this);
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_invite_share;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void bindViews() {
        super.bindViews();

        isbWhatsApp = findViewById(R.id.isb_invite_share_whatsapp);
        isbFacebook = findViewById(R.id.isb_invite_share_fb);
        isbMore = findViewById(R.id.isb_invite_share_more);
    }

    private int selectedIndex = -1;

    @Override
    protected void bindEvents() {
        super.bindEvents();

        View closeButton = findViewById(R.id.ib_invite_share_close);
        closeButton.setOnClickListener(new DebouncingOnClickListener() {
            @Override
            public void doClick(View v) {
                finish();
            }
        });

        View whatsAppShare = findViewById(R.id.isb_invite_share_whatsapp);
        whatsAppShare.setOnClickListener(new DebouncingOnClickListener() {
            @Override
            public void doClick(View v) {
                onWhatsAppClick();
            }
        });

        View fbShare = findViewById(R.id.isb_invite_share_fb);
        fbShare.setOnClickListener(new DebouncingOnClickListener() {
            @Override
            public void doClick(View v) {
                onFacebookClick();
            }
        });

        View moreShare = findViewById(R.id.isb_invite_share_more);
        moreShare.setOnClickListener(new DebouncingOnClickListener() {
            @Override
            public void doClick(View v) {
                onMoreClick();
            }
        });
    }

    private void onWhatsAppClick() {
        setSelectedIndex(0);
        TrackUtil.logEvent("invite_share_whatsapp_click");
        presenter.getShareData(ApiActions.SHARE_INFO, SharePresenter.ShareChannel.WHATSAPP, SharePresenter.ShareMethod.INVITING);
    }

    void onFacebookClick() {
        setSelectedIndex(1);
        TrackUtil.logEvent("invite_share_fb_click");
        presenter.getShareData(ApiActions.SHARE_INFO, SharePresenter.ShareChannel.FACEBOOK, SharePresenter.ShareMethod.INVITING);
    }

    void onMoreClick() {
        setSelectedIndex(2);
        TrackUtil.logEvent("invite_share_more_click");
        presenter.getShareData(ApiActions.SHARE_INFO, SharePresenter.ShareChannel.OTHER, SharePresenter.ShareMethod.INVITING);
    }

    public void setSelectedIndex(int selectedIndex) {
        this.selectedIndex = selectedIndex;

        updateSelected();
    }

    void updateSelected() {
        InviteShareButton[] buttons = new InviteShareButton[3];
        buttons[0] = isbWhatsApp;
        buttons[1] = isbFacebook;
        buttons[2] = isbMore;

        for (InviteShareButton b : buttons) {
            b.setSelected(false);
        }
        if (selectedIndex >= 0 && selectedIndex < buttons.length) {
            buttons[selectedIndex].setSelected(true);
        }
    }

    @Override
    protected int getStatusBarDrawable() {
        return 0;
    }

    @Override
    public void onApiResult(String action, Object result) {
        if (ApiActions.SHARE_INFO.equals(action)) {
            // share data ready, call the actual share
            SharePresenter.ShareAction shareAction = (SharePresenter.ShareAction) result;
            presenter.share(shareAction.getShareChannel(), shareAction.getShareInfoBean(), this);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == SharePresenter.SHARE_REQUEST_CODE) {
            // handle share error
            if (data != null) {
                ShareResultBean shareResultBean = IntentUtil.getJsonParameter(data, SHARE_RESULT_TAG, ShareResultBean.class);
                if (shareResultBean != null) {
                    if (!TextUtils.isEmpty(shareResultBean.getErrorMessage())) {
                        Toast.makeText(this, shareResultBean.getErrorMessage(), Toast.LENGTH_LONG)
                                .show();
                    }
                }
            }
        }
    }
}
