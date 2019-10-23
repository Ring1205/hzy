package com.zxycloud.zszw.fragment.mine.other;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.zxycloud.zszw.BuildConfig;
import com.zxycloud.zszw.R;
import com.zxycloud.zszw.base.BaseBackFragment;
import com.zxycloud.zszw.model.ResultAppVersionBean;
import com.zxycloud.zszw.model.bean.AppVersionBean;
import com.zxycloud.zszw.service.UpdateService;
import com.zxycloud.common.utils.CommonUtils;
import com.zxycloud.common.utils.netWork.ApiRequest;
import com.zxycloud.common.utils.netWork.NetBean;
import com.zxycloud.common.utils.netWork.NetUtils;
import com.zxycloud.common.widget.CommonDialog;

/**
 * @author leiming
 * @date 2019/4/15.
 */
public class AboutAppFragment extends BaseBackFragment implements View.OnClickListener {

    private TextView aboutUsCurrentVersion;
    private NetUtils netUtils;
    private CommonDialog updateDialog;
    private boolean isUpdate = false;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_about_app;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        setToolbarTitle(R.string.about_app).initToolbarNav();

        aboutUsCurrentVersion = findViewById(R.id.about_us_current_version);
        aboutUsCurrentVersion.append(BuildConfig.VERSION_NAME);
        setOnClickListener(this, R.id.about_us_check_new_version);
    }

    public static AboutAppFragment getInstance() {
        return new AboutAppFragment();
    }

    private void checkUpdate() {
        if (null == netUtils) {
            netUtils = NetUtils.getNewInstance(_mActivity);
        }
        netUtils.request(new NetUtils.NetRequestCallBack<ResultAppVersionBean>() {
            @Override
            public void success(String action, ResultAppVersionBean resultAppVersionBean, Object tag) {
                if (resultAppVersionBean.isSuccessful()) {
                    AppVersionBean appVersionBean = resultAppVersionBean.getData();
                    switch (appVersionBean.getForce()) {
                        case AppVersionBean.UPDATE_STATE_FORCE:
                            isUpdate = false;
                            updateDialog = new CommonDialog.Builder()
                                    .setTitleRes(R.string.dialog_update_now)
                                    .setRightRes(R.string.dialog_yes)
                                    .setCanTouchOutside(false)
                                    .setTag(appVersionBean.getAppPath())
                                    .build(_mActivity, onCommonClickListener)
                                    .show();
                            break;

                        case AppVersionBean.UPDATE_STATE_UPDATE:
                            isUpdate = false;
                            updateDialog = new CommonDialog.Builder()
                                    .setTitleRes(R.string.dialog_update_now)
                                    .setContentRes(R.string.dialog_update_discovers_new_version)
                                    .setLeftRes(R.string.dialog_no)
                                    .setRightRes(R.string.dialog_yes)
                                    .setTag(appVersionBean.getAppPath())
                                    .build(_mActivity, onCommonClickListener)
                                    .show();
                            break;

                        case AppVersionBean.UPDATE_STATE_NO:
                            CommonUtils.toast(_mActivity, R.string.toast_is_latest_version);
                            break;
                    }
                    ;
                } else {
                    CommonUtils.toast(_mActivity, resultAppVersionBean.getMessage());
                }
            }

            @Override
            public void error(String action, Throwable e, Object tag) {

            }
        }, true, new ApiRequest<>(NetBean.actionCheckAppVersion, ResultAppVersionBean.class)
                .setRequestParams("version", BuildConfig.VERSION_NAME)
                .setRequestParams("system", "android"));
    }

    @Override
    public void onStop() {
        super.onStop();
        if (null != updateDialog && updateDialog.isShowing())
            updateDialog.dismiss();
    }

    private CommonDialog.OnCommonClickListener onCommonClickListener = new CommonDialog.OnCommonClickListener() {

        @Override
        public void onClick(View view, Object tag) {
            if (view.getId() == CommonDialog.ID_RIGHT && !isUpdate) {
                isUpdate = true;
                Intent intent = new Intent(_mActivity, UpdateService.class);
                intent.putExtra(UpdateService.APP_PATH, (String) tag);
                _mActivity.startService(intent);
            }
        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.about_us_check_new_version:
                checkUpdate();
                break;
        }
    }
}
