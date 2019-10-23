package com.zxycloud.zszw.fragment.home.shortcut.device;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.zxycloud.zszw.R;
import com.zxycloud.zszw.base.BaseBackFragment;
import com.zxycloud.zszw.fragment.common.StringSelectFragment;
import com.zxycloud.zszw.listener.NetRequestListener;
import com.zxycloud.zszw.listener.OnItemClickListener;
import com.zxycloud.zszw.model.ResultChannelDetailBeans;
import com.zxycloud.zszw.model.ResultSensorTagBeans;
import com.zxycloud.common.base.BaseBean;
import com.zxycloud.common.utils.CommonUtils;
import com.zxycloud.common.utils.netWork.ApiRequest;
import com.zxycloud.common.utils.netWork.NetBean;

import java.util.ArrayList;
import java.util.List;

public class ChannelsInstallFragment extends BaseBackFragment implements Toolbar.OnMenuItemClickListener, View.OnClickListener {
    private boolean isEdit;
    private String deviceId, adapterName, channelId;
    private ResultChannelDetailBeans.DataBean mChannelDetailData;
    private List<ResultSensorTagBeans.DataBean> mSensorTagData;
    private int sensorTagCode;

    /**
     * 添加与编辑
     *
     * @param deviceId    设备id
     * @param adapterName 网关名
     * @param channelId   通道id
     * @return 设备id不为空为添加, 通道id不为空为编辑
     */
    public static ChannelsInstallFragment newInstance(String deviceId, String adapterName, String channelId) {
        Bundle bundle = new Bundle();
        bundle.putString("deviceId", deviceId);
        bundle.putString("adapterName", adapterName);
        bundle.putString("channelId", channelId);
        ChannelsInstallFragment fragment = new ChannelsInstallFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        deviceId = getArguments().getString("deviceId");
        adapterName = getArguments().getString("adapterName");
        channelId = getArguments().getString("channelId");
        isEdit = channelId != null && !channelId.isEmpty();
        if (isEdit)
            setToolbarTitle(R.string.channels_edit).initToolbarNav().setToolbarMenu(R.menu.save, this);
        else
            setToolbarTitle(R.string.channels_add).initToolbarNav().setToolbarMenu(R.menu.save, this);

        initData();

        setOnClickListener(this, R.id.et_channels_type);
    }

