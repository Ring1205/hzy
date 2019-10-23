package com.zxycloud.zszw.fragment.home.statistics;

import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.zxycloud.zszw.R;
import com.zxycloud.zszw.base.BaseSearchListFragment;
import com.zxycloud.zszw.base.MyBaseAdapter;
import com.zxycloud.zszw.event.type.AlertShowType;
import com.zxycloud.zszw.fragment.home.message.AlertDetailFragment;
import com.zxycloud.zszw.listener.NetRequestListener;
import com.zxycloud.zszw.listener.OnProjectObtainListener;
import com.zxycloud.zszw.model.ResultRecordListBean;
import com.zxycloud.zszw.model.bean.RecordBean;
import com.zxycloud.zszw.utils.StateTools;
import com.zxycloud.common.utils.CommonUtils;
import com.zxycloud.common.utils.netWork.ApiRequest;
import com.zxycloud.common.utils.netWork.NetBean;
import com.zxycloud.common.widget.BswRecyclerView.RecyclerViewHolder;
import com.zxycloud.common.widget.BswRecyclerView.SwipeItemLayout;

import java.util.List;

public class HistoryAlarmFragment extends BaseSearchListFragment implements MyBaseAdapter.OnBindViewHolderListener {
    private MyBaseAdapter mAdapter;
    private List<RecordBean> recordBeans;

    public static HistoryAlarmFragment newInstance() {
        Bundle args = new Bundle();
        HistoryAlarmFragment fragment = new HistoryAlarmFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        setSearchToolbar(false, R.string.home_stat_history);
        initSearchEditText(NetBean.actionGetRecordList, "placeName");

        RecyclerView recyclerView = findViewById(R.id.recycler);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mAdapter = new MyBaseAdapter(getContext(), R.layout.base_item,this);
        recyclerView.setAdapter(mAdapter);

        initData();
    }

    private void initData() {
        getProject(new OnProjectObtainListener() {
            @Override
            public void success(String projectId, String projectName) {
                netWork().setRefreshListener(R.id.refreshLayout, true, true, new NetRequestListener<ResultRecordListBean>() {
                    @Override
                    public void success(String action, ResultRecordListBean baseBean, Object tag) {
                        if (tag == null || (int) tag == 1)
                            recordBeans = baseBean.getData();
                        else
                            recordBeans.addAll(recordBeans.size(), baseBean.getData());
                        mAdapter.setData(recordBeans);
                    }
                }, netWork().apiRequest(NetBean.actionGetRecordList, ResultRecordListBean.class, ApiRequest.REQUEST_TYPE_POST, R.id.loading)
                        .setRequestParams("pageSize", 10)
                        .setRequestParams("pageIndex", 1)
                        .setRequestParams("projectId", projectId)
                        .setRequestParams("stateGroupCodeList", new Integer[]{RecordBean.RECORD_STATE_FIRE, RecordBean.RECORD_STATE_PREFIRE}));
            }
        });
    }

    @Override
    protected int getLayoutId() {
        return R.layout.toolbar_list;
    }

    @Override
    public void onBindViewHolder(int position, View view, RecyclerViewHolder holder) {
        final RecordBean recordBean = recordBeans.get(position);
        ((SwipeItemLayout) holder.getView(R.id.sil_drag)).setSwipeEnable(false);
        StateTools.setStateTint(recordBean.getStateGroupCode(), holder.getImageView(R.id.item_state));
        holder.setText(R.id.item_title, recordBean.getPlaceName());
        ((TextView)holder.getView(R.id.item_1)).setTextColor(getResources().getColor(StateTools.stateColor(recordBean.getStateCode())));
        holder.setTextWithLeftDrawables(R.id.item_1, R.mipmap.ic_item_state, recordBean.getStateName());
        holder.setTextWithLeftDrawables(R.id.item_2, R.mipmap.ic_item_address, recordBean.getDeviceInstallLocation());
        holder.setTextWithLeftDrawables(R.id.item_3, R.mipmap.ic_item_device_type, recordBean.getDeviceName());
        holder.setTextWithLeftDrawables(R.id.item_4, R.mipmap.ic_point_time, CommonUtils.date().format(recordBean.getReceiveTime()));
        holder.setTextWithLeftDrawables(R.id.item_5, R.mipmap.ic_item_linkage, recordBean.getPlaceAdminName().concat("(").concat(recordBean.getPlaceAdminPhoneNumber()).concat(")"));
        holder.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (recordBean.getStateGroupCode() == RecordBean.RECORD_STATE_FIRE || recordBean.getStateGroupCode() == RecordBean.RECORD_STATE_PREFIRE) {
                            start(AlertDetailFragment.newInstance(AlertShowType.ALERT_HISTORY,
                                    recordBean.getStateGroupCode() == RecordBean.RECORD_STATE_FIRE ? CommonUtils.STATE_CODE_FIRE : CommonUtils.STATE_CODE_PREFIRE,
                                    recordBean.getRecordId()));
                        }
                    }
                });
    }
}
