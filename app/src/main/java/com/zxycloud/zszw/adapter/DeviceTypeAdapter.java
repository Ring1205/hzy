package com.zxycloud.zszw.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import com.zxycloud.zszw.R;
import com.zxycloud.zszw.listener.OnItemClickListener;
import com.zxycloud.zszw.model.ResultEquTypeBean;
import com.zxycloud.common.utils.CommonUtils;
import com.zxycloud.common.widget.BswRecyclerView.RecyclerViewHolder;

import java.util.List;

public class DeviceTypeAdapter extends RecyclerView.Adapter<RecyclerViewHolder> {
    private Context context;
    private List<ResultEquTypeBean.DataBean> dataBeans;

    public DeviceTypeAdapter(Context context) {
        this.context = context;
    }

    public void setDataBeans(List<ResultEquTypeBean.DataBean> dataBeans) {
        this.dataBeans = dataBeans;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new RecyclerViewHolder(LayoutInflater.from(context).inflate(R.layout.item_check_text, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerViewHolder recyclerViewHolder, final int i) {
        final ResultEquTypeBean.DataBean dataBean = dataBeans.get(i);
        recyclerViewHolder.setText(R.id.tv_device_name, CommonUtils.string().getString(dataBean.getEquTypeName()));
        final CheckBox checkBox = recyclerViewHolder.getView(R.id.cb_device_type);
//        checkBox.setChecked(ids.contains(dataBean.getId()));
        checkBox.setVisibility(View.GONE);
        recyclerViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mClickListener.onItemClick(i, v, recyclerViewHolder);
                // 设施类型多选功能关闭，默哀。。。
                /*checkBox.setChecked(!checkBox.isChecked());
                if (checkBox.isChecked()) {
                    ids.add(dataBean.getId());
                    names.add(dataBean.getEquTypeName());
                } else {
                    ids.remove(Integer.valueOf(dataBean.getId()));
                    names.remove(dataBean.getEquTypeName());
                }
                String typeName = "";
                for (String name : names)
                    typeName = TextUtils.isEmpty(typeName) ? name : typeName.concat(",").concat(name);
                viewById.setText(typeName);*/
            }
        });
    }

    @Override
    public int getItemCount() {
        return dataBeans != null ? dataBeans.size() : 0;
    }

    private OnItemClickListener mClickListener;
    public void setOnItemClickListener(OnItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }
}
