package com.zxycloud.zszw.fragment.home;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.zxycloud.zszw.MainFragment;
import com.zxycloud.zszw.R;
import com.zxycloud.zszw.adapter.StatisticsAdapter;
import com.zxycloud.zszw.base.BaseMainFragment;
import com.zxycloud.zszw.event.JPushEvent;
import com.zxycloud.zszw.event.type.AlertShowType;
import com.zxycloud.zszw.event.type.RiskShowType;
import com.zxycloud.zszw.fragment.home.chart.LineFragment;
import com.zxycloud.zszw.fragment.home.message.AlertRealTimeDetailFragment;
import com.zxycloud.zszw.fragment.home.shortcut.area.AreaListFragment;
import com.zxycloud.zszw.fragment.home.shortcut.device.DeviceListFragment;
import com.zxycloud.zszw.fragment.home.shortcut.place.PlaceListFragment;
import com.zxycloud.zszw.fragment.home.statistics.HistoryAlarmFragment;
import com.zxycloud.zszw.fragment.service.install.area.AddAreaFragment;
import com.zxycloud.zszw.fragment.service.install.place.AddPlaceFragment;
import com.zxycloud.zszw.fragment.service.patrol.point.PointListFragment;
import com.zxycloud.zszw.fragment.service.patrol.task.TaskListFragment;
import com.zxycloud.zszw.fragment.service.risk.ReportRiskListFragment;
import com.zxycloud.zszw.fragment.service.users.UsersListFragment;
import com.zxycloud.zszw.listener.NetRequestListener;
import com.zxycloud.zszw.listener.OnItemClickListener;
import com.zxycloud.zszw.listener.OnProjectObtainListener;
import com.zxycloud.zszw.model.IconBean;
import com.zxycloud.zszw.model.JpushBean.JPushBean;
import com.zxycloud.zszw.model.JpushBean.JPushRiskBean;
import com.zxycloud.zszw.model.ResultAlarmBean;
import com.zxycloud.zszw.model.ResultAlertBean;
import com.zxycloud.zszw.model.ResultStatistcsBean;
import com.zxycloud.zszw.model.bean.AlarmBean;
import com.zxycloud.zszw.model.bean.RecordBean;
import com.zxycloud.zszw.utils.NetWorkUtil;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.zxycloud.common.base.BaseBean;
import com.zxycloud.common.base.adapter.TabAdapter;
import com.zxycloud.common.utils.CommonUtils;
import com.zxycloud.common.utils.SPUtils;
import com.zxycloud.common.utils.netWork.ApiRequest;
import com.zxycloud.common.utils.netWork.NetBean;
import com.zxycloud.common.utils.rxbus2.RxBusCode;
import com.zxycloud.common.utils.rxbus2.Subscribe;
import com.zxycloud.common.utils.rxbus2.ThreadMode;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static com.zxycloud.zszw.utils.StateTools.initListBean;

public class HomeFragment extends BaseMainFragment implements View.OnClickListener {
    /**
     * 获取当前语言环境，用于弹窗提示信息的不同语言环境的特异性拼接
     */
    private static final String LANGUAGE_CHINA = Locale.CHINA.getLanguage();

    private Toolbar mToolbar;
    private int moduleCode;
    private RecyclerView rlHomeStatistics;
    private TabLayout lineTab;
    private ViewPager linePager;
    private TextView tvTitleTask, tvTitleDevice, tvTitleAddUser, tvTitleEntryPoint;
    private Map<Integer, ArrayList<IconBean>> statisticsMap;
    private Map<Integer, ArrayList<IconBean>> shortcutMap;
    private AlarmBean alarmBean;
    private StatisticsAdapter adapter;
    private TabAdapter tabAdapter;
    private LineFragment[] fragments;

