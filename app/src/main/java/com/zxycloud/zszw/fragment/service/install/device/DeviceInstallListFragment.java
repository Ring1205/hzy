package com.zxycloud.zszw.fragment.service.install.device;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.zxycloud.zszw.R;
import com.zxycloud.zszw.adapter.DeviceInstallAdapter;
import com.zxycloud.zszw.base.BaseBackFragment;
import com.zxycloud.zszw.listener.NetRequestListener;
import com.zxycloud.zszw.listener.OnItemClickListener;
import com.zxycloud.zszw.model.RequestAllocateBean;
import com.zxycloud.zszw.model.ResultSystemListBean;
import com.zxycloud.zszw.model.bean.AllocateBean;
import com.zxycloud.zszw.model.bean.DeviceBean;
import com.zxycloud.common.base.BaseBean;
import com.zxycloud.common.utils.CommonUtils;
import com.zxycloud.common.utils.netWork.ApiRequest;
import com.zxycloud.common.utils.netWork.NetBean;

import java.util.List;

public class DeviceInstallListFragment extends BaseBackFragment implements Toolbar.OnMenuItemClickListener {
    private List<DeviceBean> deviceBeanList;
    private RecyclerView recyclerDevices;
    public DeviceInstallAdapter installAdapter;
    public int itemPosition;

    public static DeviceInstallListFragment newInstance(String gsonData, String placeId, String placeName, String picUrl) {
        DeviceInstallListFragment fragment = new DeviceInstallListFragment();
        Bundle bundle = new Bundle();
        bundle.putString("devices", gsonData);
        bundle.putString("placeId", placeId);
        bundle.putString("placeName", placeName);
        bundle.putString("picUrl", picUrl);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.device_install_list;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        setToolbarTitle(R.string.device_install_list).initToolbarNav().setToolbarMenu(R.menu.save, this);
        deviceBeanList = new Gson().fromJson(getArguments().getString("devices"), new TypeToken<List<DeviceBean>>() {
        }.getType());

        if (deviceBeanList != null && deviceBeanList.size() > 0) {
            recyclerDevices = findViewById(R.id.recycler_devices);
            recyclerDevices.setLayoutManager(new LinearLayoutManager(getContext()));
            installAdapter = new DeviceInstallAdapter(getContext(), deviceBeanList, getArguments().getString("placeId"));
            recyclerDevices.setAdapter(installAdapter);
            installAdapter.setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(int position, View view, RecyclerView.ViewHolder vh) {
                    itemPosition = position;
                    startForResult(DeviceAddFragment.newInstance(0,
                            deviceBeanList.get(position).getAdapterName(),
                            deviceBeanList.get(position).getDeviceId(),
                            getArguments().getString("placeId"),
                            getArguments().getString("placeName"),
                            getArguments().getString("picUrl")), 2011);
                }
            });

            initSystemList();
        }
        if (!TextUtils.isEmpty(getArguments().getString("placeName")))
            ((TextView)findViewById(R.id.tv_place)).setText(getArguments().getString("placeName"));
    }

    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {
        allocateDevice();
        return true;
    }

    /**
     * 分配设备
     */
    private void allocateDevice() {
        RequestAllocateBean requestAllocateBean = new RequestAllocateBean();
        requestAllocateBean.setFlag(1);
        requestAllocateBean.setDeviceDistributions(installAdapter.getDeviceDistributions());
        netWork().addRequestListener(netWork().apiRequest(NetBean.actionPostAllocateDevice, BaseBean.class, ApiRequest.REQUEST_TYPE_POST).setRequestBody(requestAllocateBean));
    }

    private void initSystemList() {
        netWork().setRequestListener(new NetRequestListener() {
            @Override
            public void success(String action, BaseBean baseBean, Object tag) {
                if (baseBean.isSuccessful())
                    switch (action){
                        case NetBean.actionGetDeviceSystem:
                            installAdapter.setSystemData(((ResultSystemListBean) (baseBean)).getData());
                            break;
                        case NetBean.actionPostAllocateDevice:
                            Bundle bundle = new Bundle();
                            bundle.putBoolean("isSucceed", true);
                            setFragmentResult(RESULT_OK, bundle);
                            finish();
                            CommonUtils.toast(getContext(), baseBean.getMessage());
                            break;
                    }
                else
                    CommonUtils.toast(getContext(), baseBean.getMessage());
            }
        }, new ApiRequest<>(NetBean.actionGetDeviceSystem, ResultSystemListBean.class).setRequestType(ApiRequest.REQUEST_TYPE_GET));
    }

    @Override
    public void onFragmentResult(int requestCode, int resultCode, Bundle data) {
        super.onFragmentResult(requestCode, resultCode, data);
        if (requestCode == 2011 && resultCode == RESULT_OK && data != null) {
            AllocateBean allocateBean = new Gson().fromJson(data.getString("allocateBean"), AllocateBean.class);
            installAdapter.setAllocateList(itemPosition, allocateBean);
        }
    }

}
