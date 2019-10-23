package com.zxycloud.zszw.model;

import com.zxycloud.common.base.BaseBean;

public class ResultAlarmBean extends BaseBean {
    /**
     * data : {"placeCount":45,"alartCount":1,"warnCount":1,"errorCount":1}
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
         * placeCount : 45
         * alartCount : 1
         * warnCount : 1
         * errorCount : 1
         */

        private int placeCount;
        private int alartCount;
        private int warnCount;
        private int errorCount;
        /**
         * onLineCount : 1
         * offLineCount : 6
         */

        private int onLineCount;
        private int offLineCount;

        public int getOnLineCount() {
            return onLineCount;
        }

        public void setOnLineCount(int onLineCount) {
            this.onLineCount = onLineCount;
        }

        public int getOffLineCount() {
            return offLineCount;
        }

        public void setOffLineCount(int offLineCount) {
            this.offLineCount = offLineCount;
        }

        public int getPlaceCount() {
            return placeCount;
        }

        public void setPlaceCount(int placeCount) {
            this.placeCount = placeCount;
        }

        public int getAlartCount() {
            return alartCount;
        }

        public void setAlartCount(int alartCount) {
            this.alartCount = alartCount;
        }

        public int getWarnCount() {
            return warnCount;
        }

        public void setWarnCount(int warnCount) {
            this.warnCount = warnCount;
        }

        public int getErrorCount() {
            return errorCount;
        }

        public void setErrorCount(int errorCount) {
            this.errorCount = errorCount;
        }
    }

}
