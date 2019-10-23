package com.zxycloud.zszw.fragment.service.patrol.point;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.zxycloud.zszw.R;
import com.zxycloud.zszw.adapter.PointAdapter;
import com.zxycloud.zszw.base.BaseBackFragment;
import com.zxycloud.zszw.listener.NetRequestListener;
import com.zxycloud.zszw.listener.OnItemClickListener;
import com.zxycloud.zszw.model.ResultPointListBean;
import com.zxycloud.common.utils.CommonUtils;
import com.zxycloud.common.utils.SPUtils;
import com.zxycloud.common.utils.netWork.ApiRequest;
import com.zxycloud.common.utils.netWork.NetBean;

import java.util.List;

public class PointListFragment extends BaseBackFragment implements Toolbar.OnMenuItemClickListener {
    private RecyclerView rlPoint;
    private PointAdapter pointAdapter;
    private List<ResultPointListBean.DataBean> dataBeans;

    public static PointListFragment newInstance() {
        PointListFragment fragment = new PointListFragment();
        fragment.setArguments(new Bundle());
        return fragment;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.toolbar_list;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        setToolbarTitle(R.string.point_list).initToolbarNav().setToolbarMenu(R.menu.add, this);

        rlPoint = findViewById(R.id.recycler);
        rlPoint.setLayoutManager(new LinearLayoutManager(getContext()));
        pointAdapter = new PointAdapter(getContext());
        rlPoint.setAdapter(pointAdapter);
        pointAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(int position, View view, RecyclerView.ViewHolder vh) {
                start(PointDetailsFragment.newInstance(dataBeans.get(position).getId()));
            }
        });

        initData();
    }

    private void initData() {
        netWork().setRefreshListener(R.id.refreshLayout, true, true, new NetRequestListener<ResultPointListBean>() {
            @Override
            public void success(String action, ResultPointListBean baseBean, Object tag) {
                if (baseBean.isSuccessful()) {
                    if (tag == null || (int) tag == 1)
                        dataBeans = baseBean.getData();
                    else
                        dataBeans.addAll(dataBeans.size(), baseBean.getData());
                    pointAdapter.setData(dataBeans);
                }
            }
        }, netWork().apiRequest(NetBean.actionPostPatrolPointList, ResultPointListBean.class, ApiRequest.REQUEST_TYPE_POST, R.id.loading).setApiType(ApiRequest.API_TYPE_PATROL)
                .setRequestParams("companyId", CommonUtils.getSPUtils(getContext()).getString(SPUtils.PROJECT_ID))
                .setRequestParams("pageIndex", 1)
                .setRequestParams("pageSize", 10));
    }

    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.action_add:
                startForResult(PointInputFragment.newInstance(null), 1117);
                break;
        }
        return true;
    }

    @Override
    public void onFragmentResult(int requestCode, int resultCode, Bundle data) {
        super.onFragmentResult(requestCode, resultCode, data);
        if (requestCode == 1117 && resultCode == RESULT_OK)
            netWork().loading();
    }
}
