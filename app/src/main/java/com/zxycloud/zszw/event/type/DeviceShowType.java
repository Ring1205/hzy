package com.zxycloud.zszw.event.type;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @author leiming
 * @date 2019/3/21.
 */
public class DeviceShowType {
    public static final int SHOW_TYPE_MAIN = 2;
    public static final int SHOW_TYPE_PLACE = 3;
    public static final int SHOW_TYPE_SYSTEM = 4;
    public static final int SHOW_TYPE_ADAPTER = 5;

    // 详情页下方显示，不要头
    // 场所详情页组合式网关列表
    public static final int SHOW_TYPE_ADAPTER_LIST = 6;
    // 场所详情页独立式探测器列表
    public static final int SHOW_TYPE_INDEPENDENT_DEVICE_LIST = 7;
    // 网关详情页网关下设备
    public static final int SHOW_TYPE_DEVICE_BELONG_ADAPTER = 8;

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({SHOW_TYPE_MAIN,
            SHOW_TYPE_PLACE,
            SHOW_TYPE_SYSTEM,
            SHOW_TYPE_ADAPTER,
            SHOW_TYPE_ADAPTER_LIST,
            SHOW_TYPE_INDEPENDENT_DEVICE_LIST,
            SHOW_TYPE_DEVICE_BELONG_ADAPTER})
    public @interface showType {
    }
}
