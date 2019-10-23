package com.zxycloud.zszw.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.zxycloud.common.base.fragment.SupportFragment;

public class BasePagerAdapter extends FragmentPagerAdapter {
    private String[] mTitles;
    private SupportFragment[] fragments;
    public BasePagerAdapter(FragmentManager fm, String... titles) {
        super(fm);
        mTitles = titles;
    }

    public BasePagerAdapter setFragments(SupportFragment... fragments){
        this.fragments = fragments;
        return this;
    }

    @Override
    public Fragment getItem(int i) {
        return fragments[i];
    }

    @Override
    public int getCount() {
        return mTitles.length;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mTitles[position];
    }
}
