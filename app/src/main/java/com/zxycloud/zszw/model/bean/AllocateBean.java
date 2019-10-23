package com.zxycloud.zszw.model.bean;

import java.util.ArrayList;
import java.util.List;

public class AllocateBean {
    private String deviceId;// 设备Id
    private String adapterName;// 网关名称
    private int deviceSystemCode;// 设备所属系统Code
    private int userDeviceTypeCode;// 选择设备类型Code
    private String deviceTypeName;// 设备类型名称 (回显用的)
    private String userDeviceTypeName;// 自定义设备类型名称
    private String placeId;// 所要分配的场所Id
    private String deviceInstallLocation;// 设备安装位置
    private Position position;// 点位图坐标
    private String deviceUnitTypeName;// 设备型号
    private String deviceSystemName;// 设备所属系统名称 (回显用的)

    public String getDeviceId() {
        return deviceId;
    }

    public String getAdapterName() {
        return adapterName;
    }

    public int getDeviceSystemCode() {
        return deviceSystemCode;
    }

    public String getUserDeviceTypeName() {
        return userDeviceTypeName;
    }

    public String getDeviceTypeName() {
        return deviceTypeName;
    }

    public void setDeviceTypeName(String deviceTypeName) {
        this.deviceTypeName = deviceTypeName;
    }

    public String getPlaceId() {
        return placeId;
    }

    public String getDeviceInstallLocation() {
        return deviceInstallLocation;
    }

    public Position getPosition() {
        return position;
    }

    public void setDeviceUnitTypeName(String deviceUnitType) {
        this.deviceUnitTypeName = deviceUnitType;
    }

    public String getDeviceSystemName() {
        return deviceSystemName;
    }

    public void setDeviceSystemName(String deviceSystemName) {
        this.deviceSystemName = deviceSystemName;
    }

    public int getUserDeviceTypeCode() {
        return userDeviceTypeCode;
    }

    public void setUserDeviceTypeCode(int userDeviceTypeCode) {
        this.userDeviceTypeCode = userDeviceTypeCode;
    }

    public String getDeviceUnitTypeName() {
        return deviceUnitTypeName;
    }

    public InstallationDetails getInstallationDetails() {
        return installationDetails;
    }

    public static class Position {
        private double layerImageX = -1;
        private double layerImageY = -1;

        public void setLayerImageX(double layerImageX) {
            this.layerImageX = layerImageX;
        }

        public void setLayerImageY(double layerImageY) {
            this.layerImageY = layerImageY;
        }

        public Position() {
        }

        public double getLayerImageX() {
            return layerImageX;
        }

        public double getLayerImageY() {
            return layerImageY;
        }
    }

    private InstallationDetails installationDetails;// 上传文件url对象

    public static class InstallationDetails {
        private String description;
        private String videoUrl;
        private String voiceUrl;
        private List<String> imgUrls;

        public void setDescription(String description) {
            this.description = description;
        }

        public void setVideoUrl(String videoUrl) {
            this.videoUrl = videoUrl;
        }

        public void setVoiceUrl(String voiceUrl) {
            this.voiceUrl = voiceUrl;
        }

        public void setImgUrls(List<String> imgUrls) {
            this.imgUrls = imgUrls;
        }
    }

    public AllocateBean() {
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public void setAdapterName(String adapterName) {
        this.adapterName = adapterName;
    }

    public void setDeviceSystemCode(int deviceSystemCode) {
        this.deviceSystemCode = deviceSystemCode;
    }

    public void setUserDeviceTypeName(String userDeviceTypeName) {
        this.userDeviceTypeName = userDeviceTypeName;
    }

    public void setPlaceId(String placeId) {
        this.placeId = placeId;
    }

    public void setDeviceInstallLocation(String deviceInstallLocation) {
        this.deviceInstallLocation = deviceInstallLocation;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    public void setInstallationDetails(InstallationDetails installationDetails) {
        this.installationDetails = installationDetails;
    }
}
