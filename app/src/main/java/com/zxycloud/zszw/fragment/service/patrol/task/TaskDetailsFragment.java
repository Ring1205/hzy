package com.zxycloud.zszw.fragment.service.patrol.task;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.design.widget.CustomTabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.zxycloud.zszw.R;
import com.zxycloud.zszw.base.BaseNFCFragment;
import com.zxycloud.zszw.dialog.PointMessagePopupWindow;
import com.zxycloud.zszw.event.type.TasksPointType;
import com.zxycloud.zszw.fragment.service.patrol.page.PointPagerFragment;
import com.zxycloud.zszw.listener.NetRequestListener;
import com.zxycloud.zszw.listener.OnNewIntentListener;
import com.zxycloud.zszw.model.ResultTaskDetailsBean;
import com.zxycloud.zszw.model.ResultTaskItemBean;
import com.zxycloud.zszw.model.bean.PatrolBean;
import com.zxycloud.zszw.widget.MyTabSelectedListener;
import com.zxycloud.common.base.BaseBean;
import com.zxycloud.common.base.adapter.TabAdapter;
import com.zxycloud.common.utils.CommonUtils;
import com.zxycloud.common.utils.netWork.ApiRequest;
import com.zxycloud.common.utils.netWork.NetBean;

import java.util.List;

