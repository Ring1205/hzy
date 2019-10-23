package com.zxycloud.zszw.event.type;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @author leiming
 * @date 2019/3/28.
 */
public class VideoShowType {
    public static final int PLAY_TYPE_LINKAGE_DEVICE = 32;
    public static final int PLAY_TYPE_LINKAGE_CAMERA = 33;
    public static final int PLAY_TYPE_INSTALL_VIDEO = 34;

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({PLAY_TYPE_LINKAGE_DEVICE, PLAY_TYPE_LINKAGE_CAMERA, PLAY_TYPE_INSTALL_VIDEO})
    public @interface PlayType {
    }

}
