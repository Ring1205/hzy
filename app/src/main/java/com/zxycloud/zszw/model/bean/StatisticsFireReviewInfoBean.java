package com.zxycloud.zszw.model.bean;

/**
 * @author leiming
 * @date 2019/4/22.
 */
public class StatisticsFireReviewInfoBean {

    /**
     * agentId : c27ec1d8c6ee409b9b2ccb00b7b8c38f
     * projectId : cf353a54ef7d421492bd12521a6ca448
     * placeId :
     * yearMonthTime : 2019-01
     * fireNumber : 98
     * fireHandleIn15m : 26
     * fireHandleIn1h : 40
     * fireHandleIn1d : 10
     * fireHandleAfter1d : 8
     * fireProcessCount : 84
     */

    private String agentId;
    private String projectId;
    private String placeId;
    private String yearMonthTime;
    private int fireNumber;
    private int fireHandleIn15m;
    private int fireHandleIn1h;
    private int fireHandleIn1d;
    private int fireHandleAfter1d;
    private int fireProcessCount;

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

    public int getFireNumber() {
        return fireNumber;
    }

    public int getFireHandleIn15m() {
        return fireHandleIn15m;
    }

    public int getFireHandleIn1h() {
        return fireHandleIn1h;
    }

    public int getFireHandleIn1d() {
        return fireHandleIn1d;
    }

    public int getFireHandleAfter1d() {
        return fireHandleAfter1d;
    }

    public int getFireProcessCount() {
        return fireProcessCount;
    }
}
