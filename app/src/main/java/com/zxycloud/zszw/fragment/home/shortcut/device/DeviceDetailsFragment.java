package com.zxycloud.zszw.fragment.home.shortcut.device;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.NestedScrollView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.zxycloud.zszw.R;
import com.zxycloud.zszw.base.BaseBackFragment;
import com.zxycloud.zszw.event.JPushEvent;
import com.zxycloud.zszw.event.type.SearchListShowType;
import com.zxycloud.zszw.event.type.VideoShowType;
import com.zxycloud.zszw.fragment.common.PlanFragment;
import com.zxycloud.zszw.fragment.common.PlayVideoFragment;
import com.zxycloud.zszw.fragment.home.shortcut.area.SearchListFragment;
import com.zxycloud.zszw.fragment.home.shortcut.place.PlaceDetailFragment;
import com.zxycloud.zszw.fragment.service.install.device.DeviceAddFragment;
import com.zxycloud.zszw.fragment.service.install.device.DeviceAssignmentFragment;
import com.zxycloud.zszw.listener.NetRequestListener;
import com.zxycloud.zszw.model.ResultDeviceBean;
import com.zxycloud.zszw.model.bean.DeviceBean;
import com.zxycloud.zszw.utils.StateTools;
import com.zxycloud.common.base.BaseBean;
import com.zxycloud.common.utils.CommonUtils;
import com.zxycloud.common.utils.DateUtils;
import com.zxycloud.common.utils.netWork.ApiRequest;
import com.zxycloud.common.utils.netWork.NetBean;

import org.greenrobot.eventbus.EventBus;

import static com.zxycloud.zszw.fragment.common.PlanFragment.POINT_TYPE_LIST;

