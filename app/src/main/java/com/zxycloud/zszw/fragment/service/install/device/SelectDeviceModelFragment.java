package com.zxycloud.zszw.fragment.service.install.device;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.zxycloud.zszw.R;
import com.zxycloud.zszw.base.BaseBackFragment;
import com.zxycloud.zszw.base.MyBaseAdapter;
import com.zxycloud.zszw.listener.NetRequestListener;
import com.zxycloud.zszw.model.ResultUnitMetadataNoAllListBean;
import com.zxycloud.common.base.BaseBean;
import com.zxycloud.common.utils.netWork.ApiRequest;
import com.zxycloud.common.utils.netWork.NetBean;
import com.zxycloud.common.widget.BswRecyclerView.RecyclerViewHolder;

import java.util.List;

public class SelectDeviceModelFragment extends BaseBackFragment implements MyBaseAdapter.OnBindViewHolderListener {
    private List<ResultUnitMetadataNoAllListBean.DataBean> systemData;
    private MyBaseAdapter baseAdapter;

    public static SelectDeviceModelFragment newInstance() {
        SelectDeviceModelFragment fragment = new SelectDeviceModelFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        setToolbarTitle(R.string.string_select_title_device_system).initToolbarNav();

        initData();

        RecyclerView recyclerView = findViewById(R.id.recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        baseAdapter = new MyBaseAdapter(getContext(), R.layout.item_textview, this);
        recyclerView.setAdapter(baseAdapter);
    }

    private void initData() {
        netWork().setRefreshListener(R.id.refreshLayout, true, true, new NetRequestListener() {
            @Override
            public void success(String action, BaseBean baseBean, Object tag) {
                if (baseBean.isSuccessful()) {
                    if (tag == null || (int) tag == 1)
                        systemData = ((ResultUnitMetadataNoAllListBean) baseBean).getData();
                    else
                        systemData.addAll(systemData.size(), ((ResultUnitMetadataNoAllListBean) baseBean).getData());
                    baseAdapter.setData(systemData);
                }
            }
        }, netWork().apiRequest(NetBean.actionGetUnitMetadataNoAllList, ResultUnitMetadataNoAllListBean.class, ApiRequest.REQUEST_TYPE_GET, R.id.loading).setRequestParams("pageIndex", 1).setRequestParams("pageSize", 20));
    }

    @Override
    public void onBindViewHolder(final int position, View view, final RecyclerViewHolder holder) {
        holder.setText(R.id.tv_point_entry, systemData.get(position).getUnitName());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("unitName", systemData.get(position).getUnitName());
                setFragmentResult(RESULT_OK, bundle);
                finish();
            }
        });
    }

    @Override
    protected int getLayoutId() {
        return R.layout.toolbar_list;
    }

}
