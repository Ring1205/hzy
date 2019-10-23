package com.zxycloud.zszw.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.zxycloud.common.widget.AlertDialog;
import com.zxycloud.zszw.R;
import com.zxycloud.zszw.listener.OnItemClickListener;
import com.zxycloud.zszw.model.bean.PlaceBean;
import com.zxycloud.zszw.utils.StateTools;
import com.zxycloud.common.base.BaseBean;
import com.zxycloud.common.utils.CommonUtils;
import com.zxycloud.common.utils.netWork.ApiRequest;
import com.zxycloud.common.utils.netWork.NetBean;
import com.zxycloud.common.utils.netWork.NetUtils;
import com.zxycloud.common.widget.BswRecyclerView.RecyclerViewHolder;
import com.zxycloud.common.widget.BswRecyclerView.SwipeItemLayout;

import java.util.List;

public class PlaceAdapter extends RecyclerView.Adapter<RecyclerViewHolder> {
    private Context context;
    private OnItemClickListener mClickListener;
    private List<PlaceBean> data;

    public PlaceAdapter(Context context) {
        this.context = context;
    }

    public void initData(List<PlaceBean> data) {
        this.data = data;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new RecyclerViewHolder(LayoutInflater.from(context).inflate(R.layout.base_item, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerViewHolder placeHolder, final int i) {
        if (!TextUtils.isEmpty(data.get(i).getStateGroupName()))
            StateTools.setStateTint(data.get(i).getStateGroupCode(), placeHolder.getImageView(R.id.item_state));

        placeHolder.setText(R.id.item_title, data.get(i).getPlaceName());
        placeHolder.setTextWithLeftDrawables(R.id.item_1, R.mipmap.ic_item_project_name, data.get(i).getProjectName());// 所属单位
        placeHolder.setTextWithLeftDrawables(R.id.item_2, R.mipmap.ic_item_address, data.get(i).getPlaceAddress());// 所在地址
        ((SwipeItemLayout) placeHolder.getView(R.id.sil_drag)).setSwipeEnable(data.get(i).isCanDelete());// 该场所是否可删除

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
                                ApiRequest apiRequest = new ApiRequest(NetBean.actionGetPlaceDelete, BaseBean.class).setRequestParams("placeId", data.get(i).getPlaceId());
                                new NetUtils(context).request(new NetUtils.NetRequestCallBack() {
                                    @Override
                                    public void success(String action, BaseBean baseBean, Object tag) {
                                        if (baseBean.isSuccessful())
                                            data.remove(i);
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
        return data != null ? data.size() : 0;
    }

    public void setOnItemClickListener(OnItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }
}
