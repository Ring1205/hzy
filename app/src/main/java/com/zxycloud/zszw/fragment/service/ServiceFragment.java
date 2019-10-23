package com.zxycloud.zszw.fragment.service;

import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.zxycloud.zszw.MainFragment;
import com.zxycloud.zszw.R;
import com.zxycloud.zszw.adapter.ServiecAdapter;
import com.zxycloud.zszw.base.BaseMainFragment;
import com.zxycloud.zszw.event.type.RiskShowType;
import com.zxycloud.zszw.fragment.home.shortcut.area.AreaListFragment;
import com.zxycloud.zszw.fragment.home.shortcut.device.DeviceListFragment;
import com.zxycloud.zszw.fragment.home.shortcut.place.PlaceListFragment;
import com.zxycloud.zszw.fragment.service.patrol.task.TaskListFragment;
import com.zxycloud.zszw.fragment.service.records.RecordStatisticsFragment;
import com.zxycloud.zszw.fragment.service.patrol.point.PointListFragment;
import com.zxycloud.zszw.fragment.service.risk.ReportRiskListFragment;
import com.zxycloud.zszw.fragment.service.unit.UnitDetailsFragment;
import com.zxycloud.zszw.fragment.service.users.UsersListFragment;
import com.zxycloud.zszw.fragment.service.video.VideoFragment;
import com.zxycloud.zszw.listener.OnHiddenReminderListener;
import com.zxycloud.zszw.listener.OnItemClickListener;
import com.zxycloud.zszw.listener.OnProjectObtainListener;
import com.zxycloud.zszw.widget.GridItemDecoration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ServiceFragment extends BaseMainFragment {
    private ServiecAdapter msgAdapter;
    private int mCount;

    public static ServiceFragment newInstance() {
        ServiceFragment fragment = new ServiceFragment();
        fragment.setArguments(new Bundle());
        return fragment;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_tab_service;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        ((Toolbar) findViewById(R.id.toolbar)).setTitle(R.string.service);

        initRecyclerView(R.id.recycler_install, R.id.recycler_patrol, R.id.recycler_message);

        ((MainFragment) getParentFragment()).setListener(new OnHiddenReminderListener() {
            @Override
            public void getCount(int count) {
                if (msgAdapter != null)
                    setAdapterData(count);
                mCount = count;
            }
        });
    }

    private void setAdapterData(int count) {
        Map<Integer, Integer> map = new HashMap<>();
        map.put(3, count);
        msgAdapter.setReConut(map);
    }

    private void initRecyclerView(@IdRes int... recyclerId) {
        for (int id : recyclerId) {
            RecyclerView recycler = findViewById(id);
            recycler.setLayoutManager(new GridLayoutManager(getContext(), 3));
            recycler.addItemDecoration(new GridItemDecoration.Builder(getContext())
                    .setHorizontalSpan(R.dimen.line_1px)
                    .setVerticalSpan(R.dimen.line_1px)
                    .setColorResource(R.color.colorLine)
                    .build());
            List<String> sers = new ArrayList<>();
            final List<Integer> installs = new ArrayList<>();
            switch (id) {
                case R.id.recycler_install:
                    installs.add(R.mipmap.ic_ser_grid_list);//区域列表
                    installs.add(R.mipmap.ic_ser_equipment_type);//场所列表
                    installs.add(R.mipmap.ic_ser_device_type_alarm);//设备列表
                    sers = new ArrayList<>(Arrays.asList(getContext().getResources().getStringArray(R.array.ser_install)));
                    break;
                case R.id.recycler_patrol:
//                    installs.add(R.mipmap.ic_ser_perform_task);//执行任务
                    installs.add(R.mipmap.ic_ser_task_point);//巡查点位
                    installs.add(R.mipmap.ic_ser_task_list);//巡查任务
                    installs.add(R.mipmap.ic_ser_hidden_report);//监督上报
                    installs.add(R.mipmap.ic_ser_hidden_to_do);//待办隐患
                    installs.add(R.mipmap.ic_ser_hidden_done);//已办隐患
                    sers = new ArrayList<>(Arrays.asList(getContext().getResources().getStringArray(R.array.ser_patrol)));
                    break;
                case R.id.recycler_message:
                    installs.add(R.mipmap.ic_ser_video_information);//视频信息
//                    installs.add(R.mipmap.ic_ser_user_management);//账号管理
                    installs.add(R.mipmap.ic_ser_operation_record);//运行记录
                    installs.add(R.mipmap.ic_ser_unit_file);//单位信息
//                    installs.add(R.mipmap.ic_ser_report_risk);//消防维保
//                    installs.add(R.mipmap.ic_ser_publicity_education);//宣传教育
                    sers = new ArrayList<>(Arrays.asList(getContext().getResources().getStringArray(R.array.ser_else)));
                    break;
            }
            ServiecAdapter adapter = new ServiecAdapter(getContext(), sers, installs);
            recycler.setAdapter(adapter);
            if (id == R.id.recycler_patrol) {
                msgAdapter = adapter;
                if (mCount != 0)
                    setAdapterData(mCount);
            }
            adapter.setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(int position, View view, RecyclerView.ViewHolder vh) {
                    switch (installs.get(position)) {
                        case R.mipmap.ic_ser_grid_list://区域列表
                            startFragment(AreaListFragment.newInstance(null));
                            break;
                        case R.mipmap.ic_ser_equipment_type://场所列表
                            startFragment(PlaceListFragment.newInstance(null));
                            break;
                        case R.mipmap.ic_ser_device_type_alarm://设备列表
                            startFragment(DeviceListFragment.newInstance());
                            break;
                        case R.mipmap.ic_ser_perform_task://执行任务
                            startFragment(TaskListFragment.newInstance());
                            break;
                        case R.mipmap.ic_ser_task_point://巡查点位
                            startFragment(PointListFragment.newInstance());
                            break;
                        case R.mipmap.ic_ser_task_list://任务列表
                            startFragment(TaskListFragment.newInstance());
                            break;
                        case R.mipmap.ic_ser_hidden_report://监督上报
                            startFragment(ReportRiskListFragment.newInstance(RiskShowType.SHOW_TYPE_REPORT));
                            break;
                        case R.mipmap.ic_ser_hidden_to_do://待办隐患
                            startFragment(ReportRiskListFragment.newInstance(RiskShowType.SHOW_TYPE_TO_DO));
                            break;
                        case R.mipmap.ic_ser_hidden_done://已办隐患
                            startFragment(ReportRiskListFragment.newInstance(RiskShowType.SHOW_TYPE_DONE));
                            break;
                        case R.mipmap.ic_ser_video_information://视频信息
                            startFragment(VideoFragment.newInstance());
                            break;
                        case R.mipmap.ic_ser_user_management://账号管理
                            startFragment(UsersListFragment.newInstance());
                            break;
                        case R.mipmap.ic_ser_operation_record://运行记录
                            startFragment(RecordStatisticsFragment.newInstance());
                            break;
                        case R.mipmap.ic_ser_unit_file://单位信息
                            getProject(new OnProjectObtainListener() {
                                @Override
                                public void success(String projectId, String projectName) {
                                    startFragment(UnitDetailsFragment.newInstance(projectId));
                                }
                            });
                            break;
                        case R.mipmap.ic_ser_report_risk://消防维保
                            startFragment(ReportRiskListFragment.newInstance(RiskShowType.SHOW_TYPE_TO_DO));
                            break;
                        case R.mipmap.ic_ser_publicity_education://宣传教育
                            startFragment(ReportRiskListFragment.newInstance(RiskShowType.SHOW_TYPE_TO_DO));
                            break;
                    }
                }
            });
        }
    }
}
