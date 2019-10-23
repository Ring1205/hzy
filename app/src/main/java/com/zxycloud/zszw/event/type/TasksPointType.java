package com.zxycloud.zszw.event.type;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @author leiming
 * @date 2019/3/26.
 */
public class TasksPointType {
    public static final int POINT_ALL = 123;// 全部
    public static final int POINT_NON = 0;// 未执行
    public static final int POINT_NORMAL = 1;// 正常
    public static final int POINT_ABNORMAL = 2;// 异常

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({POINT_ALL, POINT_NON, POINT_NORMAL, POINT_ABNORMAL})
    public @interface showType {
    }
}
