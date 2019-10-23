package com.zxycloud.zszw.fragment.statistics;

import android.os.Bundle;
import android.support.design.widget.CustomTabLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.widget.TextView;

import com.zxycloud.zszw.R;
import com.zxycloud.zszw.adapter.BasePagerAdapter;
import com.zxycloud.zszw.base.BaseMainFragment;
import com.zxycloud.zszw.fragment.statistics.chart.AlarmFalseAnalyzeFragment;
import com.zxycloud.zszw.fragment.statistics.chart.DeviceTypeAlarmAnalyzeFragment;
import com.zxycloud.zszw.fragment.statistics.chart.FaultAnalyzeFragment;
import com.zxycloud.zszw.fragment.statistics.chart.FireReviewAnalyzeFragment;
import com.zxycloud.zszw.fragment.statistics.chart.FireStateFragment;
import com.zxycloud.zszw.fragment.statistics.chart.OnlineAnalyzeFragment;
import com.zxycloud.common.utils.CommonUtils;
import com.zxycloud.common.utils.rxbus2.RxBusCode;
import com.zxycloud.common.utils.rxbus2.Subscribe;
import com.zxycloud.common.utils.rxbus2.ThreadMode;

public class StatisticsFragment extends BaseMainFragment {
    private CustomTabLayout tabCount;
    private ViewPager vPCount;
    private ChartUpDataFragment[] chartFragments;

    public static StatisticsFragment newInstance() {
        Bundle args = new Bundle();
        StatisticsFragment fragment = new StatisticsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_tab_statistics;
    }


    @Override
    protected void initView(Bundle savedInstanceState) {
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.count);

        tabCount = findViewById(R.id.tab_count);
        vPCount = findViewById(R.id.viewPager_count);

        chartFragments = new ChartUpDataFragment[]{
                FireStateFragment.newInstance()
                , FireReviewAnalyzeFragment.newInstance()
                , FaultAnalyzeFragment.newInstance()
                , DeviceTypeAlarmAnalyzeFragment.newInstance()
                , AlarmFalseAnalyzeFragment.newInstance()
//                , RiskAnalyzeFragment.newInstance()
//                , PatrolAnalyzeFragment.newInstance()
                , OnlineAnalyzeFragment.newInstance()};
        vPCount.setAdapter(new BasePagerAdapter(getChildFragmentManager(),
                getString(R.string.fire_alarm_status), getString(R.string.check_fire_alarm), getString(R.string.equipment_failure_analysis), getString(R.string.equipment_type_alarm), getString(R.string.alarm_false_positives)
//                , getString(R.string.hidden_trouble_statistics), getString(R.string.inspect_statistical)
                , getString(R.string.online_analysis)).setFragments(chartFragments));
        tabCount.setupWithViewPager(vPCount);
        tabCount.addOnTabSelectedListener(onTabSelectedListener);
        tabCount.post(new Runnable() {
            @Override
            public void run() {
                _mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        TextView tv_tab = tabCount.getTabAt(0).getCustomView().findViewById(R.id.tv_tab);
                        tv_tab.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 21);
                        tv_tab.setTextColor(getResources().getColor(R.color.common_color_text));
                    }
                });
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        CommonUtils.registerRxBus(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        CommonUtils.unRegisterRxBus(this);
    }

    @Subscribe(code = RxBusCode.RX_PROJECT_CHANGED, threadMode = ThreadMode.MAIN)
    public void onProjectChanged() {
        for (Fragment fragment : getChildFragmentManager().getFragments())
            ((ChartUpDataFragment) fragment).upData();
    }

    private TabLayout.OnTabSelectedListener onTabSelectedListener = new TabLayout.OnTabSelectedListener() {
        @Override
        public void onTabSelected(TabLayout.Tab tab) {
            //获取textview
            TextView tv_tab = tab.getCustomView().findViewById(R.id.tv_tab);
            tv_tab.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 21);
            tv_tab.setTextColor(getResources().getColor(R.color.common_color_text));
        }

        @Override
        public void onTabUnselected(TabLayout.Tab tab) {
            //获取textview
            TextView tv_tab = tab.getCustomView().findViewById(R.id.tv_tab);
            tv_tab.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16.5f);
            tv_tab.setTextColor(getResources().getColor(R.color.common_color_text_level_3));
        }

        @Override
        public void onTabReselected(TabLayout.Tab tab) {

        }
    };
}
