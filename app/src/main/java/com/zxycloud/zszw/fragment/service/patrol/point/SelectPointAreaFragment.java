package com.zxycloud.zszw.fragment.service.patrol.point;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.zxycloud.zszw.R;
import com.zxycloud.zszw.adapter.SelectAreaAdapter;
import com.zxycloud.zszw.base.BaseSearchListFragment;
import com.zxycloud.zszw.event.type.SelectType;
import com.zxycloud.zszw.listener.NetRequestListener;
import com.zxycloud.zszw.listener.OnItemClickListener;
import com.zxycloud.zszw.listener.OnProjectObtainListener;
import com.zxycloud.zszw.model.ResultPointAreaListBean;
import com.zxycloud.zszw.model.bean.AreaBean;
import com.zxycloud.common.base.BaseBean;
import com.zxycloud.common.utils.netWork.ApiRequest;
import com.zxycloud.common.utils.netWork.NetBean;

import java.util.ArrayList;
import java.util.List;

public class SelectPointAreaFragment extends BaseSearchListFragment {
    private SelectAreaAdapter pointAdapter;
    private List<AreaBean> areaBeans;

    public static SelectPointAreaFragment newInstance(@SelectType.addCode int addType) {
        SelectPointAreaFragment fragment = new SelectPointAreaFragment();
        Bundle args = new Bundle();
        args.putInt("addType", addType);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.toolbar_list;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        setSearchToolbar(false, R.string.select_area);
        initSearchEditText(NetBean.actionGetAreaMenuList, "areaName");

        RecyclerView recyclerView = findViewById(R.id.recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        pointAdapter = new SelectAreaAdapter(getContext());
        recyclerView.setAdapter(pointAdapter);
        pointAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(int position, View view, RecyclerView.ViewHolder vh) {
                Bundle bundle = new Bundle();
                bundle.putString("areaName", areaBeans.get(position).getAreaName());
                bundle.putString("areaId", areaBeans.get(position).getAreaId());
                setFragmentResult(RESULT_OK, bundle);
                finish();
            }
        });

        getProject(new OnProjectObtainListener() {
            @Override
            public void success(String projectId, final String projectName) {
                netWork().setRefreshListener(R.id.refreshLayout, true, true, new NetRequestListener() {
                    @Override
                    public void success(String action, BaseBean baseBean, Object tag) {
                        if (baseBean.isSuccessful()) {
                            List<AreaBean> areaBean = new ArrayList<>();
                            for (ResultPointAreaListBean.DataBean datum : ((ResultPointAreaListBean) baseBean).getData()) {
                                areaBean.add(new AreaBean(datum, projectName));
                            }
                            if (tag == null || (int) tag == 1)
                                areaBeans = areaBean;
                            else
                                areaBeans.addAll(areaBeans.size(), areaBean);

                            pointAdapter.setData(areaBeans);
                        }
                    }
                }, netWork().apiRequest(NetBean.actionGetAreaMenuList, ResultPointAreaListBean.class, ApiRequest.REQUEST_TYPE_POST, R.id.loading)
                        .setApiType(ApiRequest.API_TYPE_PATROL)
                        .setRequestParams("companyId", projectId)
                        .setRequestParams("pageSize", 10)
                        .setRequestParams("pageIndex", 1));
            }
        });
    }
}
