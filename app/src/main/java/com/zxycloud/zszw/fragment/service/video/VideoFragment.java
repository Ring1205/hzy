package com.zxycloud.zszw.fragment.service.video;


import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.zxycloud.zszw.R;
import com.zxycloud.zszw.adapter.VideoAdapter;
import com.zxycloud.zszw.base.BaseBackFragment;
import com.zxycloud.zszw.base.MyBaseAdapter;
import com.zxycloud.zszw.event.type.VideoShowType;
import com.zxycloud.zszw.fragment.common.PlayVideoFragment;
import com.zxycloud.zszw.listener.NetRequestListener;
import com.zxycloud.zszw.listener.OnItemClickListener;
import com.zxycloud.zszw.listener.OnProjectObtainListener;
import com.zxycloud.zszw.model.ResultProjectListBean;
import com.zxycloud.zszw.model.ResultVideoListBean;
import com.zxycloud.zszw.model.bean.ProjectBean;
import com.zxycloud.zszw.model.bean.VideoBean;
import com.zxycloud.common.base.BaseBean;
import com.zxycloud.common.utils.netWork.ApiRequest;
import com.zxycloud.common.utils.netWork.NetBean;
import com.zxycloud.common.widget.BswRecyclerView.RecyclerViewHolder;
import com.zxycloud.common.widget.DropdownButton;

import java.util.List;

/**
 * Created by YoKeyword on 16/2/9.
 */
public class VideoFragment extends BaseBackFragment implements MyBaseAdapter.OnBindViewHolderListener {
    private DropdownButton btnDropDown;
    private VideoAdapter adapter;
    private List<VideoBean> videoBeans;
    private List<ProjectBean> projectBean;
    private TextView tvProjectName;

    public static VideoFragment newInstance() {
        Bundle args = new Bundle();
        VideoFragment fragment = new VideoFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.service_video;
    }

    private MyBaseAdapter myBaseAdapter;

    @Override
    protected void initView(Bundle savedInstanceState) {
        setToolbarTitle(R.string.videos).initToolbarNav();
        RecyclerView rlVideo = findViewById(R.id.rl_video);
        btnDropDown = findViewById(R.id.btn_drop_down);
        tvProjectName = findViewById(R.id.tv_project_name);

        rlVideo.setLayoutManager(new GridLayoutManager(getContext(), 3));
        adapter = new VideoAdapter(getContext());
        adapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(int position, View view, RecyclerView.ViewHolder vh) {
                start(PlayVideoFragment.newInstance(VideoShowType.PLAY_TYPE_LINKAGE_CAMERA, videoBeans.get(position).getVideoId()));
            }
        });
        rlVideo.setAdapter(adapter);
        myBaseAdapter = new MyBaseAdapter(getContext(), R.layout.dropdown_item, this);
        btnDropDown.setAdapter(myBaseAdapter);

        getVideoList();
    }

    private void getVideoList() {
        getProject(new OnProjectObtainListener() {
            @Override
            public void success(String projectId, String projectName) {
                tvProjectName.setText(projectName);
                netWork().setRefreshListener(R.id.refresh_layout, true, true, new NetRequestListener() {
                    @Override
                    public void success(String action, BaseBean baseBean, Object tag) {
                        if (baseBean.isSuccessful()) {
                            if (tag == null || (int) tag == 1)
                                videoBeans = ((ResultVideoListBean) baseBean).getData();
                            else
                                videoBeans.addAll(videoBeans.size(), ((ResultVideoListBean) baseBean).getData());
                            adapter.setData(videoBeans);
                        }
                    }
                }, netWork().apiRequest(NetBean.actionGetVideoList, ResultVideoListBean.class, ApiRequest.REQUEST_TYPE_POST, R.id.loading)
                        .setRequestParams("pageSize", 10)
                        .setRequestParams("pageIndex", 1)
                        .setRequestParams("projectId", projectId));
//                netWork().setRequestListener(new NetRequestListener() {
//                    @Override
//                    public void success(String action, BaseBean baseBean, Object tag) {
//                        if (baseBean.isSuccessful()) {
//                            projectBean = ((ResultProjectListBean) baseBean).getData();
//                            myBaseAdapter.setData(projectBean);
//                        }
//                    }
//                }, netWork().apiRequest(NetBean.actionGetProjectList, ResultProjectListBean.class, ApiRequest.REQUEST_TYPE_POST)
//                        .setRequestParams("pageSize", 100)
//                        .setRequestParams("pageIndex", 1));
            }
        });
    }

    @Override
    public void onBindViewHolder(final int position, View view, RecyclerViewHolder holder) {
        holder.setText(R.id.tv_pro_name, projectBean.get(position).getProjectName());
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                netWork().setRequestListener(NetBean.actionGetVideoList)
                        .setRequestParams("pageSize", 10)
                        .setRequestParams("pageIndex", 1)
                        .setRequestParams("projectId", projectBean.get(position).getProjectId()).setTag(1);
                tvProjectName.setText(projectBean.get(position).getProjectName());
                btnDropDown.onDismiss();
            }
        });
    }
}
