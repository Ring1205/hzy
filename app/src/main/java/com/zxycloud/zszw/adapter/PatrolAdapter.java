package com.zxycloud.zszw.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.zxycloud.zszw.R;
import com.zxycloud.zszw.dialog.PointMessagePopupWindow;
import com.zxycloud.zszw.fragment.service.patrol.task.TaskDetailsFragment;
import com.zxycloud.zszw.model.ResultTaskItemBean;
import com.zxycloud.zszw.model.bean.PatrolBean;
import com.zxycloud.zszw.model.bean.PointStateBean;
import com.zxycloud.common.base.BaseBean;
import com.zxycloud.common.utils.CommonUtils;
import com.zxycloud.common.utils.netWork.ApiRequest;
import com.zxycloud.common.utils.netWork.NetBean;
import com.zxycloud.common.utils.netWork.NetUtils;
import com.zxycloud.common.widget.BswRecyclerView.RecyclerViewHolder;

import java.util.List;

public class PatrolAdapter extends RecyclerView.Adapter<RecyclerViewHolder> {
    private Context mContext;
    private boolean isShow;
    private List<PointStateBean> pointStateBeans;
    private PointStartAdapter pointAdapter;
    private TaskDetailsFragment fragment;
    private List<PatrolBean.TaskPointVOListBean> pointVOListBeans;

    public PatrolAdapter(Context context, TaskDetailsFragment fragment) {
        mContext = context;
        this.fragment = fragment;
    }

    public void setData(List<PatrolBean.TaskPointVOListBean> pointVOListBeans) {
        this.pointVOListBeans = pointVOListBeans;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new RecyclerViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_patro, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerViewHolder recyclerViewHolder, int i) {
        final PatrolBean.TaskPointVOListBean bean = pointVOListBeans.get(i);
        switch (bean.getResultState()) {
            case "0"://未检查
                recyclerViewHolder.setVisibility(R.id.tv_point_warn, View.GONE);
                recyclerViewHolder.setImageRes(R.id.iv_point_start, R.mipmap.ic_point_non);
                break;
            case "1"://正常
                recyclerViewHolder.setVisibility(R.id.tv_point_warn, View.VISIBLE);
                setTvPointRecord((TextView) recyclerViewHolder.getView(R.id.tv_point_warn), true);
                recyclerViewHolder.setImageRes(R.id.iv_point_start, R.mipmap.ic_point_normal);
                break;
            default://异常
                recyclerViewHolder.setVisibility(R.id.tv_point_warn, View.VISIBLE);
                setTvPointRecord((TextView) recyclerViewHolder.getView(R.id.tv_point_warn), false);
                recyclerViewHolder.setImageRes(R.id.iv_point_start, R.mipmap.ic_point_exception);
                break;
        }
        recyclerViewHolder.setText(R.id.tv_point_title, CommonUtils.string().getString(bean.getPatrolPointName()));
        recyclerViewHolder.setText(R.id.tv_point_type, CommonUtils.string().getString(bean.getPatrolItemTypeName()));
        recyclerViewHolder.setText(R.id.tv_point_device_num, CommonUtils.string().getString(bean.getDeviceCount()));
        recyclerViewHolder.setText(R.id.tv_point_address, CommonUtils.string().getString(bean.getAddress()));
        final RecyclerView recyclerView = recyclerViewHolder.getView(R.id.rl_itme);
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        recyclerView.setAdapter(new PointStartAdapter(mContext));
        recyclerViewHolder.setOnClickListener(R.id.tv_point_warn, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (pointAdapter != recyclerView.getAdapter()) {
                    if (pointAdapter != null)
                        pointAdapter.setData(null);
                    initNetWork(bean.getId(), (PointStartAdapter) recyclerView.getAdapter(), "1");
                } else {
                    if (isShow)
                        pointAdapter.setData(pointStateBeans);
                    else
                        pointAdapter.setData(null);
                    isShow = !isShow;
                }
            }
        });
        recyclerViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ("2".equals(bean.getResultState()))
                    initNetWork(bean.getId(), pointAdapter, "2");
            }
        });
    }

    private void setTvPointRecord(TextView view, boolean b) {
        if (b) {
            view.setText(R.string.point_qualified_records);
            view.setTextColor(mContext.getResources().getColor(R.color.normal));
            view.setCompoundDrawablesWithIntrinsicBounds(mContext.getResources().getDrawable(R.mipmap.ic_point_pro_state), null, mContext.getResources().getDrawable(R.mipmap.ic_point_down_nor), null);
        } else {
            view.setText(R.string.point_unqualified_records);
            view.setTextColor(mContext.getResources().getColor(R.color.fault));
            view.setCompoundDrawablesWithIntrinsicBounds(mContext.getResources().getDrawable(R.mipmap.ic_point_warn), null, mContext.getResources().getDrawable(R.mipmap.ic_point_down), null);
        }
    }

    private void initNetWork(String id, final PointStartAdapter adapter, final String state) {
        new NetUtils(mContext).request(new NetUtils.NetRequestCallBack() {
            @Override
            public void success(String action, BaseBean baseBean, Object tag) {
                if (baseBean.isSuccessful())
                    if (state.equals("2")) {
                        PointMessagePopupWindow submitPopupWindow = new PointMessagePopupWindow(fragment);
                        submitPopupWindow.setData(((ResultTaskItemBean) baseBean).getData().getDes(), ((ResultTaskItemBean) baseBean).getData().getImgUrls());
                        submitPopupWindow.show();
                    } else {
                        pointAdapter = adapter;
                        pointStateBeans = ((ResultTaskItemBean) baseBean).getData().getTaskItemVOList();
                        pointAdapter.setData(pointStateBeans);
                    }
                else
                    CommonUtils.toast(mContext, baseBean.getMessage());
            }

            @Override
            public void error(String action, Throwable e, Object tag) {

            }
        }, false, new ApiRequest(NetBean.actionPostTaskPointDetails, ResultTaskItemBean.class)
                .setRequestType(ApiRequest.REQUEST_TYPE_POST)
                .setApiType(ApiRequest.API_TYPE_PATROL)
                .setRequestParams("id", id));
    }

    @Override
    public int getItemCount() {
        return pointVOListBeans != null ? pointVOListBeans.size() : 0;
    }
}
