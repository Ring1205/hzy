package com.zxycloud.zszw.event.type;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @author leiming
 * @date 2019/3/21.
 */
public class PlaceShowType {
    public static final int SHOW_TYPE_BACK = 2;
    public static final int SHOW_TYPE_MAIN = 3;

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({SHOW_TYPE_BACK,
            SHOW_TYPE_MAIN})
    public @interface showType {
    }
}
