package com.zxycloud.zszw.model.bean;

public class PointStateBean {
    /**
     * id : 5a665de47f344fbda7ff9545bd4dfcdb
     * resultState : 0
     * patrolItemName : 智能灭火器巡查项7
     * equType : 1
     * equTypeName : 灭火器
     */
    private String id;
    private int resultState;
    private String patrolItemName;
    private int equType;
    private String equTypeName;
    private String resultStateName;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getResultState() {
        return resultState;
    }

    public void setResultState(int resultState) {
        this.resultState = resultState;
    }

    public String getPatrolItemName() {
        return patrolItemName;
    }

    public void setPatrolItemName(String patrolItemName) {
        this.patrolItemName = patrolItemName;
    }

    public int getEquType() {
        return equType;
    }

    public void setEquType(int equType) {
        this.equType = equType;
    }

    public String getEquTypeName() {
        return equTypeName;
    }

    public void setEquTypeName(String equTypeName) {
        this.equTypeName = equTypeName;
    }

    public String getResultStateName() {
        return resultStateName;
    }

    public void setResultStateName(String resultStateName) {
        this.resultStateName = resultStateName;
    }
}
