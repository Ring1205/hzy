package com.zxycloud.zszw.fragment.service.patrol.task;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;

import com.zxycloud.zszw.R;
import com.zxycloud.zszw.base.BaseQRCodeFragment;
import com.zxycloud.zszw.listener.NetRequestListener;
import com.zxycloud.zszw.model.ResultTaskItemBean;
import com.zxycloud.common.base.BaseBean;
import com.zxycloud.common.utils.netWork.ApiRequest;
import com.zxycloud.common.utils.netWork.NetBean;

public class TaskCaptureFragment extends BaseQRCodeFragment implements View.OnClickListener {

    public static TaskCaptureFragment newInstance(String taskId) {
        TaskCaptureFragment fragment = new TaskCaptureFragment();
        Bundle bundle = new Bundle();
        bundle.putString("TaskId", taskId);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.capture;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        setmToolbar(R.string.capture);

        setFragmentId(R.id.fl_capture);

        setOnClickListener(this, R.id.btn_photo, R.id.btn_light);

        findViewById(R.id.btn_input).setVisibility(View.GONE);
    }

    @Override
    protected void onQrCodeSuccess(Bitmap mBitmap, String result) {
        if (mBitmap != null && result != null)
            netWork().setRequestListener(new NetRequestListener() {
                @Override
                public void success(String action, BaseBean baseBean, Object tag) {
                    if (baseBean.isSuccessful())
                        startWithPop(TaskSubmitFragment.newInstance(getArguments().getString("TaskId"), ((ResultTaskItemBean) baseBean).getData().getTagNumber()));
                     else
                        quitPopuwindow(baseBean.getMessage());
                }
            }, netWork().apiRequest(NetBean.actionPostTaskPointDetails, ResultTaskItemBean.class, ApiRequest.REQUEST_TYPE_POST).setApiType(ApiRequest.API_TYPE_PATROL)
                    .setRequestParams("patrolTaskId", getArguments().getString("TaskId"))
                    .setRequestParams("tagNumber", result.replace("\uFEFF", "")));
    }

    @Override
    public void onClick(final View view) {
        switch (view.getId()) {
            case R.id.btn_photo:
                openPhoto();
                break;
            case R.id.btn_light:
                openLight();
                break;
        }
    }

    private void quitPopuwindow(String title) {
        final com.zxycloud.common.widget.AlertDialog builder = new com.zxycloud.common.widget.AlertDialog(getContext()).builder();
        builder.setTitle(title).setMsg(R.string.hint_quit_title)
                .setNegativeButton(R.string.dialog_no, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        setFragmentId(R.id.fl_capture);
                    }
                })
                .setPositiveButton(R.string.dialog_yes, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
                    }
                }).show();
    }
}