public class TaskDetailsFragment extends BaseNFCFragment implements OnNewIntentListener, View.OnClickListener, Toolbar.OnMenuItemClickListener {
    private List<PatrolBean.TaskPointVOListBean> Taskdata;
    private CustomTabLayout mTab;
    private ViewPager mViewPager;
    private NestedScrollView pointScroll;
    private RelativeLayout rlCard;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (pointScroll != null && rlCard != null) {
                ViewGroup.LayoutParams params = rlCard.getLayoutParams();
                params.height = pointScroll.getHeight();
                rlCard.setLayoutParams(params);
            }
        }
    };

    public static TaskDetailsFragment newInstance(int start, String taskId) {
        TaskDetailsFragment fragment = new TaskDetailsFragment();
        Bundle arg = new Bundle();
        arg.putInt("start", start);
        arg.putString("TaskId", taskId);
        fragment.setArguments(arg);
        return fragment;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.task_detail;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        if (getArguments().getInt("start") != 1)
            setToolbarTitle(R.string.task_detail).initToolbarNav();
        else
            setToolbarTitle(R.string.task_detail).initToolbarNav().setToolbarMenu(R.menu.menu_nfc_qr, this);

        mTab = findViewById(R.id.ctl_tab);
        mViewPager = findViewById(R.id.vp_pager);
        rlCard = findViewById(R.id.rl_card);
        pointScroll = findViewById(R.id.point_scroll);

        initData();

        setOnClickListener(this, R.id.tv_stick);
    }

    @Override
    public void onLazyInitView(@Nullable Bundle savedInstanceState) {
        super.onLazyInitView(savedInstanceState);
        mViewPager.setAdapter(new TabAdapter(_mActivity, getChildFragmentManager()
                , new Fragment[]{PointPagerFragment.newInstance(TasksPointType.POINT_ALL, getArguments().getString("TaskId"))
                , PointPagerFragment.newInstance(TasksPointType.POINT_NON, getArguments().getString("TaskId"))
                , PointPagerFragment.newInstance(TasksPointType.POINT_NORMAL, getArguments().getString("TaskId"))
                , PointPagerFragment.newInstance(TasksPointType.POINT_ABNORMAL, getArguments().getString("TaskId"))}
                , new int[]{R.string.tap_all, R.string.work_state_unchecked, R.string.work_state_normal, R.string.work_state_warn}));

        mTab.setupWithViewPager(mViewPager);
        mTab.addOnTabSelectedListener(new MyTabSelectedListener(getContext(), 14, 12));
        mTab.setTabMaxWidth(CommonUtils.measureScreen().dp2px(_mActivity, 100));
    }

    private void initData() {
        netWork().setRequestListener(new NetRequestListener() {
            @Override
            public void success(String action, BaseBean baseBean, Object tag) {
                if (baseBean.isSuccessful())
                    switch (action) {
                        case NetBean.actionPostTaskPointDetails:
                            start(TaskSubmitFragment.newInstance(getArguments().getString("TaskId"), ((ResultTaskItemBean) baseBean).getData().getTagNumber()));
                            break;
                        case NetBean.actionGetTaskDetails:
                            PatrolBean bean = ((ResultTaskDetailsBean) baseBean).getData();
                            ((EditText) findViewById(R.id.edit_title_patrol_task)).setText(bean.getPatrolTaskName());// 任务名称
                            String UserName = "";
                            for (PatrolBean.PlanUserListBean planUserListBean : bean.getPlanUserList())
                                if (UserName.isEmpty())
                                    UserName = planUserListBean.getUserAccount().concat(TextUtils.isEmpty(planUserListBean.getUserName()) ? "" : "(".concat(planUserListBean.getUserName()).concat(")"));
                                else
                                    UserName = UserName.concat(",").concat(planUserListBean.getUserAccount()).concat(TextUtils.isEmpty(planUserListBean.getUserName()) ? "" : "(".concat(planUserListBean.getUserName()).concat(")"));

                            ((EditText) findViewById(R.id.edit_title_inspectors)).setText(UserName);// 巡查人员
                            ((EditText) findViewById(R.id.edit_title_patrol_progress)).setText(bean.getProgress());// 任务进度
                            ((EditText) findViewById(R.id.edit_title_task_time_limit)).setText(bean.getStartTime().concat(" - ").concat(bean.getEndTime()));// 起止时间
                            Taskdata = bean.getTaskPointVOList();
                            if (!Taskdata.isEmpty())
                                mHandler.sendMessage(new Message());
                            break;
                    }
                else
                    CommonUtils.toast(getContext(), baseBean.getMessage());
            }
        }, netWork().apiRequest(NetBean.actionGetTaskDetails, ResultTaskDetailsBean.class, ApiRequest.REQUEST_TYPE_GET).setApiType(ApiRequest.API_TYPE_PATROL)
                .setRequestParams("id", getArguments().getString("TaskId")));
    }

    @Override
    public void onIntentData(String nfcID, final String labelID) {
        if (labelID != null) {
            netWork().addRequestListener(netWork().apiRequest(NetBean.actionPostTaskPointDetails, ResultTaskItemBean.class, ApiRequest.REQUEST_TYPE_POST).setApiType(ApiRequest.API_TYPE_PATROL)
                    .setRequestParams("patrolTaskId", getArguments().getString("TaskId"))
                    .setRequestParams("tagNumber", labelID));
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_stick:
                pointScroll.fling(findViewById(R.id.rl_card).getTop());
                pointScroll.smoothScrollTo(0, findViewById(R.id.rl_card).getTop());
                break;
        }
    }

    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.scan_nfc:
                showScanPopupWindow(this);
                break;
            case R.id.scan_qr_code:
                jumpCaptureFragment(getArguments().getString("TaskId"));
                break;
        }
        return true;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            netWork().setRequestListener(NetBean.actionGetTaskDetails);
            for (Fragment fragment : getChildFragmentManager().getFragments())
                ((PointPagerFragment) fragment).upData();
        }
    }
    private PointMessagePopupWindow submitPopupWindow;
    public void setSubmitPopupWindow(PointMessagePopupWindow submitPopupWindow){
        this.submitPopupWindow = submitPopupWindow;
    }
    @Override
    public void onFragmentResult(int requestCode, int resultCode, Bundle data) {
        super.onFragmentResult(requestCode, resultCode, data);
        if (requestCode == 7001 && submitPopupWindow != null)
            submitPopupWindow.show();
    }

}
