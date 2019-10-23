package com.zxycloud.zszw.model.bean;

/**
 * @author leiming
 * @date 2019/4/22.
 */
public class StatisticsFaultDeviceBean {

    /**
     * faultId : 5df5039da8244188a4c3de6a043b4b2f
     * placeId : 1c454ca2-a8c4-4268-9bdf-9cd787e2c901
     * placeName : 燃烧山脉
     * adapterName : 01_NB_CHENJIANWEI20190312003076
     * deviceTypeName : 感烟火灾探测器
     * deviceInstallLocation : 来广营一路
     * createTime : 1552449187812
     */

    private String faultId;
    private String placeId;
    private String placeName;
    private String adapterName;
    private String deviceTypeName;
    private String deviceInstallLocation;
    private long createTime;

    public String getFaultId() {
        return faultId;
    }

    public String getPlaceId() {
        return placeId;
    }

    public String getPlaceName() {
        return placeName;
    }

    public String getAdapterName() {
        return adapterName;
    }

    public String getDeviceTypeName() {
        return deviceTypeName;
    }

    public String getDeviceInstallLocation() {
        return deviceInstallLocation;
    }

    public long getCreateTime() {
        return createTime;
    }
}
