package com.zxycloud.zszw;

import android.app.Activity;
import android.os.Bundle;
import android.support.multidex.MultiDex;

import com.zxycloud.zszw.routerAction.MainAction;
import com.zxycloud.startup.routerAction.LogoutAction;
import com.zxycloud.startup.ui.LoginActivity;
import com.zxycloud.startup.ui.StartActivity;
import com.sarlmoclen.router.SRouter;
import com.sarlmoclen.router.forMonitor.LogoutActionName;
import com.sarlmoclen.router.forMonitor.MainActionName;
import com.zxycloud.common.CommonApp;
import com.zxycloud.common.base.fragment.Fragmentation;
import com.zxycloud.common.base.fragment.helper.ExceptionHandler;
import com.zxycloud.common.utils.CommonUtils;

import cn.jpush.android.api.JPushInterface;

public class MyApplication extends CommonApp {

    @Override
    public void onCreate() {
        super.onCreate();
        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacksImpl());
        Fragmentation.builder()
                // 设置 栈视图 模式为 （默认）悬浮球模式   SHAKE: 摇一摇唤出  NONE：隐藏， 仅在Debug环境生效
                .stackViewMode(Fragmentation.BUBBLE)
                .debug(BuildConfig.DEBUG) // 实际场景建议.debug(BuildConfig.DEBUG)
                /**
         * 可以获取到{@link me.yokeyword.fragmentation.exception.AfterSaveStateTransactionWarning}
         * 在遇到After onSaveInstanceState时，不会抛出异常，会回调到下面的ExceptionHandler
         */
                .handleException(new ExceptionHandler() {
                    @Override
                    public void onException(Exception e) {
                        // 以Bugtags为例子: 把捕获到的 Exception 传到 Bugtags 后台。
                        // Bugtags.sendException(e);
                    }
                }).install();
    }

    @Override
    public void registerAction() {
        MultiDex.install(this);
        SRouter.getInstance().registerAction(MainActionName.name, new MainAction());
        SRouter.getInstance().registerAction(LogoutActionName.name, new LogoutAction());
    }

    private class ActivityLifecycleCallbacksImpl implements ActivityLifecycleCallbacks {

        @Override
        public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
            CommonUtils.addActivity(activity);
            CommonUtils.log().i("open : " + activity.getClass().getSimpleName());
            if (activity instanceof LoginActivity || activity instanceof StartActivity) {
                if (!JPushInterface.isPushStopped(activity))
                    JPushInterface.stopPush(activity);
            }
        }

        @Override
        public void onActivityStarted(Activity activity) {
        }

        @Override
        public void onActivityResumed(Activity activity) {

        }

        @Override
        public void onActivityPaused(Activity activity) {

        }

        @Override
        public void onActivityStopped(Activity activity) {
        }

        @Override
        public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

        }

        @Override
        public void onActivityDestroyed(Activity activity) {
            CommonUtils.removeActivity(activity);
            CommonUtils.log().i("destroyed : " + activity.getClass().getSimpleName());
        }
    }
}
