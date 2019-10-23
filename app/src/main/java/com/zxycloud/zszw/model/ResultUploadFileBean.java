package com.zxycloud.zszw.model;

import com.zxycloud.common.base.BaseBean;

public class ResultUploadFileBean extends BaseBean {
    /**
     * data : {"url":""}
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
         * url :
         */

        private String url;

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }
    }
}
