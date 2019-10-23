package com.zxycloud.zszw.model.request;

import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author leiming
 * @date 2019/4/12.
 */
public class RequestRiskProcessReportBean {
//    private final String PROCESS_STATE_NO_RECTIFICATION = "0";
//    private final String PROCESS_STATE_RECTIFICATION = "1";
//    private final String PROCESS_STATE_PENDING_TRIAL = "2";
//    private final String PROCESS_STATE_QUALIFIED = "3";
//    private final String PROCESS_STATE_UNQUALIFIED = "4";

    /**
     * description : 处理这个隐患需要无限手套我说第二次了
     * processResult : 2
     * hiddenId : 76f88f993e80426aaaab4b72bd95eb9e
     * imgUrls : ["http://192.168.32.106:8000/static/images/upload/2019-04-13/86bcf30f-4a1e-4eaf-8a61-1766c2db0ff8.png"]
     * videoUrl :
     * voiceUrl :
     */

    private String description;
    private String processResult;
    private String hiddenId;
    private String videoUrl;
    private String voiceUrl;
    private List<String> imgUrls;

    public RequestRiskProcessReportBean(String hiddenId, String processResult) {
        this.hiddenId = hiddenId;
        this.processResult = processResult;
        imgUrls = new ArrayList<>();
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setVideoUrl(List<String> videoUrls) {
        videoUrl = videoUrls.size() > 0 ? videoUrls.get(0) : null;
    }

    public void setVoiceUrl(List<String> voiceUrls) {
        voiceUrl = voiceUrls.size() > 0 ? voiceUrls.get(0) : null;
    }

    public void setImgUrls(List<String> imgUrls) {
        this.imgUrls = imgUrls;
    }

    public void setImgUrl(List<String> imgUrls) {
        this.imgUrls = imgUrls;
    }

    public boolean canReport(String processState) {
        if (processState.equals("2") || processState.equals("4"))
            return !(TextUtils.isEmpty(videoUrl) && TextUtils.isEmpty(voiceUrl) && imgUrls.size() == 0);
        else
            return true;
    }

    public void removeImgUrl(String imgUrl) {
        imgUrls.remove(imgUrl);
    }
}