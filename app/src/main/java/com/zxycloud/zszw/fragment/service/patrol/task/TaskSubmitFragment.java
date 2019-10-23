package com.zxycloud.zszw.fragment.service.patrol.task;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.zxycloud.zszw.R;
import com.zxycloud.zszw.adapter.AccessoryAdapter;
import com.zxycloud.zszw.base.BaseBackFragment;
import com.zxycloud.zszw.base.MyBaseAdapter;
import com.zxycloud.zszw.dialog.SubmitPopupWindow;
import com.zxycloud.zszw.listener.NetRequestListener;
import com.zxycloud.zszw.model.PathTypeBean;
import com.zxycloud.zszw.model.RequestTaskSubmitBean;
import com.zxycloud.zszw.model.ResultTaskItemBean;
import com.zxycloud.zszw.model.bean.PointStateBean;
import com.zxycloud.common.base.BaseBean;
import com.zxycloud.common.utils.CommonUtils;
import com.zxycloud.common.utils.Luban;
import com.zxycloud.common.utils.PermissionUtils;
import com.zxycloud.common.utils.netWork.ApiRequest;
import com.zxycloud.common.utils.netWork.NetBean;
import com.zxycloud.common.widget.BswRecyclerView.RecyclerViewHolder;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class TaskSubmitFragment extends BaseBackFragment implements View.OnClickListener {
    private MyBaseAdapter taskProjectAdapter;
    private ResultTaskItemBean.DataBean dataBean;

    public static TaskSubmitFragment newInstance(String taskId, String patrol) {
        TaskSubmitFragment fragment = new TaskSubmitFragment();
        Bundle arg = new Bundle();
        arg.putString("taskId", taskId);
        arg.putString("patrol", patrol);
        fragment.setArguments(arg);
        return fragment;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        setToolbarTitle(R.string.detail_description).initToolbarNav();

        RecyclerView rlProjectList = findViewById(R.id.rl_project_list);
        rlProjectList.setLayoutManager(new LinearLayoutManager(getContext()));
        taskProjectAdapter = new MyBaseAdapter(getContext(), R.layout.item_task_project_submit, holderListener);
        rlProjectList.setAdapter(taskProjectAdapter);

        initData();
        setOnClickListener(this, R.id.ll_submit);
    }

    private void initData() {
        netWork().setRequestListener(new NetRequestListener() {
            @Override
            public void success(String action, BaseBean baseBean, Object tag) {
                if (baseBean.isSuccessful())
                    switch (action) {
                        case NetBean.actionPostTaskPointDetails:
                            dataBean = ((ResultTaskItemBean) baseBean).getData();
                            TextView tvAddress = findViewById(R.id.tv_address);
                            if (TextUtils.isEmpty(dataBean.getAddress()))
                                tvAddress.setVisibility(View.GONE);
                            else
                                tvAddress.setText(dataBean.getAddress());
                            tvAddress.requestFocus();
                            taskProjectAdapter.setData(dataBean.getTaskItemVOList());
                            break;
                        case NetBean.actionPostEditTask:
                            CommonUtils.toast(getContext(),baseBean.getMessage());
                            finish();
                            break;
                    }
                else
                    CommonUtils.toast(getContext(), baseBean.getMessage());
            }
        }, netWork().apiRequest(NetBean.actionPostTaskPointDetails, ResultTaskItemBean.class, ApiRequest.REQUEST_TYPE_POST).setApiType(ApiRequest.API_TYPE_PATROL)
                .setRequestParams("patrolTaskId", getArguments().getString("taskId"))
                .setRequestParams("tagNumber", getArguments().getString("patrol")));
    }

    private MyBaseAdapter.OnBindViewHolderListener holderListener = new MyBaseAdapter.OnBindViewHolderListener() {
        @Override
        public void onBindViewHolder(int position, View view, RecyclerViewHolder holder) {
            final PointStateBean bean = dataBean.getTaskItemVOList().get(position);
            holder.setText(R.id.tv_project_index, bean.getPatrolItemName());
            holder.setText(R.id.tv_device_type, bean.getEquTypeName());

            checkedItemView(holder.getImageView(R.id.iv_check_normal), bean.getResultState() != 1 ? 0 : 1);
            checkedItemView(holder.getImageView(R.id.iv_check_abnormal), bean.getResultState() != 2 ? 0 : 2);

            setItemOnClickListener(holder.getView(R.id.ll_normal), position, bean, 1);
            setItemOnClickListener(holder.getView(R.id.ll_fault), position, bean, 2);
        }

        private void checkedItemView(ImageView img, int state) {
            switch (state) {
                case 0:
                    img.setImageResource(R.mipmap.ic_point_state_non);
                    break;
                case 1:
                    img.setImageResource(R.mipmap.ic_point_state_nor);
                    break;
                case 2:
                    img.setImageResource(R.mipmap.ic_point_state_warn);
                    break;
            }
        }

        private void setItemOnClickListener(View view, final int position, final PointStateBean bean, final int i) {
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    bean.setResultState(i);
                    taskProjectAdapter.notifyItemChanged(position);
                    boolean isAllNomber = true;

                    for (PointStateBean pointStateBean : dataBean.getTaskItemVOList())
                        if (pointStateBean.getResultState() != 1)
                            isAllNomber = false;
                    checkAllChangeView(isAllNomber);
                }
            });
        }
    };

    @Override
    protected int getLayoutId() {
        return R.layout.task_submit;
    }

    @Override
    public void onClick(final View v) {
        switch (v.getId()) {
            case R.id.ll_submit://提交
                setSubmitData();
                if (submitData.hasUnselected()) {
                    CommonUtils.toast(getContext(), R.string.toast_task_project_state);
                    return;
                }
                if (submitData.hasAbnormal() && TextUtils.isEmpty(submitData.getDes())) {
                    for (int i = 0; i < submitData.getDataBeans().size(); i++) {
                        if (submitData.getDataBeans().get(i).getResultState() != 1) {
                            CommonUtils.toast(getContext(), R.string.toast_accessory_error);
                            submitPopupWindow = new SubmitPopupWindow(this);
                            submitPopupWindow.show();
                            submitPopupWindow.setAddImgOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    PermissionUtils.setRequestPermissions(TaskSubmitFragment.this, new PermissionUtils.PermissionGrant() {
                                        @Override
                                        public Integer[] onPermissionGranted() {
                                            return new Integer[]{PermissionUtils.CODE_CAMERA, PermissionUtils.CODE_READ_EXTERNAL_STORAGE, PermissionUtils.CODE_WRITE_EXTERNAL_STORAGE};
                                        }

                                        @Override
                                        public void onRequestResult(List<String> deniedPermission) {
                                            if (CommonUtils.judgeListNull(deniedPermission) == 0)
                                                imgPath = CommonUtils.openCamera(TaskSubmitFragment.this, 2011);
                                        }
                                    });
                                }
                            });
                            submitPopupWindow.setAdditionalListener(new SubmitPopupWindow.OnAdditionalListener() {
                                @Override
                                public void setInformation(String message) {
                                    submitData.setDes(message);
                                    onClick(v);
                                }

                                @Override
                                public void setVoiceUrl(String voiceUrl) {
                                    submitData.setVoiceUrl(voiceUrl);
                                }

                                @Override
                                public void setVideoUrl(String videoUrl) {
                                    submitData.setVideoUrl(videoUrl);
                                }

                                @Override
                                public void addImageUrl(String imageUrl) {
                                    mImgUrls.add(imageUrl);
                                }

                                @Override
                                public void removeImageUrl(String imageUrl) {
                                    mImgUrls.remove(imageUrl);
                                }
                            });
                            return;
                        }
                    }
                }
                submitData.setId(dataBean.getId());
                submitData.setImgUrls(mImgUrls);
                netWork().addRequestListener(netWork().apiRequest(NetBean.actionPostEditTask, BaseBean.class, ApiRequest.REQUEST_TYPE_POST).setApiType(ApiRequest.API_TYPE_PATROL).setRequestBody(submitData));
                break;
        }
    }

    private RequestTaskSubmitBean submitData = new RequestTaskSubmitBean();
    private List<String> mImgUrls = new ArrayList<>();
    private SubmitPopupWindow submitPopupWindow;
    private String imgPath;

    public void setSubmitData() {
        List<RequestTaskSubmitBean.DataBean> dataBeans = new ArrayList<>();
        for (PointStateBean bean : dataBean.getTaskItemVOList()) {
            dataBeans.add(new RequestTaskSubmitBean.DataBean(bean.getId(), bean.getResultState()));
        }
        submitData.setDataBeans(dataBeans);
    }

    private void checkAllChangeView(boolean isCheck) {
        if (isCheck) {
            ((TextView) findViewById(R.id.tv_submit)).setTextColor(getResources().getColor(R.color.colorLine));
            ((TextView) findViewById(R.id.tv_submit)).setTextColor(getResources().getColor(R.color.colorLine));
        } else {
            boolean isSubmit = true;
            for (PointStateBean bean : dataBean.getTaskItemVOList())
                if (bean.getResultState() == 0)
                    isSubmit = false;
            if (isSubmit)
                ((TextView) findViewById(R.id.tv_submit)).setTextColor(getResources().getColor(R.color.colorLine));
            else
                ((TextView) findViewById(R.id.tv_submit)).setTextColor(getResources().getColor(R.color.color_text_submit));
        }
    }

    /**
     * 相机返回
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_CANCELED && requestCode == 2011) {
            String saveAlbumPath = Luban.get(_mActivity).load(new File(imgPath)).launch().getAbsolutePath();
            PathTypeBean bean = new PathTypeBean();
            bean.setDataPath(saveAlbumPath);
            bean.setType(AccessoryAdapter.TYPE_PHOTO);
            submitPopupWindow.setPathData(bean);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onFragmentResult(int requestCode, int resultCode, Bundle data) {
        super.onFragmentResult(requestCode, resultCode, data);
        if (requestCode == 5110 && submitPopupWindow != null)
            submitPopupWindow.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionUtils.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
