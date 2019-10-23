package com.zxycloud.zszw.event.type;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @author leiming
 * @date 2019/3/21.
 */
public class RecordShowType {
    public static final int SHOW_TYPE_STATISTICS = 2;
    public static final int SHOW_TYPE_SERVICE = 3;

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({SHOW_TYPE_STATISTICS,
            SHOW_TYPE_SERVICE})
    public @interface showType {
    }
}
