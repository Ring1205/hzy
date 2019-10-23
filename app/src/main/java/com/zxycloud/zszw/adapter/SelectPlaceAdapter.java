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

public class SelectPlaceAdapter extends RecyclerView.Adapter<RecyclerViewHolder> {
    private Context context;
    private List<PlaceBean> areaBeans;
    private SparseArray<String> sparseArray;

    public SelectPlaceAdapter(Context context) {
        this.context = context;
        sparseArray = CommonUtils.string().formatStringLength(context
                , R.string.title_place_name
                , R.string.title_existing_equipment
                , R.string.title_address);
    }

    public void setData(List<PlaceBean> areaBeans) {
        this.areaBeans = areaBeans;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new RecyclerViewHolder(LayoutInflater.from(context).inflate(R.layout.item_select, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerViewHolder holder, final int i) {
        final PlaceBean areaBean = areaBeans.get(i);// 子级区域类型：0未分配/1区域/2场所

        holder.setText(R.id.item_title, sparseArray.get(R.string.title_place_name).concat(areaBean.getPlaceName()));
        holder.setText(R.id.item_info, sparseArray.get(R.string.title_existing_equipment).concat(CommonUtils.string().getString(areaBean.getAloneDeviceCount()+areaBean.getAdapterCount())).concat("\n")
                .concat(sparseArray.get(R.string.title_address)).concat(areaBean.getPlaceAddress()));

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mClickListener.onItemClick(i, v, holder);
            }
        });
    }

    @Override
    public int getItemCount() {
        return areaBeans != null ? areaBeans.size() : 0;
    }

    private OnItemClickListener mClickListener;

    public void setOnItemClickListener(OnItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }
}
