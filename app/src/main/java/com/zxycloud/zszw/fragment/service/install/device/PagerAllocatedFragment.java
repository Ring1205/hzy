package com.zxycloud.zszw.fragment.service.install.device;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.zxycloud.zszw.R;
import com.zxycloud.zszw.adapter.AllocationAdapter;
import com.zxycloud.zszw.base.BaseBackFragment;
import com.zxycloud.zszw.listener.NetRequestListener;
import com.zxycloud.zszw.listener.OnCheckedChangeListener;
import com.zxycloud.zszw.model.ResultDeviceListBean;
import com.zxycloud.zszw.model.bean.DeviceBean;
import com.zxycloud.common.utils.netWork.ApiRequest;
import com.zxycloud.common.utils.netWork.NetBean;

import java.util.ArrayList;
import java.util.List;

public class PagerAllocatedFragment extends BaseBackFragment {
    public static int UNALLOCATED = 0;// 0:显示未分配
    public static int ALLOCATED = 1;// 1:显示分配
    private RecyclerView recycler;
    private AllocationAdapter adapter;
    private List<DeviceBean> deviceBeans = new ArrayList<>();
    private DeviceAssignmentFragment deviceAssignmentFragment;
    private ApiRequest request;

    public static PagerAllocatedFragment newInstance(int state, String adapterName) {
        PagerAllocatedFragment fragment = new PagerAllocatedFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("allocatedState", state);
        bundle.putString("adapterName", adapterName);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.recycler_button;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        recycler = findViewById(R.id.recycler);
        recycler.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new AllocationAdapter(getContext());
        recycler.setAdapter(adapter);
        deviceAssignmentFragment = ((DeviceAssignmentFragment) getParentFragment());
        adapter.setOnItemCheckListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(boolean allChecked, boolean isChecked, int position) {
                deviceAssignmentFragment.fabChecked(allChecked);
                deviceAssignmentFragment.setDeviceBean(isChecked, deviceBeans.get(position));
            }
        });
        recycler.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 5)
                    deviceAssignmentFragment.fabAnimator(true);
                else if (dy < -5)
                    deviceAssignmentFragment.fabAnimator(false);
            }
        });

        initData();
    }

    private void initData() {
        if (request == null) {
            request = netWork().apiRequest(NetBean.getActionPostAdapterDeviceList, ResultDeviceListBean.class, ApiRequest.REQUEST_TYPE_POST, R.id.loading)
                    .setRequestParams("flag", 1)// 1 所有设备(包括网关和普通设备) 2/3 返回网关设备
                    .setRequestParams("adapterName", getArguments().getString("adapterName"))// 网关名称
                    .setRequestParams("distributionState", getArguments().getInt("allocatedState"))// 显示是否已经分配的设备列表 0:显示未分配 1:显示分配 如果不传显示所有
                    .setRequestParams("pageSize", 10)
                    .setRequestParams("pageIndex", 1);
            netWork().setRefreshListener(R.id.refreshLayout, true, true, new NetRequestListener<ResultDeviceListBean>() {
                @Override
                public void success(String action, ResultDeviceListBean baseBean, Object tag) {
                    if (baseBean.isSuccessful() && baseBean.getData() != null && baseBean.getData().size() > 0) {
                        int i = (int) request.getRequestParams().get("pageIndex");
                        if (i == 1) {
                            deviceAssignmentFragment.clearDeviceList();
                            deviceBeans = baseBean.getData();
                        } else {
                            deviceBeans.addAll(deviceBeans.size(), baseBean.getData());
                        }
                        adapter.setData(deviceBeans);
                    }
                }
            }, request);
        }
    }

    public void isCheckAll(boolean obj) {
        // 全选
        for (DeviceBean deviceBean : deviceBeans) {
            deviceBean.setCheck(obj);
            if (obj)
                deviceAssignmentFragment.setDeviceBean(!obj, deviceBean);
            deviceAssignmentFragment.setDeviceBean(obj, deviceBean);
        }
        adapter.setData(deviceBeans);
    }
}
