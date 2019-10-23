package com.zxycloud.zszw.model;

import com.zxycloud.common.base.BaseBean;

public class ResultChannelDetailBeans extends BaseBean {
    /**
     * data : {"channelId":"02a44298-517f-4c28-81e7-619559679c4f","adapterName":"01_NB_18122701010100045113","channelNumber":3,"sensorTagCode":30002,"sensorTagName":"","deviceInstallLocation":"宿舍","deviceStateGroupCode":99,"deviceStateGroupName":"正常","unit":"℃","multiple":10,"collectValue":32}
     */

    private DataBean data;

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * channelId : 02a44298-517f-4c28-81e7-619559679c4f
         * adapterName : 01_NB_18122701010100045113
         * channelNumber : 3
         * sensorTagCode : 30002
         * sensorTagName :
         * deviceInstallLocation : 宿舍
         * deviceStateGroupCode : 99
         * deviceStateGroupName : 正常
         * unit : ℃
         * multiple : 10
         * collectValue : 32
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
        /**
         * maxRange : 400
         * minRange : 0
         * maxAlarm : 400
         * minAlarm : 0
         * collectValue : 2.273
         */

        private int maxRange;
        private int minRange;
        private int maxAlarm;
        private int minAlarm;
        private double collectValue;

        public String getChannelId() {
            return channelId;
        }

        public void setChannelId(String channelId) {
            this.channelId = channelId;
        }

        public String getAdapterName() {
            return adapterName;
        }

        public void setAdapterName(String adapterName) {
            this.adapterName = adapterName;
        }

        public int getChannelNumber() {
            return channelNumber;
        }

        public void setChannelNumber(int channelNumber) {
            this.channelNumber = channelNumber;
        }

        public int getSensorTagCode() {
            return sensorTagCode;
        }

        public void setSensorTagCode(int sensorTagCode) {
            this.sensorTagCode = sensorTagCode;
        }

        public String getSensorTagName() {
            return sensorTagName;
        }

        public void setSensorTagName(String sensorTagName) {
            this.sensorTagName = sensorTagName;
        }

        public String getDeviceInstallLocation() {
            return deviceInstallLocation;
        }

        public void setDeviceInstallLocation(String deviceInstallLocation) {
            this.deviceInstallLocation = deviceInstallLocation;
        }

        public int getDeviceStateGroupCode() {
            return deviceStateGroupCode;
        }

        public void setDeviceStateGroupCode(int deviceStateGroupCode) {
            this.deviceStateGroupCode = deviceStateGroupCode;
        }

        public String getDeviceStateGroupName() {
            return deviceStateGroupName;
        }

        public void setDeviceStateGroupName(String deviceStateGroupName) {
            this.deviceStateGroupName = deviceStateGroupName;
        }

        public String getUnit() {
            return unit;
        }

        public void setUnit(String unit) {
            this.unit = unit;
        }

        public int getMultiple() {
            return multiple;
        }

        public void setMultiple(int multiple) {
            this.multiple = multiple;
        }

        public int getMaxRange() {
            return maxRange;
        }

        public void setMaxRange(int maxRange) {
            this.maxRange = maxRange;
        }

        public int getMinRange() {
            return minRange;
        }

        public void setMinRange(int minRange) {
            this.minRange = minRange;
        }

        public int getMaxAlarm() {
            return maxAlarm;
        }

        public void setMaxAlarm(int maxAlarm) {
            this.maxAlarm = maxAlarm;
        }

        public int getMinAlarm() {
            return minAlarm;
        }

        public void setMinAlarm(int minAlarm) {
            this.minAlarm = minAlarm;
        }

        public double getCollectValue() {
            return collectValue;
        }

        public void setCollectValue(double collectValue) {
            this.collectValue = collectValue;
        }
    }
}
