package com.zxycloud.zszw.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.zxycloud.zszw.R;
import com.zxycloud.zszw.listener.OnItemClickListener;
import com.zxycloud.zszw.model.bean.ProjectBean;

import java.util.List;

public class UnitAdapter extends RecyclerView.Adapter<UnitAdapter.UserHolder> {
    private Context context;
    private List<ProjectBean> data;
    private OnItemClickListener mClickListener;

    public UnitAdapter(Context context) {
        this.context = context;
    }

    public void setData(List<ProjectBean> data) {
        this.data = data;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public UserHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new UserHolder(LayoutInflater.from(context).inflate(R.layout.item_unit, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final UserHolder holder, int i) {
        ((TextView) holder.itemView.findViewById(R.id.tv_unit_name)).setText(data.get(i).getProjectName());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mClickListener.onItemClick(holder.getAdapterPosition(), v, holder);
            }
        });
    }

    @Override
    public int getItemCount() {
        return data != null ? data.size() : 0;
    }

    class UserHolder extends RecyclerView.ViewHolder {
        public UserHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

    public void setOnItemClickListener(OnItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }
}
