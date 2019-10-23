package com.zxycloud.zszw.fragment.home.shortcut.area;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;

import com.zxycloud.zszw.R;
import com.zxycloud.zszw.adapter.AreaAdapter;
import com.zxycloud.zszw.base.BaseSearchListFragment;
import com.zxycloud.zszw.fragment.service.install.area.AddAreaFragment;
import com.zxycloud.zszw.listener.NetRequestListener;
import com.zxycloud.zszw.listener.OnItemClickListener;
import com.zxycloud.zszw.model.ResultAreaListBean;
import com.zxycloud.zszw.model.bean.AreaBean;
import com.zxycloud.common.base.BaseBean;
import com.zxycloud.common.utils.CommonUtils;
import com.zxycloud.common.utils.SPUtils;
import com.zxycloud.common.utils.netWork.ApiRequest;
import com.zxycloud.common.utils.netWork.NetBean;

import java.util.List;

public class AreaListFragment extends BaseSearchListFragment implements Toolbar.OnMenuItemClickListener {
    private RecyclerView recyclerView;
    private String projectId;
    private List<AreaBean> areaBeans;
    private AreaAdapter pointAdapter;

    public static AreaListFragment newInstance(String areaId) {
        AreaListFragment fragment = new AreaListFragment();
        Bundle args = new Bundle();
        args.putString("areaId", areaId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.toolbar_list;
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void initView(Bundle savedInstanceState) {
        setAddSearchToolbar(false,R.string.area_list, this);
        initSearchEditText(NetBean.actionPostAreaList, "areaName");

        recyclerView = findViewById(R.id.recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        pointAdapter = new AreaAdapter(this, getContext());
        recyclerView.setAdapter(pointAdapter);
        pointAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(int position, View view, RecyclerView.ViewHolder vh) {
                start(AreaDetailFragment.newInstance(areaBeans.get(position).getAreaId()));
            }
        });

        projectId = CommonUtils.getSPUtils(_mActivity).getString(SPUtils.PROJECT_ID);

        final ApiRequest<ResultAreaListBean> apiRequest = netWork().apiRequest(NetBean.actionPostAreaList, ResultAreaListBean.class, ApiRequest.REQUEST_TYPE_POST, R.id.loading)
                .setRequestParams("areaName", null)// 区域名称模糊搜索字段，不传或传空时搜索全部
                .setRequestParams("projectId", CommonUtils.getSPUtils(getContext()).getString(SPUtils.PROJECT_ID))// 父级单位Id（若projectId与areaId都没有，则根据权限搜索，若没有areaId，则搜索单位下所有有权限的一级区域）
                .setRequestParams("areaId", getArguments().getString("areaId"))// 父级区域Id
                .setRequestParams("pageSize", 10)
                .setRequestParams("pageIndex", 1);
        if (!TextUtils.isEmpty(projectId))
            apiRequest.setRequestParams("projectId", projectId);

        netWork().setRefreshListener(R.id.refreshLayout, true, true, new NetRequestListener() {
            @Override
            public void success(String action, BaseBean baseBean, Object tag) {
                if (baseBean.isSuccessful()){
                    int i = (int) apiRequest.getRequestParams().get("pageIndex");
                    if (i == 1) {
                        areaBeans = ((ResultAreaListBean) baseBean).getData();
                        pointAdapter.setData(areaBeans);
                    } else {
                        areaBeans.addAll(areaBeans.size(), ((ResultAreaListBean) baseBean).getData());
                        pointAdapter.setData(areaBeans);
                    }
                }
            }
        },apiRequest);
    }

    public boolean onMenuItemClick(MenuItem menuItem) {
        super.onMenuItemClick(menuItem);
        switch (menuItem.getItemId()) {
            case R.id.action_add:
                startForResult(AddAreaFragment.newInstance(null, null), 1102);
                break;
        }
        return true;
    }

    @Override
    public void onFragmentResult(int requestCode, int resultCode, Bundle data) {
        super.onFragmentResult(requestCode, resultCode, data);
        if (requestCode == 1102 && resultCode == RESULT_OK) {
            // 成功添加区域
            netWork().loading();
        }
    }
}
