package com.zxycloud.zszw.model.bean;

/**
 * @author leiming
 * @date 2019/4/22.
 */
public class StatisticsRiskInfoBean {
    /**
     * agentId : 3e776589-68f9-4e34-b227-fa1a9ee1580a
     * projectId : b7f1b8a1-6242-47de-b271-865dea35ae2e
     * placeId :
     * yearMonthTime : 2018-12
     * hiddenCount : 0
     * hiddenProcessCount : 0
     */

    private String agentId;
    private String projectId;
    private String placeId;
    private String yearMonthTime;
    private int hiddenCount;
    private int hiddenProcessCount;

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

    public int getHiddenCount() {
        return hiddenCount;
    }

    public int getHiddenProcessCount() {
        return hiddenProcessCount;
    }
}
