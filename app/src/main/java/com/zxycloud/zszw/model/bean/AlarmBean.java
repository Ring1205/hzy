package com.zxycloud.zszw.model.bean;

import com.zxycloud.zszw.base.BaseDataBean;

import java.util.List;

/**
 * @author leiming
 * @date 2019/3/28.
 */
public class AlarmBean extends BaseDataBean {

    /**
     * recordId : 运行日志Id
     * projectName : 单位名称
     * areaName : 区域名称
     * placeName : 场所名称
     * receiveTime : 1
     * deviceFlag : 独立式、复合式网关、普通设备等，用于判断进入设备详情页时进入网关的还是设备的
     * deviceInstallLocation : 设备安装位置
     * deviceId : 设备Id
     * gcj02Latitude : 高德维度
     * gcj02Longitude : 高德经度
     * wgs84Latitude : google维度
     * wgs84Longitude : google经度
     * stateGroupName : 状态组类型
     * processTypeName : 复核结果
     * processUserName : 复核人
     * processTime : 复核时间
     * isHasInstallationDetail : 0
     * isHasLayerPoint : 0
     * isHasCamera : 0
     * publicFireDetachments : [{"name":"消防支队名称","address":"消防支队地址","gcj02Latitude":"高德维度","gcj02Longitude":"高德经度","wgs84Latitude":"google维度","wgs84Longitude":"google经度"}]
     * publicFireFightingAndRescues : [{"name":"灭火救援站名称","address":"灭火救援站地址","gcj02Latitude":"高德维度","gcj02Longitude":"高德经度","wgs84Latitude":"google维度","wgs84Longitude":"google经度"}]
     * publicMedicalStations : [{"name":"医疗服务站名称","address":"医疗服务站地址","gcj02Latitude":"高德维度","gcj02Longitude":"高德经度","wgs84Latitude":"google维度","wgs84Longitude":"google经度"}]
     * publicWaters : [{"name":"水源点名称","address":"水源点地址","gcj02Latitude":"高德维度","gcj02Longitude":"高德经度","wgs84Latitude":"google维度","wgs84Longitude":"google经度"}]
     * happenTime : 1
     */
    private String recordId;
    private String projectName;
    private String areaName;
    private String placeName;
    private long receiveTime;
    private String deviceFlag;
    private String deviceInstallLocation;
    private String deviceId;
    private String adapterName;
    private double gcj02Latitude;
    private double gcj02Longitude;
    private double wgs84Latitude;
    private double wgs84Longitude;
    private int stateGroupCode;
    private String stateGroupName;
    private String processTypeName;
    private String processUserName;
    private long processTime;
    private int isHasInstallationDetail;
    private int isHasLayerPoint;
    private int isHasCamera;
    private long happenTime;
    private int userDeviceTypeCode;
    private String userDeviceTypeName;
    private List<PublicFireDetachmentsBean> publicFireDetachments;
    private List<PublicFireFightingAndRescuesBean> publicFireFightingAndRescues;
    private List<PublicMedicalStationsBean> publicMedicalStations;
    private List<PublicWatersBean> publicWaters;

    public String getRecordId() {
        return formatUtils.getString(recordId);
    }

    public String getProjectName() {
        return formatUtils.getString(projectName);
    }

    public String getAreaName() {
        return formatUtils.getString(areaName);
    }

    public String getPlaceName() {
        return formatUtils.getString(placeName);
    }

    public long getReceiveTime() {
        return receiveTime;
    }

    public String getDeviceFlag() {
        return formatUtils.getString(deviceFlag);
    }

    public String getDeviceInstallLocation() {
        return formatUtils.getString(deviceInstallLocation);
    }

    public String getDeviceId() {
        return formatUtils.getString(deviceId);
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

    public String getStateGroupName() {
        return formatUtils.getString(stateGroupName);
    }

    public int getIsHasInstallationDetail() {
        return isHasInstallationDetail;
    }

    public int getIsHasLayerPoint() {
        return isHasLayerPoint;
    }

    public int getIsHasCamera() {
        return isHasCamera;
    }

    public long getHappenTime() {
        return happenTime;
    }

    public String getProcessTypeName() {
        return formatUtils.getString(processTypeName);
    }

    public String getProcessUserName() {
        return formatUtils.getString(processUserName);
    }

    public long getProcessTime() {
        return processTime;
    }

    public int getUserDeviceTypeCode() {
        return userDeviceTypeCode;
    }

    public String getUserDeviceTypeName() {
        return formatUtils.getString(userDeviceTypeName);
    }

    public List<PublicFireDetachmentsBean> getPublicFireDetachments() {
        return publicFireDetachments;
    }

    public List<PublicFireFightingAndRescuesBean> getPublicFireFightingAndRescues() {
        return publicFireFightingAndRescues;
    }

    public List<PublicMedicalStationsBean> getPublicMedicalStations() {
        return publicMedicalStations;
    }

    public List<PublicWatersBean> getPublicWaters() {
        return publicWaters;
    }

    public String getAdapterName() {
        return adapterName;
    }

    public int getStateGroupCode() {
        return stateGroupCode;
    }
}
