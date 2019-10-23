package com.zxycloud.zszw.fragment.service.install.device;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;

import com.zxycloud.zszw.R;
import com.zxycloud.zszw.base.BaseSearchListFragment;
import com.zxycloud.zszw.base.MyBaseAdapter;
import com.zxycloud.zszw.listener.NetRequestListener;
import com.zxycloud.zszw.model.ResultDeviceTypeListBean;
import com.zxycloud.common.base.BaseBean;
import com.zxycloud.common.utils.CommonUtils;
import com.zxycloud.common.utils.netWork.ApiRequest;
import com.zxycloud.common.utils.netWork.NetBean;
import com.zxycloud.common.widget.BswRecyclerView.RecyclerViewHolder;

import java.util.ArrayList;
import java.util.List;

public class SelectDeviceTypeFragment extends BaseSearchListFragment {
    private MyBaseAdapter baseAdapter;
    private List<ResultDeviceTypeListBean.DataBean> dataBeans, getDataBeans;

    public static SelectDeviceTypeFragment newInstance() {
        SelectDeviceTypeFragment fragment = new SelectDeviceTypeFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.toolbar_list;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        setSearchToolbar(false, R.string.string_select_title_device_type);
        // 网络搜索，已改本地搜索
//        initSearchEditText(NetBean.actionGetDeviceTypeList, "name");
        // 本地搜索
        initSearchEditText(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                setTextWatcher(s);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        RecyclerView recyclerView = findViewById(R.id.recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        baseAdapter = new MyBaseAdapter(getContext(), R.layout.item_textview, new MyBaseAdapter.OnBindViewHolderListener() {
            @Override
            public void onBindViewHolder(final int position, View view, RecyclerViewHolder holder) {
                if (getDataBeans != null && getDataBeans.size() > position) {
                    holder.setText(R.id.tv_point_entry, getDataBeans.get(position).getDeviceTypeName());
                    holder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Bundle bundle = new Bundle();
                            bundle.putInt("deviceTypeCode", getDataBeans.get(position).getDeviceTypeCode());
                            bundle.putString("deviceTypeName", getDataBeans.get(position).getDeviceTypeName());
                            setFragmentResult(RESULT_OK, bundle);
                            finish();
                        }
                    });
                }
            }
        });
        recyclerView.setAdapter(baseAdapter);

        netWork().setRefreshListener(R.id.refreshLayout, false, false, new NetRequestListener() {
            @Override
            public void success(String action, BaseBean baseBean, Object tag) {
                if (baseBean.isSuccessful()) {
                    dataBeans = ((ResultDeviceTypeListBean) baseBean).getData();
                    setTextWatcher("");
                    baseAdapter.setData(((ResultDeviceTypeListBean) baseBean).getData());
                } else {
                    CommonUtils.toast(getContext(), baseBean.getMessage());
                }
            }
        }, netWork().apiRequest(NetBean.actionGetDeviceTypeList, ResultDeviceTypeListBean.class, ApiRequest.REQUEST_TYPE_GET, R.id.loading));
    }

    /**
     * 字符串过滤
     *
     * @param s 若为空则显示全部
     */
    private void setTextWatcher(CharSequence s) {
        getDataBeans = new ArrayList<>();
        if (dataBeans != null) {
            for (ResultDeviceTypeListBean.DataBean dataBean : dataBeans) {
                if (dataBean.getDeviceTypeName().contains(s)) {
                    getDataBeans.add(dataBean);
                }
            }
            baseAdapter.setData(getDataBeans);
        }
    }

}
