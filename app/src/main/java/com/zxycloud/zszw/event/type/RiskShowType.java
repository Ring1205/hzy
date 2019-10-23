package com.zxycloud.zszw.event.type;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @author leiming
 * @date 2019/3/21.
 */
public class RiskShowType {
    /**
     * 隐患列表类型
     * <p>
     * {@link RiskShowType#SHOW_TYPE_REPORT 监督上报}
     * {@link RiskShowType#SHOW_TYPE_RECORD 首页隐患}
     * {@link RiskShowType#SHOW_TYPE_TO_DO  服务-隐患-待办隐患}
     * {@link RiskShowType#SHOW_TYPE_DONE   服务-隐患-已办隐患}
     */
    public static final int SHOW_TYPE_REPORT = 2;
    public static final int SHOW_TYPE_RECORD = 3;
    public static final int SHOW_TYPE_TO_DO = 4;
    public static final int SHOW_TYPE_DONE = 5;

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({SHOW_TYPE_REPORT
            , SHOW_TYPE_RECORD
            , SHOW_TYPE_TO_DO
            , SHOW_TYPE_DONE})
    public @interface showType {
    }
}
