package com.zxycloud.zszw.model.bean;

public class StatisticsOnlineAdapterBean {

    /**
     * adapterName : 01_NB_18091701010360001078
     * connectStatus : 1
     * lastConnectTime : 1554944493117
     * userDeviceTypeName : 点型光电感烟火灾探测器
     */

    private String adapterName;
    private int connectStatus;
    private long lastConnectTime;
    private String userDeviceTypeName;

    public String getAdapterName() {
        return adapterName;
    }

    public int getConnectStatus() {
        return connectStatus;
    }

    public long getLastConnectTime() {
        return lastConnectTime;
    }

    public String getUserDeviceTypeName() {
        return userDeviceTypeName;
    }

    public void setUserDeviceTypeName(String userDeviceTypeName) {
        this.userDeviceTypeName = userDeviceTypeName;
    }
}
