package com.zxycloud.zszw.model.request;

import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author leiming
 * @date 2019/4/12.
 */
public class RequestRiskReportBean {
    public static final int SOURCE_CODE_PATROL = 1;
    public static final int SOURCE_CODE_SOCIAL = 2;
    public static final int SOURCE_CODE_MONITOR = 3;

    /**
     * hiddenLevel : 2
     * imgUrls : ["http://192.168.32.106:8000/static/images/upload/2019-04-17/e00ad4ce-3666-42a5-a09f-36cd9b0b3b26.jpg","http://192.168.32.106:8000/static/images/upload/2019-04-16/c3bcea81-b0e7-4aff-88cf-7d4e19dfeb9e.png"]
     * projectId : cf353a54ef7d421492bd12521a6ca448
     * sourceCode : 3
     * title : 这是一个消防的危险
     * videoUrl : http://192.168.32.106:8000/static/images/upload/2019-04-16/693074ec-07d6-4155-bd08-62ef829a3972.mp4
     * voiceUrl : http://192.168.32.106:8000/static/images/upload/2019-04-16/3855dff9-71be-466b-b158-36bef2130727.mp3
     * description :
     * hiddenAddress :
     */

    private int hiddenLevel;
    private String projectId;
    private int sourceCode;
    private String title;
    private String videoUrl;
    private String voiceUrl;
    private String description;
    private String hiddenAddress;
    private List<String> imgUrls;

    public RequestRiskReportBean(int hiddenLevel, String projectId, int sourceCode, String title) {
        this.hiddenLevel = hiddenLevel;
        this.projectId = projectId;
        this.sourceCode = sourceCode;
        this.title = title;
        imgUrls = new ArrayList<>();
    }

    public void setVideoUrl(List<String> videoUrls) {
        videoUrl = videoUrls.size() > 0 ? videoUrls.get(0) : null;
    }

    public void setVoiceUrl(List<String> voiceUrls) {
        voiceUrl = voiceUrls.size() > 0 ? voiceUrls.get(0) : null;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setHiddenAddress(String hiddenAddress) {
        this.hiddenAddress = hiddenAddress;
    }

    public void setImgUrl(List<String> imgUrls) {
        this.imgUrls = imgUrls;
    }

    public void removeImgUrl(String imgUrl) {
        imgUrls.remove(imgUrl);
    }

    public boolean canReport() {
        return !(TextUtils.isEmpty(videoUrl) && TextUtils.isEmpty(voiceUrl) && imgUrls.size() == 0);
    }
}
