package com.zxycloud.zszw.model;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @author leiming
 * @date 2019/3/19.
 */
public class HistoryTypeBean {
    public static final int TYPE_HISTORY_PLACE_DETAIL = 20;
    public static final int TYPE_HISTORY_PLACE_CHANGE = 21;
    public static final int TYPE_HISTORY_DEVICE_SYSTEM = 22;
    public static final int TYPE_HISTORY_DEVICE_PLACE = 23;
    public static final int TYPE_HISTORY_DEVICE_STATE = 24;
    public static final int TYPE_HISTORY_DEVICE_ADAPTER = 25;
    public static final int TYPE_HISTORY_AREA = 26;
    public static final int TYPE_HISTORY_RECORD = 27;
    public static final int TYPE_REAL_TIME_RECORD = 28;
    public static final int TYPE_HISTORY_PROJECT_CHANGE_ADD_ALL = 29;
    public static final int TYPE_HISTORY_PROJECT_CHANGE = 30;
    public static final int TYPE_HISTORY_PROJECT_RESULT = 31;
    public static final int TYPE_HISTORY_PROJECT_RESULT_ALL = 32;
    public static final int TYPE_HISTORY_PROJECT_DETAIL = 33;
    public static final int TYPE_HISTORY_RISK = 34;
    public static final int TYPE_HISTORY_RISK_DISTRIBUTION = 35;

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({TYPE_HISTORY_PLACE_DETAIL
            , TYPE_HISTORY_PLACE_CHANGE
            , TYPE_HISTORY_DEVICE_SYSTEM
            , TYPE_HISTORY_DEVICE_PLACE
            , TYPE_HISTORY_DEVICE_STATE
            , TYPE_HISTORY_DEVICE_ADAPTER
            , TYPE_HISTORY_AREA
            , TYPE_HISTORY_RECORD
            , TYPE_REAL_TIME_RECORD
            , TYPE_HISTORY_PROJECT_CHANGE_ADD_ALL
            , TYPE_HISTORY_PROJECT_CHANGE
            , TYPE_HISTORY_PROJECT_RESULT
            , TYPE_HISTORY_PROJECT_RESULT_ALL
            , TYPE_HISTORY_RISK
            , TYPE_HISTORY_PROJECT_DETAIL
            , TYPE_HISTORY_RISK_DISTRIBUTION})
    public @interface HistoryType {
    }
}
