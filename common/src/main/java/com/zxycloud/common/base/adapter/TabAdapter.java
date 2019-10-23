package com.zxycloud.common.base.adapter;

import android.content.Context;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.zxycloud.common.utils.CommonUtils;

/**
 * ViewPager的Tab适配器
 *
 * @author leiming
 * @date 2018/4/26.
 */

public class TabAdapter extends FragmentPagerAdapter {
    private Context context;
    private Fragment[] listFragment;                         //fragment列表
    private String[] listTitle;                              //tab名的列表
    private int[] listTitleRes;                              //tab名的列表

    public TabAdapter(FragmentManager fm, Fragment[] listFragment, String[] listTitle) {
        super(fm);
        this.listFragment = listFragment;
        this.listTitle = listTitle;
    }

    public TabAdapter(Context context, FragmentManager fm, Fragment[] listFragment, @StringRes int[] listTitleRes) {
        super(fm);
        this.listFragment = listFragment;
        this.listTitleRes = listTitleRes;
        this.context = context;
    }

    @Override
    public Fragment getItem(int position) {
        return listFragment[position];
    }

    @Override
    public int getCount() {
        return listFragment.length;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return null != listTitleRes ? CommonUtils.string().getString(context, listTitleRes[position]) : listTitle[position];
    }
}
