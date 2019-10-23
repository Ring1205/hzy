package com.zxycloud.zszw;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.zxycloud.zszw.event.TabSelectedEvent;
import com.zxycloud.zszw.event.cope.EventBusActivityScope;
import com.zxycloud.zszw.fragment.service.ServiceFragment;
import com.zxycloud.zszw.fragment.statistics.StatisticsFragment;
import com.zxycloud.zszw.fragment.home.HomeFragment;
import com.zxycloud.zszw.fragment.mine.MineFragment;
import com.zxycloud.zszw.listener.OnHiddenReminderListener;
import com.zxycloud.zszw.model.ResultProjectListBean;
import com.zxycloud.zszw.model.bean.ReminderBean;
import com.zxycloud.common.base.fragment.SupportFragment;
import com.zxycloud.common.utils.CommonUtils;
import com.zxycloud.common.utils.SPUtils;
import com.zxycloud.common.utils.netWork.ApiRequest;
import com.zxycloud.common.utils.netWork.NetBean;
import com.zxycloud.common.utils.netWork.NetUtils;
import com.zxycloud.common.widget.BottomBarTab.BottomBar;
import com.zxycloud.common.widget.BottomBarTab.BottomBarTab;

public class MainFragment extends SupportFragment {
    public static final int FIRST = 0;
    public static final int SECOND = 1;
    public static final int THIRD = 2;
    public static final int FOURTH = 3;

    private SupportFragment[] mFragments = new SupportFragment[4];

    private BottomBar mBottomBar;
    private OnHiddenReminderListener listener;

    public void setListener(OnHiddenReminderListener listener) {
        this.listener = listener;
    }

    public static MainFragment newInstance() {
        Bundle args = new Bundle();
        MainFragment fragment = new MainFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        initView(view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        SupportFragment firstFragment = findChildFragment(HomeFragment.class);
        if (firstFragment == null) {
            mFragments[FIRST] = HomeFragment.newInstance();
            mFragments[SECOND] = StatisticsFragment.newInstance();
            mFragments[THIRD] = ServiceFragment.newInstance();
            mFragments[FOURTH] = MineFragment.newInstance();

            loadMultipleRootFragment(R.id.fl_tab_container, FIRST,
                    mFragments[FIRST],
                    mFragments[SECOND],
                    mFragments[THIRD],
                    mFragments[FOURTH]);
        } else {
            // 这里库已经做了Fragment恢复,所有不需要额外的处理了, 不会出现重叠问题

            // 这里我们需要拿到mFragments的引用
            mFragments[FIRST] = firstFragment;
            mFragments[SECOND] = findChildFragment(StatisticsFragment.class);
            mFragments[THIRD] = findChildFragment(ServiceFragment.class);
            mFragments[FOURTH] = findChildFragment(MineFragment.class);
        }
    }

    private void initView(View view) {
        mBottomBar = view.findViewById(R.id.bottomBar);

        mBottomBar
                .addItem(new BottomBarTab(_mActivity, R.mipmap.tab_home, getString(R.string.home)))
                .addItem(new BottomBarTab(_mActivity, R.mipmap.tab_statistics, getString(R.string.count)))
                .addItem(new BottomBarTab(_mActivity, R.mipmap.tab_service, getString(R.string.service)))
                .addItem(new BottomBarTab(_mActivity, R.mipmap.tab_me, getString(R.string.mine)));

        mBottomBar.setOnTabSelectedListener(new BottomBar.OnTabSelectedListener() {
            @Override
            public void onTabSelected(int position, int prePosition) {
                showHideFragment(mFragments[position], mFragments[prePosition]);
            }

            @Override
            public void onTabUnselected(int position) {

            }

            @Override
            public void onTabReselected(int position) {
                EventBusActivityScope.getDefault(_mActivity).post(new TabSelectedEvent(position));
            }
        });
        getProjectIDAndName();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden)
            getProjectIDAndName();
    }

    private String pId;

    private void getProjectIDAndName() {
        pId = CommonUtils.getSPUtils(_mActivity).getString(SPUtils.PROJECT_ID);
        if (TextUtils.isEmpty(pId))
            new NetUtils(_mActivity).request(new NetUtils.NetRequestCallBack<ResultProjectListBean>() {
                @Override
                public void success(String action, ResultProjectListBean baseBean, Object tag) {
                    if (baseBean.isSuccessful() && baseBean.getData() != null && !baseBean.getData().isEmpty()) {
                        CommonUtils.getSPUtils(getContext())
                                .put(SPUtils.PROJECT_ID, baseBean.getData().get(0).getProjectId())
                                .put(SPUtils.PROJECT_NAME, baseBean.getData().get(0).getProjectName());
                        pId = baseBean.getData().get(0).getProjectId();
                        hiddenRecord();
                    }
                }

                @Override
                public void error(String action, Throwable e, Object tag) {
                }
            }, false, new ApiRequest(NetBean.actionGetProjectList, ResultProjectListBean.class).setRequestType(ApiRequest.REQUEST_TYPE_POST)
                    .setRequestParams("pageSize", 10)
                    .setRequestParams("pageIndex", 1));
        else
            hiddenRecord();
    }

    // 未读消息
    public void hiddenRecord() {
        new NetUtils(_mActivity).request(new NetUtils.NetRequestCallBack<ReminderBean>() {
            @Override
            public void success(String action, ReminderBean baseBean, Object tag) {
                if (baseBean.isSuccessful()) {
                    mBottomBar.getItem(THIRD).setUnreadCount(baseBean.getData());
                    listener.getCount(baseBean.getData());
                }
            }

            @Override
            public void error(String action, Throwable e, Object tag) {
            }
        }, false, new ApiRequest(NetBean.actionGetHiddenRecord, ReminderBean.class).setRequestType(ApiRequest.REQUEST_TYPE_GET)
                .setRequestParams("projectId", pId));
    }

}