public class DeviceDetailsFragment extends BaseBackFragment implements View.OnClickListener {
    private DeviceBean deviceBean;
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
            NestedScrollView scrollView = findViewById(R.id.adapter_scroll);
            scrollView.fling(0);
            scrollView.smoothScrollTo(0,0);
        }};

    public static DeviceDetailsFragment newInstance(String deviceId, String adapterName) {
        Bundle args = new Bundle();
        args.putString("deviceId", deviceId);
        args.putString("adapterName", adapterName);
        DeviceDetailsFragment fragment = new DeviceDetailsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.device_detail;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        setToolbarTitle(R.string.device_details).initToolbarNav();

        flDeviceList = findViewById(R.id.fl_devices);

        mHandler.sendMessage(new Message());

        initData();

        setOnClickListener(this, R.id.ll_device_channels, R.id.et_device_video, R.id.et_device_installation, R.id.et_device_point);
    }

    @org.greenrobot.eventbus.Subscribe(sticky = true, threadMode = org.greenrobot.eventbus.ThreadMode.MAIN)
    public void onPushRefresh(JPushEvent event) {
        initData();
    }

    private void initData() {
        netWork().setRefreshListener(R.id.refresh_layout, true, false, new NetRequestListener() {
            @Override
            public void success(String action, BaseBean baseBean, Object tag) {
                if (baseBean.isSuccessful()) {
                    switch (action) {
                        case NetBean.actionGetDeviceDetail:
                            deviceBean = ((ResultDeviceBean) baseBean).getData();
                            DateUtils dateUtils = CommonUtils.date();

                            // 设备标识（1独立设备/2非独立设备的网关/3控制器/4普通设备/5通道
                            switch (deviceBean.getDeviceFlag()) {
                                case 4:// 普通设备(无 在/离线)
                                    findViewById(R.id.til_gateway).setVisibility(View.GONE);
                                    findViewById(R.id.til_imei).setVisibility(View.GONE);
                                case 1:// 独立设备
                                    findViewById(R.id.ll_adapter).setVisibility(View.GONE);
                                    findViewById(R.id.ll_device_head).setVisibility(View.VISIBLE);
                                    findViewById(R.id.ll_layout_back).setBackgroundColor(getResources().getColor(R.color.white));

                                    ((EditText) findViewById(R.id.et_device_type_name)).setText(deviceBean.getUserDeviceTypeName().concat("[").concat(CommonUtils.string().getString(deviceBean.getDeviceNumber())).concat("]"));// 设备名称
                                    ((EditText) findViewById(R.id.et_device_state)).setText(deviceBean.getDeviceStateGroupName());// 设备状态
                                    ((EditText) findViewById(R.id.et_device_system)).setText(deviceBean.getDeviceSystemName());// 所属系统
                                    ((EditText) findViewById(R.id.et_device_install_location)).setText(deviceBean.getDeviceInstallLocation());// 安装位置
                                    ((EditText) findViewById(R.id.et_device_place_name)).setText(deviceBean.getPlaceName());// 场所名称
                                    ((EditText) findViewById(R.id.et_device_gateway_name)).setText(deviceBean.getConnectStatus() != 0 ? R.string.online : R.string.offline);// 在线离线
                                    ((EditText) findViewById(R.id.et_device_imei)).setText(CommonUtils.string().getString(deviceBean.getImei()));// IMEI
                                    ((EditText) findViewById(R.id.et_gateway_name)).setText(deviceBean.getAdapterName());// 所属设备
                                    ((EditText) findViewById(R.id.et_device_commissioning_date)).setText(dateUtils.format(deviceBean.getDeviceCommissionTime(), "yyyy-MM-dd"));// 投运日期
                                    ((EditText) findViewById(R.id.et_device_service_deadline)).setText(dateUtils.format(deviceBean.getDeviceUseEndTime(), "yyyy-MM-dd"));// 服务截止日期
                                    ((EditText) findViewById(R.id.et_device_channels)).setText(CommonUtils.string().getString(deviceBean.getChannelCount()));// 通道数
                                    findViewById(R.id.ll_device_channels).setVisibility(deviceBean.getChannelCount() != -1 ? View.VISIBLE : View.GONE);
                                    findViewById(R.id.et_device_video).setVisibility(deviceBean.isHasCamera() != 0 ? View.VISIBLE : View.GONE);
                                    findViewById(R.id.et_device_installation).setVisibility(deviceBean.isHasInstallationDetail() != 0 ? View.VISIBLE : View.GONE);
                                    findViewById(R.id.et_device_point).setVisibility(deviceBean.isHasLayerPoint() != 0 ? View.VISIBLE : View.GONE);
                                    break;
                                case 3:// 控制器(无 在/离线)
                                    findViewById(R.id.tv_adapter_online).setVisibility(View.GONE);
                                case 2:// 非独立设备的网关
                                    findViewById(R.id.ll_adapter).setVisibility(View.VISIBLE);
                                    findViewById(R.id.ll_device_head).setVisibility(View.GONE);

                                    if (findChildFragment(SearchListFragment.class) != null)
                                        findChildFragment(SearchListFragment.class).replaceFragment(SearchListFragment.newInstance(
                                                SearchListShowType.SHOW_TYPE_CONTROLLER_OR_ADAPTER,
                                                deviceBean.getAdapterName(),
                                                deviceBean.getDeviceId(),
                                                getPreFragment() instanceof PlaceDetailFragment ? ((PlaceDetailFragment) getPreFragment()).getPlaceId() : null), false);
                                    else
                                        loadRootFragment(R.id.fl_devices, SearchListFragment.newInstance(
                                                SearchListShowType.SHOW_TYPE_CONTROLLER_OR_ADAPTER,
                                                deviceBean.getAdapterName(),
                                                deviceBean.getDeviceId(),
                                                getPreFragment() instanceof PlaceDetailFragment ? ((PlaceDetailFragment) getPreFragment()).getPlaceId() : null));


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

                                    StateTools.setStateTint(deviceBean.getDeviceStateGroupCode(), (ImageView) findViewById(R.id.iv_adapter_state));//设备状态
                                    ((TextView) findViewById(R.id.tv_adapter_title)).setText(deviceBean.getUserDeviceTypeName().concat("[").concat(CommonUtils.string().getString(deviceBean.getDeviceNumber())).concat("]"));//设备名称
                                    ((TextView) findViewById(R.id.tv_adapter_code)).setText(deviceBean.getAdapterName());//网关名称
                                    ((TextView) findViewById(R.id.tv_adapter_online)).setText(deviceBean.getConnectStatus() != 0 ? R.string.online : R.string.offline);//在线/离线
                                    ((TextView) findViewById(R.id.tv_adapter_online)).setCompoundDrawablesWithIntrinsicBounds(deviceBean.getConnectStatus() != 0 ? R.mipmap.ic_adapter_online : R.mipmap.ic_adapter_offline, 0, 0, 0);//在线/离线
                                    ((TextView) findViewById(R.id.tv_adapter_place)).setText(deviceBean.getPlaceName());//场所名称
                                    ((TextView) findViewById(R.id.tv_adapter_location)).setText(deviceBean.getDeviceInstallLocation());//安装位置
                                    break;
                                case 5:// 通道
                                    findViewById(R.id.ll_adapter).setVisibility(View.GONE);
                                    findViewById(R.id.ll_device_head).setVisibility(View.GONE);
                                    break;
                            }
                            break;
                    }
                }
            }
        }, netWork().apiRequest(NetBean.actionGetDeviceDetail, ResultDeviceBean.class, ApiRequest.REQUEST_TYPE_GET, R.id.loading)
                .setRequestParams("deviceId", getArguments().getString("deviceId"))
                .setRequestParams("adapterName", getArguments().getString("adapterName")));
    }

    @Override
    public void onClick(View view) {
        if (deviceBean != null)
            switch (view.getId()) {
                case R.id.ll_device_channels:// 通道入口
                    start(ChannelListFragment.newInstance(deviceBean.getDeviceId(), deviceBean.getAdapterName()));
                    break;
                case R.id.et_device_video:// 视频联动
                    start(PlayVideoFragment.newInstance(VideoShowType.PLAY_TYPE_LINKAGE_DEVICE, deviceBean.getDeviceId(), deviceBean.getAdapterName()));
                    break;
                case R.id.et_device_installation:// 安装详情
                    start(DeviceInstallLocationFragment.newInstance(deviceBean.getDeviceId(), deviceBean.getAdapterName()));
                    break;
                case R.id.et_device_point:// 点位图
                    start(PlanFragment.newInstance(POINT_TYPE_LIST, null, deviceBean.getDeviceId(), deviceBean.getAdapterName()));
                    break;
            }
    }

    public int getToolbarBottom() {
        return mToolbar != null ? mToolbar.getBottom() : 0;
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
