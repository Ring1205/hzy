package com.zxycloud.zszw.model;

import java.util.List;

public class RequestTaskSubmitBean {
    private String id;
    private String des;
    private String videoUrl;
    private String voiceUrl;
    private List<String> imgUrls;
    private List<DataBean> taskItemList;

    public String getDes() {
        return des;
    }

    public void setDes(String des) {
        this.des = des;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public String getVoiceUrl() {
        return voiceUrl;
    }

    public void setVoiceUrl(String voiceUrl) {
        this.voiceUrl = voiceUrl;
    }

    public List<String> getImgUrls() {
        return imgUrls;
    }

    public void setImgUrls(List<String> imgUrls) {
        this.imgUrls = imgUrls;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<DataBean> getDataBeans() {
        return taskItemList;
    }

    public void setDataBeans(List<DataBean> dataBeans) {
        this.taskItemList = dataBeans;
    }

    public boolean hasAbnormal(){
        if (taskItemList != null)
            for (DataBean dataBean : taskItemList) {
                if (dataBean.getResultState() != 1)
                    return true;
            }
        return false;
    }
    public boolean hasUnselected(){
        if (taskItemList != null)
            for (DataBean dataBean : taskItemList) {
                if (dataBean.getResultState() == 0)
                    return true;
            }
        return false;
    }

    public static class DataBean {

        private String id;
        private int resultState;

        public DataBean(String id, int resultState) {
            this.id = id;
            this.resultState = resultState;
        }

        public int getResultState() {
            return resultState;
        }

        public void setResultState(int resultState) {
            this.resultState = resultState;
        }

        public String getId() {

            return id;
        }

        public void setId(String id) {
            this.id = id;
        }
    }
}
