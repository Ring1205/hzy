package com.zxycloud.zszw.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.zxycloud.zszw.R;
import com.zxycloud.zszw.listener.OnItemClickListener;
import com.zxycloud.zszw.model.bean.AllocateBean;
import com.zxycloud.zszw.model.bean.DeviceBean;
import com.zxycloud.zszw.model.bean.SystemBean;
import com.zxycloud.common.utils.CommonUtils;
import com.zxycloud.common.widget.BswRecyclerView.RecyclerViewHolder;
import com.zxycloud.common.widget.DropdownButton;

import java.util.ArrayList;
import java.util.List;

public class DeviceInstallAdapter extends RecyclerView.Adapter<RecyclerViewHolder> {
    private Context context;
    private String placeId;
    private List<DeviceBean> deviceBeans;
    private OnItemClickListener mClickListener;
    private List<SystemBean> systemData;
    private List<AllocateBean> deviceDistributions;

    public DeviceInstallAdapter(Context context, List<DeviceBean> deviceBeans, String placeId) {
        this.placeId = placeId;
        this.context = context;
        this.deviceBeans = deviceBeans;
        deviceDistributions = new ArrayList<>();
    }

    public void setSystemData(List<SystemBean> systemData) {
        this.systemData = systemData;
        notifyDataSetChanged();
    }

    public void setAllocateList(int position, AllocateBean bean) {
        deviceDistributions.set(position, bean);
        notifyDataSetChanged();
    }

    public List<AllocateBean> getDeviceDistributions() {
        return deviceDistributions;
    }

    @NonNull
    @Override
    public RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new RecyclerViewHolder(LayoutInflater.from(context).inflate(R.layout.item_install_device, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerViewHolder holder, final int i) {
        final AllocateBean allocateBean;
        if (deviceDistributions.size() > i) {
            allocateBean = deviceDistributions.get(i);
            if (systemData != null)
                for (SystemBean systemDatum : systemData)
                    if (systemDatum.getDeviceSystemCode() == allocateBean.getDeviceSystemCode())
                        ((EditText) holder.getView(R.id.et_system)).setText(systemDatum.getDeviceSystemName());
        } else {
            allocateBean = new AllocateBean();
            allocateBean.setDeviceId(deviceBeans.get(i).getDeviceId());
            allocateBean.setAdapterName(deviceBeans.get(i).getAdapterName());
            allocateBean.setDeviceSystemCode(deviceBeans.get(i).getDeviceSystemCode());
            allocateBean.setDeviceSystemName(deviceBeans.get(i).getDeviceSystemName());
            allocateBean.setDeviceUnitTypeName(deviceBeans.get(i).getDeviceUnitTypeName());
            allocateBean.setUserDeviceTypeName(deviceBeans.get(i).getUserDeviceTypeName());
            allocateBean.setUserDeviceTypeCode(deviceBeans.get(i).getUserDeviceTypeCode());
            allocateBean.setPlaceId(placeId);
            allocateBean.setDeviceInstallLocation(deviceBeans.get(i).getDeviceInstallLocation());
            deviceDistributions.add(allocateBean);
        }

        holder.setText(R.id.tv_device_name, // 设备名称
                CommonUtils.string().getString(context, R.string.title_device_name)
                        .concat(CommonUtils.string().getString(allocateBean.getUserDeviceTypeName())));
        holder.setText(R.id.tv_device_number,// 设备编码
                CommonUtils.string().getString(context, R.string.title_device_number)
                        .concat(CommonUtils.string().getString(deviceBeans.get(i).getDeviceNumber())));

        holder.getView(R.id.bt_install_details).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mClickListener.onItemClick(i, v, holder);
            }
        });
        TextViewAdapter textAdapter = new TextViewAdapter(context);
        ((DropdownButton) holder.getView(R.id.ddb_belong)).setAdapter(textAdapter);
        textAdapter.setList(systemData);
        textAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(int position, View view, RecyclerView.ViewHolder vh) {
                ((EditText) holder.getView(R.id.et_system)).setText(systemData.get(position).getDeviceSystemName());
                deviceDistributions.get(i).setDeviceSystemCode(systemData.get(position).getDeviceSystemCode());
                deviceDistributions.get(i).setDeviceSystemName(systemData.get(position).getDeviceSystemName());
                ((DropdownButton) holder.getView(R.id.ddb_belong)).onDismiss();
            }
        });

        final EditText etBelong = holder.getView(R.id.et_belong);
        etBelong.setTag(i);
        etBelong.setText(deviceDistributions.get(i).getDeviceInstallLocation());
        etBelong.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (etBelong.hasFocus())
                    deviceDistributions.get((Integer) etBelong.getTag()).setDeviceInstallLocation(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return deviceBeans != null ? deviceBeans.size() : 0;
    }

    public void setOnItemClickListener(OnItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

}
