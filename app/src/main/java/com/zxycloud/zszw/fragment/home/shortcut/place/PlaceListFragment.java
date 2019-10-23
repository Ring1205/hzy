package com.zxycloud.zszw.fragment.home.shortcut.place;

import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.zxycloud.zszw.R;
import com.zxycloud.zszw.adapter.PlaceAdapter;
import com.zxycloud.zszw.base.BaseSearchListFragment;
import com.zxycloud.zszw.event.JPushEvent;
import com.zxycloud.zszw.fragment.service.install.place.AddPlaceFragment;
import com.zxycloud.zszw.listener.NetRequestListener;
import com.zxycloud.zszw.listener.OnItemClickListener;
import com.zxycloud.zszw.model.ResultPlaceListBean;
import com.zxycloud.zszw.model.bean.PlaceBean;
import com.zxycloud.common.base.BaseBean;
import com.zxycloud.common.utils.CommonUtils;
import com.zxycloud.common.utils.SPUtils;
import com.zxycloud.common.utils.netWork.ApiRequest;
import com.zxycloud.common.utils.netWork.NetBean;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

public class PlaceListFragment extends BaseSearchListFragment implements Toolbar.OnMenuItemClickListener {
    private RecyclerView recyclerView;
    private PlaceAdapter pointAdapter;
    private List<PlaceBean> databeans;
    private String areaId;

    public static PlaceListFragment newInstance(String areaId) {
        PlaceListFragment fragment = new PlaceListFragment();
        Bundle bundle = new Bundle();
        bundle.putString("areaId", areaId);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.toolbar_list;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        setAddSearchToolbar(false, R.string.place_list, this);
        initSearchEditText(NetBean.actionPostPlaceList, "placeName");
        recyclerView = findViewById(R.id.recycler);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        pointAdapter = new PlaceAdapter(getContext());
        recyclerView.setAdapter(pointAdapter);
        pointAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(int position, View view, RecyclerView.ViewHolder vh) {
                start(PlaceDetailFragment.newInstance(databeans.get(position).getPlaceId()));
            }
        });
        areaId = getArguments().getString("areaId");

        getPlaceList();
    }

    private void getPlaceList(){
        final ApiRequest<ResultPlaceListBean> apiRequest = netWork().apiRequest(NetBean.actionPostPlaceList, ResultPlaceListBean.class, ApiRequest.REQUEST_TYPE_POST, R.id.loading)
                .setRequestParams("placeName", null)// 场所名称模糊搜索字段
                .setRequestParams("projectId", CommonUtils.getSPUtils(getContext()).getString(SPUtils.PROJECT_ID))// 项目Id（若不填，则返回权限下的场所列表，若没有areaId，则返回对应单位中权限下的场所列表）
                .setRequestParams("areaId", areaId)// 父级区域Id
//                .setRequestParams("stateCode", 0)// 状态编码（不传取全部）（1火警、2预警、3启动、4监管、5反馈、6故障、7屏蔽、96离线、99正常）
                .setRequestParams("pageSize", 10)
                .setRequestParams("pageIndex", 1);

        netWork().setRefreshListener(R.id.refreshLayout, true, true, new NetRequestListener() {
            @Override
            public void success(String action, BaseBean baseBean, Object tag) {
                if (baseBean.isSuccessful()) {
                    int i = (int) apiRequest.getRequestParams().get("pageIndex");
                    if (i == 1) {
                        databeans = ((ResultPlaceListBean) baseBean).getData();
                    } else {
                        databeans.addAll(databeans.size(), ((ResultPlaceListBean) baseBean).getData());
                    }
                    pointAdapter.initData(databeans);
                }
            }
        }, apiRequest);
    }

    @org.greenrobot.eventbus.Subscribe(sticky = true, threadMode = org.greenrobot.eventbus.ThreadMode.MAIN)
    public void onPushRefresh(JPushEvent event) {
        getPlaceList();
    }

    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {
        super.onMenuItemClick(menuItem);
        switch (menuItem.getItemId()) {
            case R.id.action_add:
                startForResult(AddPlaceFragment.newInstance(areaId, areaId != null ? (databeans != null && databeans.size() > 0 ? databeans.get(0).getAreaName() : null) : null), 1210);
                break;
        }
        return true;
    }

    @Override
    public void onFragmentResult(int requestCode, int resultCode, Bundle data) {
        super.onFragmentResult(requestCode, resultCode, data);
        if (requestCode == 1210 && resultCode == RESULT_OK)
            netWork().loading();
    }
    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }
}
