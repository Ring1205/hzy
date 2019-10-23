package com.zxycloud.zszw.model.bean;

import com.zxycloud.common.base.BaseBean;

public class TaskBean extends BaseBean {
    /**
     * id : 92a5e7d1ee0e44528deadbbfba9081df
     * patrolTaskName : 10天巡检计划
     * patrolState : 4
     * patrolStateName : 未完成
     * startDate : 2019-04-05
     * endDate : 2019-04-05
     * startTime : 2019-04-05
     * endTime : 2019-04-05
     */

    private String id;
    private String patrolTaskName;
    private int patrolState;
    private String patrolStateName;
    private String startDate;
    private String endDate;
    private String startTime;
    private String endTime;

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

    public int getPatrolState() {
        return patrolState;
    }

    public void setPatrolState(int patrolState) {
        this.patrolState = patrolState;
    }

    public String getPatrolStateName() {
        return patrolStateName;
    }

    public void setPatrolStateName(String patrolStateName) {
        this.patrolStateName = patrolStateName;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
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
}
