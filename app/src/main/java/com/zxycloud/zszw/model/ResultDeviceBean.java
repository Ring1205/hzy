package com.zxycloud.zszw.model;

import com.zxycloud.zszw.model.bean.DeviceBean;
import com.zxycloud.common.base.BaseBean;

/**
 * @author leiming
 * @date 2019/3/20.
 */
public class ResultDeviceBean extends BaseBean {
    private DeviceBean data;

    public DeviceBean getData() {
        return data;
    }

    public void setData(DeviceBean data) {
        this.data = data;
    }
}
