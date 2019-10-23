package com.zxycloud.zszw.model.JpushBean;

import com.zxycloud.zszw.model.bean.AlarmBean;

/**
 * @author leiming
 * @date 2019/4/8.
 */
public class ResultJPushBean extends JPushBean {

    /**
     * alertContent : 翼之城演示项目 场所1 1970-01-01发生预警
     * mType : prefire
     * pushRecord : {"areaName":"翼之城第二区域","deviceFlag":1,"deviceId":"33f9a2bf-35e1-4fe3-9b0c-61e17fdb6989","deviceInstallLocation":"202","gcj02Latitude":39.969734,"gcj02Longitude":116.427844,"happenTime":0,"isHasInstallationDetail":0,"isHasLayerPoint":0,"placeName":"dc76e08c-b54d-4ed4-9ec7-d648a14e48c4","processTime":0,"processTypeName":"未复核","processUserName":"","projectName":"","publicFireDetachments":[],"publicFireFightingAndRescues":[],"publicMedicalStations":[],"publicWaters":[],"receiveTime":1554944492691,"recordId":"b356a21c6410487bb816ffae241532ce","stateGroupName":"预警","userDeviceTypeCode":78,"userDeviceTypeName":"点型光电感烟火灾探测器","wgs84Latitude":39.96834175,"wgs84Longitude":116.4216124}
     * url :
     */

    private String alertContent;
    private String mType;
    private AlarmBean pushRecord;
    private String url;

    public String getAlertContent() {
        return alertContent;
    }

    public void setAlertContent(String alertContent) {
        this.alertContent = alertContent;
    }

    public String getMType() {
        return mType;
    }

    public void setMType(String mType) {
        this.mType = mType;
    }

    public AlarmBean getPushRecord() {
        return pushRecord;
    }

    public void setPushRecord(AlarmBean pushRecord) {
        this.pushRecord = pushRecord;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
