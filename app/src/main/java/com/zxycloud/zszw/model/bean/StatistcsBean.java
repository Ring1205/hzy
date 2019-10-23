package com.zxycloud.zszw.model.bean;

import com.zxycloud.common.base.BaseBean;

public class StatistcsBean extends BaseBean {
    /**
     * patrolTaskCount : 32
     * patrolPointCount : 6
     * firstAreaCount : 18
     * placeCount : 9
     * devicesCount : 29
     * malfunctionCount : 6
     * userCount : 1
     */

    private int patrolTaskCount;
    private int patrolPointCount;
    private int firstAreaCount;
    private int placeCount;
    private int devicesCount;
    private int malfunctionCount;
    private int userCount;

    public int getPatrolTaskCount() {
        return patrolTaskCount;
    }

    public void setPatrolTaskCount(int patrolTaskCount) {
        this.patrolTaskCount = patrolTaskCount;
    }

    public int getPatrolPointCount() {
        return patrolPointCount;
    }

    public void setPatrolPointCount(int patrolPointCount) {
        this.patrolPointCount = patrolPointCount;
    }

    public int getFirstAreaCount() {
        return firstAreaCount;
    }

    public void setFirstAreaCount(int firstAreaCount) {
        this.firstAreaCount = firstAreaCount;
    }

    public int getPlaceCount() {
        return placeCount;
    }

    public void setPlaceCount(int placeCount) {
        this.placeCount = placeCount;
    }

    public int getDevicesCount() {
        return devicesCount;
    }

    public void setDevicesCount(int devicesCount) {
        this.devicesCount = devicesCount;
    }

    public int getMalfunctionCount() {
        return malfunctionCount;
    }

    public void setMalfunctionCount(int malfunctionCount) {
        this.malfunctionCount = malfunctionCount;
    }

    public int getUserCount() {
        return userCount;
    }

    public void setUserCount(int userCount) {
        this.userCount = userCount;
    }
}
