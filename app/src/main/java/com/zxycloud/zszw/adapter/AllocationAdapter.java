package com.zxycloud.zszw.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import com.zxycloud.zszw.R;
import com.zxycloud.zszw.listener.OnCheckedChangeListener;
import com.zxycloud.zszw.model.bean.DeviceBean;
import com.zxycloud.common.utils.CommonUtils;
import com.zxycloud.common.utils.DateUtils;
import com.zxycloud.common.widget.BswRecyclerView.RecyclerViewHolder;

import java.util.List;

public class AllocationAdapter extends RecyclerView.Adapter<AllocationAdapter.DeviceHolder> {
    private Context context;
    private SparseArray<String> sparseArray;
    private List<DeviceBean> deviceBeans;
    private OnCheckedChangeListener itemClickListener;

    public AllocationAdapter(Context context) {
        this.context = context;
        sparseArray = CommonUtils.string().formatStringLength(context
                , R.string.title_device_name
                , R.string.title_device_number
                , R.string.title_validity);
    }

    public void setData(List<DeviceBean> deviceBeans) {
        this.deviceBeans = deviceBeans;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public DeviceHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new DeviceHolder(LayoutInflater.from(context).inflate(R.layout.item_allcated, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final DeviceHolder deviceHolder, final int i) {
        DeviceBean bean = deviceBeans.get(i);

        deviceHolder.setText(R.id.tv_allcated_info, sparseArray.get(R.string.title_device_name).concat(bean.getUserDeviceTypeName()).concat("\n")// 设备名称
                .concat(sparseArray.get(R.string.title_device_number).concat(bean.getDeviceNumber())).concat("\n") // 设备编号
                .concat(sparseArray.get(R.string.title_validity)).concat(DateUtils.format(bean.getDeviceUseEndTime(),"yyyy-MM-dd")));// 服务截止日期

        final CheckBox checkBox = deviceHolder.getView(R.id.cb_allcated);
        checkBox.setChecked(bean.isCheck());
        checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deviceBeans.get(i).setCheck(checkBox.isChecked());
                boolean bx = true;
                for (DeviceBean deviceBean : deviceBeans)
                    if (!deviceBean.isCheck())
                        bx = false;
                itemClickListener.onCheckedChanged(bx, checkBox.isChecked(), i);
            }
        });
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

    public void setOnItemCheckListener(OnCheckedChangeListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }
}
