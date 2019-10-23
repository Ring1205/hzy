package com.zxycloud.zszw.event.type;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @author leiming
 * @date 2019/3/28.
 */
public class AlertShowType {
    public static final int ALERT_HAPPENING = 5;
    public static final int ALERT_HISTORY = 6;

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({ALERT_HAPPENING,
            ALERT_HISTORY})
    public @interface ShowType {
    }
}
