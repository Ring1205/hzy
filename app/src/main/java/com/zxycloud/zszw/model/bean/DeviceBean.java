package com.zxycloud.zszw.model.bean;

import com.zxycloud.zszw.base.BaseDataBean;

/**
 * @author leiming
 * @date 2019/3/22.
 */
public class DeviceBean extends BaseDataBean {

    /**
     * deviceId                 : 设备Id
     * deviceFlag               : 设备标识（1独立设备/2非独立设备的网关/3控制器/4普通设备/5通道
     * deviceTypeCode           : 设备类型编码
     * deviceTypeName           : 设备类型名称
     * userDeviceTypeCode       : 1 用户自定义设备类型编码
     * userDeviceTypeName       : 用户自定义设备类型名称
     * deviceNumber             : 设备二次码
     * deviceInstallLocation    : 设备安装位置
     * adapterId                : 网关Id
     * adapterName              : 网关名称
     * deviceStateGroupCode     : 设备状态组状态编码
     * deviceStateGroupName     : 设备状态组状态名称
     * placeName                : 所属场所名称
     * deviceCommissionTime     : 设备投运日期
     * deviceUseEndTime         : 设备服务截止时间
     * isHasCamera              : 是否支持视频联动
     * signalSource             : 信号源(移动、联通、电信、Wifi）
     * connectStatus            : 是否在线 0:离线 1:在线
     * imei                     : 设备IMEI
     * placeId                  : 场所Id，用于获取点位平面图
     * isHasInstallationDetail  : 是否有安装详情（安装描述、图片、视频、语音）
     * isHasLayerPoint          : 是否支持设备平面图点位
     * deviceSystemName         : 设备所属系统的名称
     * deviceSystemCode         : 设备所属系统的编码
     * deviceCount              : 当设备为组合式网关时，网关下的设备数量
     * channelCount             : 设备下是否可以添加通道，-1不能添加； >-1则能添加,并且返回通道数量
     * deviceUnitTypeName       : 设备型号
     * isAdded                  : 是否已经分配 0:未分配 1:已经分配
     */

    private String deviceId;
    private int deviceFlag;
    private int deviceTypeCode;
    private String deviceTypeName;
    private int userDeviceTypeCode;
    private String userDeviceTypeName;
    private String deviceNumber;
    private String deviceInstallLocation;
    private String adapterId;
    private String adapterName;
    private int deviceStateGroupCode;
    private String deviceStateGroupName;
    private String placeName;
    private long deviceCommissionTime;
    private long deviceUseEndTime;
    private int isHasCamera;
    private String signalSource;
    private int connectStatus;
    private String imei;
    private String placeId;
    private int isHasInstallationDetail;
    private int isHasLayerPoint;
    private String deviceSystemName;
    private int deviceSystemCode;
    private int deviceCount;
    private int isAdded;
    private int subDeviceStateGroupCode;
    private String subDeviceStateGroupName;
    private int channelCount;
    private String deviceUnitTypeName;

    public String getDeviceId() {
        return deviceId;
    }

    public int getDeviceFlag() {
        return deviceFlag;
    }

    public int getDeviceTypeCode() {
        return deviceTypeCode;
    }

    public String getDeviceTypeName() {
        return deviceTypeName;
    }

    public int getUserDeviceTypeCode() {
        return userDeviceTypeCode;
    }

    public String getUserDeviceTypeName() {
        return userDeviceTypeName;
    }

