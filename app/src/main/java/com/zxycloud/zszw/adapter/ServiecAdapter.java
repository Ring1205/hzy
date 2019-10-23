package com.zxycloud.zszw.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.zxycloud.zszw.R;
import com.zxycloud.zszw.listener.OnItemClickListener;
import com.zxycloud.common.utils.CommonUtils;
import com.zxycloud.common.widget.BswRecyclerView.RecyclerViewHolder;

import java.util.List;
import java.util.Map;

public class ServiecAdapter extends RecyclerView.Adapter<RecyclerViewHolder> {
    private Context context;
    private List<String> services;
    private List<Integer> installs;
    private Map<Integer, Integer> itemCount;// 小红点标记

    public ServiecAdapter(Context context, List<String> services, List<Integer> installs) {
        this.context = context;
        this.services = services;
        this.installs = installs;
    }

    public void setReConut(Map<Integer, Integer> itemCount) {
        this.itemCount = itemCount;
        if (installs != null && services != null)
            for (Map.Entry<Integer, Integer> entry : itemCount.entrySet()) {
                int num = entry.getKey();
                notifyItemChanged(num);
            }
    }

    @NonNull
    @Override
    public RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new RecyclerViewHolder(LayoutInflater.from(context).inflate(R.layout.item_service, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerViewHolder recyclerViewHolder, final int i) {
        //设置item的高度跟随宽度走
        ViewGroup.LayoutParams parm = recyclerViewHolder.itemView.getLayoutParams();
        parm.height = CommonUtils.measureScreen().getScreenWidth(context) / 3 - 2 * ((ViewGroup.MarginLayoutParams) parm).leftMargin;
        recyclerViewHolder.itemView.setLayoutParams(parm);

        if (itemCount != null && itemCount.get(i) != null && itemCount.get(i) > 0)
            recyclerViewHolder.setVisibility(R.id.iv_reminder, View.VISIBLE);
        else
            recyclerViewHolder.setVisibility(R.id.iv_reminder, View.GONE);

        recyclerViewHolder.setText(R.id.tv_ser, services.get(i));
        recyclerViewHolder.setImageRes(R.id.iv_ser, installs.get(i));
        recyclerViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mClickListener.onItemClick(i, v, recyclerViewHolder);
            }
        });
    }

    @Override
    public int getItemCount() {
        return services.size();
    }

    private OnItemClickListener mClickListener;

    public void setOnItemClickListener(OnItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

}
