package com.zxycloud.zszw.fragment.home.shortcut.device;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.LinearLayoutManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.zxycloud.zszw.R;
import com.zxycloud.zszw.base.BaseBackFragment;
import com.zxycloud.zszw.event.type.DeviceShowType;
import com.zxycloud.zszw.event.type.PlaceShowType;
import com.zxycloud.zszw.fragment.home.shortcut.place.PlaceDetailFragment;
import com.zxycloud.zszw.listener.NetRequestListener;
import com.zxycloud.zszw.listener.OnProjectObtainListener;
import com.zxycloud.zszw.model.ResultDeviceListBean;
import com.zxycloud.zszw.model.bean.DeviceBean;
import com.zxycloud.zszw.utils.StateTools;
import com.zxycloud.common.utils.CommonStateBean;
import com.zxycloud.common.utils.CommonUtils;
import com.zxycloud.common.utils.netWork.ApiRequest;
import com.zxycloud.common.utils.netWork.NetBean;
import com.zxycloud.common.utils.netWork.NetUtils;
import com.zxycloud.common.widget.BswRecyclerView.BswRecyclerView;
import com.zxycloud.common.widget.BswRecyclerView.ConvertViewCallBack;
import com.zxycloud.common.widget.BswRecyclerView.RecyclerViewHolder;
import com.zxycloud.common.widget.CustomPopupWindows;

import java.util.List;

public class DeviceViewPagerFragment extends BaseBackFragment implements View.OnClickListener {
    private BswRecyclerView<DeviceBean> deviceRv;
    private CustomPopupWindows deviceStateWindows;

    private String showString = "";
    private String searchKeyName = "";
    @SuppressWarnings("FieldCanBeLocal")
    private NetUtils netUtils;

    private int showType;
    private EditText deviceSearchEt;
    private TextView deviceStateTv;

    public static DeviceViewPagerFragment newInstance(@DeviceShowType.showType int showType, String searchString) {
        Bundle args = new Bundle();
        args.putInt("showType", showType);
        args.putString("showString", searchString);
        DeviceViewPagerFragment fragment = new DeviceViewPagerFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_device_search_layout;
    }

