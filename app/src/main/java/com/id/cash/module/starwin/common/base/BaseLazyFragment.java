package com.id.cash.module.starwin.common.base;

import android.support.v4.app.Fragment;

import com.id.cash.common.LogUtil;

/**
 * Created by linchen on 2018/7/18.
 */
public abstract class BaseLazyFragment extends Fragment {
    protected boolean isVisible;

    /**
     * to judge if is visible to user
     * @param isVisibleToUser
     */
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        if(getUserVisibleHint()) {
            this.isVisible = true;
            onVisible();
        } else {
            this.isVisible = false;
            onInvisible();
        }
    }


    /**
     * when the  fragment is not visible to user
     */
    protected void onInvisible() {
        LogUtil.v("do onInvisible");
    }

    /**
     * when the fragment is visible to user
     *
     * lazyload
     */
    protected void onVisible() {
        LogUtil.v("do onVisible");
        lazyLoad();
    }

    protected abstract void lazyLoad();
}
