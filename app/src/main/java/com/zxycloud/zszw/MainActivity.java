package com.zxycloud.zszw;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.view.View;

import com.zxycloud.zszw.listener.OnNewIntentListener;
import com.zxycloud.zszw.model.ResultAppVersionBean;
import com.zxycloud.zszw.model.bean.AppVersionBean;
import com.zxycloud.zszw.service.InitService;
import com.zxycloud.zszw.service.UpdateService;
import com.zxycloud.common.base.fragment.SupportActivity;
import com.zxycloud.common.base.fragment.anim.DefaultHorizontalAnimator;
import com.zxycloud.common.base.fragment.anim.FragmentAnimator;
import com.zxycloud.common.utils.CommonUtils;
import com.zxycloud.common.utils.NotificationSetUtil;
import com.zxycloud.common.utils.SPUtils;
import com.zxycloud.common.utils.SystemUtil;
import com.zxycloud.common.utils.netWork.ApiRequest;
import com.zxycloud.common.utils.netWork.NetBean;
import com.zxycloud.common.utils.netWork.NetUtils;
import com.zxycloud.common.widget.CommonDialog;
import com.zxycloud.common.widget.ScanPopupWindow;

import cn.jpush.android.api.JPushInterface;

public class MainActivity extends SupportActivity {

    private InitService initService;

    private OnNewIntentListener mOnScanListener;// NFC信息反馈
    private NetUtils netUtils;
    private CommonDialog updateDialog;
    private CommonDialog notificationSetDialog;
    private SPUtils spUtils;

