package com.zxycloud.zszw.fragment.service.patrol.point;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.EditText;

import com.zxycloud.zszw.R;
import com.zxycloud.zszw.adapter.PointStartAdapter;
import com.zxycloud.zszw.base.BaseBackFragment;
import com.zxycloud.zszw.listener.NetRequestListener;
import com.zxycloud.zszw.model.ResultPatrolPointBean;
import com.zxycloud.common.base.BaseBean;
import com.zxycloud.common.utils.netWork.ApiRequest;
import com.zxycloud.common.utils.netWork.NetBean;

public class PointDetailsFragment extends BaseBackFragment implements Toolbar.OnMenuItemClickListener {
    private PointStartAdapter adapter;
    private ResultPatrolPointBean.DataBean dataBean;

    public static PointDetailsFragment newInstance(String id) {
        PointDetailsFragment fragment = new PointDetailsFragment();
        Bundle agr = new Bundle();
        agr.putString("PointId",id);
        fragment.setArguments(agr);
        return fragment;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.point_details;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        setToolbarTitle(R.string.point_detail).initToolbarNav().setToolbarMenu(R.menu.point_change,this);

        RecyclerView recyclerView = findViewById(R.id.rl_inspect_pro);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new PointStartAdapter(getContext());
        adapter.setShowImg(true);
        recyclerView.setAdapter(adapter);

        initData();
    }

    private void initData() {
        netWork().setRequestListener(new NetRequestListener() {
            @Override
            public void success(String action, BaseBean baseBean, Object tag) {
                if (baseBean.isSuccessful()){
                    dataBean = ((ResultPatrolPointBean)baseBean).getData();
                    ((EditText)findViewById(R.id.edit_point_name)).setText(dataBean.getPatrolPointName());
                    ((EditText)findViewById(R.id.edit_point_location)).setText(dataBean.getAddress());
                    ((EditText)findViewById(R.id.edit_patrol_type)).setText(dataBean.getPatrolItemTypeName());
                    ((EditText)findViewById(R.id.edit_patrol_made_date)).setText(dataBean.getProducedDate());
                    ((EditText)findViewById(R.id.edit_patrol_start_date)).setText(dataBean.getOpenDate());
                    adapter.setData(dataBean.getPatrolItemVOList());
                }
            }
        }, netWork().apiRequest(NetBean.actionGetPatrolPointDetails, ResultPatrolPointBean.class, ApiRequest.REQUEST_TYPE_GET).setApiType(ApiRequest.API_TYPE_PATROL)
                .setRequestParams("id", getArguments().getString("PointId")));
    }

    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.action_change:
                startForResult(PointInputFragment.newInstance(getArguments().getString("PointId")), 2210);
                break;
        }
        return true;
    }

    @Override
    public void onFragmentResult(int requestCode, int resultCode, Bundle data) {
        super.onFragmentResult(requestCode, resultCode, data);
        if (requestCode == 2210 && resultCode == RESULT_OK)
            netWork().loading();
    }
}
