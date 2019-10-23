package com.zxycloud.zszw.model;

import com.zxycloud.common.base.BaseBean;

import java.util.List;

public class ResultUnitMetadataNoAllListBean extends BaseBean {
    /**
     * data : [{"id":"3e9e61bd-1ace-11e9-85f7-000c29c693b1","unitCode":1,"unitName":"TX3252","i18nCode":"zh"},{"id":"3e9e61bd-1ace-11e9-85f7-000c29c693b1","unitCode":1,"unitName":"TX3252","i18nCode":"zh"},{"id":"3e9e61bd-1ace-11e9-85f7-000c29c693b1","unitCode":1,"unitName":"TX3252","i18nCode":"zh"},{"id":"3e9e61bd-1ace-11e9-85f7-000c29c693b1","unitCode":1,"unitName":"TX3252","i18nCode":"zh"},{"id":"3e9e61bd-1ace-11e9-85f7-000c29c693b1","unitCode":1,"unitName":"TX3252","i18nCode":"zh"},{"id":"3e9e61bd-1ace-11e9-85f7-000c29c693b1","unitCode":1,"unitName":"TX3252","i18nCode":"zh"},{"id":"3e9e61bd-1ace-11e9-85f7-000c29c693b1","unitCode":1,"unitName":"TX3252","i18nCode":"zh"},{"id":"3e9e61bd-1ace-11e9-85f7-000c29c693b1","unitCode":1,"unitName":"TX3252","i18nCode":"zh"},{"id":"3e9e61bd-1ace-11e9-85f7-000c29c693b1","unitCode":1,"unitName":"TX3252","i18nCode":"zh"},{"id":"3e9e61bd-1ace-11e9-85f7-000c29c693b1","unitCode":1,"unitName":"TX3252","i18nCode":"zh"},{"id":"3e9e61bd-1ace-11e9-85f7-000c29c693b1","unitCode":1,"unitName":"TX3252","i18nCode":"zh"},{"id":"3e9e61bd-1ace-11e9-85f7-000c29c693b1","unitCode":1,"unitName":"TX3252","i18nCode":"zh"}]
     * pageIndex : 1
     * pageSize : 20
     */

    private int pageIndex;
    private int pageSize;
    private List<DataBean> data;

    public int getPageIndex() {
        return pageIndex;
    }

    public void setPageIndex(int pageIndex) {
        this.pageIndex = pageIndex;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * id : 3e9e61bd-1ace-11e9-85f7-000c29c693b1
         * unitCode : 1
         * unitName : TX3252
         * i18nCode : zh
         */

        private String id;
        private int unitCode;
        private String unitName;
        private String i18nCode;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public int getUnitCode() {
            return unitCode;
        }

        public void setUnitCode(int unitCode) {
            this.unitCode = unitCode;
        }

        public String getUnitName() {
            return unitName;
        }

        public void setUnitName(String unitName) {
            this.unitName = unitName;
        }

        public String getI18nCode() {
            return i18nCode;
        }

        public void setI18nCode(String i18nCode) {
            this.i18nCode = i18nCode;
        }
    }
}
