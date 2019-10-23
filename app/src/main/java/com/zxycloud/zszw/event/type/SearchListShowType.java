package com.zxycloud.zszw.event.type;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @author leiming
 * @date 2019/3/21.
 */
public class SearchListShowType {
    public static final int SHOW_TYPE_CONTROLLER_OR_ADAPTER = 2;
    public static final int SHOW_TYPE_AREA_OF_AREA = 3;
    public static final int SHOW_TYPE_PLACE_OF_AREA = 4;

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({SHOW_TYPE_CONTROLLER_OR_ADAPTER,
            SHOW_TYPE_AREA_OF_AREA,
            SHOW_TYPE_PLACE_OF_AREA})
    public @interface showType {
    }
}
