package com.zxycloud.zszw.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.zxycloud.common.widget.AlertDialog;
import com.zxycloud.zszw.R;
import com.zxycloud.zszw.fragment.home.shortcut.area.AreaListFragment;
import com.zxycloud.zszw.listener.OnItemClickListener;
import com.zxycloud.zszw.model.bean.AreaBean;
import com.zxycloud.common.base.BaseBean;
import com.zxycloud.common.utils.CommonUtils;
import com.zxycloud.common.utils.netWork.ApiRequest;
import com.zxycloud.common.utils.netWork.NetBean;
import com.zxycloud.common.utils.netWork.NetUtils;
import com.zxycloud.common.widget.BswRecyclerView.RecyclerViewHolder;
import com.zxycloud.common.widget.BswRecyclerView.SwipeItemLayout;

import java.util.List;

public class AreaAdapter extends RecyclerView.Adapter<AreaAdapter.AreaHolder> {
    private Context context;
    private OnItemClickListener mClickListener;
    private List<AreaBean> areaBeans;
    private AreaListFragment areaListFragment;

    public AreaAdapter(AreaListFragment areaListFragment, Context context) {
        this.context = context;
        this.areaListFragment = areaListFragment;
    }

    public void setData(List<AreaBean> areaBeans) {
        this.areaBeans = areaBeans;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public AreaHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new AreaHolder(LayoutInflater.from(context).inflate(R.layout.base_item, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final AreaHolder placeHolder, final int i) {
        final AreaBean areaBean = areaBeans.get(i);
//        ((SwipeItemLayout) (placeHolder.getView(R.id.sil_drag))).setSwipeEnable(areaBean.isCanDelete());//该区域是否可删除
        placeHolder.setText(R.id.item_title, areaBean.getAreaName());
        placeHolder.setTextWithLeftDrawables(R.id.item_1, R.mipmap.ic_item_project_name, areaBean.getProjectName());// 所属项目
        placeHolder.setTextWithLeftDrawables(R.id.item_2, R.mipmap.ic_item_project_type, areaBean.getProjectType());// 项目类型
        placeHolder.setOnClickListener(R.id.card_delete, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog builder = new AlertDialog(context).builder();
                builder.setTitle(R.string.hint)
                        .setMsg(R.string.hint_delete)
                        .setNegativeButton(R.string.dialog_no, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                notifyDataSetChanged();
                            }
                        })
                        .setPositiveButton(R.string.dialog_yes, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                ApiRequest apiRequest = new ApiRequest(NetBean.actionGetDeleteArea, BaseBean.class).setRequestParams("areaId", areaBean.getAreaId());
                                new NetUtils(context).request(new NetUtils.NetRequestCallBack() {
                                    @Override
                                    public void success(String action, BaseBean baseBean, Object tag) {
                                        if (baseBean.isSuccessful())
                                            areaBeans.remove(i);
                                        else
                                            CommonUtils.toast(context, baseBean.getMessage());
                                        notifyDataSetChanged();
                                    }

                                    @Override
                                    public void error(String action, Throwable e, Object tag) {
                                    }
                                }, false, apiRequest);
                            }
                        }).show();
            }
        });
        placeHolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mClickListener.onItemClick(placeHolder.getAdapterPosition(), v, placeHolder);
            }
        });
    }

    @Override
    public int getItemCount() {
        return areaBeans != null ? areaBeans.size() : 0;
    }

    class AreaHolder extends RecyclerViewHolder {
        View itemView;

        public AreaHolder(@NonNull View itemView) {
            super(itemView);
            this.itemView = itemView;
        }
    }

    public void setOnItemClickListener(OnItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }
}
