package com.zxycloud.zszw.fragment.service.records;

import android.os.Bundle;
import android.support.design.widget.CustomTabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.zxycloud.zszw.R;
import com.zxycloud.zszw.base.BaseBackFragment;
import com.zxycloud.zszw.event.type.RiskShowType;
import com.zxycloud.zszw.fragment.common.SearchHistoryFragment;
import com.zxycloud.zszw.fragment.service.records.viewpager.RecordRealTimeShowFragment;
import com.zxycloud.zszw.fragment.service.records.viewpager.RecordShowFragment;
import com.zxycloud.zszw.fragment.service.risk.ReportRiskListFragment;
import com.zxycloud.zszw.model.HistoryTypeBean;
import com.zxycloud.zszw.widget.MyTabSelectedListener;
import com.zxycloud.common.base.adapter.TabAdapter;
import com.zxycloud.common.utils.CommonUtils;

public class RecordStatisticsFragment extends BaseBackFragment {

    private BaseBackFragment[] showFragments;
    private int selectPosition;

    public static RecordStatisticsFragment newInstance() {
        Bundle args = new Bundle();
        RecordStatisticsFragment fragment = new RecordStatisticsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_custom_base;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        setToolbarTitle(R.string.records)
                .initToolbarNav()
                .setToolbarMenu(R.menu.menu_search, itemClickListener);

        CustomTabLayout recordTab = findViewById(R.id.ctl_tab);
        ViewPager recordPager = findViewById(R.id.vp_pager);

        String[] titles = getResources().getStringArray(R.array.record_menu);

        showFragments = new BaseBackFragment[]{
//                RecordRealTimeShowFragment.newInstance(RecordRealTimeShowFragment.FROM_TYPE_STATISTICS, RecordRealTimeShowFragment.STATE_CODE_FIRE)
//                , RecordRealTimeShowFragment.newInstance(RecordRealTimeShowFragment.FROM_TYPE_STATISTICS, RecordRealTimeShowFragment.STATE_CODE_PREFIRE)
                RecordShowFragment.newInstance(RecordShowFragment.FROM_TYPE_STATISTICS, RecordShowFragment.STATE_CODE_FIRE)
                , RecordShowFragment.newInstance(RecordShowFragment.FROM_TYPE_STATISTICS, RecordShowFragment.STATE_CODE_PREFIRE)
                , RecordShowFragment.newInstance(RecordShowFragment.FROM_TYPE_STATISTICS, RecordShowFragment.STATE_CODE_FAULT)
                , RecordShowFragment.newInstance(RecordShowFragment.FROM_TYPE_STATISTICS, RecordShowFragment.STATE_CODE_EVENT)
                , ReportRiskListFragment.newInstance(RiskShowType.SHOW_TYPE_RECORD)
                , RecordShowFragment.newInstance(RecordShowFragment.FROM_TYPE_STATISTICS, RecordShowFragment.STATE_CODE_OFFLINE)};

        recordPager.setAdapter(new TabAdapter(getChildFragmentManager(), showFragments, titles));
        recordPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {
                selectPosition = i;
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });

        recordTab.setupWithViewPager(recordPager);
        recordTab.addOnTabSelectedListener(new MyTabSelectedListener(getContext()));
        recordTab.setTabMaxWidth(CommonUtils.measureScreen().dp2px(_mActivity, 40));
    }

    private Toolbar.OnMenuItemClickListener itemClickListener = new Toolbar.OnMenuItemClickListener() {
        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            // TODO: 2019/5/27 根据状态填搜索对应的历史信息，需要传递状态码
            Integer[] stateArray;

            BaseBackFragment showFragment = showFragments[selectPosition];
            if (showFragment instanceof RecordRealTimeShowFragment) {
                stateArray = ((RecordRealTimeShowFragment) showFragment).getStateArray();
                extraTransaction().startDontHideSelf(SearchHistoryFragment.getInstance(HistoryTypeBean.TYPE_REAL_TIME_RECORD, stateArray, mToolbar.getHeight()));
            } else if (showFragment instanceof RecordShowFragment) {
                stateArray = ((RecordShowFragment) showFragment).getStateArray();
                extraTransaction().startDontHideSelf(SearchHistoryFragment.getInstance(HistoryTypeBean.TYPE_HISTORY_RECORD, stateArray, mToolbar.getHeight()));
            } else if (showFragment instanceof ReportRiskListFragment) {
                extraTransaction().startDontHideSelf(SearchHistoryFragment.getInstance(HistoryTypeBean.TYPE_HISTORY_RISK, mToolbar.getHeight()));
            }
            return true;
        }
    };
}
