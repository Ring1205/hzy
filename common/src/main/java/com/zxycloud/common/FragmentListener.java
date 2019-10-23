package com.zxycloud.common;

import android.support.annotation.IdRes;
import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @author leiming
 * @date 2019/2/26.
 */
public interface FragmentListener {
    /**
     * 展示Fragment的方式：Show/Hide
     */
    public static final int FRAGMENT_START_SHOW = 33;
    /**
     * 展示Fragment的方式：replace
     */
    public static final int FRAGMENT_START_REPLACE = 34;

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({FRAGMENT_START_SHOW, FRAGMENT_START_REPLACE})
    @interface FragmentStartType {
    }

    @IdRes
    int fragmentLayoutId();

    @FragmentStartType
    int fragmentStartType();
}
