package com.zxycloud.common.utils;

import android.support.annotation.IntDef;
import android.util.SparseArray;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @author leiming
 * @date 2019/1/16.
 */
public class TimeUpUtils {
    public static final int TIME_UP_CLICK = 55;
    public static final int TIME_UP_JUMP = 56;
    private SparseArray<Long> lastSaveTimes;

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({TIME_UP_CLICK, TIME_UP_JUMP})
    @interface TimeUpType {
    }

    /**
     * 实践间隔判断（默认500ms时间间隔）
     *
     * @param type      类型
     * @param judgeTime 当前时间
     * @return 是否在间隔时间内
     */
    public boolean isTimeUp(@TimeUpType int type, long judgeTime) {
        return isTimeUp(type, judgeTime, 500L);
    }

    /**
     * 实践间隔判断
     *
     * @param type         类型
     * @param judgeTime    当前时间
     * @param intervalTime 间隔时间
     * @return 是否在间隔时间内
     */
    public boolean isTimeUp(@TimeUpType int type, long judgeTime, long intervalTime) {
        if (CommonUtils.isEmpty(lastSaveTimes)) {
            lastSaveTimes = new SparseArray<>();
        }
        Long tempTime = lastSaveTimes.get(type);
        if (CommonUtils.isEmpty(tempTime)) {
            lastSaveTimes.put(type, judgeTime);
            return true;
        } else {
            if (judgeTime - tempTime > intervalTime) {
                lastSaveTimes.put(type, judgeTime);
                return true;
            } else {
                return false;
            }
        }
    }

    public boolean isTimeDown(@TimeUpType int type, long intervalTime) {
        long judgeTime = System.currentTimeMillis();
        if (CommonUtils.isEmpty(lastSaveTimes)) {
            lastSaveTimes = new SparseArray<>();
        }
        Long tempTime = lastSaveTimes.get(type);
        lastSaveTimes.put(type, judgeTime);
        if (CommonUtils.isEmpty(tempTime)) {
            return true;
        } else {
            CommonUtils.log().i("点击事件间隔" + (judgeTime - tempTime));
            if (judgeTime - tempTime > intervalTime) {
                return true;
            } else {
                return false;
            }
        }
    }

}
