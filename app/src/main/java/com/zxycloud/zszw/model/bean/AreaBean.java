package com.zxycloud.zszw.model.bean;

import com.zxycloud.zszw.base.BaseDataBean;
import com.zxycloud.zszw.model.ResultPointAreaListBean;

/**
 * @author leiming
 * @date 2019/3/18.
 */
public class AreaBean extends BaseDataBean {
    /**
     * areaAdminId              : 管理员id
     * areaAdminName            : 管理员名称
     * areaAdminPhone           : 管理员电话
     * areaId                   : 区域Id
     * areaManageRegion         : 管理区域
     * areaName                 : 区域名
     * projectName              : 单位名称
     * projectType              : 单位类型
     * areaPrincipalId          : 负责人Id
     * subAreaType              : 子区域类型
     * areaPrincipalName        : 区域负责人姓名
     * areaPrincipalPhoneNumber : 区域负责人联系电话
     * subAreaCount             : 子级数量（根据子级类型判断是区域还是场所）
     */

    private String areaName;
    private String areaAdminId;
    private String areaAdminName;
    private String areaAdminPhone;
    private String projectName;
    private String projectType;
    private String areaId;
    private String areaManageRegion;
    private String areaPrincipalId;
    private String areaPrincipalName;
    private String areaPrincipalPhoneNumber;
    private String areaAddress;
    private int subAreaType;
    private int subAreaCount;
    private int isAdd;
    private int level;
    private boolean canDelete;

    public String getAreaAdminId() {
        return areaAdminId;
    }

    public void setAreaAdminId(String areaAdminId) {
        this.areaAdminId = areaAdminId;
    }

    public AreaBean(String areaName) {
        this.areaName = areaName;
    }
    public AreaBean() {
    }
    public AreaBean(ResultPointAreaListBean.DataBean bean, String projectName) {
        this.areaId = bean.getId();
        this.areaName = bean.getAreaName();
        this.level = bean.getLevel();
        this.projectName = projectName;
    }
    public int getIsAdd() {
        return isAdd;
    }

    public void setIsAdd(int isAdd) {
        this.isAdd = isAdd;
    }

    public String getProjectType() {
        return projectType;
    }
    public String getProjectName() {
        return projectName;
    }

    public String getAreaManageRegion() {
        return areaManageRegion;
    }

    public String getAreaPrincipalName() {
        return areaPrincipalName;
    }

    public boolean isCanDelete() {
        return canDelete;
    }

    public String getAreaName() {
        return areaName;
    }

    public void setSubAreaType(int subAreaType) {
        this.subAreaType = subAreaType;
    }

    public int getSubAreaCount() {
        return subAreaCount;
    }

    public String getAreaPrincipalPhoneNumber() {
        return areaPrincipalPhoneNumber;
    }

    public void setSubAreaCount(int subAreaCount) {
        this.subAreaCount = subAreaCount;
    }

    public String getAreaId() {
        return areaId;
    }

    public String getAreaManagerRegion() {
        return areaManageRegion;
    }

    public void setAreaName(String areaName) {
        this.areaName = areaName;
    }

    public void setAreaAdminName(String areaAdminName) {
        this.areaAdminName = areaAdminName;
    }

    public void setAreaAdminPhone(String areaAdminPhone) {
        this.areaAdminPhone = areaAdminPhone;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public void setProjectType(String projectType) {
        this.projectType = projectType;
    }

    public void setAreaId(String areaId) {
        this.areaId = areaId;
    }

    public void setAreaManageRegion(String areaManageRegion) {
        this.areaManageRegion = areaManageRegion;
    }

    public void setAreaPrincipalId(String areaPrincipalId) {
        this.areaPrincipalId = areaPrincipalId;
    }

    public void setAreaPrincipalName(String areaPrincipalName) {
        this.areaPrincipalName = areaPrincipalName;
    }

    public void setAreaPrincipalPhoneNumber(String areaPrincipalPhoneNumber) {
        this.areaPrincipalPhoneNumber = areaPrincipalPhoneNumber;
    }

    public String getAreaAddress() {
        return areaAddress;
    }

    public void setAreaAddress(String areaAddress) {
        this.areaAddress = areaAddress;
    }

    public void setCanDelete(boolean canDelete) {
        this.canDelete = canDelete;
    }

    public void setCheck(boolean check) {
        isCheck = check;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public String getAreaPrincipalId() {
        return areaPrincipalId;
    }

    public int getSubAreaType() {
        return subAreaType;
    }

    public String getAreaAdminName() {
        return areaAdminName;
    }

    public String getAreaAdminPhone() {
        return areaAdminPhone;
    }

    private boolean isCheck;

    public boolean isCheck() {
        return isCheck;
    }

    public void setCheck(Boolean check) {
        isCheck = check;
    }
}

