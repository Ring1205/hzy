package com.zxycloud.zszw.model;

import com.zxycloud.common.base.BaseBean;

import java.util.List;

public class ResultSensorTagBeans extends BaseBean {
    private List<DataBean> data;

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * id : 426077b8-336a-11e9-85f7-000c29c693b7
         * sensorTagCode : 30001
         * i18nCode : zh
         * sensorName : 存在探测
         * unit :
         * sortScort : 1
         */

        private String id;
        private int sensorTagCode;
        private String i18nCode;
        private String sensorName;
        private String unit;
        private int sortScort;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public int getSensorTagCode() {
            return sensorTagCode;
        }

        public void setSensorTagCode(int sensorTagCode) {
            this.sensorTagCode = sensorTagCode;
        }

        public String getI18nCode() {
            return i18nCode;
        }

        public void setI18nCode(String i18nCode) {
            this.i18nCode = i18nCode;
        }

        public String getSensorName() {
            return sensorName;
        }

        public void setSensorName(String sensorName) {
            this.sensorName = sensorName;
        }

        public String getUnit() {
            return unit;
        }

        public void setUnit(String unit) {
            this.unit = unit;
        }

        public int getSortScort() {
            return sortScort;
        }

        public void setSortScort(int sortScort) {
            this.sortScort = sortScort;
        }
    }
}
