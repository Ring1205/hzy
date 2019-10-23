package com.zxycloud.zszw.event.type;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @author leiming
 * @date 2019/3/26.
 */
public class TasksPagerType {
    public static final int TASK_ALL = 123;// 全部任务
    public static final int TASK_DNS = 0;// 未开始任务
    public static final int TASK_DOING = 1;// 执行中任务
    public static final int TASK_DONE = 2;// 已完成任务
    public static final int TASK_STALE = 3;// 已过期任务

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({TASK_ALL, TASK_DNS, TASK_DOING, TASK_DONE, TASK_STALE})
    public @interface showType {
    }
}
