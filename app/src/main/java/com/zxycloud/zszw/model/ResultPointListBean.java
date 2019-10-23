package com.zxycloud.zszw.model;

import com.zxycloud.common.base.BaseBean;

import java.util.List;

public class ResultPointListBean extends BaseBean {
    /**
     * data : [{"id":"fneeef325a614f99a0d37623940478f1","patrolPointName":"巡查点饭店","itemCount":0,"address":"前门大街门口"},{"id":"fneeef325a614f99a0d37623940478f1","patrolPointName":"巡查点饭店","itemCount":0,"address":"前门大街门口"},{"id":"fneeef325a614f99a0d37623940478f1","patrolPointName":"巡查点饭店","itemCount":0,"address":"前门大街门口"},{"id":"fneeef325a614f99a0d37623940478f1","patrolPointName":"巡查点饭店","itemCount":0,"address":"前门大街门口"},{"id":"fneeef325a614f99a0d37623940478f1","patrolPointName":"巡查点饭店","itemCount":0,"address":"前门大街门口"}]
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
         * id : fneeef325a614f99a0d37623940478f1
         * patrolPointName : 巡查点饭店
         * itemCount : 0
         * address : 前门大街门口
         */

        private String id;
        private String patrolPointName;
        private int itemCount;
        private String address;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getPatrolPointName() {
            return patrolPointName;
        }

        public void setPatrolPointName(String patrolPointName) {
            this.patrolPointName = patrolPointName;
        }

        public int getItemCount() {
            return itemCount;
        }

        public void setItemCount(int itemCount) {
            this.itemCount = itemCount;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }
    }
}