    public String getDeviceNumber() {
        return deviceNumber;
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

    public String getImei() {
        return imei;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }

    public int getDeviceStateGroupCode() {
        return deviceStateGroupCode;
    }

    public String getDeviceStateGroupName() {
        return deviceStateGroupName;
    }

    public String getPlaceName() {
        return placeName;
    }

    public long getDeviceCommissionTime() {
        return deviceCommissionTime;
    }

    public long getDeviceUseEndTime() {
        return deviceUseEndTime;
    }

    public int isHasCamera() {
        return isHasCamera;
    }

    public String getSignalSource() {
        return signalSource;
    }

    public int getConnectStatus() {
        return connectStatus;
    }

    public String getPlaceId() {
        return placeId;
    }

    public int isHasInstallationDetail() {
        return isHasInstallationDetail;
    }

    public int isHasLayerPoint() {
        return isHasLayerPoint;
    }

    public String getDeviceSystemName() {
        return deviceSystemName;
    }

    public int getDeviceSystemCode() {
        return deviceSystemCode;
    }

    public int getDeviceCount() {
        return deviceCount;
    }

    public int getSubDeviceStateGroupCode() {
        return subDeviceStateGroupCode;
    }

    public String getSubDeviceStateGroupName() {
        return subDeviceStateGroupName;
    }

    private boolean isCheck;

    public boolean isCheck() {
        return isCheck;
    }

    public void setCheck(Boolean check) {
        isCheck = check;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public void setDeviceFlag(int deviceFlag) {
        this.deviceFlag = deviceFlag;
    }

    public void setDeviceTypeCode(int deviceTypeCode) {
        this.deviceTypeCode = deviceTypeCode;
    }

    public void setDeviceTypeName(String deviceTypeName) {
        this.deviceTypeName = deviceTypeName;
    }

    public void setUserDeviceTypeCode(int userDeviceTypeCode) {
        this.userDeviceTypeCode = userDeviceTypeCode;
    }

    public void setUserDeviceTypeName(String userDeviceTypeName) {
        this.userDeviceTypeName = userDeviceTypeName;
    }

    public void setDeviceNumber(String deviceNumber) {
        this.deviceNumber = deviceNumber;
    }

    public void setDeviceInstallLocation(String deviceInstallLocation) {
        this.deviceInstallLocation = deviceInstallLocation;
    }

    public void setAdapterId(String adapterId) {
        this.adapterId = adapterId;
    }

    public void setAdapterName(String adapterName) {
        this.adapterName = adapterName;
    }

    public void setDeviceStateGroupCode(int deviceStateGroupCode) {
        this.deviceStateGroupCode = deviceStateGroupCode;
    }

    public void setDeviceStateGroupName(String deviceStateGroupName) {
        this.deviceStateGroupName = deviceStateGroupName;
    }

    public void setPlaceName(String placeName) {
        this.placeName = placeName;
    }

    public void setDeviceCommissionTime(long deviceCommissionTime) {
        this.deviceCommissionTime = deviceCommissionTime;
    }

    public void setDeviceUseEndTime(long deviceUseEndTime) {
        this.deviceUseEndTime = deviceUseEndTime;
    }

    public int getIsHasCamera() {
        return isHasCamera;
    }

    public void setIsHasCamera(int isHasCamera) {
        this.isHasCamera = isHasCamera;
    }

    public void setSignalSource(String signalSource) {
        this.signalSource = signalSource;
    }

    public void setConnectStatus(int connectStatus) {
        this.connectStatus = connectStatus;
    }

    public void setPlaceId(String placeId) {
        this.placeId = placeId;
    }

    public int getIsHasInstallationDetail() {
        return isHasInstallationDetail;
    }

    public void setIsHasInstallationDetail(int isHasInstallationDetail) {
        this.isHasInstallationDetail = isHasInstallationDetail;
    }

    public int getIsHasLayerPoint() {
        return isHasLayerPoint;
    }

    public void setIsHasLayerPoint(int isHasLayerPoint) {
        this.isHasLayerPoint = isHasLayerPoint;
    }

    public void setDeviceSystemName(String deviceSystemName) {
        this.deviceSystemName = deviceSystemName;
    }

    public void setDeviceSystemCode(int deviceSystemCode) {
        this.deviceSystemCode = deviceSystemCode;
    }

    public void setDeviceCount(int deviceCount) {
        this.deviceCount = deviceCount;
    }

    public int getIsAdded() {
        return isAdded;
    }

    public void setIsAdded(int isAdded) {
        this.isAdded = isAdded;
    }

    public void setSubDeviceStateGroupCode(int subDeviceStateGroupCode) {
        this.subDeviceStateGroupCode = subDeviceStateGroupCode;
    }

    public void setSubDeviceStateGroupName(String subDeviceStateGroupName) {
        this.subDeviceStateGroupName = subDeviceStateGroupName;
    }

    public int getChannelCount() {
        return channelCount;
    }

    public void setChannelCount(int channelCount) {
        this.channelCount = channelCount;
    }

    public String getDeviceUnitTypeName() {
        return deviceUnitTypeName;
    }

    public void setDeviceUnitTypeName(String deviceUnitTypeName) {
        this.deviceUnitTypeName = deviceUnitTypeName;
    }

    public void setCheck(boolean check) {
        isCheck = check;
    }
}