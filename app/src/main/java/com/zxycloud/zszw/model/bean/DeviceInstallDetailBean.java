package com.zxycloud.zszw.model.bean;

import com.zxycloud.zszw.base.BaseDataBean;

import java.util.List;

/**
 * @author leiming
 * @date 2019/3/22.
 */
public class DeviceInstallDetailBean extends BaseDataBean {
    /**
     * description :
     * videoUrl : http////
     * voiceUrl : http////
     * imgUrl : ["http////","http////","http////"]
     */

    private String description;
    private String videoUrl;
    private String voiceUrl;
    private List<String> imgUrl;

    public String getDescription() {
        return formatUtils.getString(description);
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public String getVoiceUrl() {
        return voiceUrl;
    }

    public List<String> getImgUrl() {
        return imgUrl;
    }
}