package com.zxycloud.zszw.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.zxycloud.zszw.R;
import com.zxycloud.zszw.listener.OnItemClickListener;
import com.zxycloud.zszw.model.ResultCardTypeBean;
import com.zxycloud.common.utils.CommonUtils;
import com.zxycloud.common.widget.BswRecyclerView.RecyclerViewHolder;

import java.util.List;

public class SelectCardTypeAdapter extends RecyclerView.Adapter<RecyclerViewHolder> {
    private Context context;
    private OnItemClickListener mClickListener;
    private List<ResultCardTypeBean.DataBean> dataBeans;
    public SelectCardTypeAdapter(Context context) {
        this.context = context;
    }

    public void setDataBeans(List<ResultCardTypeBean.DataBean> dataBeans) {
        this.dataBeans = dataBeans;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new RecyclerViewHolder(LayoutInflater.from(context).inflate(R.layout.item_textview, viewGroup,false));
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerViewHolder recyclerViewHolder, final int i) {
        recyclerViewHolder.setText(R.id.tv_point_entry, CommonUtils.string().getString(dataBeans.get(i).getCardTypeName()));
        recyclerViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mClickListener.onItemClick(i, v, recyclerViewHolder);
            }
        });
    }

    @Override
    public int getItemCount() {
        return dataBeans != null ? dataBeans.size() : 0;
    }
    public void setOnItemClickListener(OnItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }
}
