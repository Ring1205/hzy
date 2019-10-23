package com.zxycloud.zszw.model.bean;

import com.zxycloud.common.base.BaseBean;

import java.util.List;

public class PatrolBean extends BaseBean {
    /**
     * id : 31d4d29fca01443daf0c1ad42130101b
     * patrolTaskName : 10天巡检计划
     * startTime : 2019-04-09
     * endTime : 2019-04-09
     * taskPointVOList : [{"id":"e8387202f69a47e8819a9d635237593e","resultStateName":"未执行","patrolPointName":"巡查点望京","tagNumber":"taga111222333444","address":"北京市故宫五角大楼4号楼","resultState":"1","deviceCount":1,"patrolItemTypeName":""},{"id":"e8387202f69a47e8819a9d635237593e","resultStateName":"未执行","patrolPointName":"巡查点望京","tagNumber":"taga111222333444","address":"北京市故宫五角大楼4号楼","resultState":"1","deviceCount":1,"patrolItemTypeName":""}]
     * progress : 1/2
     * planUserList : [{"userId":"","userAccount":"","userName":""}]
     */

    private String id;
    private String patrolTaskName;
    private String startTime;
    private String endTime;
    private String progress;
    private List<TaskPointVOListBean> taskPointVOList;
    private List<PlanUserListBean> planUserList;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPatrolTaskName() {
        return patrolTaskName;
    }

    public void setPatrolTaskName(String patrolTaskName) {
        this.patrolTaskName = patrolTaskName;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getProgress() {
        return progress;
    }

    public void setProgress(String progress) {
        this.progress = progress;
    }

    public List<TaskPointVOListBean> getTaskPointVOList() {
        return taskPointVOList;
    }

    public void setTaskPointVOList(List<TaskPointVOListBean> taskPointVOList) {
        this.taskPointVOList = taskPointVOList;
    }

    public List<PlanUserListBean> getPlanUserList() {
        return planUserList;
    }

    public void setPlanUserList(List<PlanUserListBean> planUserList) {
        this.planUserList = planUserList;
    }

    public static class TaskPointVOListBean {
        /**
         * id : e8387202f69a47e8819a9d635237593e
         * resultStateName : 未执行
         * patrolPointName : 巡查点望京
         * tagNumber : taga111222333444
         * address : 北京市故宫五角大楼4号楼
         * resultState : 1
         * deviceCount : 1
         * patrolItemTypeName :
         */

        private String id;
        private String resultStateName;
        private String patrolPointName;
        private String tagNumber;
        private String address;
        private String resultState;
        private int deviceCount;
        private String patrolItemTypeName;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getResultStateName() {
            return resultStateName;
        }

        public void setResultStateName(String resultStateName) {
            this.resultStateName = resultStateName;
        }

        public String getPatrolPointName() {
            return patrolPointName;
        }

        public void setPatrolPointName(String patrolPointName) {
            this.patrolPointName = patrolPointName;
        }

        public String getTagNumber() {
            return tagNumber;
        }

        public void setTagNumber(String tagNumber) {
            this.tagNumber = tagNumber;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public String getResultState() {
            return resultState;
        }

        public void setResultState(String resultState) {
            this.resultState = resultState;
        }

        public int getDeviceCount() {
            return deviceCount;
        }

        public void setDeviceCount(int deviceCount) {
            this.deviceCount = deviceCount;
        }

        public String getPatrolItemTypeName() {
            return patrolItemTypeName;
        }

        public void setPatrolItemTypeName(String patrolItemTypeName) {
            this.patrolItemTypeName = patrolItemTypeName;
        }
    }

    public static class PlanUserListBean {
        /**
         * userId :
         * userAccount :
         * userName :
         */

        private String userId;
        private String userAccount;
        private String userName;

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

        public String getUserAccount() {
            return userAccount;
        }

        public void setUserAccount(String userAccount) {
            this.userAccount = userAccount;
        }

        public String getUserName() {
            return userName;
        }

        public void setUserName(String userName) {
            this.userName = userName;
        }
    }
}
