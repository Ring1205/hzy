package com.zxycloud.zszw.model.bean;

/**
 * @author leiming
 * @date 2019/4/22.
 */
public class StatisticsFaultBean {

    /**
     * agentId : c27ec1d8c6ee409b9b2ccb00b7b8c38f
     * projectId : 0104c5f93f9245eab704071ceca6fb29
     * placeId :
     * yearMonthTime : 2018-05
     * faultNumber : 12
     */

    private String agentId;
    private String projectId;
    private String placeId;
    private String yearMonthTime;
    private int faultNumber;

    public String getAgentId() {
        return agentId;
    }

    public String getProjectId() {
        return projectId;
    }

    public String getPlaceId() {
        return placeId;
    }

    public String getYearMonthTime() {
        return yearMonthTime;
    }

    public int getFaultNumber() {
        return faultNumber;
    }
}
