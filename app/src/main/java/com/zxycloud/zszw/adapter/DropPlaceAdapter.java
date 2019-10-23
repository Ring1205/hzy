package com.zxycloud.zszw.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.zxycloud.zszw.R;
import com.zxycloud.zszw.listener.OnItemClickListener;
import com.zxycloud.zszw.model.bean.PlaceBean;
import com.zxycloud.common.utils.CommonUtils;
import com.zxycloud.common.widget.BswRecyclerView.RecyclerViewHolder;

import java.util.List;

public class DropPlaceAdapter extends RecyclerView.Adapter<RecyclerViewHolder> {
    private Context context;
    private List<PlaceBean> data;
    private OnItemClickListener mClickListener;
    private SparseArray<String> sparseArray;

    public DropPlaceAdapter(Context context) {
        this.context = context;
        sparseArray = CommonUtils.string().formatStringLength(context
                , R.string.title_place_name
                , R.string.title_address);
    }

    @NonNull
    @Override
    public RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new RecyclerViewHolder(LayoutInflater.from(context).inflate(R.layout.item_down_place, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerViewHolder recyclerViewHolder, final int i) {
        recyclerViewHolder.setText(R.id.tv_place, sparseArray.get(R.string.title_place_name).concat(data.get(i).getPlaceName()));// 场所名称
        recyclerViewHolder.setText(R.id.tv_place_location, sparseArray.get(R.string.title_address).concat(data.get(i).getPlaceAddress()));// 所在地址
        recyclerViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mClickListener.onItemClick(i, v, recyclerViewHolder);
            }
        });
    }

    @Override
    public int getItemCount() {
        return data != null ? data.size() : 0;
    }

    public void setData(List<PlaceBean> data) {
        this.data = data;
        notifyDataSetChanged();
    }

    public void setOnItemClickListener(OnItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }
}
