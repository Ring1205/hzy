package com.zxycloud.startup.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.zxycloud.startup.R;
import com.zxycloud.startup.bean.ResultSignInBean;
import com.zxycloud.startup.bean.SignInBean;
import com.sarlmoclen.router.SRouter;
import com.sarlmoclen.router.SRouterRequest;
import com.sarlmoclen.router.SRouterResponse;
import com.sarlmoclen.router.forMonitor.MainActionName;
import com.zxycloud.common.base.fragment.SupportActivity;
import com.zxycloud.common.utils.CommonUtils;
import com.zxycloud.common.utils.PermissionUtils;
import com.zxycloud.common.utils.SPUtils;
import com.zxycloud.common.utils.SystemUtil;
import com.zxycloud.common.utils.TimerUtils;
import com.zxycloud.common.utils.netWork.ApiRequest;
import com.zxycloud.common.utils.netWork.NetBean;
import com.zxycloud.common.utils.netWork.NetUtils;

import java.util.List;

/**
 * 启动页
 *
 * @author leiming
 * @date 2019/3/7 11:55
 */
public class StartActivity extends SupportActivity {

    private boolean directLogin = false;
    private boolean notWrong = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (CommonUtils.hasActivity(LoginActivity.class)) {
            startActivity(new Intent(StartActivity.this, LoginActivity.class));
            finish();
            return;
        }
        if (CommonUtils.hasActivity("MainActivity")) {
            SRouterResponse mSRouterResponse = SRouter.getInstance().sendMessage(
                    StartActivity.this, SRouterRequest.creat()
                            .action(MainActionName.name));
            finish();
            return;
        }
        setContentView(R.layout.activity_start);

        // 设置状态栏白底黑字
        SystemUtil.StatusBarLightMode(this);

        init();
    }

    private void init() {
        final SPUtils spUtils = CommonUtils.getSPUtils(this);
        spUtils.remove(SPUtils.USER_ID, SPUtils.USER_NAME, SPUtils.USER_PHONE, SPUtils.USER_EMAIL);

        if (spUtils.getBoolean(SPUtils.INIT_PERMISSION)) {
            loginJudge();
        } else {
            PermissionUtils.setRequestPermissions(this, new PermissionUtils.PermissionGrant() {
                @Override
                public Integer[] onPermissionGranted() {
                    return new Integer[]{PermissionUtils.CODE_ALL_PERMISSION};
                }

                @Override
                public void onRequestResult(List<String> deniedPermission) {
                    // 更改APP系统音量，功能暂时隐藏
//                    notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
//                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N
//                            && null != notificationManager &&
//                            ! notificationManager.isNotificationPolicyAccessGranted()) {
//                        showAlertDialog(R.string.notices, R.string.request_do_not_disturb, R.string.to_set, R.string.set_later);
//                    }
                    spUtils.put(SPUtils.INIT_PERMISSION, true);
                    startActivity(new Intent(StartActivity.this, LoginActivity.class));
                    StartActivity.this.finish();
                }
            });
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionUtils.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void loginJudge() {
        TimerUtils timerUtils = new TimerUtils(1800, 1800, onBaseTimerCallBack);
        timerUtils.start();

        NetUtils netUtils = new NetUtils(this);

        if (netUtils.hasToken()) {
            directLogin = true;
            netUtils.request(new NetUtils.NetRequestCallBack<ResultSignInBean>() {
                @Override
                public void success(String action, ResultSignInBean resultSignInBean, Object tag) {
                    if (resultSignInBean.isSuccessful()) {
                        SignInBean signInBean = resultSignInBean.getData();
                        if (!signInBean.isChangePassword()) {
                            SPUtils spUtils = CommonUtils.getSPUtils(StartActivity.this);
                            spUtils.put(SPUtils.USER_ID, signInBean.getUserId());
                            spUtils.put(SPUtils.USER_NAME, signInBean.getUserName());
                            String userPhone = signInBean.getPhoneNumber();
                            String userEmail = signInBean.getEmail();
                            if (!TextUtils.isEmpty(userPhone)) {
                                spUtils.put(SPUtils.USER_PHONE, userPhone);
                            }
                            if (!TextUtils.isEmpty(userEmail)) {
                                spUtils.put(SPUtils.USER_EMAIL, userEmail);
                            }
                            notWrong = true;
                        }
                    }
                }

                @Override
                public void error(String action, Throwable e, Object tag) {
                }
            }, false, new ApiRequest<>(NetBean.actionSignInByTokenId, ResultSignInBean.class));
        }
    }

    private TimerUtils.OnBaseTimerCallBack onBaseTimerCallBack = new TimerUtils.OnBaseTimerCallBack() {

        @Override
        public void onTick(Object tag, long millisUntilFinished) {
            CommonUtils.log().i(millisUntilFinished);
        }

        @Override
        public void onFinish(Object tag) {
            if (directLogin && notWrong) {
                if (!CommonUtils.hasActivity(LoginActivity.class)) {
                    SRouterResponse mSRouterResponse = SRouter.getInstance().sendMessage(
                            StartActivity.this, SRouterRequest.creat()
                                    .action(MainActionName.name));
                    finish();
                }
            } else {
                if (!CommonUtils.hasActivity("MainActivity")) {
                    startActivity(new Intent(StartActivity.this, LoginActivity.class));
                }
            }
            StartActivity.this.finish();
        }
    };
}
