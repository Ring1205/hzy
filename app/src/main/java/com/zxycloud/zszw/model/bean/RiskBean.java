package com.zxycloud.zszw.model.bean;

import com.zxycloud.zszw.base.BaseDataBean;

import java.util.List;

/**
 * @author leiming
 * @date 2019/3/18.
 */
public class RiskBean extends BaseDataBean {


    /**
     * id : 76f88f993e80426aaaab4b72bd95eb9e
     * title : 这是一个消防的危险
     * description : 隐患描述
     * createUserName : 雷霆咆哮
     * createTime : 2019/04/20
     * videoUrl : http://192.168.32.106:8000/static/images/upload/2019-04-16/693074ec-07d6-4155-bd08-62ef829a3972.mp4
     * voiceUrl : http://192.168.32.106:8000/static/images/upload/2019-04-16/3855dff9-71be-466b-b158-36bef2130727.mp3
     * imgUrls : ["http://192.168.32.106:8000/static/images/upload/2019-04-17/e00ad4ce-3666-42a5-a09f-36cd9b0b3b26.jpg","http://192.168.32.106:8000/static/images/upload/2019-04-16/c3bcea81-b0e7-4aff-88cf-7d4e19dfeb9e.png"]
     * projectName :
     * hiddenAddress :
     */

    private String id;
    private String title;
    private String description;
    private String createUserName;
    private String createTime;
    private String videoUrl;
    private String voiceUrl;
    private String projectName;
    private String projectId;
    private String hiddenAddress;
    private String principalUserName;
    private List<String> imgUrls;
    /**
     * hiddenLevel : 1
     * processUserName :
     * processResultName : 未整改
     */

    private int hiddenLevel;
    private String hiddenLevelName;
    private String processUserName;
    private String processTime;
    private String processResultName;
    private String processResult;
    private String info;
    private String sourceCodeName;
    private String sourceCode;

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return formatUtils.getString(description);
    }

    public String getCreateUserName() {
        return formatUtils.getString(createUserName);
    }

    public String getCreateTime() {
        return formatUtils.getString(createTime);
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public String getVoiceUrl() {
        return voiceUrl;
    }

    public String getProjectName() {
        return formatUtils.getString(projectName);
    }

    public String getProjectId() {
        return projectId;
    }

    public String getHiddenAddress() {
        return formatUtils.getString(hiddenAddress);
    }

    public List<String> getImgUrls() {
        return imgUrls;
    }

    public int getHiddenLevel() {
        return hiddenLevel;
    }

    public String getHiddenLevelName() {
        return formatUtils.getString(hiddenLevelName);
    }

    public String getProcessUserName() {
        return formatUtils.getString(processUserName);
    }

    public String getProcessTime() {
        return formatUtils.getString(processTime);
    }

    public String getProcessResultName() {
        return formatUtils.getString(processResultName);
    }

    public String getInfo() {
        return formatUtils.getString(info);
    }

    public String getPrincipalUserName() {
        return formatUtils.getString(principalUserName);
    }

    public String getProcessResult() {
        return formatUtils.getString(processResult);
    }

    public String getSourceCodeName() {
        return formatUtils.getString(sourceCodeName);
    }

    public String getSourceCode() {
        return sourceCode;
    }
}