    public static HomeFragment newInstance() {
        Bundle args = new Bundle();
        HomeFragment fragment = new HomeFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_tab_home;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        loadData();
        tvTitleTask = findViewById(R.id.tv_title_task);
        tvTitleDevice = findViewById(R.id.tv_title_device);
        tvTitleAddUser = findViewById(R.id.tv_title_add_user);
        tvTitleEntryPoint = findViewById(R.id.tv_title_entry_point);
        mToolbar = findViewById(R.id.toolbar);
        lineTab = findViewById(R.id.line_tab);
        linePager = findViewById(R.id.line_pager);
        mToolbar.setTitle(R.string.home);

//        if (isPushMsg) {
//            mToolbar.findViewById(R.id.include_push).setVisibility(View.GONE);
//            menuItem.setIcon(R.mipmap.ic_message);
//        } else {
//            mToolbar.findViewById(R.id.include_push).setVisibility(View.VISIBLE);
//            menuItem.setIcon(R.mipmap.ic_push_msg);
//        }

//            mToolbar.findViewById(R.id.include_push).setVisibility(View.VISIBLE);
//            mToolbar.inflateMenu(R.menu.home_head_push);//TODO 消息小铃铛（有未读消息）

//            mToolbar.findViewById(R.id.include_push).setVisibility(View.GONE);
//            mToolbar.inflateMenu(R.menu.home_head_msg);//TODO 消息小铃铛

        moduleCode = CommonUtils.getSPUtils(getContext()).getInt(SPUtils.USER_MODULE_CODE);
        moduleCode = 1; //TODO test快捷方式
        rlHomeStatistics = findViewById(R.id.rl_home_statistics);
        rlHomeStatistics.setLayoutManager(new GridLayoutManager(getContext(), 3));
        adapter = new StatisticsAdapter(getContext());
        rlHomeStatistics.setAdapter(adapter);
        adapter.setListData(statisticsMap.get(moduleCode));

        List<TextView> list = new ArrayList<>();
        list.add(tvTitleTask);
        list.add(tvTitleDevice);
        list.add(tvTitleAddUser);
        list.add(tvTitleEntryPoint);

        int shortcutKey = 0;
        for (int i : new int[]{2, 4}) {
            if ((moduleCode & i) != 0) {
                shortcutKey = +i;
            }
        }
        shortcutKey = 2; //TODO test快捷方式
        if (shortcutKey != 0) {
            for (int i = 0; i < shortcutMap.get(shortcutKey).size(); i++) {
                final IconBean bean = shortcutMap.get(shortcutKey).get(i);
                TextView textView = list.get(i);
                textView.setVisibility(View.VISIBLE);
                textView.setText(bean.getNameID());
                textView.setCompoundDrawablesWithIntrinsicBounds(
                        textView.getCompoundDrawables()[0],
                        getResources().getDrawable(bean.getIconID()),
                        textView.getCompoundDrawables()[2],
                        textView.getCompoundDrawables()[0]);
                textView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        switch (bean.getNameID()) {
                            case R.string.home_infrastructure:// 基础设施
                                break;
                            case R.string.home_hidden_danger:// 监督上报
                                startFragment(ReportRiskListFragment.newInstance(RiskShowType.SHOW_TYPE_REPORT));
                                break;
                            case R.string.home_site_entry:// 场所添加
                                startFragment(AddPlaceFragment.newInstance(null, null));
                                break;
                            case R.string.home_add_area:// 添加区域
                                startFragment(AddAreaFragment.newInstance(null, null));
                                break;
                            case R.string.home_device:// 查看设备
                                startFragment(DeviceListFragment.newInstance());
                                break;
                            case R.string.home_task:// 任务巡查
                                startFragment(TaskListFragment.newInstance());
                                break;
                        }
                    }
                });
            }
            findViewById(R.id.include_shortcut).setVisibility(View.VISIBLE);
        } else {
            findViewById(R.id.include_shortcut).setVisibility(View.GONE);
        }

        adapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(int position, View view, RecyclerView.ViewHolder vh) {
                switch (statisticsMap.get(moduleCode).get(position).getNameID()) {
                    case R.string.home_stat_area:// 管辖区域
                        startFragment(AreaListFragment.newInstance(null));
                        break;
                    case R.string.home_stat_device:// 设备列表
                        startFragment(DeviceListFragment.newInstance());
                        break;
                    case R.string.home_stat_account:// 账户管理
                        startFragment(UsersListFragment.newInstance());
                        break;
                    case R.string.home_stat_point:// 巡查点位
                        startFragment(PointListFragment.newInstance());
                        break;
                    case R.string.home_stat_inspection:// 巡查任务
                        startFragment(TaskListFragment.newInstance());
                        break;
                    case R.string.home_stat_history:// 历史告警
                        startFragment(HistoryAlarmFragment.newInstance());
                        break;
                    case R.string.home_stat_place:// 场所列表
                        startFragment(PlaceListFragment.newInstance(null));
                        break;
                }
            }
        });

        fragments = new LineFragment[]{LineFragment.newInstance(7), LineFragment.newInstance(15)};

        tabAdapter = new TabAdapter(_mActivity, getFragmentManager()
                , fragments
                , new int[]{R.string.home_chart_week, R.string.home_chart_month});
        linePager.setAdapter(tabAdapter);

        lineTab.setupWithViewPager(linePager);

        getHomeInfo();
    }

    private void getHomeInfo() {
        getProject(new OnProjectObtainListener() {
            @Override
            public void success(String projectId, String projectName) {
                netWork().setOnLayoutRefreshListener(onLayoutRefreshListener).setRefreshListener(R.id.refreshLayout, true, false, new NetRequestListener() {
                            @Override
                            public void success(String action, BaseBean baseBean, Object tag) {
                                if (baseBean.isSuccessful()) {
                                    switch (action) {
                                        case NetBean.actionGetFirstState:
                                            alarmBean = ((ResultAlertBean) baseBean).getData();
                                            if (alarmBean.getStateGroupCode() == 99) {
                                                findViewById(R.id.include_push).setVisibility(View.GONE);
                                                return;
                                            }
                                            String language = Locale.getDefault().getLanguage();
                                            StringBuffer message;
                                            if (language.equals(LANGUAGE_CHINA)) {//中文拼接
                                                message = new StringBuffer("您位于")
                                                        .append(alarmBean.getPlaceName())
                                                        .append("的")
                                                        .append(alarmBean.getUserDeviceTypeName())
                                                        .append("于")
                                                        .append(CommonUtils.date().format(alarmBean.getReceiveTime()))
                                                        .append("发生")
                                                        .append(alarmBean.getStateGroupName());
                                            } else {//外文拼接
                                                message = new StringBuffer("您位于")
                                                        .append(alarmBean.getPlaceName())
                                                        .append("的")
                                                        .append(alarmBean.getUserDeviceTypeName())
                                                        .append("于")
                                                        .append(CommonUtils.date().format(alarmBean.getReceiveTime()))
                                                        .append("发生")
                                                        .append(alarmBean.getStateGroupName());
                                            }

                                            ImageView imsg = mToolbar.findViewById(R.id.iv_msg_start);
                                            switch (alarmBean.getStateGroupCode()) {
                                                case CommonUtils.STATE_CODE_FIRE:
                                                    imsg.setImageResource(R.mipmap.ic_msg_fire);
                                                    setOnClickListener(HomeFragment.this, R.id.include_push, R.id.tv_msg, R.id.iv_msg_start);
                                                    break;
                                                case CommonUtils.STATE_CODE_PREFIRE:
                                                    imsg.setImageResource(R.mipmap.ic_msg_warn);
                                                    setOnClickListener(HomeFragment.this, R.id.include_push, R.id.tv_msg, R.id.iv_msg_start);
                                                    break;
                                                case CommonUtils.STATE_CODE_FAULT:
                                                    imsg.setImageResource(R.mipmap.ic_msg_fault);
                                                    setOnClickListener(onClickListener, R.id.include_push, R.id.tv_msg, R.id.iv_msg_start);
                                                    break;
                                                case CommonUtils.STATE_CODE_EVENT:
                                                    imsg.setImageResource(R.mipmap.ic_msg_event);
                                                    setOnClickListener(onClickListener, R.id.include_push, R.id.tv_msg, R.id.iv_msg_start);
                                                    break;
                                                case CommonUtils.STATE_CODE_RISK:
                                                    imsg.setImageResource(R.mipmap.ic_msg_danger);
                                                    setOnClickListener(onClickListener, R.id.include_push, R.id.tv_msg, R.id.iv_msg_start);
                                                    break;
                                            }
                                            findViewById(R.id.include_push).setVisibility(View.VISIBLE);
                                            TextView tvMsg = mToolbar.findViewById(R.id.tv_msg);
                                            tvMsg.setText(message);
                                            tvMsg.requestFocus();
                                            break;
                                        case NetBean.actionGetStatisticsNumber:
                                            adapter.setBean(((ResultStatistcsBean) baseBean).getData());
                                            tabAdapter.notifyDataSetChanged();
                                            break;
                                        case NetBean.actionGetAlarmMubmer:
                                            ((TextView) findViewById(R.id.tv_now_alarm)).setText(CommonUtils.string().getString(getContext(), R.string.home_now_alarm).concat("\n")
                                                    .concat(CommonUtils.string().getString(((ResultAlarmBean) baseBean).getData().getAlartCount())));
                                            break;
                                        case NetBean.actionDeviceOnlineInfo:
                                            ((TextView) findViewById(R.id.tv_now_online)).setText(CommonUtils.string().getString(getContext(), R.string.home_now_online).concat("\n")
                                                    .concat(CommonUtils.string().getString(((ResultAlarmBean) baseBean).getData().getOnLineCount())));
                                            ((TextView) findViewById(R.id.tv_now_offline)).setText(CommonUtils.string().getString(getContext(), R.string.home_now_offline).concat("\n")
                                                    .concat(CommonUtils.string().getString(((ResultAlarmBean) baseBean).getData().getOffLineCount())));
                                            break;
                                    }
                                } else {
                                    CommonUtils.toast(_mActivity, baseBean.getMessage());
                                }
                            }
                        }, new ApiRequest<>(NetBean.actionGetFirstState, ResultAlertBean.class).setRequestParams("projectId", projectId)
                        , netWork().apiRequest(NetBean.actionGetStatisticsNumber, ResultStatistcsBean.class, ApiRequest.REQUEST_TYPE_GET).setRequestParams("projectId", projectId)
                        , netWork().apiRequest(NetBean.actionGetAlarmMubmer, ResultAlarmBean.class, ApiRequest.REQUEST_TYPE_GET).setApiType(ApiRequest.API_TYPE_STATISTICS).setRequestParams("projectId", projectId)
                        , netWork().apiRequest(NetBean.actionDeviceOnlineInfo, ResultAlarmBean.class, ApiRequest.REQUEST_TYPE_GET).setApiType(ApiRequest.API_TYPE_STATISTICS).setRequestParams("projectId", projectId));
            }
        });

        for (LineFragment f : fragments)
            f.outRefresh();
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

        }
    };

    private void loadData() {
        statisticsMap = new HashMap<>();
        statisticsMap.put(1, initListBean(new int[]{R.string.home_stat_inspection, R.string.home_stat_area, R.string.home_stat_place, R.string.home_stat_device, R.string.home_stat_point, R.string.home_stat_history}));
        statisticsMap.put(2, initListBean(new int[]{R.string.home_stat_area, R.string.home_stat_device, R.string.home_stat_place}));
        statisticsMap.put(3, initListBean(new int[]{R.string.home_stat_area, R.string.home_stat_device, R.string.home_stat_place, R.string.home_stat_account, R.string.home_stat_history}));
        statisticsMap.put(4, initListBean(new int[]{R.string.home_stat_point, R.string.home_stat_inspection}));
        statisticsMap.put(5, initListBean(new int[]{R.string.home_stat_area, R.string.home_stat_device, R.string.home_stat_account, R.string.home_stat_place, R.string.home_stat_inspection, R.string.home_stat_point, R.string.home_stat_history}));
        statisticsMap.put(6, initListBean(new int[]{R.string.home_stat_area, R.string.home_stat_device, R.string.home_stat_place, R.string.home_stat_point, R.string.home_stat_inspection}));
        statisticsMap.put(7, initListBean(new int[]{R.string.home_stat_area, R.string.home_stat_device, R.string.home_stat_account, R.string.home_stat_place, R.string.home_stat_point, R.string.home_stat_inspection, R.string.home_stat_history}));

        shortcutMap = new HashMap<>();
        shortcutMap.put(2, initListBean(new int[]{
                R.string.home_device,
                R.string.home_add_area,
                R.string.home_site_entry,
                R.string.home_hidden_danger}));
        shortcutMap.put(4, initListBean(new int[]{
                R.string.home_hidden_danger,
                R.string.home_task}));
        shortcutMap.put(6, initListBean(new int[]{
                R.string.home_device,
                R.string.home_infrastructure,
                R.string.home_hidden_danger,
                R.string.home_task}));
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.include_push:
            case R.id.tv_msg:
            case R.id.iv_msg_start:
//                startFragment(FireAlarmFragment.newInstance());
                ((MainFragment) getParentFragment())
                        .start(AlertRealTimeDetailFragment.newInstance(AlertShowType.ALERT_HISTORY
                                , alarmBean.getStateGroupCode() == RecordBean.RECORD_STATE_FIRE ? CommonUtils.STATE_CODE_FIRE : CommonUtils.STATE_CODE_PREFIRE
                                , alarmBean.getRecordId()));
                break;
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        CommonUtils.registerRxBus(this);
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        CommonUtils.unRegisterRxBus(this);
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(code = RxBusCode.RX_PROJECT_CHANGED, threadMode = ThreadMode.MAIN)
    public void onProjectChanged() {
        getHomeInfo();

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                _mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        netWork().setRequestListener(NetBean.actionGetAlarmMubmer);
                        netWork().setRequestListener(NetBean.actionDeviceOnlineInfo);
                    }
                });
            }
        }).start();
    }


    @org.greenrobot.eventbus.Subscribe(sticky = true, threadMode = org.greenrobot.eventbus.ThreadMode.MAIN)
    public void onPushRefresh(JPushEvent event) {
        getHomeInfo();
        JPushBean jPushBean = event.getjPushBean();
        if (jPushBean instanceof JPushRiskBean) {
            JPushRiskBean riskBean = ((JPushRiskBean) jPushBean);
            if (riskBean.isRefreshRedPoint()) {
                ((MainFragment) getParentFragment()).hiddenRecord();
            }
        }
    }

    private NetWorkUtil.OnLayoutRefreshListener onLayoutRefreshListener = new NetWorkUtil.OnLayoutRefreshListener() {
        @Override
        public void onRefresh(RefreshLayout refreshLayout) {
            getHomeInfo();
        }

        @Override
        public void onLoadMore(RefreshLayout refreshLayout) {

        }
    };
}
