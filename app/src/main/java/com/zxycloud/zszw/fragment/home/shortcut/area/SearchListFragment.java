package com.zxycloud.zszw.fragment.home.shortcut.area;

import android.annotation.SuppressLint;
import android.os.Bundle;
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
import com.zxycloud.zszw.base.BaseDataBean;
import com.zxycloud.zszw.event.type.DeviceShowType;
import com.zxycloud.zszw.event.type.PlaceShowType;
import com.zxycloud.zszw.event.type.SearchListShowType;
import com.zxycloud.zszw.fragment.home.shortcut.device.DeviceDetailsFragment;
import com.zxycloud.zszw.fragment.home.shortcut.place.PlaceDetailFragment;
import com.zxycloud.zszw.fragment.service.install.device.SelectPlaceFragment;
import com.zxycloud.zszw.listener.NetRequestListener;
import com.zxycloud.zszw.model.ResultAreaListBean;
import com.zxycloud.zszw.model.ResultDeviceListBean;
import com.zxycloud.zszw.model.ResultPlaceListBean;
import com.zxycloud.zszw.model.bean.AreaBean;
import com.zxycloud.zszw.model.bean.DeviceBean;
import com.zxycloud.zszw.model.bean.PlaceBean;
import com.zxycloud.zszw.utils.StateTools;
import com.zxycloud.common.base.BaseBean;
import com.zxycloud.common.utils.CommonStateBean;
import com.zxycloud.common.utils.CommonUtils;
import com.zxycloud.common.utils.SPUtils;
import com.zxycloud.common.utils.netWork.ApiRequest;
import com.zxycloud.common.utils.netWork.NetBean;
import com.zxycloud.common.utils.netWork.NetUtils;
import com.zxycloud.common.widget.AlertDialog;
import com.zxycloud.common.widget.BswRecyclerView.BswRecyclerView;
import com.zxycloud.common.widget.BswRecyclerView.ConvertViewCallBack;
import com.zxycloud.common.widget.BswRecyclerView.RecyclerViewHolder;
import com.zxycloud.common.widget.BswRecyclerView.SwipeItemLayout;
import com.zxycloud.common.widget.CustomPopupWindows;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("ConstantConditions")
public class SearchListFragment extends BaseBackFragment implements View.OnClickListener {
    private BswRecyclerView<BaseDataBean> listRv;
    private CustomPopupWindows deviceStateWindows;

    private String searchKeyName = "";
    private int pageSize = 20;
    @SuppressWarnings("FieldCanBeLocal")
    private int pageIndex = 1;
    private NetUtils netUtils;

    private int showType;
    private EditText deviceSearchEt;
    private TextView deviceStateTv;

