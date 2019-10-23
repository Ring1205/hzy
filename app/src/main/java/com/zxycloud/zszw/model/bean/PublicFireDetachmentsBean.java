package com.zxycloud.zszw.model.bean;

import com.zxycloud.zszw.base.BaseDataBean;

/**
 * 消防支队
 *
 * @author leiming
 * @date 2019/4/9.
 */
public class PublicFireDetachmentsBean extends BaseDataBean {
    /**
     * name : 消防支队名称
     * address : 消防支队地址
     * gcj02Latitude : 高德维度
     * gcj02Longitude : 高德经度
     * wgs84Latitude : google维度
     * wgs84Longitude : google经度
     */

    private String name;
    private String address;
    private double gcj02Latitude;
    private double gcj02Longitude;
    private double wgs84Latitude;
    private double wgs84Longitude;

    public String getName() {
        return formatUtils.getString(name);
    }

    public String getAddress() {
        return formatUtils.getString(address);
    }

    public double getGcj02Latitude() {
        return gcj02Latitude;
    }

    public double getGcj02Longitude() {
        return gcj02Longitude;
    }

    public double getWgs84Latitude() {
        return wgs84Latitude;
    }

    public double getWgs84Longitude() {
        return wgs84Longitude;
    }
}
