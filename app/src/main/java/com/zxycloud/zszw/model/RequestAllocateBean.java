package com.zxycloud.zszw.model;

import com.zxycloud.zszw.model.bean.AllocateBean;

import java.util.List;

public class RequestAllocateBean {
    private int flag;
    private List<AllocateBean> deviceDistributions;

    public void setFlag(int flag) {
        this.flag = flag;
    }

    public void setDeviceDistributions(List<AllocateBean> deviceDistributions) {
        this.deviceDistributions = deviceDistributions;
    }
}
