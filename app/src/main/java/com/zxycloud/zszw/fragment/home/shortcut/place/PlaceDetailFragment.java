package com.zxycloud.zszw.fragment.home.shortcut.place;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.NestedScrollView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.zxycloud.zszw.R;
import com.zxycloud.zszw.base.BaseBackFragment;
import com.zxycloud.zszw.dialog.DevicePopupWindow;
import com.zxycloud.zszw.event.JPushEvent;
import com.zxycloud.zszw.event.type.LinkmanShowType;
import com.zxycloud.zszw.fragment.home.shortcut.device.DeviceBaseFragment;
import com.zxycloud.zszw.fragment.service.install.area.LinkmanListFragment;
import com.zxycloud.zszw.fragment.service.install.device.ScanDeviceFragment;
import com.zxycloud.zszw.fragment.service.install.device.SelectPlaceFragment;
import com.zxycloud.zszw.listener.NetRequestListener;
import com.zxycloud.zszw.model.ResultPlaceBean;
import com.zxycloud.zszw.model.bean.DeviceBean;
import com.zxycloud.zszw.model.bean.PlaceBean;
import com.zxycloud.zszw.utils.StateTools;
import com.zxycloud.common.base.BaseBean;
import com.zxycloud.common.utils.CommonUtils;
import com.zxycloud.common.utils.netWork.ApiRequest;
import com.zxycloud.common.utils.netWork.NetBean;
import com.zxycloud.common.utils.netWork.NetUtils;
import com.zxycloud.common.widget.AlertDialog;

import org.greenrobot.eventbus.EventBus;

