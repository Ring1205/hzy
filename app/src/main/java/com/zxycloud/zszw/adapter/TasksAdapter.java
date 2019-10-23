package com.zxycloud.zszw.adapter;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.zxycloud.zszw.R;
import com.zxycloud.zszw.listener.MyOnClickListener;
import com.zxycloud.zszw.listener.OnItemClickListener;
import com.zxycloud.zszw.model.bean.TaskBean;
import com.zxycloud.zszw.utils.StateTools;
import com.zxycloud.common.widget.BswRecyclerView.RecyclerViewHolder;
import com.zxycloud.common.widget.BswRecyclerView.SwipeItemLayout;

import java.util.List;

public class TasksAdapter extends RecyclerView.Adapter<RecyclerViewHolder> {
    private Context context;
    private OnItemClickListener mClickListener;
    private MyOnClickListener nfcListener, qrListener;
    private List<TaskBean> taskBeans;

    public TasksAdapter(Context context) {
        this.context = context;
    }

    public void setData(List<TaskBean> taskBeans) {
        this.taskBeans = taskBeans;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new RecyclerViewHolder(LayoutInflater.from(context).inflate(R.layout.item_task_layout, viewGroup, false));
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBindViewHolder(@NonNull final RecyclerViewHolder recyclerViewHolder, final int i) {
        final TaskBean bean = taskBeans.get(i);

        recyclerViewHolder.setText(R.id.item_title, bean.getPatrolTaskName());// 任务名称
        recyclerViewHolder.setText(R.id.item_info, bean.getStartTime().concat(" ~ ").concat(bean.getEndTime()));// 任务起止时间

        int resId;
        // 任务状态 巡查状态 (0未开始 1执行中 2已完成 3已过期)
        switch (bean.getPatrolState()){
            case 0://未开始
                resId = R.mipmap.ic_task_dns;
                break;
            case 1://执行中
                resId = R.mipmap.ic_task_doing;
                break;
            case 2://已完成
                resId = R.mipmap.ic_task_done;
                break;
            default://已过期
                resId = R.mipmap.ic_task_stale;
                break;
        }
        recyclerViewHolder.setImageRes(R.id.iv_start, resId);

        StateTools.setViewColor(context,recyclerViewHolder.getImageView(R.id.nfc_task),R.mipmap.ic_task_nfc,bean.getPatrolState() != 1 ? Color.GRAY : Color.BLACK);
        StateTools.setViewColor(context,recyclerViewHolder.getImageView(R.id.qr_code_task),R.mipmap.ic_task_qr_code,bean.getPatrolState() != 1 ? Color.GRAY : Color.BLACK);

        ((SwipeItemLayout) (recyclerViewHolder.getView(R.id.sil_drag))).setSwipeEnable(false);
        recyclerViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mClickListener.onItemClick(recyclerViewHolder.getAdapterPosition(), v, recyclerViewHolder);
            }
        });

        recyclerViewHolder.setOnClickListener(R.id.nfc_task, bean.getPatrolState() != 1 ? null : new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nfcListener.onClick(i, v);
            }
        });
        recyclerViewHolder.setOnClickListener(R.id.qr_code_task, bean.getPatrolState() != 1 ? null : new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                qrListener.onClick(i, v);
            }
        });
    }

    @Override
    public int getItemCount() {
        return taskBeans != null ? taskBeans.size() : 0;
    }

    public void setOnItemClickListener(OnItemClickListener itemClickListener, MyOnClickListener nfcListener, MyOnClickListener qrListener) {
        this.mClickListener = itemClickListener;
        this.nfcListener = nfcListener;
        this.qrListener = qrListener;
    }
}
