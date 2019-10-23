package com.zxycloud.zszw.model.bean;

import android.text.TextUtils;

import com.zxycloud.zszw.base.BaseDataBean;

/**
 * @author leiming
 * @date 2019/3/25.
 */
public class VideoBean extends BaseDataBean {

    /**
     * videoId :
     * videoName :
     * videoImg :
     */
    private String videoId;
    private String videoName;
    private String videoImg;
    private String videoPath;
    private String sourceAddr;

    public String getVideoId() {
        return videoId;
    }

    public String getVideoName() {
        return videoName;
    }

    public boolean hasVideoImg() {
        return ! TextUtils.isEmpty(videoImg);
    }

    public String getVideoImg() {
        return videoImg;
    }

    public String getVideoPath() {
        return videoPath;
    }

    public String getSourceAddr() {
        return sourceAddr;
    }
}