public class PlaceDetailFragment extends BaseBackFragment implements View.OnClickListener {
    private String placeId;
    private PlaceBean placeBean;
    private FrameLayout flDeviceList;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (flDeviceList != null) {
                ViewGroup.LayoutParams params = flDeviceList.getLayoutParams();
                params.height = findViewById(R.id.refresh_layout).getHeight();
                flDeviceList.setLayoutParams(params);
            }
        }
    };

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            NestedScrollView scrollView = findViewById(R.id.place_scroll);
            scrollView.fling(0);
            scrollView.smoothScrollTo(0,0);
        }};

    public static PlaceDetailFragment newInstance(String placeId) {
        Bundle args = new Bundle();
        args.putString("placeId", placeId);
        PlaceDetailFragment fragment = new PlaceDetailFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.place_detail;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        setToolbarTitle(R.string.place_details).initToolbarNav();

        placeId = getArguments().getString("placeId");
        flDeviceList = findViewById(R.id.fl_device_list);

        mHandler.sendMessage(new Message());

        getPlaceDetail();

        setOnClickListener(this, R.id.place_view_device, R.id.ll_add_device);
    }

    @Override
    public void onClick(View view) {
        if (placeBean != null)
            switch (view.getId()) {
                case R.id.place_view_device:
                    start(LinkmanListFragment.newInstance(LinkmanShowType.LINKMAN_TYPE_PLACE, placeId));
                    break;
                case R.id.ll_add_device:
                    start(ScanDeviceFragment.newInstance(placeBean.getPlaceId(), placeBean.getPlaceName(), placeBean.getPicUrl()));
                    break;
            }
    }

    @org.greenrobot.eventbus.Subscribe(sticky = true, threadMode = org.greenrobot.eventbus.ThreadMode.MAIN)
    public void onPushRefresh(JPushEvent event) {
        getPlaceDetail();
    }

    /**
     * 获取场所详情数据
     */
    private void getPlaceDetail() {
        netWork().setRefreshListener(R.id.refresh_layout, true, false, new NetRequestListener() {
            @Override
            public void success(String action, BaseBean baseBean, Object tag) {
                if (baseBean.isSuccessful()) {
                    switch (action) {
                        case NetBean.actionGetPlaceDetail:
                            placeBean = ((ResultPlaceBean) baseBean).getData();
                            if (placeBean != null){
                                if (findChildFragment(DeviceBaseFragment.class) != null)
                                    findChildFragment(DeviceBaseFragment.class).replaceFragment(DeviceBaseFragment.newInstance(placeId, placeBean.getPlaceName()), false);
                                else
                                    loadRootFragment(R.id.fl_device_list, DeviceBaseFragment.newInstance(placeId, placeBean.getPlaceName()));
                            }
                            StateTools.setStateTint(placeBean.getStateGroupCode(), (ImageView) findViewById(R.id.iv_place_state));// 场所状态
                            ((TextView) findViewById(R.id.tv_place_title)).setText(CommonUtils.string().getString(placeBean.getPlaceName()));// 场所名称
                            ((TextView)findViewById(R.id.tv_place_project)).setText(CommonUtils.string().getString(placeBean.getProjectName()));// 所属单位
                            ((TextView)findViewById(R.id.tv_place_address)).setText(CommonUtils.string().getString(placeBean.getPlaceAddress()));// 区域地址
                            ((TextView)findViewById(R.id.tv_place_admin)).setText(CommonUtils.string().getString(placeBean.getPlaceAdminName()));// 管理人员
                            ((TextView)findViewById(R.id.tv_place_phone)).setText(CommonUtils.string().getString(placeBean.getPlaceAdminPhoneNumber()));// 手机号码
                            ((TextView) findViewById(R.id.tv_device_count)).setText(CommonUtils.string().getString(getContext(), R.string.title_contacts).concat(CommonUtils.string().getString(placeBean.getLinkmanList().size())));// 联系人数量

                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        Thread.sleep(800);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                    handler.sendMessage(new Message());
                                }
                            }).start();
                            break;
                    }
                } else {
                    CommonUtils.toast(getContext(), baseBean.getMessage());
                }
            }
        }, netWork().apiRequest(NetBean.actionGetPlaceDetail, ResultPlaceBean.class, ApiRequest.REQUEST_TYPE_GET, R.id.loading).setRequestParams("placeId", placeId));
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden)
            netWork().loading();
    }

    DevicePopupWindow popupWindow;
    public void getOnLongClickListener(final DeviceBean deviceBean) {
        popupWindow = new DevicePopupWindow(getContext(), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                start(SelectPlaceFragment.newInstance(deviceBean.getAdapterName(), deviceBean.getDeviceId()));
                popupWindow.dismiss();
            }
        }, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deviceDeleteOrUnassign(false, deviceBean.getAdapterName(), deviceBean.getDeviceId());
                popupWindow.dismiss();
            }
        }, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deviceDeleteOrUnassign(true, deviceBean.getAdapterName(), deviceBean.getDeviceId());
                popupWindow.dismiss();
            }
        });
        View rootView = LayoutInflater.from(getContext()).inflate(R.layout.activity_base, null);
        popupWindow.showAtLocation(rootView, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
    }

    /**
     * 删除和取消分配
     *
     * @param isDelete    是否删除
     * @param adapterName 网关名
     * @param deviceId    设备id
     */
    private void deviceDeleteOrUnassign(final boolean isDelete, final String adapterName, final String deviceId) {
        final ApiRequest request;
        if (isDelete)
            request = new ApiRequest(NetBean.actionPostDeleteDeviceInfo, BaseBean.class)
                    .setRequestType(ApiRequest.REQUEST_TYPE_POST)
                    .setRequestParams("deviceId", deviceId)
                    .setRequestParams("adapterName", adapterName);
        else
            request = new ApiRequest(NetBean.actionPostDeviceUnassign, BaseBean.class)
                    .setRequestType(ApiRequest.REQUEST_TYPE_POST)
                    .setRequestParams("deviceId", deviceId)
                    .setRequestParams("adapterName", adapterName);
        AlertDialog builder = new AlertDialog(getContext()).builder();
        builder.setTitle(isDelete ? R.string.hint_delete_device_title : R.string.hint_unassign_device_title)
                .setMsg(isDelete ? R.string.hint_delete_device_msg : R.string.hint_unassign_device_msg)
                .setNegativeButton(R.string.dialog_no, null)
                .setPositiveButton(R.string.dialog_yes, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new NetUtils(getContext()).request(new NetUtils.NetRequestCallBack() {
                                                               @Override
                                                               public void success(String action, BaseBean baseBean, Object tag) {
                                                                   if (baseBean.isSuccessful())
                                                                       netWork().loading();
                                                                   CommonUtils.toast(getContext(), baseBean.getMessage());
                                                               }

                                                               @Override
                                                               public void error(String action, Throwable e, Object tag) {
                                                               }
                                                           }
                                , false
                                , request);
                    }
                }).show();
    }

    public String getPlaceId() {
        return placeBean.getPlaceId();
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