    public static SearchListFragment newInstance(@SearchListShowType.showType int showType, String title, String areaId) {
        Bundle args = new Bundle();
        args.putInt("showType", showType);
        args.putString("areaId", areaId);
        args.putString("title", title);
        SearchListFragment fragment = new SearchListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public static SearchListFragment newInstance(@SearchListShowType.showType int showType, String title, String deviceParentId, String placeId) {
        Bundle args = new Bundle();
        args.putInt("showType", showType);
        args.putString("deviceParentId", deviceParentId);
        args.putString("placeId", placeId);
        args.putString("title", title);
        SearchListFragment fragment = new SearchListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_search_list_layout;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void initView(Bundle savedInstanceState) {
        if (null != listRv) {
            // ViewPager缓存的时候，取缓存时保存的数据，以免用户锚点失效
            return;
        }
        Bundle bundle = getArguments();
        showType = bundle.getInt("showType", PlaceShowType.SHOW_TYPE_MAIN);

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
                getList();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        if (showType == DeviceShowType.SHOW_TYPE_ADAPTER_LIST) {
            deviceSearchEt.setHint(R.string.device_search_hint);
        } else if (showType == SearchListShowType.SHOW_TYPE_AREA_OF_AREA) {
            deviceStateTv.setVisibility(View.GONE);
            findViewById(R.id.view_all_start).setVisibility(View.GONE);
        }

        listRv = findViewById(R.id.list_rv);
        listRv.initAdapter(R.layout.base_item, deviceCallBack)
                .setLayoutManager(new LinearLayoutManager(getContext()));
        getList();

        setOnClickListener(this, R.id.device_clear_et, R.id.device_state_tv);
    }

    @SuppressWarnings("unchecked")
    private void getList() {
        String projectId = CommonUtils.getSPUtils(_mActivity).getString(SPUtils.PROJECT_ID);
        int deviceStateGroupCode = (int) deviceStateTv.getTag();
        ApiRequest apiRequest = null;
        switch (showType) {
            case SearchListShowType.SHOW_TYPE_CONTROLLER_OR_ADAPTER:
                ((TextView) findViewById(R.id.search_list_title)).setText(R.string.string_device_adapter_title);
                apiRequest = netWork().apiRequest(NetBean.actionPostParentDeviceIdList, ResultDeviceListBean.class, ApiRequest.REQUEST_TYPE_POST, R.id.load_layout)
                        .setRequestParams("deviceParentId", getArguments().getString("deviceParentId"))
                        .setRequestParams("placeId", getArguments().getString("placeId"));
                if (deviceStateGroupCode != -1)
                    apiRequest.setRequestParams("deviceStateGroupCode", deviceStateGroupCode);

                if (!TextUtils.isEmpty(searchKeyName))
                    apiRequest.setRequestParams("searchKeyName", searchKeyName);
                break;

            case SearchListShowType.SHOW_TYPE_PLACE_OF_AREA:
                ((TextView) findViewById(R.id.search_list_title)).setText(R.string.search_place_list);
                apiRequest = netWork().apiRequest(NetBean.actionPostPlaceList, ResultPlaceListBean.class, ApiRequest.REQUEST_TYPE_POST, R.id.load_layout)
                        .setRequestParams("areaId", getArguments().getString("areaId"));
                if (deviceStateGroupCode != -1)
                    apiRequest.setRequestParams("stateCode", deviceStateGroupCode);
                if (!TextUtils.isEmpty(searchKeyName))
                    apiRequest.setRequestParams("placeName", searchKeyName);
                break;

            case SearchListShowType.SHOW_TYPE_AREA_OF_AREA:
                ((TextView) findViewById(R.id.search_list_title)).setText(R.string.search_area_list);
                apiRequest = netWork().apiRequest(NetBean.actionPostAreaList, ResultAreaListBean.class, ApiRequest.REQUEST_TYPE_POST, R.id.load_layout)
                        .setRequestParams("areaId", getArguments().getString("areaId"));
                if (!TextUtils.isEmpty(searchKeyName))
                    apiRequest.setRequestParams("areaName", searchKeyName);
                break;
        }

        if (null == apiRequest) {
            finish();
            CommonUtils.toast(_mActivity, R.string.data_abnormal);
            return;
        }
        apiRequest.setRequestParams("pageSize", pageSize)
                .setRequestParams("pageIndex", pageIndex);
        if (!TextUtils.isEmpty(projectId))
            apiRequest.setRequestParams("projectId", projectId);

        if (null == netUtils)
            netUtils = NetUtils.getNewInstance(_mActivity);

        final ApiRequest<ResultDeviceListBean> finalApiRequest = apiRequest;
        netWork().setRefreshListener(R.id.refresh_layout, false, true, new NetRequestListener<BaseBean>() {
            @Override
            public void success(String action, BaseBean baseBean, Object tag) {
                if (baseBean.isSuccessful()) {
                    int currentIndex = (int) finalApiRequest.getRequestParams().get("pageIndex");
                    List<BaseDataBean> baseDataBeans = new ArrayList<>();
                    switch (action) {
                        case NetBean.actionPostPlaceList:
                            baseDataBeans.addAll(((ResultPlaceListBean) baseBean).getData());
                            break;

                        case NetBean.actionPostAreaList:
                            baseDataBeans.addAll(((ResultAreaListBean) baseBean).getData());
                            break;

                        case NetBean.actionPostParentDeviceIdList:
                            baseDataBeans.addAll(((ResultDeviceListBean) baseBean).getData());
                            break;
                    }
                    listRv.setData(baseDataBeans, currentIndex, pageSize);
                }
            }
        }, apiRequest);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
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
                                                getList();
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

    private ConvertViewCallBack<BaseDataBean> deviceCallBack = new ConvertViewCallBack<BaseDataBean>() {

        @Override
        public void convert(RecyclerViewHolder holder, final BaseDataBean dataBean, int position, int layoutTag) {
            if (dataBean instanceof DeviceBean) {
                formatDevice(holder, (DeviceBean) dataBean, position);
            } else if (dataBean instanceof PlaceBean) {
                formatPlace(holder, (PlaceBean) dataBean, position);
            } else if (dataBean instanceof AreaBean) {
                formatArea(holder, (AreaBean) dataBean, position);
            }
        }

        @Override
        public void loadingFinished() {

        }

        /**
         * 格式化设备
         * @param holder 列表holder
         * @param deviceBean 设备类
         */
        private void formatDevice(RecyclerViewHolder holder, final DeviceBean deviceBean, final int position) {
            holder.setText(R.id.item_title, deviceBean.getUserDeviceTypeName());

            holder.setTextWithLeftDrawables(R.id.item_1, R.mipmap.ic_item_device_number, deviceBean.getDeviceFlag() == 2 ? deviceBean.getAdapterName() : deviceBean.getDeviceNumber());
            holder.setTextWithLeftDrawables(R.id.item_2, R.mipmap.ic_item_project_name, deviceBean.getPlaceName());
            holder.setTextWithLeftDrawables(R.id.item_3, R.mipmap.ic_item_installation, deviceBean.getDeviceInstallLocation());

            if (deviceBean.getDeviceFlag() == 2)
                holder.setVisibility(R.id.include_gateway_state, View.VISIBLE)
                        .setText(R.id.item_belong_state, getResources().getColor(StateTools.stateColor(deviceBean.getSubDeviceStateGroupCode())), deviceBean.getSubDeviceStateGroupName());
            else
                holder.setVisibility(R.id.include_gateway_state, View.GONE);

            holder.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (showType == SearchListShowType.SHOW_TYPE_CONTROLLER_OR_ADAPTER)
                        ((DeviceDetailsFragment) getParentFragment()).start(DeviceDetailsFragment.newInstance(deviceBean.getDeviceId(), deviceBean.getAdapterName()));
                    else
                        ((AreaDetailFragment) getParentFragment()).start(DeviceDetailsFragment.newInstance(deviceBean.getDeviceId(), deviceBean.getAdapterName()));
                }
            });

            StateTools.setStateTint(deviceBean.getDeviceStateGroupCode(), holder.getImageView(R.id.item_state));

            holder.setOnClickListener(R.id.card_allocation, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listRv.notifyItemChanged(position);
                    if (showType == SearchListShowType.SHOW_TYPE_CONTROLLER_OR_ADAPTER)
                        ((DeviceDetailsFragment) getParentFragment()).start(SelectPlaceFragment.newInstance(deviceBean.getAdapterName(), deviceBean.getDeviceId()));
                    else
                        ((AreaDetailFragment) getParentFragment()).start(SelectPlaceFragment.newInstance(deviceBean.getAdapterName(), deviceBean.getDeviceId()));
                }
            });
            holder.setOnClickListener(R.id.card_unassign, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    deviceDeleteOrUnassign(false, deviceBean.getAdapterName(), deviceBean.getDeviceId(), position);
                }
            });
            holder.setOnClickListener(R.id.card_delete, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    deviceDeleteOrUnassign(true, deviceBean.getAdapterName(), deviceBean.getDeviceId(), position);
                }
            });
        }

        /**
         * 删除和取消分配
         *
         * @param isDelete    是否是删除
         * @param adapterName 网关名
         * @param deviceId    设备id
         * @param position
         */
        private void deviceDeleteOrUnassign(final boolean isDelete, final String adapterName, final String deviceId, final int position) {
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
                                                                           switch (action) {
                                                                               // 取消分配
                                                                               case NetBean.actionPostDeviceUnassign:
                                                                                   netWork().loading();
                                                                                   break;
                                                                               // 删除设备
                                                                               case NetBean.actionPostDeleteDeviceInfo:
                                                                                   listRv.removeItem(position);
                                                                                   listRv.notifyDataSetChanged();
                                                                                   break;
                                                                           }
                                                                       CommonUtils.toast(getContext(), baseBean.getMessage());
                                                                   }

                                                                   @Override
                                                                   public void error(String action, Throwable e, Object tag) {
                                                                   }
                                                               }
                                    , false
                                    , request);
                            listRv.notifyItemChanged(position);
                        }
                    }).show();
        }

        /**
         * 格式化场所
         *
         * @param holder    列表holder
         * @param placeBean 场所类
         * @param position
         */
        private void formatPlace(RecyclerViewHolder holder, final PlaceBean placeBean, final int position) {
            holder.setText(R.id.item_title, placeBean.getPlaceName());
            holder.setTextWithLeftDrawables(R.id.item_1, R.mipmap.ic_item_project_name, placeBean.getProjectName());
            holder.setTextWithLeftDrawables(R.id.item_2, R.mipmap.ic_item_address, placeBean.getPlaceAddress());

            holder.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (showType == SearchListShowType.SHOW_TYPE_CONTROLLER_OR_ADAPTER)
                                ((DeviceDetailsFragment) getParentFragment()).start(PlaceDetailFragment.newInstance(placeBean.getPlaceId()));
                            else
                                ((AreaDetailFragment) getParentFragment()).start(PlaceDetailFragment.newInstance(placeBean.getPlaceId()));
                        }
                    });
            StateTools.setStateTint(placeBean.getStateGroupCode(), holder.getImageView(R.id.item_state));

            ((SwipeItemLayout) holder.getView(R.id.sil_drag)).setSwipeEnable(placeBean.isCanDelete());
            holder.setOnClickListener(R.id.card_delete, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new NetUtils(getContext()).request(new NetUtils.NetRequestCallBack() {
                        @Override
                        public void success(String action, BaseBean baseBean, Object tag) {
                            if (baseBean.isSuccessful()) {
                                listRv.removeItem(position);
                                listRv.notifyDataSetChanged();
                            }
                            CommonUtils.toast(getContext(), baseBean.getMessage());
                        }

                        @Override
                        public void error(String action, Throwable e, Object tag) {

                        }
                    }, false, new ApiRequest(NetBean.actionGetPlaceDelete, BaseBean.class)
                            .setRequestParams("placeId", placeBean.getPlaceId()));
                }
            });
        }

        /**
         * 格式化区域
         *
         * @param holder   列表holder
         * @param areaBean 区域类
         * @param position
         */
        private void formatArea(RecyclerViewHolder holder, final AreaBean areaBean, final int position) {
            holder.setText(R.id.item_title, areaBean.getAreaName());

            holder.setTextWithLeftDrawables(R.id.item_1, R.mipmap.ic_item_project_name, areaBean.getProjectName());
            holder.setTextWithLeftDrawables(R.id.item_2, R.mipmap.ic_item_project_type, areaBean.getProjectType());

            holder.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (showType == SearchListShowType.SHOW_TYPE_CONTROLLER_OR_ADAPTER)
                        ((DeviceDetailsFragment) getParentFragment()).start(AreaDetailFragment.newInstance(areaBean.getAreaId()));
                    else
                        ((AreaDetailFragment) getParentFragment()).start(AreaDetailFragment.newInstance(areaBean.getAreaId()));
                }
            });

            ((SwipeItemLayout) holder.getView(R.id.sil_drag)).setSwipeEnable(areaBean.isCanDelete());
            holder.setOnClickListener(R.id.card_delete, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new NetUtils(getContext())
                            .request(new NetUtils.NetRequestCallBack() {
                                         @Override
                                         public void success(String action, BaseBean baseBean, Object tag) {
                                             if (baseBean.isSuccessful()) {
                                                 listRv.removeItem(position);
                                                 listRv.notifyDataSetChanged();
                                             }
                                             CommonUtils.toast(getContext(), baseBean.getMessage());
                                         }

                                         @Override
                                         public void error(String action, Throwable e, Object tag) {

                                         }
                                     }
                                    , false
                                    , new ApiRequest(NetBean.actionGetDeleteArea, BaseBean.class)
                                            .setRequestParams("areaId", areaBean.getAreaId()));
                }
            });
        }
    };

}