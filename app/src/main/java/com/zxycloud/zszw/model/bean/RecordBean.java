package com.zxycloud.zszw.model.bean;

import com.zxycloud.zszw.base.BaseDataBean;

/**
 * @author leiming
 * @date 2019/3/22.
 */
public class RecordBean extends BaseDataBean {
    public static final int RECORD_STATE_FIRE = 1;
    public static final int RECORD_STATE_PREFIRE = 2;
    /**
     * recordId                 : 运行日志Id
     * receiveTime              : 接受时间
     * deviceName               : 设备名称
     * stateGroupCode           : 状态组编码
     * stateGroupName           : 状态组名称
     * placeName                : 场所名称
     * AreaName                 : 区域名称
     * projectName              : 单位名称
     * deviceInstallLocation    : 设备安装位置
     * placeAdminName           : 管理员姓名
     */

    private long receiveTime;
    private String deviceName;
    private int stateGroupCode;
    private String stateGroupName;
    private String placeName;
    private String AreaName;
    private String projectName;
    private String deviceInstallLocation;
    private String placeAdminName;
    private String placeAdminPhoneNumber;
    private String recordId;
    private String stateName;
    private int stateCode;

    public long getReceiveTime() {
        return receiveTime;
    }

    public String getDeviceName() {
        return formatUtils.getString(deviceName);
    }

    public int getStateGroupCode() {
        return stateGroupCode;
    }

    public String getStateGroupName() {
        return formatUtils.getString(stateGroupName);
    }

    public int getStateCode() {
        return stateCode;
    }

    public String getStateName() {
        return stateName;
    }

    public String getPlaceName() {
        return formatUtils.getString(placeName);
    }

    public String getAreaName() {
        return formatUtils.getString(AreaName);
    }

    public String getProjectName() {
        return formatUtils.getString(projectName);
    }

    public String getDeviceInstallLocation() {
        return formatUtils.getString(deviceInstallLocation);
    }

    public String getPlaceAdminName() {
        return formatUtils.getString(placeAdminName);
    }

    public String getPlaceAdminPhoneNumber() {
        return formatUtils.getString(placeAdminPhoneNumber);
    }

    public String getRecordId() {
        return formatUtils.getString(recordId);
    }
}
