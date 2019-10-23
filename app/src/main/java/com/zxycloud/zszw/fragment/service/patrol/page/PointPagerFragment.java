package com.zxycloud.zszw.fragment.service.patrol.page;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;

import com.zxycloud.zszw.R;
import com.zxycloud.zszw.adapter.PatrolAdapter;
import com.zxycloud.zszw.base.BaseBackFragment;
import com.zxycloud.zszw.event.type.TasksPointType;
import com.zxycloud.zszw.fragment.service.patrol.task.TaskDetailsFragment;
import com.zxycloud.zszw.listener.NetRequestListener;
import com.zxycloud.zszw.model.ResultTaskDetailsBean;
import com.zxycloud.common.base.BaseBean;
import com.zxycloud.common.utils.CommonUtils;
import com.zxycloud.common.utils.netWork.ApiRequest;
import com.zxycloud.common.utils.netWork.NetBean;

import static com.zxycloud.zszw.event.type.TasksPointType.POINT_ALL;

public class PointPagerFragment extends BaseBackFragment implements View.OnClickListener {
    public PatrolAdapter adapter;
    private EditText etTaskSearch;

    public static PointPagerFragment newInstance(@TasksPointType.showType int type, String taskId) {
        PointPagerFragment fragment = new PointPagerFragment();
        Bundle arg = new Bundle();
        arg.putInt("type", type);
        arg.putString("TaskId", taskId);
        fragment.setArguments(arg);
        return fragment;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.search_list;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        etTaskSearch = findViewById(R.id.et_task_search);
        RecyclerView recycler = findViewById(R.id.recycler);
        recycler.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new PatrolAdapter(getContext(),(TaskDetailsFragment)getParentFragment());
        recycler.setAdapter(adapter);

        etTaskSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                netWork().setRequestListener(NetBean.actionGetTaskDetails, 800L)
                        .setRequestParams("patrolPointName", s.toString().trim())
                        .setRequestParams("pageIndex", 1).setTag(1);
                if (netWork().getRefreshLayout() != null)
                    netWork().getRefreshLayout().resetNoMoreData();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        initData();
        setOnClickListener(this, R.id.et_search_clear);
    }

    private void initData() {
        netWork().setRefreshListener(R.id.refreshLayout, false, false, new NetRequestListener() {
            @Override
            public void success(String action, BaseBean baseBean, Object tag) {
                if (baseBean.isSuccessful())
                    adapter.setData(((ResultTaskDetailsBean) baseBean).getData().getTaskPointVOList());
                 else
                    CommonUtils.toast(getContext(), baseBean.getMessage());
            }
        }, netWork().apiRequest(NetBean.actionGetTaskDetails, ResultTaskDetailsBean.class, ApiRequest.REQUEST_TYPE_GET, R.id.loading).setApiType(ApiRequest.API_TYPE_PATROL)
                .setRequestParams("id", getArguments().getString("TaskId")).setRequestParams("resultState", getArguments().getInt("type") != POINT_ALL ? getArguments().getInt("type") : null));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.et_search_clear:
                etTaskSearch.setText("");
                break;
        }
    }

    public void upData(){
        netWork().loading();
    }

}
