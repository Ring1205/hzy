package com.zxycloud.zszw.model.bean;

/**
 * @author leiming
 * @date 2019/4/15.
 */
public class AppVersionBean {
    public static final int UPDATE_STATE_NO = 0;
    public static final int UPDATE_STATE_UPDATE = 1;
    public static final int UPDATE_STATE_FORCE = 2;

    /**
     * force : 是否强制更新 0:不更新 1:升级 2:强制升级
     * version : 1.0.1
     * appPath : http://xf.tandatech.com:1080/apk/zszw.apk
     */

    private int force;
    private String version;
    private String appPath;

    public int getForce() {
        return force;
    }

    public void setForce(int force) {
        this.force = force;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getAppPath() {
        return appPath;
    }

    public void setAppPath(String appPath) {
        this.appPath = appPath;
    }
}
