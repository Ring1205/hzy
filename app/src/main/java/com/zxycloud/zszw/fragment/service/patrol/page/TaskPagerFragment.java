package com.zxycloud.zszw.fragment.service.patrol.page;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;

import com.zxycloud.zszw.R;
import com.zxycloud.zszw.adapter.TasksAdapter;
import com.zxycloud.zszw.base.BaseBackFragment;
import com.zxycloud.zszw.event.type.TasksPagerType;
import com.zxycloud.zszw.fragment.service.patrol.task.TaskDetailsFragment;
import com.zxycloud.zszw.fragment.service.patrol.task.TaskListFragment;
import com.zxycloud.zszw.listener.MyOnClickListener;
import com.zxycloud.zszw.listener.NetRequestListener;
import com.zxycloud.zszw.listener.OnItemClickListener;
import com.zxycloud.zszw.model.ResultTaskListBean;
import com.zxycloud.zszw.model.bean.TaskBean;
import com.zxycloud.common.base.BaseBean;
import com.zxycloud.common.utils.CommonUtils;
import com.zxycloud.common.utils.SPUtils;
import com.zxycloud.common.utils.netWork.ApiRequest;
import com.zxycloud.common.utils.netWork.NetBean;

import java.util.List;

public class TaskPagerFragment extends BaseBackFragment implements MyOnClickListener, View.OnClickListener {
    private TasksAdapter histroyAdapter;
    private RecyclerView rlPatrolHistory;
    private EditText etTaskSearch;
    private List<TaskBean> taskData;

    public static TaskPagerFragment newInstance(@TasksPagerType.showType int type) {
        TaskPagerFragment fragment = new TaskPagerFragment();
        Bundle arg = new Bundle();
        arg.putInt("type", type);
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
        rlPatrolHistory = findViewById(R.id.recycler);
        rlPatrolHistory.setLayoutManager(new LinearLayoutManager(getContext()));
        histroyAdapter = new TasksAdapter(getContext());
        rlPatrolHistory.setAdapter(histroyAdapter);
        histroyAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(int position, View view, RecyclerView.ViewHolder vh) {
                ((TaskListFragment) getParentFragment()).start(TaskDetailsFragment.newInstance(taskData.get(position).getPatrolState(), taskData.get(position).getId()));
            }
        }, this, this);

        netWork().setRefreshListener(R.id.refreshLayout, true, true, new NetRequestListener() {
            @Override
            public void success(String action, BaseBean baseBean, Object tag) {
                if (baseBean.isSuccessful()) {
                    if (tag == null || (int) tag == 1)
                        taskData = ((ResultTaskListBean) baseBean).getData();
                    else
                        taskData.addAll(taskData.size(), ((ResultTaskListBean) baseBean).getData());
                    histroyAdapter.setData(taskData);
                }
            }
        }, netWork().apiRequest(NetBean.actionPostPatrolTaskList, ResultTaskListBean.class, ApiRequest.REQUEST_TYPE_POST, R.id.loading).setApiType(ApiRequest.API_TYPE_PATROL)
                .setRequestParams("companyId", CommonUtils.getSPUtils(getContext()).getString(SPUtils.PROJECT_ID))
                .setRequestParams("pageIndex", 1)
                .setRequestParams("pageSize", 10)
                .setRequestParams("patrolStateList", getArguments().getInt("type") != TasksPagerType.TASK_ALL ? new int[]{getArguments().getInt("type")} : new int[]{0, 1, 2, 3}));

        etTaskSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                netWork().setRequestListener(NetBean.actionPostPatrolTaskList, 800L)
                        .setRequestParams("patrolTaskName", s.toString().trim())
                        .setRequestParams("pageIndex", 1).setTag(1);
                if (netWork().getRefreshLayout() != null)
                    netWork().getRefreshLayout().resetNoMoreData();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        setOnClickListener(this, R.id.et_search_clear);
    }


    @Override
    public void onClick(int position, View view) {
        ((TaskListFragment) getParentFragment()).setPatrolTaskId(taskData.get(position).getId());
        switch (view.getId()) {
            case R.id.nfc_task:
                ((TaskListFragment) getParentFragment()).showScanPopupWindow(((TaskListFragment) getParentFragment()));
                break;
            case R.id.qr_code_task:
                ((TaskListFragment) getParentFragment()).jumpCaptureFragment(taskData.get(position).getId());
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.et_search_clear:
                etTaskSearch.setText("");
                break;
        }
    }

    public void upData() {
        netWork().loading();
    }
}
