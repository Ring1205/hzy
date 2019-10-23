package com.zxycloud.zszw.model;

/**
 * @author leiming
 * @date 2019/4/22.
 */
public class StatisticsFireInfoBean {

    /**
     * trueAlarmCount : 0
     * falseAlarmCount : 9
     * testAlarmCount : 0
     * noProcessAlarmCount : 9
     * currentAlarmCount : 0
     * prefixAlarmCount : 2
     */

    private int trueAlarmCount;
    private int falseAlarmCount;
    private int testAlarmCount;
    private int noProcessAlarmCount;
    private int currentAlarmCount;
    private int prefixAlarmCount;

    public int getTrueAlarmCount() {
        return trueAlarmCount;
    }

    public int getFalseAlarmCount() {
        return falseAlarmCount;
    }

    public int getTestAlarmCount() {
        return testAlarmCount;
    }

    public int getNoProcessAlarmCount() {
        return noProcessAlarmCount;
    }

    public int getCurrentAlarmCount() {
        return currentAlarmCount;
    }

    public int getPrefixAlarmCount() {
        return prefixAlarmCount;
    }

    public boolean isStateCanShow() {
        return (currentAlarmCount + prefixAlarmCount) != 0;
    }

    public boolean isReviewCanShow() {
        return (trueAlarmCount + falseAlarmCount + testAlarmCount + noProcessAlarmCount) != 0;
    }
}
