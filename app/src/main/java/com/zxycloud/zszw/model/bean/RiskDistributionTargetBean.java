package com.zxycloud.zszw.model.bean;

import com.zxycloud.zszw.base.BaseDataBean;

import java.util.List;

/**
 * 隐患分配的目标bean（现为一级区域，后期可能一直到场所）
 */
public class RiskDistributionTargetBean extends BaseDataBean {
    /**
     * id :
     * title :
     * projectName :
     * principalUserId :
     * hiddenAddress :
     * principalState :
     * areaOrPlaceId : 33d1d3c7-f512-4e3d-81ea-f057c7e4d343
     * description :
     * createUserName :
     * processUserName :
     * processResultName :
     * principalUserRoleHR :
     * gcj02LatitudeHR :
     * gcj02LongitudeHR :
     * wgs84LatitudeHR :
     * wgs84LongitudeHR :
     * videoUrl :
     * voiceUrl :
     * imgUrls : []
     * fileList : []
     * plName :
     * areaOrPlaceName : 默认区域
     * adminId :
     */

    private String id;
    private String title;
    private String projectName;
    private String principalUserId;
    private String hiddenAddress;
    private String principalState;
    private String areaOrPlaceId;
    private String description;
    private String createUserName;
    private String processUserName;
    private String processResultName;
    private String principalUserRoleHR;
    private String gcj02LatitudeHR;
    private String gcj02LongitudeHR;
    private String wgs84LatitudeHR;
    private String wgs84LongitudeHR;
    private String videoUrl;
    private String voiceUrl;
    private String plName;
    private String areaOrPlaceName;
    private String adminId;
    private List<?> imgUrls;
    private List<?> fileList;

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getProjectName() {
        return projectName;
    }

    public String getPrincipalUserId() {
        return principalUserId;
    }

    public String getHiddenAddress() {
        return hiddenAddress;
    }

    public String getPrincipalState() {
        return principalState;
    }

    public String getAreaOrPlaceId() {
        return areaOrPlaceId;
    }

    public String getDescription() {
        return description;
    }

    public String getCreateUserName() {
        return createUserName;
    }

    public String getProcessUserName() {
        return processUserName;
    }

    public String getProcessResultName() {
        return processResultName;
    }

    public String getPrincipalUserRoleHR() {
        return principalUserRoleHR;
    }

    public String getGcj02LatitudeHR() {
        return gcj02LatitudeHR;
    }

    public String getGcj02LongitudeHR() {
        return gcj02LongitudeHR;
    }

    public String getWgs84LatitudeHR() {
        return wgs84LatitudeHR;
    }

    public String getWgs84LongitudeHR() {
        return wgs84LongitudeHR;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public String getVoiceUrl() {
        return voiceUrl;
    }

    public String getPlName() {
        return plName;
    }

    public String getAreaOrPlaceName() {
        return areaOrPlaceName;
    }

    public String getAdminId() {
        return adminId;
    }

    public List<?> getImgUrls() {
        return imgUrls;
    }

    public List<?> getFileList() {
        return fileList;
    }
}
