package com.zxycloud.zszw.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.zxycloud.zszw.R;
import com.zxycloud.zszw.listener.OnItemClickListener;
import com.zxycloud.zszw.model.ResultPointListBean;
import com.zxycloud.common.utils.CommonUtils;
import com.zxycloud.common.widget.BswRecyclerView.RecyclerViewHolder;
import com.zxycloud.common.widget.BswRecyclerView.SwipeItemLayout;

import java.util.List;

public class PointAdapter extends RecyclerView.Adapter<RecyclerViewHolder> {
    private Context context;
    private OnItemClickListener mClickListener;
    private List<ResultPointListBean.DataBean> taskBeans;

    public PointAdapter(Context context) {
        this.context = context;
    }

    public void setData(List<ResultPointListBean.DataBean> taskBeans){
        this.taskBeans = taskBeans;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new RecyclerViewHolder(LayoutInflater.from(context).inflate(R.layout.base_item, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerViewHolder holder, int i) {
        ResultPointListBean.DataBean bean = taskBeans.get(i);
        ((SwipeItemLayout)holder.getView(R.id.sil_drag)).setSwipeEnable(false);

        holder.setText(R.id.item_title, bean.getPatrolPointName());// 巡查点名称
        holder.setTextWithLeftDrawables(R.id.item_1, R.mipmap.ic_item_device_number, CommonUtils.string().getString(bean.getItemCount()));// 巡查项数
        holder.setTextWithLeftDrawables(R.id.item_2, R.mipmap.ic_item_address, bean.getAddress());// 巡查点位置

        holder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mClickListener.onItemClick(holder.getAdapterPosition(), v, holder);
            }
        });
    }

    @Override
    public int getItemCount() {
        return taskBeans != null ? taskBeans.size() : 0;
    }

    public void setOnItemClickListener(OnItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }
}
