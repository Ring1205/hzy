package com.zxycloud.zszw.model.bean;

import com.zxycloud.common.base.BaseBean;

public class SystemBean extends BaseBean {
    /**
     * deviceSystemName : 设备系统名称
     * deviceSystemCode : 设备系统编码
     */

    private String deviceSystemName;
    private int deviceSystemCode;

    public String getDeviceSystemName() {
        return deviceSystemName;
    }

    public void setDeviceSystemName(String deviceSystemName) {
        this.deviceSystemName = deviceSystemName;
    }

    public int getDeviceSystemCode() {
        return deviceSystemCode;
    }

    public void setDeviceSystemCode(int deviceSystemCode) {
        this.deviceSystemCode = deviceSystemCode;
    }
}
