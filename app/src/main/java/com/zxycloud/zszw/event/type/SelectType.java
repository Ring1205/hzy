package com.zxycloud.zszw.event.type;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @author leiming
 * @date 2019/3/28.
 */
public class SelectType {
    public static final int AREA_TYPE = 1;
    public static final int PLACE_TYPE = 2;

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({AREA_TYPE, PLACE_TYPE})
    public @interface addCode {
    }
}
