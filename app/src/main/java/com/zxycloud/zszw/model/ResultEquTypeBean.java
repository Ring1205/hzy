package com.zxycloud.zszw.model;

import com.zxycloud.common.base.BaseBean;

import java.util.List;

public class ResultEquTypeBean extends BaseBean {
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
         * equTypeName : 防火门
         */

        private int id;
        private int patrolType;
        private String equTypeName;

        public int getPatrolType() {
            return patrolType;
        }

        public void setPatrolType(int patrolType) {
            this.patrolType = patrolType;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getEquTypeName() {
            return equTypeName;
        }

        public void setEquTypeName(String equTypeName) {
            this.equTypeName = equTypeName;
        }
    }
}
