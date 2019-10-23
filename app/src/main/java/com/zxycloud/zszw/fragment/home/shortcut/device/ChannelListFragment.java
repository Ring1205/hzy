package com.zxycloud.zszw.fragment.home.shortcut.device;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.zxycloud.zszw.R;
import com.zxycloud.zszw.base.BaseBackFragment;
import com.zxycloud.zszw.listener.NetRequestListener;
import com.zxycloud.zszw.model.ChannelBean;
import com.zxycloud.zszw.model.request.ResultChannelListBean;
import com.zxycloud.zszw.utils.StateTools;
import com.zxycloud.common.base.BaseBean;
import com.zxycloud.common.utils.CommonUtils;
import com.zxycloud.common.utils.TimeUpUtils;
import com.zxycloud.common.utils.netWork.ApiRequest;
import com.zxycloud.common.utils.netWork.NetBean;
import com.zxycloud.common.utils.netWork.NetUtils;
import com.zxycloud.common.widget.BswRecyclerView.BswFilterLayoutFilter;
import com.zxycloud.common.widget.BswRecyclerView.BswLayoutItem;
import com.zxycloud.common.widget.BswRecyclerView.BswRecyclerView;
import com.zxycloud.common.widget.BswRecyclerView.ConvertViewCallBack;
import com.zxycloud.common.widget.BswRecyclerView.LimitAnnotation;
import com.zxycloud.common.widget.BswRecyclerView.RecyclerViewHolder;

@SuppressWarnings("ConstantConditions")
public class ChannelListFragment extends BaseBackFragment {
    private BswRecyclerView<ChannelBean> listRv;
    private NetUtils netUtils;

    private String deviceId;
    private String adapterName;

    public static ChannelListFragment newInstance(String deviceId, String adapterName) {
        Bundle args = new Bundle();
        args.putString("deviceId", deviceId);
        args.putString("adapterName", adapterName);
        ChannelListFragment fragment = new ChannelListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_channel_list_layout;
    }

    @SuppressWarnings("unchecked")
    private void getList() {
        ApiRequest apiRequest = netWork().apiRequest(NetBean.actionGetChannelListByDevice, ResultChannelListBean.class, ApiRequest.REQUEST_TYPE_POST, R.id.load_layout)
                .setRequestParams("deviceId", getArguments().getString("deviceId"));
        apiRequest.setRequestParams("pageSize", 10)
                .setRequestParams("pageIndex", 1);
        if (null == netUtils) {
            netUtils = NetUtils.getNewInstance(_mActivity);
        }
        final ApiRequest<ResultChannelListBean> finalApiRequest = apiRequest;
        netWork().setRefreshListener(R.id.refresh_layout, true, true, new NetRequestListener<BaseBean>() {
            @Override
            public void success(String action, BaseBean baseBean, Object tag) {
                if (baseBean.isSuccessful()) {
                    int currentIndex = (int) finalApiRequest.getRequestParams().get("pageIndex");
                    listRv.setData(((ResultChannelListBean) baseBean).getData(), currentIndex, 10);
                }
            }
        }, apiRequest);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void initView(Bundle savedInstanceState) {
        deviceId = getArguments().getString("deviceId");
        adapterName = getArguments().getString("adapterName");

        setToolbarTitle(R.string.string_channel_title).setToolbarMenu(R.menu.add, onMenuItemClickListener).initToolbarNav();

        listRv = findViewById(R.id.list_rv);
        listRv.initAdapter(R.layout.base_item, deviceCallBack)
                .setLayoutManager()
                .setLayoutFilterCallBack(layoutFilter);
        getList();
    }

    private ConvertViewCallBack<ChannelBean> deviceCallBack = new ConvertViewCallBack<ChannelBean>() {

        @Override
        public void convert(RecyclerViewHolder holder, final ChannelBean channelBean, final int position, int layoutTag) {
            holder.setText(R.id.item_title, channelBean.getSensorTagName());

            holder.setTextWithLeftDrawables(R.id.item_1, R.mipmap.ic_item_device_number, CommonUtils.string().getString(channelBean.getChannelNumber()));
            holder.setTextWithLeftDrawables(R.id.item_2, R.mipmap.ic_item_installation, channelBean.getDeviceInstallLocation());
            holder.setTextWithLeftDrawables(R.id.item_3, R.mipmap.ic_item_value, channelBean.getCollectValue().concat(channelBean.getUnit()));

            holder.setOnClickListener(R.id.channel_delete, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (CommonUtils.timeUpUtils().isTimeUp(TimeUpUtils.TIME_UP_CLICK, System.currentTimeMillis())) {
                        if (null == netUtils)
                            netUtils = NetUtils.getNewInstance(_mActivity);
                        netUtils.request(new NetUtils.NetRequestCallBack() {
                                             @Override
                                             public void success(String action, BaseBean baseBean, Object tag) {
                                                 if (baseBean.isSuccessful())
                                                     listRv.removeItem(position);
                                                 else
                                                     CommonUtils.toast(_mActivity, baseBean.getMessage());
                                             }

                                             @Override
                                             public void error(String action, Throwable e, Object tag) {
                                             }
                                         }
                                , true, new ApiRequest<>(NetBean.actionPostDeleteChannel, BaseBean.class)
                                        .setRequestType(ApiRequest.REQUEST_TYPE_POST)
                                        .setRequestParams("channelId", channelBean.getChannelId())
                                        .setRequestParams("adapterName", adapterName));
                    }
                    // 关闭列表的侧滑
                    listRv.closeSwipe();
                }
            }).setOnClickListener(R.id.channel_edit, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (CommonUtils.timeUpUtils().isTimeUp(TimeUpUtils.TIME_UP_CLICK, System.currentTimeMillis()))
                        startForResult(ChannelsInstallFragment.newInstance(deviceId, adapterName, channelBean.getChannelId()), 1211);
                    // 关闭列表的侧滑
                    listRv.closeSwipe();
                }
            });
            StateTools.setStateTint(channelBean.getDeviceStateGroupCode(), holder.getImageView(R.id.item_state));
        }

        @Override
        public void loadingFinished() {

        }
    };

    private BswFilterLayoutFilter<ChannelBean> layoutFilter = new BswFilterLayoutFilter<ChannelBean>() {
        @Override
        public void performFilter(ChannelBean channelBean, BswLayoutItem layoutItem) {
            layoutItem.put(LimitAnnotation.LAYOUT_RIGHT, R.layout.item_channel_right_layout);
        }
    };

    private Toolbar.OnMenuItemClickListener onMenuItemClickListener = new Toolbar.OnMenuItemClickListener() {
        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            if (CommonUtils.timeUpUtils().isTimeUp(TimeUpUtils.TIME_UP_CLICK, System.currentTimeMillis())) {
                startForResult(ChannelsInstallFragment.newInstance(deviceId, adapterName, null), 1211);
                return true;
            } else return false;
        }
    };

    @Override
    public void onFragmentResult(int requestCode, int resultCode, Bundle data) {
        super.onFragmentResult(requestCode, resultCode, data);
        if (requestCode == 1211 && resultCode == RESULT_OK && data != null)
            getList();
    }
}