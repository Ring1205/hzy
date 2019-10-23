package com.zxycloud.zszw.fragment.service.patrol.task;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CustomTabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;

import com.zxycloud.zszw.R;
import com.zxycloud.zszw.base.BaseNFCFragment;
import com.zxycloud.zszw.event.type.TasksPagerType;
import com.zxycloud.zszw.fragment.service.patrol.page.TaskPagerFragment;
import com.zxycloud.zszw.listener.NetRequestListener;
import com.zxycloud.zszw.listener.OnNewIntentListener;
import com.zxycloud.zszw.model.ResultTaskItemBean;
import com.zxycloud.zszw.widget.MyTabSelectedListener;
import com.zxycloud.common.base.BaseBean;
import com.zxycloud.common.base.adapter.TabAdapter;
import com.zxycloud.common.utils.CommonUtils;
import com.zxycloud.common.utils.netWork.ApiRequest;
import com.zxycloud.common.utils.netWork.NetBean;

public class TaskListFragment extends BaseNFCFragment implements OnNewIntentListener {
    private CustomTabLayout mTab;
    private ViewPager mViewPager;

    public static TaskListFragment newInstance() {
        Bundle args = new Bundle();
        TaskListFragment fragment = new TaskListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_custom_base;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        setToolbarTitle(R.string.task_list).initToolbarNav();

        mTab = findViewById(R.id.ctl_tab);
        mViewPager = findViewById(R.id.vp_pager);
    }

    @Override
    public void onLazyInitView(@Nullable Bundle savedInstanceState) {
        super.onLazyInitView(savedInstanceState);
        mViewPager.setAdapter(new TabAdapter(_mActivity, getChildFragmentManager()
                , new Fragment[]{TaskPagerFragment.newInstance(TasksPagerType.TASK_ALL),
                TaskPagerFragment.newInstance(TasksPagerType.TASK_DNS),
                TaskPagerFragment.newInstance(TasksPagerType.TASK_DOING),
                TaskPagerFragment.newInstance(TasksPagerType.TASK_DONE),
                TaskPagerFragment.newInstance(TasksPagerType.TASK_STALE)}
                , new int[]{R.string.tap_all, R.string.tap_task_dns, R.string.tap_task_doing, R.string.tap_task_done, R.string.tap_task_stale}));

        mTab.setupWithViewPager(mViewPager);
        mTab.addOnTabSelectedListener(new MyTabSelectedListener(getContext()));
        mTab.setTabMaxWidth(CommonUtils.measureScreen().dp2px(_mActivity, 100));

        mTab.getTabAt(2).select();
        mViewPager.setCurrentItem(2);
    }

    private String patrolTaskId;

    public void setPatrolTaskId(String taskId) {
        patrolTaskId = taskId;
    }

    @Override
    public void onIntentData(String nfcID, String labelID) {
        if (patrolTaskId != null)
            netWork().setRequestListener(new NetRequestListener() {
                @Override
                public void success(String action, BaseBean baseBean, Object tag) {
                    if (baseBean.isSuccessful())
                        start(TaskSubmitFragment.newInstance(patrolTaskId, ((ResultTaskItemBean) baseBean).getData().getTagNumber()));
                    else
                        CommonUtils.toast(getContext(), baseBean.getMessage());
                }
            }, netWork().apiRequest(NetBean.actionPostTaskPointDetails, ResultTaskItemBean.class, ApiRequest.REQUEST_TYPE_POST).setApiType(ApiRequest.API_TYPE_PATROL)
                    .setRequestParams("patrolTaskId", patrolTaskId)
                    .setRequestParams("tagNumber", labelID));
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden)
            for (Fragment fragment : getChildFragmentManager().getFragments())
                ((TaskPagerFragment) fragment).upData();
    }
}