    private boolean isUpdate = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);

        // 设置状态栏白底黑字
        SystemUtil.StatusBarLightMode(this);
        spUtils = CommonUtils.getSPUtils(this);

        bindService(new Intent(this, InitService.class), conn, Context.BIND_AUTO_CREATE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
            if (!NotificationSetUtil.isNotificationEnabled(this))
                if (spUtils.getBoolean(SPUtils.NOTICE_PERMISSION_ASK, true)) {
                    notificationSetDialog = new CommonDialog.Builder()
                            .setTitleRes(R.string.notification_set_title)
                            .setContentRes(R.string.notification_set_content)
                            .setRightRes(R.string.dialog_yes)
                            .setLeftRes(R.string.dialog_no)
                            .setShowCb()
                            .build(this, notificationClickListener)
                            .show();
                }

        if (findFragment(MainFragment.class) == null)
            loadRootFragment(R.id.activity_base_fl, MainFragment.newInstance());

        checkUpdate();
    }

    @Override
    protected void onStart() {
        super.onStart();
        // 极光接收推送开启   不可删除
        if (JPushInterface.isPushStopped(getApplicationContext()))
            JPushInterface.resumePush(getApplicationContext());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        if (null != initService)
        try {
            this.unbindService(conn);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressedSupport() {
        // 对于 4个类别的主Fragment内的回退back逻辑,已经在其onBackPressedSupport里各自处理了
        super.onBackPressedSupport();
    }

    @Override
    public FragmentAnimator onCreateFragmentAnimator() {
        // 设置横向(和安卓4.x动画相同)
        return new DefaultHorizontalAnimator();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (mOnScanListener != null)
            mOnScanListener.onIntentData(ScanPopupWindow.readNFCId(intent), ScanPopupWindow.readNFCFromTag(intent));
    }

    public void setmOnScanListener(OnNewIntentListener mOnScanListener) {
        this.mOnScanListener = mOnScanListener;
    }

    @Override
    public void onStop() {
        super.onStop();
        if (null != updateDialog && updateDialog.isShowing())
            updateDialog.dismiss();
        if (null != notificationSetDialog && notificationSetDialog.isShowing())
            notificationSetDialog.dismiss();
    }

    /**
     * 判断APP是否需要升级
     */
    private void checkUpdate() {
        if (null == netUtils)
            netUtils = new NetUtils(this);

        netUtils.request(new NetUtils.NetRequestCallBack<ResultAppVersionBean>() {
            @Override
            public void success(String action, ResultAppVersionBean resultAppVersionBean, Object tag) {
                if (resultAppVersionBean.isSuccessful()) {
                    AppVersionBean appVersionBean = resultAppVersionBean.getData();
                    switch (appVersionBean.getForce()) {
                        // 强制升级
                        case AppVersionBean.UPDATE_STATE_FORCE:
                            isUpdate = false;
                            updateDialog = new CommonDialog.Builder()
                                    .setTitleRes(R.string.dialog_update_now)
                                    .setRightRes(R.string.dialog_yes)
                                    .setCanTouchOutside(false)
                                    .setTag(appVersionBean.getAppPath())
                                    .build(MainActivity.this, onCommonClickListener)
                                    .show();
                            break;

                        // 非强制升级
                        case AppVersionBean.UPDATE_STATE_UPDATE:
                            isUpdate = false;
                            updateDialog = new CommonDialog.Builder()
                                    .setTitleRes(R.string.dialog_update_now)
                                    .setContentRes(R.string.dialog_update_discovers_new_version)
                                    .setLeftRes(R.string.dialog_no)
                                    .setRightRes(R.string.dialog_yes)
                                    .setTag(appVersionBean.getAppPath())
                                    .build(MainActivity.this, onCommonClickListener)
                                    .show();
                            break;

                        // 后台判断版本级别，不需提示
                        case AppVersionBean.UPDATE_STATE_NO:
//                            CommonUtils.toast(MainActivity.this, R.string.toast_is_latest_version);
                            break;
                    }
                    ;
                } else {
                    CommonUtils.toast(MainActivity.this, resultAppVersionBean.getMessage());
                }
            }

            @Override
            public void error(String action, Throwable e, Object tag) {

            }
        }, true, new ApiRequest<>(NetBean.actionCheckAppVersion, ResultAppVersionBean.class)
                .setRequestParams("version", BuildConfig.VERSION_NAME)
                .setRequestParams("system", "android"));
    }

    private CommonDialog.OnCommonClickListener onCommonClickListener = new CommonDialog.OnCommonClickListener() {
        @Override
        public void onClick(View view, Object tag) {
            if (view.getId() == CommonDialog.ID_RIGHT && !isUpdate) {
                isUpdate = true;
                Intent intent = new Intent(MainActivity.this, UpdateService.class);
                intent.putExtra(UpdateService.APP_PATH, (String) tag);
                startService(intent);
            } else if (view.getId() == CommonDialog.ID_LEFT) {
                updateDialog.dismiss();
            }
        }
    };

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private CommonDialog.OnCommonClickListener notificationClickListener = new CommonDialog.OnCommonClickListener() {
        @Override
        public void onClick(View view, Object tag) {
            //判断是否需要开启通知栏功能
            if (view.getId() == CommonDialog.ID_RIGHT) {
                //判断是否需要开启通知栏功能
                NotificationSetUtil.OpenNotificationSetting(MainActivity.this, new NotificationSetUtil.OnNextLitener() {
                    @Override
                    public void onNext() {

                    }
                });
            } else if (view.getId() == CommonDialog.ID_LEFT) {
                // 若取消权限获取，则不再获取权限
                spUtils.put(SPUtils.NOTICE_PERMISSION_ASK, !notificationSetDialog.isCbChecked());
            }
        }
    };

    private ServiceConnection conn = new ServiceConnection() {
        /** 获取服务对象时的操作 */
        public void onServiceConnected(ComponentName name, IBinder service) {
            // TODO Auto-generated method stub
            initService = ((InitService.ServiceBinder) service).getService();

        }

        /** 无法获取到服务对象时的操作 */
        public void onServiceDisconnected(ComponentName name) {
            // TODO Auto-generated method stub
            initService = null;
        }

    };
}