    private void initData() {
        netWork().setRequestListener(new NetRequestListener() {
                                         @Override
                                         public void success(String action, BaseBean baseBean, Object tag) {
                                             if (baseBean.isSuccessful())
                                                 switch (action) {
                                                     case NetBean.actionGetSensorTagList:
                                                         mSensorTagData = ((ResultSensorTagBeans) baseBean).getData();
                                                         break;
                                                     case NetBean.actionGetChannelDetail:
                                                         mChannelDetailData = ((ResultChannelDetailBeans) baseBean).getData();
                                                         sensorTagCode = mChannelDetailData.getSensorTagCode();
                                                         ((EditText) findViewById(R.id.et_channels_type)).setText(mChannelDetailData.getSensorTagName());// 通道类型
                                                         ((EditText) findViewById(R.id.et_channels_unit)).setText(mChannelDetailData.getUnit());// 采集单位
                                                         ((EditText) findViewById(R.id.et_channels_code)).setText(CommonUtils.string().getString(mChannelDetailData.getChannelNumber()));// 通道号
                                                         ((EditText) findViewById(R.id.et_amplification_factor)).setText(CommonUtils.string().getString(mChannelDetailData.getMultiple()));// 放大倍数
                                                         ((EditText) findViewById(R.id.et_install_location)).setText(mChannelDetailData.getDeviceInstallLocation());// 安装位置
                                                         ((EditText) findViewById(R.id.et_install_max_range)).setText(CommonUtils.string().getString(mChannelDetailData.getMaxRange()));// 最大采集模拟量值
                                                         ((EditText) findViewById(R.id.et_install_min_range)).setText(CommonUtils.string().getString(mChannelDetailData.getMinRange()));// 最小采集模拟量值
                                                         ((EditText) findViewById(R.id.et_install_max_alarm)).setText(CommonUtils.string().getString(mChannelDetailData.getMaxAlarm()));// 最大报警模拟量值
                                                         ((EditText) findViewById(R.id.et_install_min_alarm)).setText(CommonUtils.string().getString(mChannelDetailData.getMinAlarm()));// 最小报警模拟量值
                                                         break;
                                                     case NetBean.actionPostEditChannel:
                                                     case NetBean.actionPostAddChannelInDevice:
                                                         Bundle bundle = new Bundle();
                                                         setFragmentResult(RESULT_OK, bundle);
                                                         finish();
                                                         break;
                                                 }
                                             else
                                                 CommonUtils.toast(getContext(), baseBean.getMessage());
                                         }
                                     },
                netWork().apiRequest(NetBean.actionGetSensorTagList, ResultSensorTagBeans.class, ApiRequest.REQUEST_TYPE_GET),// 通道类型列表
                isEdit ? netWork().apiRequest(NetBean.actionGetChannelDetail, ResultChannelDetailBeans.class, ApiRequest.REQUEST_TYPE_GET)// 编辑时的通道详情
                        .setRequestParams("adapterName", adapterName).setRequestParams("channelId", channelId) : null);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.device_channels_install;
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {
        Integer maxRange = CommonUtils.string().getInt((EditText) findViewById(R.id.et_install_max_range));
        Integer minRange = CommonUtils.string().getInt((EditText) findViewById(R.id.et_install_min_range));
        Integer maxAlarm = CommonUtils.string().getInt((EditText) findViewById(R.id.et_install_max_alarm));
        Integer minAlarm = CommonUtils.string().getInt((EditText) findViewById(R.id.et_install_min_alarm));

        if (((EditText) findViewById(R.id.et_channels_type)).getText().toString().trim().isEmpty())
            CommonUtils.toast(getContext(), R.string.toast_select_channel_type);
        else if (((EditText) findViewById(R.id.et_channels_unit)).getText().toString().trim().isEmpty())
            CommonUtils.toast(getContext(), R.string.toast_acquisition_unit);
        else if (((EditText) findViewById(R.id.et_channels_code)).getText().toString().trim().isEmpty())
            CommonUtils.toast(getContext(), R.string.toast_channel_number);
        else if (((EditText) findViewById(R.id.et_amplification_factor)).getText().toString().trim().isEmpty() || Integer.valueOf(((EditText) findViewById(R.id.et_amplification_factor)).getText().toString().trim()) < 1)
            CommonUtils.toast(getContext(), R.string.toast_channel_factor);
        else if (null == maxRange || maxRange < 0 || maxRange > 100000)
            CommonUtils.toast(getContext(), R.string.toast_channel_max_range);
        else if (null == minRange || minRange < 0 || minRange > 100000)
            CommonUtils.toast(getContext(), R.string.toast_channel_min_range);
        else if (minRange > maxRange)
            CommonUtils.toast(getContext(), R.string.toast_channel_min_range_greater_max_range);
        else if (null != maxAlarm && null != minAlarm && minAlarm > maxAlarm)
            CommonUtils.toast(getContext(), R.string.toast_channel_min_alarm_greater_max_alarm);
        else if (null != maxAlarm && null != minAlarm &&
                (minAlarm < 0 || minAlarm > 100000 || maxAlarm < 0 || maxAlarm > 100000))
            CommonUtils.toast(getContext(), R.string.toast_channel_min_or_max_alarm_wrong);
        else {
            ApiRequest<BaseBean> apiRequest = isEdit ?
                    netWork().apiRequest(NetBean.actionPostEditChannel, BaseBean.class, ApiRequest.REQUEST_TYPE_POST)// 编辑通道
                            .setRequestParams("channelId", channelId)
                            .setRequestParams("adapterName", adapterName)
                            .setRequestParams("channelNumber", Integer.valueOf(((EditText) findViewById(R.id.et_channels_code)).getText().toString()))
                            .setRequestParams("sensorTagCode", sensorTagCode)
                            .setRequestParams("deviceInstallLocation", ((EditText) findViewById(R.id.et_install_location)).getText().toString())
                            .setRequestParams("unit", ((EditText) findViewById(R.id.et_channels_unit)).getText().toString())
                            .setRequestParams("multiple", Integer.valueOf(((EditText) findViewById(R.id.et_amplification_factor)).getText().toString()))
                            .setRequestParams("maxRange", maxRange)
                            .setRequestParams("minRange", minRange)
                    : netWork().apiRequest(NetBean.actionPostAddChannelInDevice, BaseBean.class, ApiRequest.REQUEST_TYPE_POST)// 添加通道
                    .setRequestParams("deviceId", deviceId)
                    .setRequestParams("adapterName", adapterName)
                    .setRequestParams("channelNumber", Integer.valueOf(((EditText) findViewById(R.id.et_channels_code)).getText().toString()))
                    .setRequestParams("sensorTagCode", sensorTagCode)
                    .setRequestParams("deviceInstallLocation", ((EditText) findViewById(R.id.et_install_location)).getText().toString())
                    .setRequestParams("unit", ((EditText) findViewById(R.id.et_channels_unit)).getText().toString())
                    .setRequestParams("multiple", Integer.valueOf(((EditText) findViewById(R.id.et_amplification_factor)).getText().toString()))
                    .setRequestParams("maxRange", maxRange)
                    .setRequestParams("minRange", minRange);

            if (null != maxAlarm)
                apiRequest.setRequestParams("maxAlarm", maxAlarm);
            if (null != minAlarm)
                apiRequest.setRequestParams("minAlarm", minAlarm);
            netWork().addRequestListener(apiRequest);
        }
        return true;
    }

    @Override
    public void onClick(View v) {
        if (null == mSensorTagData) {
            CommonUtils.toast(_mActivity, R.string.data_abnormal);
            netWork().setRequestListener(NetBean.actionGetSensorTagList);
            return;
        }
        ArrayList<String> data = new ArrayList<>();
        for (ResultSensorTagBeans.DataBean dataBean : mSensorTagData)
            data.add(dataBean.getSensorName());
        StringSelectFragment selectFragmentss = StringSelectFragment.newInstance(R.string.channels_type, data);
        selectFragmentss.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(int position, View view, RecyclerView.ViewHolder vh) {
                ((EditText) findViewById(R.id.et_channels_type)).setText(mSensorTagData.get(position).getSensorName());// 通道类型
                ((EditText) findViewById(R.id.et_channels_unit)).setText(mSensorTagData.get(position).getUnit());// 采集单位
                sensorTagCode = mSensorTagData.get(position).getSensorTagCode();
            }
        });
        start(selectFragmentss);
    }
}
