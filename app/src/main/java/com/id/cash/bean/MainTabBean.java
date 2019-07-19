package com.id.cash.bean;

import android.support.annotation.DrawableRes;
import android.support.annotation.IdRes;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;

/**
 * Created by linchen on 2018/6/4.
 */

public class MainTabBean {
    private Fragment fragment;
    private int title;
    private int icon;
    private int iconSelected;

    public MainTabBean(Fragment fragment,  @StringRes int title, @DrawableRes int icon, @DrawableRes int iconSelected) {
        this.fragment = fragment;
        this.title = title;
        this.icon = icon;
        this.iconSelected = iconSelected;
    }

    public Fragment getFragment() {
        return fragment;
    }

    public @StringRes int getTitle() {
        return title;
    }

    public int getIcon() {
        return icon;
    }

    public int getIconSelected() {
        return iconSelected;
    }
}
