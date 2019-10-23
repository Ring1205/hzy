package com.zxycloud.zszw.fragment.home.shortcut.device;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.zxycloud.zszw.R;
import com.zxycloud.zszw.adapter.DeviceAdapter;
import com.zxycloud.zszw.base.BaseSearchListFragment;
import com.zxycloud.zszw.event.JPushEvent;
import com.zxycloud.zszw.fragment.service.install.device.DeviceAddFragment;
import com.zxycloud.zszw.fragment.service.install.device.DeviceAssignmentFragment;
import com.zxycloud.zszw.fragment.service.install.device.SelectPlaceFragment;
import com.zxycloud.zszw.listener.NetRequestListener;
import com.zxycloud.zszw.listener.OnItemClickListener;
import com.zxycloud.zszw.listener.OnProjectObtainListener;
import com.zxycloud.zszw.model.ResultDeviceListBean;
import com.zxycloud.zszw.model.bean.DeviceBean;
import com.zxycloud.common.base.BaseBean;
import com.zxycloud.common.utils.netWork.ApiRequest;
import com.zxycloud.common.utils.netWork.NetBean;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

public class DeviceListFragment extends BaseSearchListFragment implements Toolbar.OnMenuItemClickListener {
    private RecyclerView recyclerView;
    private DeviceAdapter adapter;
    private List<DeviceBean> deviceBeans;
    private ApiRequest<ResultDeviceListBean> apiRequest;

    public static DeviceListFragment newInstance() {
        DeviceListFragment fragment = new DeviceListFragment();
        fragment.setArguments(new Bundle());
        return fragment;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.toolbar_list;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        setAddSearchToolbar(true, R.string.device_list, this);

        recyclerView = findViewById(R.id.recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new DeviceAdapter(getContext());
        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(int position, View view, RecyclerView.ViewHolder vh) {
                start(DeviceDetailsFragment.newInstance(deviceBeans.get(position).getDeviceId(), deviceBeans.get(position).getAdapterName()));
            }
        });
        adapter.setOnAllocationListener(new DeviceAdapter.OnAllocationListener() {
            @Override
            public void allocationDevice(String adapterName, String deviceId) {
                start(SelectPlaceFragment.newInstance(adapterName, deviceId));
            }

            @Override
            public void updateData() {
                netWork().loading();
            }
        });

        initData();
        initSearchEditText(apiRequest.getAction(), "searchKeyName");
    }

    @org.greenrobot.eventbus.Subscribe(sticky = true, threadMode = org.greenrobot.eventbus.ThreadMode.MAIN)
    public void onPushRefresh(JPushEvent event) {
        initData();
    }


    private void initData() {
        getProject(new OnProjectObtainListener() {
            @Override
            public void success(String projectId, String projectName) {
                apiRequest = netWork().apiRequest(NetBean.actionPostDeviceList, ResultDeviceListBean.class, ApiRequest.REQUEST_TYPE_POST, R.id.loading)
                        .setRequestParams("deviceStateGroupCode", 0);// 设备状态Code 0为全部状态（1火警、2预警、3启动、4监管、5反馈、6故障、7屏蔽、96离线、99正常）
                apiRequest
                        .setRequestParams("projectId", projectId)// 父级单位Id（若projectId与areaId都没有，则根据权限搜索，若没有areaId，则搜索单位下所有有权限的一级区域）
                        .setRequestParams("searchKeyName", getArguments().getString("areaId"))// 模糊搜索内容 可以是 设备二次码、安装位置、网关名称
                        .setRequestParams("pageSize", 10)
                        .setRequestParams("pageIndex", 1);

                netWork().setRefreshListener(R.id.refreshLayout, true, true, new NetRequestListener() {
                    @Override
                    public void success(String action, BaseBean baseBean, Object tag) {
                        if (baseBean.isSuccessful()) {
                            int i = (int) apiRequest.getRequestParams().get("pageIndex");
                            if (i == 1) {
                                deviceBeans = ((ResultDeviceListBean) baseBean).getData();
                                adapter.setData(deviceBeans);
                            } else {
                                deviceBeans.addAll(deviceBeans.size(), ((ResultDeviceListBean) baseBean).getData());
                                adapter.setData(deviceBeans);
                            }
                        }
                    }
                }, apiRequest);
            }
        });
    }

    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {
        super.onMenuItemClick(menuItem);
        switch (menuItem.getItemId()) {
            case R.id.action_add:
                start(SelectPlaceFragment.newInstance(null, null));
                break;
        }
        return true;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        if (!hidden && getTopFragment() != null)
            if ((getTopFragment() instanceof DeviceAssignmentFragment) || (getTopFragment() instanceof DeviceAddFragment))
                netWork().loading();
        super.onHiddenChanged(hidden);
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }
}
