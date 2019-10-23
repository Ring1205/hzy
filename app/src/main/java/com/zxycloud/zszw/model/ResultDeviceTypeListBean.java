package com.zxycloud.zszw.model;

import com.zxycloud.common.base.BaseBean;

import java.util.List;

public class ResultDeviceTypeListBean extends BaseBean {
    private List<DataBean> data;

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * id : 23a90de5-6314-11e9-b3ee-000c29c693b7
         * deviceTypeCode : 213
         * i18nCode : zh
         * deviceTypeName : 三向地埋标志灯
         * abbreviation :
         */

        private String id;
        private int deviceTypeCode;
        private String i18nCode;
        private String deviceTypeName;
        private String abbreviation;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public int getDeviceTypeCode() {
            return deviceTypeCode;
        }

        public void setDeviceTypeCode(int deviceTypeCode) {
            this.deviceTypeCode = deviceTypeCode;
        }

        public String getI18nCode() {
            return i18nCode;
        }

        public void setI18nCode(String i18nCode) {
            this.i18nCode = i18nCode;
        }

        public String getDeviceTypeName() {
            return deviceTypeName;
        }

        public void setDeviceTypeName(String deviceTypeName) {
            this.deviceTypeName = deviceTypeName;
        }

        public String getAbbreviation() {
            return abbreviation;
        }

        public void setAbbreviation(String abbreviation) {
            this.abbreviation = abbreviation;
        }
    }
}
