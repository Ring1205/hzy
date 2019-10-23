package com.zxycloud.zszw.fragment.service.install.device;

import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.zxycloud.zszw.R;
import com.zxycloud.zszw.base.BaseQRCodeFragment;
import com.zxycloud.zszw.listener.NetRequestListener;
import com.zxycloud.zszw.model.AdapterVerifyBean;
import com.zxycloud.common.base.BaseBean;
import com.zxycloud.common.utils.CommonUtils;
import com.zxycloud.common.utils.SPUtils;
import com.zxycloud.common.utils.netWork.ApiRequest;
import com.zxycloud.common.utils.netWork.NetBean;

public class ScanDeviceFragment extends BaseQRCodeFragment implements View.OnClickListener {
    public static ScanDeviceFragment newInstance(String placeId, String placeName, String picUrl) {
        ScanDeviceFragment fragment = new ScanDeviceFragment();
        Bundle bundle = new Bundle();
        bundle.putString("placeId", placeId);
        bundle.putString("placeName", placeName);
        bundle.putString("picUrl", picUrl);
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

        setOnClickListener(this, R.id.btn_photo, R.id.btn_light, R.id.btn_input);

    }

    @Override
    protected void onQrCodeSuccess(Bitmap mBitmap, String result) {
        if (mBitmap != null && result != null)
            verifyAdapter(result);// 验证网关
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
            case R.id.btn_input:
                final EditText edit = new EditText(getContext());
                new AlertDialog.Builder(getContext())
                        .setTitle(R.string.title_device_number)
                        .setView(edit)
                        .setPositiveButton(R.string.dialog_yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                if (!TextUtils.isEmpty(edit.getText().toString()))
                                    verifyAdapter(edit.getText().toString());// 验证网关
                            }
                        }).setNegativeButton(R.string.dialog_no, null).create().show();
                break;
        }
    }

    private void verifyAdapter(final String result) {
        ApiRequest apiRequest = netWork().apiRequest(NetBean.actionPostVerifyAdapter, AdapterVerifyBean.class, ApiRequest.REQUEST_TYPE_POST)
                .setRequestParams("projectId", CommonUtils.getSPUtils(getContext()).getString(SPUtils.PROJECT_ID))// 单位Id
                .setRequestParams("adapterName", result);// 网关名称
        netWork().setRequestListener(new NetRequestListener() {
            @Override
            public void success(String action, BaseBean baseBean, Object tag) {
                if (baseBean.isSuccessful()) {
                    AdapterVerifyBean.DataBean dataBean = ((AdapterVerifyBean) baseBean).getData();
                    switch (dataBean.getDistributionState()) {
                        case 0:
                            CommonUtils.toast(getContext(), baseBean.getMessage());
                            quitPopuwindow(baseBean.getMessage());
                            break;
                        case 1:
                            CommonUtils.toast(getContext(), baseBean.getMessage());
                            quitPopuwindow(baseBean.getMessage());
                            break;
                        case 2:
                        case 3:
                            if (dataBean.getAdapterType() != 3) // 组合式
                                showDeviceType(result, dataBean.getDeviceId());
                            else // 独立式
                                startWithPop(DeviceAddFragment.newInstance(1, result,
                                        dataBean.getDeviceId(),
                                        getArguments().getString("placeId"),
                                        getArguments().getString("placeName"),
                                        getArguments().getString("picUrl")));
                            break;
                    }
                } else {
                    quitPopuwindow(baseBean.getMessage());
                    CommonUtils.toast(getContext(), baseBean.getMessage());
                }
            }
        }, apiRequest);
    }

    private void showDeviceType(final String result, final String deviceId) {
        com.zxycloud.common.widget.AlertDialog builder = new com.zxycloud.common.widget.AlertDialog(getContext()).builder();
        builder.setTitle(R.string.device_allocation_type).
                setCancelable(false).
                setMsg(R.string.device_allocation_type_msg).
                setNegativeButton(R.string.device_allocation_type_left, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        com.zxycloud.common.widget.AlertDialog builder = new com.zxycloud.common.widget.AlertDialog(getContext()).builder();
                        builder.setTitle(R.string.device_allocation_type).
                                setCancelable(false).
                                setMsg(R.string.device_allocation_type_gateway_msg).
                                setNegativeButton(R.string.device_allocation_type_gateway_left, new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        startWithPop(DeviceAddFragment.newInstance(2, result,
                                                deviceId,
                                                getArguments().getString("placeId"),
                                                getArguments().getString("placeName"),
                                                getArguments().getString("picUrl")));
                                    }
                                }).setPositiveButton(R.string.device_allocation_type_gateway_right, R.color.black, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                startWithPop(DeviceAddFragment.newInstance(3, result,
                                        deviceId,
                                        getArguments().getString("placeId"),
                                        getArguments().getString("placeName"),
                                        getArguments().getString("picUrl")));
                            }
                        }).show();
                    }
                }).setPositiveButton(R.string.device_allocation_type_right, R.color.black, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startWithPop(DeviceAssignmentFragment.newInstance(result,
                                getArguments().getString("placeId"),
                                getArguments().getString("placeName"),
                                getArguments().getString("picUrl")));
                    }
                }).show();
    }

    private void quitPopuwindow(String title) {
        final com.zxycloud.common.widget.AlertDialog builder = new com.zxycloud.common.widget.AlertDialog(getContext()).builder();
        builder.setTitle(title).
                setCancelable(false).
                setMsg(R.string.hint_quit_title).
                setNegativeButton(R.string.dialog_no, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        setFragmentId(R.id.fl_capture);
                    }
                }).setPositiveButton(R.string.dialog_yes, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
                    }
                }).show();
    }

}
