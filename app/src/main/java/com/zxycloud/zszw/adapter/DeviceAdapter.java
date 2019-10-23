package com.zxycloud.zszw.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.zxycloud.zszw.R;
import com.zxycloud.zszw.listener.OnItemClickListener;
import com.zxycloud.zszw.model.bean.DeviceBean;
import com.zxycloud.zszw.utils.StateTools;
import com.zxycloud.common.base.BaseBean;
import com.zxycloud.common.utils.CommonUtils;
import com.zxycloud.common.utils.netWork.ApiRequest;
import com.zxycloud.common.utils.netWork.NetBean;
import com.zxycloud.common.utils.netWork.NetUtils;
import com.zxycloud.common.widget.AlertDialog;
import com.zxycloud.common.widget.BswRecyclerView.RecyclerViewHolder;

import java.util.List;

public class DeviceAdapter extends RecyclerView.Adapter<DeviceAdapter.DeviceHolder> {
    private Context context;
    private List<DeviceBean> deviceBeans;
    private OnItemClickListener mClickListener;

    public DeviceAdapter(Context context) {
        this.context = context;
    }

    public void setData(List<DeviceBean> deviceBeans) {
        this.deviceBeans = deviceBeans;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public DeviceHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new DeviceHolder(LayoutInflater.from(context).inflate(R.layout.base_item, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final DeviceHolder deviceHolder, final int i) {
        final DeviceBean bean = deviceBeans.get(i);
        // 是否为网关设备
        if (bean.getDeviceFlag() == 2)
            deviceHolder.setVisibility(R.id.include_gateway_state, View.VISIBLE)
                    .setText(R.id.item_belong_state, context.getResources().getColor(StateTools.stateColor(bean.getSubDeviceStateGroupCode())), bean.getSubDeviceStateGroupName());// 网关状态
        else
            deviceHolder.setVisibility(R.id.include_gateway_state, View.GONE);

        deviceHolder.setText(R.id.item_title, bean.getUserDeviceTypeName());// 设备名称
        deviceHolder.setTextWithLeftDrawables(R.id.item_1, R.mipmap.ic_item_device_number, bean.getDeviceFlag() == 2 ? bean.getAdapterName() : bean.getDeviceNumber());// 网关名称
        deviceHolder.setTextWithLeftDrawables(R.id.item_2, R.mipmap.ic_item_project_name, bean.getPlaceName());// 所属场所
        deviceHolder.setTextWithLeftDrawables(R.id.item_3, R.mipmap.ic_item_installation, bean.getDeviceInstallLocation());// 安装位置

        // 设备状态
        StateTools.setStateTint(bean.getDeviceStateGroupCode(), deviceHolder.getImageView(R.id.item_state));
        // 分配
        deviceHolder.setOnClickListener(R.id.card_delete, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deviceDeleteOrUnassign(true, bean.getAdapterName(), bean.getDeviceId(), i);
            }
        });// 删除设备
        deviceHolder.setOnClickListener(R.id.card_allocation, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onAllocationListener.allocationDevice(bean.getAdapterName(), bean.getDeviceId());
                notifyItemChanged(i);
            }
        });// 取消分配
        deviceHolder.setOnClickListener(R.id.card_unassign, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deviceDeleteOrUnassign(false, bean.getAdapterName(), bean.getDeviceId(), i);
            }
        });

        deviceHolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mClickListener.onItemClick(deviceHolder.getAdapterPosition(), v, deviceHolder);
            }
        });

    }

    /**
     * 删除和取消分配
     *
     * @param isDelete    是否删除
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
        AlertDialog builder = new AlertDialog(context).builder();
        builder.setTitle(isDelete ? R.string.hint_delete_device_title : R.string.hint_unassign_device_title)
                .setMsg(isDelete ? R.string.hint_delete_device_msg : R.string.hint_unassign_device_msg)
                .setNegativeButton(R.string.dialog_no, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        notifyDataSetChanged();
                    }
                })
                .setPositiveButton(R.string.dialog_yes, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new NetUtils(context).request(new NetUtils.NetRequestCallBack() {
                                                          @Override
                                                          public void success(String action, BaseBean baseBean, Object tag) {
                                                              if (baseBean.isSuccessful())
                                                                  switch (action) {
                                                                      // 取消分配
                                                                      case NetBean.actionPostDeviceUnassign:
                                                                          onAllocationListener.updateData();
                                                                          break;
                                                                      // 删除设备
                                                                      case NetBean.actionPostDeleteDeviceInfo:
                                                                          deviceBeans.remove(position);
                                                                          break;
                                                                  }
                                                              notifyDataSetChanged();
                                                              CommonUtils.toast(context, baseBean.getMessage());
                                                          }

                                                          @Override
                                                          public void error(String action, Throwable e, Object tag) {
                                                          }
                                                      }
                                , false
                                , request);
                        notifyItemChanged(position);
                    }
                }).show();
    }

    @Override
    public int getItemCount() {
        return deviceBeans != null ? deviceBeans.size() : 0;
    }

    class DeviceHolder extends RecyclerViewHolder {
        public DeviceHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

    public void setOnItemClickListener(OnItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    public void setOnAllocationListener(OnAllocationListener onAllocationListener) {
        this.onAllocationListener = onAllocationListener;
    }

    OnAllocationListener onAllocationListener;

    public interface OnAllocationListener {
        void allocationDevice(String name, String id);

        void updateData();
    }

}
