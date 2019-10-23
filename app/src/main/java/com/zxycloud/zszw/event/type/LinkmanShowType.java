package com.zxycloud.zszw.event.type;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @author leiming
 * @date 2019/3/26.
 */
public class LinkmanShowType {
    public static final int LINKMAN_TYPE_PLACE = 5;
    public static final int LINKMAN_TYPE_AREA = 6;

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({LINKMAN_TYPE_PLACE,
            LINKMAN_TYPE_AREA})
    public @interface showType {
    }
}