    @SuppressWarnings("unchecked")
    private void getDeviceList() {
        getProject(new OnProjectObtainListener() {
            @Override
            public void success(String projectId, String projectName) {
                final ApiRequest<ResultDeviceListBean> apiRequest = netWork().apiRequest(NetBean.actionPostPlaceDeviceList, ResultDeviceListBean.class, ApiRequest.REQUEST_TYPE_POST, R.id.load_layout)
                        .setRequestParams("pageSize", 10)
                        .setRequestParams("pageIndex", 1)
                        .setRequestParams("placeId", showString)
                        .setRequestParams("projectId", projectId);
                if (!TextUtils.isEmpty(searchKeyName)) {
                    apiRequest.setRequestParams("searchKeyName", searchKeyName);
                }
                int deviceStateGroupCode = (int) deviceStateTv.getTag();
                if (deviceStateGroupCode != -1)
                    apiRequest.setRequestParams("deviceStateGroupCode", deviceStateGroupCode);
                switch (showType) {
                    case DeviceShowType.SHOW_TYPE_INDEPENDENT_DEVICE_LIST:
                        apiRequest.setRequestParams("deviceFlag", 1);
                        break;

                    case DeviceShowType.SHOW_TYPE_ADAPTER_LIST:
                        apiRequest.setRequestParams("deviceFlag", 2);
                        break;
                }
                if (null == netUtils)
                    netUtils = NetUtils.getNewInstance(_mActivity);

                netWork().setRefreshListener(R.id.refresh_layout, false, true, new NetRequestListener<ResultDeviceListBean>() {
                    @Override
                    public void success(String action, ResultDeviceListBean baseBean, Object tag) {
                        if (baseBean.isSuccessful()) {
                            List<DeviceBean> deviceBeans = baseBean.getData();
                            //noinspection ConstantConditions
                            int currentIndex = (int) apiRequest.getRequestParams().get("pageIndex");
                            deviceRv.setData(deviceBeans, currentIndex, 10);
                        }
                    }
                }, apiRequest);
            }
        });

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void initView(Bundle savedInstanceState) {
        if (null != deviceRv)// ViewPager缓存的时候，取缓存时保存的数据，以免用户锚点失效
            return;

        Bundle bundle = getArguments();
        showType = bundle.getInt("showType", PlaceShowType.SHOW_TYPE_MAIN);
        showString = bundle.getString("showString", "");

        deviceStateTv = findViewById(R.id.device_state_tv);
        deviceStateTv.setTag(-1);
        deviceStateWindows = CustomPopupWindows.getInstance(_mActivity, deviceStateTv, R.layout.popup_list_match_parent_layout);

        deviceSearchEt = findViewById(R.id.device_search_et);
        deviceSearchEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchKeyName = s.toString();
                getDeviceList();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        if (showType == DeviceShowType.SHOW_TYPE_ADAPTER_LIST)
            deviceSearchEt.setHint(R.string.device_search_hint);
        deviceRv = findViewById(R.id.list_rv);
        deviceRv.initAdapter(R.layout.base_item, deviceCallBack).setLayoutManager(new LinearLayoutManager(getContext()));

        getDeviceList();

        setOnClickListener(this, R.id.device_clear_et, R.id.device_state_tv);
    }

    // 数据处理
    private ConvertViewCallBack<DeviceBean> deviceCallBack = new ConvertViewCallBack<DeviceBean>() {

        @Override
        public void convert(RecyclerViewHolder holder, final DeviceBean deviceBean, int position, int layoutTag) {
            holder.setText(R.id.item_title, deviceBean.getUserDeviceTypeName());
            holder.setTextWithLeftDrawables(R.id.item_1, R.mipmap.ic_item_device_number, deviceBean.getDeviceFlag() == 2 ? deviceBean.getAdapterName() : deviceBean.getDeviceNumber());
            holder.setTextWithLeftDrawables(R.id.item_2, R.mipmap.ic_item_project_name, deviceBean.getPlaceName());
            holder.setTextWithLeftDrawables(R.id.item_3, R.mipmap.ic_item_installation, deviceBean.getDeviceInstallLocation());

            if (deviceBean.getDeviceFlag() == 2)
                holder.setVisibility(R.id.include_gateway_state, View.VISIBLE)
                        .setText(R.id.item_belong_state, getResources().getColor(StateTools.stateColor(deviceBean.getSubDeviceStateGroupCode())), deviceBean.getSubDeviceStateGroupName());
            else
                holder.setVisibility(R.id.include_gateway_state, View.GONE);

            StateTools.setStateTint(deviceBean.getDeviceStateGroupCode(), holder.getImageView(R.id.item_state));

            holder.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((DeviceBaseFragment) getParentFragment()).startFragment(DeviceDetailsFragment.newInstance(deviceBean.getDeviceId(), deviceBean.getAdapterName()));
                }
            });
            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    ((PlaceDetailFragment) getParentFragment().getParentFragment()).getOnLongClickListener(deviceBean);
                    return false;
                }
            });
        }

        @Override
        public void loadingFinished() {
        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.device_clear_et:
                deviceSearchEt.setText("");
                break;

            case R.id.device_state_tv:
                deviceStateWindows.showPopu(new CustomPopupWindows.InitCustomPopupListener() {
                    @Override
                    public void initPopup(CustomPopupWindows.PopupHolder holder) {
                        BswRecyclerView<CommonStateBean> popupRv = holder.getView(R.id.popup_rv);
                        popupRv.initAdapter(R.layout.item_popup_text_layout, new ConvertViewCallBack<CommonStateBean>() {
                            @Override
                            public void convert(RecyclerViewHolder holder, final CommonStateBean commonStateBean, final int position, int layoutTag) {
                                holder.setText(R.id.tv_popup_text, commonStateBean.getStateName())
                                        .setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                deviceStateWindows.dismiss();
                                                deviceStateTv.setText(commonStateBean.getStateName());
                                                deviceStateTv.setTag(commonStateBean.getStateCode());
                                                getDeviceList();
                                            }
                                        });
                            }

                            @Override
                            public void loadingFinished() {

                            }
                        }).setLayoutManager()
                                .setDecoration()
                                .setData(CommonUtils.getCommonList(_mActivity));
                        ViewGroup.LayoutParams lp = popupRv.getLayoutParams();
                        int[] location = new int[2];
                        deviceStateTv.getLocationOnScreen(location);
                        lp.height = CommonUtils.measureScreen().getScreenHeight(_mActivity) - (location[1] + deviceStateTv.getHeight());
                        popupRv.setBackgroundResource(android.R.color.white);
                        popupRv.setLayoutParams(lp);
                    }
                });
                break;
        }
    }
}