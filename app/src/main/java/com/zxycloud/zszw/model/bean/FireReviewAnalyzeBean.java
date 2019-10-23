package com.zxycloud.zszw.model.bean;

/**
 * @author leiming
 * @date 2019/4/23.
 */
public class FireReviewAnalyzeBean {

    /**
     * id : 1d2b889f907d41888ec5c9a0d5c30142
     * systemCode : 11
     * deviceName : 点型光电感烟火灾探测器
     * deviceStateCode : 1
     * deviceStateName : 火警
     * processType : 2
     * processName : 火警误报
     * processUser : 翼之城演示
     * processTime : 1555204870140
     * deviceStateGroupCode : 1
     * deviceStateGroupName : 火警
     * projectId : b7f1b8a1-6242-47de-b271-865dea35ae2e
     * projectName : 翼之城演示项目
     * placeId : dc76e08c-b54d-4ed4-9ec7-d648a14e48c4
     * placeName : 场所1
     * placeLinkMan : (13146649262)
     * deviceId : 33f9a2bf-35e1-4fe3-9b0c-61e17fdb6989
     * deviceTypeCode : 78
     * deviceInstallLocation : 202
     * adapterId : 33f9a2bf-35e1-4fe3-9b0c-61e17fdb6989
     * adapterName : 01_NB_18091701010360001078
     * happenTime : 0
     * receiveTime : 1554875633852
     * collectInfo|3 : [{"value":17,"key":"温度"}]
     * gcj02Latitude : 39.969734
     * gcj02Longitude : 116.427844
     * wgs84Latitude : 39.96834175
     * wgs84Longitude : 116.4216124
     */

    private String id;
    private int systemCode;
    private String deviceName;
    private int deviceStateCode;
    private String deviceStateName;
    private int processType;
    private String processName;
    private String processUser;
    private long processTime;
    private int deviceStateGroupCode;
    private String deviceStateGroupName;
    private String projectId;
    private String projectName;
    private String placeId;
    private String placeName;
    private String placeLinkMan;
    private String deviceId;
    private int deviceTypeCode;
    private String deviceInstallLocation;
    private String adapterId;
    private String adapterName;
    private long happenTime;
    private long receiveTime;
    private double gcj02Latitude;
    private double gcj02Longitude;
    private double wgs84Latitude;
    private double wgs84Longitude;

    public String getId() {
        return id;
    }

    public int getSystemCode() {
        return systemCode;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public int getDeviceStateCode() {
        return deviceStateCode;
    }

    public String getDeviceStateName() {
        return deviceStateName;
    }

    public int getProcessType() {
        return processType;
    }

    public String getProcessName() {
        return processName;
    }

    public String getProcessUser() {
        return processUser;
    }

    public long getProcessTime() {
        return processTime;
    }

    public int getDeviceStateGroupCode() {
        return deviceStateGroupCode;
    }

    public String getDeviceStateGroupName() {
        return deviceStateGroupName;
    }

    public String getProjectId() {
        return projectId;
    }

    public String getProjectName() {
        return projectName;
    }

    public String getPlaceId() {
        return placeId;
    }

    public String getPlaceName() {
        return placeName;
    }

    public String getPlaceLinkMan() {
        return placeLinkMan;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public int getDeviceTypeCode() {
        return deviceTypeCode;
    }

    public String getDeviceInstallLocation() {
        return deviceInstallLocation;
    }

    public String getAdapterId() {
        return adapterId;
    }

    public String getAdapterName() {
        return adapterName;
    }

    public long getHappenTime() {
        return happenTime;
    }

    public long getReceiveTime() {
        return receiveTime;
    }

    public double getGcj02Latitude() {
        return gcj02Latitude;
    }

    public double getGcj02Longitude() {
        return gcj02Longitude;
    }

    public double getWgs84Latitude() {
        return wgs84Latitude;
    }

    public double getWgs84Longitude() {
        return wgs84Longitude;
    }
}
