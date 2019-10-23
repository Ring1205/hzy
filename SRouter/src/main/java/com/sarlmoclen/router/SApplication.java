package com.sarlmoclen.router;

import android.app.Application;
import android.content.res.Configuration;
import android.support.multidex.MultiDexApplication;

/**
 * Created by sarlmoclen on 2017/5/24.
 */


public abstract class SApplication extends MultiDexApplication {

    @Override
    public void onCreate() {
        super.onCreate();
        registerAction();
    }

    public abstract void registerAction();

    @Override
    public void onTerminate() {
        super.onTerminate();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

}
