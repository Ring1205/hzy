package com.zxycloud.zszw.model;

import com.zxycloud.zszw.base.BaseDataBean;

public class ChannelBean extends BaseDataBean {

    /**
     * channelId : 4fd94031-c16e-43a4-9b21-6d44f3caba21
     * adapterName : TX3252_20190702100000211
     * channelNumber : 1
     * sensorTagCode : 0
     * sensorTagName :
     * deviceInstallLocation : aa
     * deviceStateGroupCode : 99
     * deviceStateGroupName : 正常
     * unit :
     * multiple : 0
     * collectValue : 0
     */

    private String channelId;
    private String adapterName;
    private int channelNumber;
    private int sensorTagCode;
    private String sensorTagName;
    private String deviceInstallLocation;
    private int deviceStateGroupCode;
    private String deviceStateGroupName;
    private String unit;
    private int multiple;
    private String collectValue;

    public String getChannelId() {
        return channelId;
    }

    public String getAdapterName() {
        return adapterName;
    }

    public int getChannelNumber() {
        return channelNumber;
    }

    public int getSensorTagCode() {
        return sensorTagCode;
    }

    public String getSensorTagName() {
        return sensorTagName;
    }

    public String getDeviceInstallLocation() {
        return deviceInstallLocation;
    }

    public int getDeviceStateGroupCode() {
        return deviceStateGroupCode;
    }

    public String getDeviceStateGroupName() {
        return deviceStateGroupName;
    }

    public String getUnit() {
        return unit;
    }

    public int getMultiple() {
        return multiple;
    }

    public String getCollectValue() {
        return collectValue;
    }
}
