package com.zxycloud.zszw.fragment.service.install.device;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;

import com.zxycloud.zszw.R;
import com.zxycloud.zszw.adapter.SelectPlaceAdapter;
import com.zxycloud.zszw.base.BaseSearchListFragment;
import com.zxycloud.zszw.listener.NetRequestListener;
import com.zxycloud.zszw.listener.OnItemClickListener;
import com.zxycloud.zszw.model.ResultPlaceListBean;
import com.zxycloud.zszw.model.bean.PlaceBean;
import com.zxycloud.common.base.BaseBean;
import com.zxycloud.common.utils.CommonUtils;
import com.zxycloud.common.utils.SPUtils;
import com.zxycloud.common.utils.netWork.ApiRequest;
import com.zxycloud.common.utils.netWork.NetBean;

import java.util.List;

public class SelectPlaceFragment extends BaseSearchListFragment {
    private SelectPlaceAdapter pointAdapter;
    private List<PlaceBean> areaBeans;

    public static SelectPlaceFragment newInstance(String adapterName, String deviceId) {
        SelectPlaceFragment fragment = new SelectPlaceFragment();
        Bundle args = new Bundle();
        args.putString("adapterName", adapterName);
        args.putString("deviceId", deviceId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.toolbar_list;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        setSearchToolbar(false,R.string.select_place);

        initSearchEditText(NetBean.actionPostPlaceList, "placeName");

        RecyclerView recyclerView = findViewById(R.id.recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        pointAdapter = new SelectPlaceAdapter(getContext());
        recyclerView.setAdapter(pointAdapter);
        pointAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(int position, View view, RecyclerView.ViewHolder vh) {
                if (TextUtils.isEmpty(getArguments().getString("adapterName")))
                    startWithPop(ScanDeviceFragment.newInstance(areaBeans.get(position).getPlaceId(), areaBeans.get(position).getPlaceName(), areaBeans.get(position).getPicUrl()));
                else
                    startWithPop(DeviceAddFragment.newInstance(1,
                            getArguments().getString("adapterName"),
                            getArguments().getString("deviceId"),
                            areaBeans.get(position).getPlaceId(),
                            areaBeans.get(position).getPlaceName(),
                            areaBeans.get(position).getPicUrl()));
                }
        });

        netWork().setRefreshListener(R.id.refreshLayout, true, true, new NetRequestListener() {
            @Override
            public void success(String action, BaseBean baseBean, Object tag) {
                if (baseBean.isSuccessful()) {
                    if (tag == null || (int) tag == 1)
                        areaBeans = ((ResultPlaceListBean) baseBean).getData();
                    else
                        areaBeans.addAll(areaBeans.size(), ((ResultPlaceListBean) baseBean).getData());

                    pointAdapter.setData(areaBeans);
                }
            }
        }, netWork().apiRequest(NetBean.actionPostPlaceList, ResultPlaceListBean.class, ApiRequest.REQUEST_TYPE_POST, R.id.loading)
                .setRequestParams("placeName", null)// 场所名称模糊搜索字段
                .setRequestParams("projectId", CommonUtils.getSPUtils(getContext()).getString(SPUtils.PROJECT_ID))
                .setRequestParams("pageSize", 10)
                .setRequestParams("pageIndex", 1));
    }
}
