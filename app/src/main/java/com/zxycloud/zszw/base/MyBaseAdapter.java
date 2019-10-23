package com.zxycloud.zszw.base;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.zxycloud.common.widget.BswRecyclerView.RecyclerViewHolder;

import java.util.List;

public class MyBaseAdapter<T> extends RecyclerView.Adapter<RecyclerViewHolder> {
    private int resource;
    private List<T> data;
    private Context context;
    private OnBindViewHolderListener mClickListener;

    public MyBaseAdapter(Context context, @LayoutRes int resource, OnBindViewHolderListener itemClickListener) {
        this.context = context;
        this.resource = resource;
        this.mClickListener = itemClickListener;
    }
    public void setData(List<T> data){
        this.data = data;
        notifyDataSetChanged();
    }

    public List<T> getData() {
        return data;
    }

    @NonNull
    @Override
    public RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new RecyclerViewHolder(LayoutInflater.from(context).inflate(resource, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewHolder recyclerViewHolder, int i) {
        mClickListener.onBindViewHolder(i, recyclerViewHolder.itemView, recyclerViewHolder);
    }

    @Override
    public int getItemCount() {
        return data != null ? data.size() : 0;
    }

    public interface OnBindViewHolderListener{
        void onBindViewHolder(int position, View view, RecyclerViewHolder holder);
    }
}
