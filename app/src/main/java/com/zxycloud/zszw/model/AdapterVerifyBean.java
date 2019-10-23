package com.zxycloud.zszw.model;

import com.zxycloud.common.base.BaseBean;

public class AdapterVerifyBean extends BaseBean {
    private DataBean data;

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean extends BaseBean{
        /**
         * distributionState : 0:网关未激活,网关没有找到 1:权限错误 2:网关未分配 3:网关已经分配
         * deviceCount : 网关下设备数量（网关、控制器、通道、基本设备、通道的总数量）
         * adapterType : 网关类型（ 1、 网关+控制器（主机）+设备 2、 网关+设备 3、 独立网关 ）
         */

        private int distributionState;
        private int deviceCount;
        private int adapterType;
        private String deviceId;

        public String getDeviceId() {
            return deviceId;
        }

        public void setDeviceId(String deviceId) {
            this.deviceId = deviceId;
        }

        public int getDistributionState() {
            return distributionState;
        }

        public void setDistributionState(int distributionState) {
            this.distributionState = distributionState;
        }

        public int getAdapterType() {
            return adapterType;
        }

        public void setAdapterType(int adapterType) {
            this.adapterType = adapterType;
        }

        public int getDeviceCount() {
            return deviceCount;
        }

        public void setDeviceCount(int deviceCount) {
            this.deviceCount = deviceCount;
        }
    }
}
