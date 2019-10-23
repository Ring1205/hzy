package com.zxycloud.zszw.model;

import com.zxycloud.common.base.BaseBean;

import java.util.List;

public class ResultPointAreaListBean extends BaseBean {
    /**
     * data : [{"id":"1e98c6ea8d4b41b590154199e076a47f","areaName":"二级A01区","level":2},{"id":"1e98c6ea8d4b41b590154199e076a47f","areaName":"二级A01区","level":2},{"id":"1e98c6ea8d4b41b590154199e076a47f","areaName":"二级A01区","level":2},{"id":"1e98c6ea8d4b41b590154199e076a47f","areaName":"二级A01区","level":2}]
     * pageIndex : 1
     * pageSize : 5
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
         * id : 1e98c6ea8d4b41b590154199e076a47f
         * areaName : 二级A01区
         * level : 2
         */

        private String id;
        private String areaName;
        private int level;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getAreaName() {
            return areaName;
        }

        public void setAreaName(String areaName) {
            this.areaName = areaName;
        }

        public int getLevel() {
            return level;
        }

        public void setLevel(int level) {
            this.level = level;
        }
    }
}
