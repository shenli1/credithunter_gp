package com.id.cash.adapter;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

import com.id.cash.bean.MainTabBean;

/**
 * Created by linchen on 2018/6/4.
 */

public class MainViewPagerAdapter extends FragmentPagerAdapter {
    private List<MainTabBean> mainTabBeanList;

    public MainViewPagerAdapter(FragmentManager fragmentManager, List<MainTabBean> tabBeanList) {
        super(fragmentManager);
        this.mainTabBeanList = tabBeanList;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return super.getPageTitle(position);
    }

    @Override
    public Fragment getItem(int position) {
        return mainTabBeanList.get(position).getFragment();
    }

    @Override
    public int getCount() {
        return mainTabBeanList.size();
    }
}
