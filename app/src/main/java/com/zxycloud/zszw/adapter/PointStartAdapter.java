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

import java.util.List;

public class PointStartAdapter extends RecyclerView.Adapter<RecyclerViewHolder> {
    private Context context;
    private boolean isShowImg;
    private List<PointStateBean> mDataBean;

    public PointStartAdapter(Context context) {
        this.context = context;
    }

    public void setData(List<PointStateBean> dataBean) {
        this.mDataBean = dataBean;
        notifyDataSetChanged();
    }
    public void setShowImg(boolean isShowImg){
        this.isShowImg = isShowImg;
    }

    @NonNull
    @Override
    public RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new RecyclerViewHolder(LayoutInflater.from(context).inflate(R.layout.item_task_patrol_point, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewHolder recyclerViewHolder, int i) {
        PointStateBean bean = mDataBean.get(i);
        if (isShowImg)
            recyclerViewHolder.setVisibility(R.id.iv_pro_state, View.GONE);
        if (bean.getResultState() != 1)
            recyclerViewHolder.setImageRes(R.id.iv_pro_state, R.mipmap.ic_point_warn);
        else
            recyclerViewHolder.setImageRes(R.id.iv_pro_state, R.mipmap.ic_point_pro_state);

        recyclerViewHolder.setText(R.id.tv_task_type, bean.getPatrolItemName());
        recyclerViewHolder.setText(R.id.tv_task_state, bean.getEquTypeName());
    }

    @Override
    public int getItemCount() {
        return mDataBean != null ? mDataBean.size() : 0;
    }
}
