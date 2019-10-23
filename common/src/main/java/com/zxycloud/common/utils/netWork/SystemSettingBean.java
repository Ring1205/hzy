package com.zxycloud.common.utils.netWork;

/**
 * @author leiming
 * @date 2019/3/18.
 */
public class SystemSettingBean {

    /**
     * businessUrl : http://192.168.32.106/AppHzyControllerApi/1.0/
     * fileUploadUrl : http://192.168.32.106/fileUpload/1.0/
     * statisticsUrl : http://192.168.32.106/StatisticsController/1.0/
     * patrolUrl : http://192.168.32.106/patrolApi/1.0/
     */
    private String businessUrl;
    private String fileUploadUrl;
    private String statisticsUrl;
    private String patrolUrl;

    public String getBusinessUrl() {
        return businessUrl;
    }

    public String getFileUploadUrl() {
        return fileUploadUrl;
    }

    public String getStatisticsUrl() {
        return statisticsUrl;
    }

    public String getPatrolUrl() {
        return patrolUrl;
    }
}
