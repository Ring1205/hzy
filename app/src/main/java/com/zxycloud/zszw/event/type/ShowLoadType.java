package com.zxycloud.zszw.event.type;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @author leiming
 * @date 2019/3/21.
 */
public class ShowLoadType {
    public static final int SHOW_LOADING = 301;// 加载中
    public static final int SHOW_EMPTY = 302;  // 空数据
    public static final int SHOW_ERROR = 303;  // 网络错误
    public static final int SHOW_CONTENT = 304;// 请求成功
    @Retention(RetentionPolicy.SOURCE)
    @IntDef({SHOW_LOADING,
            SHOW_EMPTY,
            SHOW_ERROR,
            SHOW_CONTENT})
    public @interface showType {
    }
}
