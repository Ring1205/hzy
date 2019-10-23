package com.zxycloud.common.utils;

public class CommonStateBean {
    private String stateName;
    private int stateCode;

    public CommonStateBean(String stateName, int stateCode) {
        this.stateName = stateName;
        this.stateCode = stateCode;
    }

    public String getStateName() {
        return stateName;
    }

    public int getStateCode() {
        return stateCode;
    }
}