package com.zxycloud.zszw.model;

import com.zxycloud.zszw.model.bean.DeviceBean;
import com.zxycloud.common.base.BaseBean;

import java.util.List;

/**
 * @author leiming
 * @date 2019/3/20.
 */
public class ResultDeviceListBean extends BaseBean {
    private List<DeviceBean> data;

    public List<DeviceBean> getData() {
        return data;
    }
}
