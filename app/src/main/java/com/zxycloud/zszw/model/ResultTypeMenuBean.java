package com.zxycloud.zszw.model;

import com.zxycloud.common.base.BaseBean;

import java.util.List;

public class ResultTypeMenuBean extends BaseBean {
    private List<DataBean> data;

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * id : 1
         * patrolItemTypeName : 消防巡查
         */

        private int id;
        private String patrolItemTypeName;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getPatrolItemTypeName() {
            return patrolItemTypeName;
        }

        public void setPatrolItemTypeName(String patrolItemTypeName) {
            this.patrolItemTypeName = patrolItemTypeName;
        }
    }
}
