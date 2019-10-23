package com.zxycloud.zszw.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.zxycloud.zszw.R;
import com.zxycloud.zszw.model.bean.PointStateBean;
import com.zxycloud.common.widget.BswRecyclerView.RecyclerViewHolder;

import java.util.ArrayList;
import java.util.List;

public class PatrolProAdapter extends RecyclerView.Adapter<RecyclerViewHolder> {
    private Context context;
    private List<PointStateBean> dataBeans;
    private List<String> ids;

    public PatrolProAdapter(Context context) {
        this.context = context;
    }

    public void setDataBeans(List<PointStateBean> dataBeans) {
        this.dataBeans = dataBeans;
        ids = new ArrayList<>();
        if (dataBeans != null)
            for (PointStateBean dataBean : dataBeans)
                ids.add(dataBean.getId());
        notifyDataSetChanged();
    }

    public Object[] getIntener() {
        return ids.toArray();
    }

    @Override
    public int getItemViewType(int position) {
        return position != 0 ? super.getItemViewType(position) : 1101;
    }

    @NonNull
    @Override
    public RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        if (i != 1101)
            return new RecyclerViewHolder(LayoutInflater.from(context).inflate(R.layout.item_patrol_project, viewGroup, false));
        else
            return new RecyclerViewHolder(LayoutInflater.from(context).inflate(R.layout.item_head_project, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewHolder recyclerViewHolder, final int i) {
        if (i > 0) {
            int position = i - 1;
            final PointStateBean data = dataBeans.get(position);
            recyclerViewHolder.setText(R.id.tv_patrol_pro, data.getPatrolItemName());
            recyclerViewHolder.setOnClickListener(R.id.iv_delete, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ids.remove(data.getId());
                    dataBeans.remove(data);
                    notifyDataSetChanged();
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return dataBeans != null ? dataBeans.size() + 1 : 0;
    }
}
