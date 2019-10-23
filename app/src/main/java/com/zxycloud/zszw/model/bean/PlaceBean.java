package com.zxycloud.zszw.model.bean;

import com.zxycloud.zszw.base.BaseDataBean;

import java.util.List;

/**
 * @author leiming
 * @date 2019/3/18.
 */
public class PlaceBean extends BaseDataBean {

    /**
     * placeId                      : 场所Id
     * placeName                    : 场所名称
     * projectName                  : 所属单位名称
     * placeAddress                 : 场所位置
     * deviceCount                  : 场所下设备数量
     * stateGroupCode               : 场所状态编码
     * stateGroupName               : 场所状态名称
     * placePrincipalName           : 场所管理员姓名
     * placePrincipalPhoneNumber    : 场所管理员联系电话
     */

    private String placeId;
    private String placeName;
    private String placeRegion;
    private String projectName;
    private String placeAddress;
    private int deviceCount;
    private int stateGroupCode;
    private String stateGroupName;
    private String placePrincipalName;
    private String placePrincipalPhoneNumber;
    /**
     * areaName :
     * placePhoneNumber : 15001321366
     * placeAdminName : 奥赛
     * placeAdminPhoneNumber :
     * linkmanList : [{"id":"22973f0b-f902-4c2f-8ca8-e31c1252c355","placeId":"4eab5082-989c-4b61-b7f8-73615d78706a","linkmanName":"测试添加场所","linkmanPhoneNumber":"1364564564"}]
     * picUrl : http://192.168.32.106:8000/static/images/upload/2019-03-31/c022d284-3d19-4112-b406-1d79baa138e5.jpg
     */

    private String areaName;
    private String placePhoneNumber;
    private String placeAdminName;
    private String placeAdminPhoneNumber;
    private String picUrl;
    private boolean canDelete;
    private List<LinkmanListBean> linkmanList;
    /**
     * aloneDeviceCount : 0
     * adapterCount : 0
     */

    private int aloneDeviceCount;
    private int adapterCount;

    public PlaceBean(int i) {
        this.placeName = "我是萌萌哒场所名" + i;
        this.placeRegion = "这个沟那个屯";
        this.placeAddress = "就在那个哪";
        this.stateGroupName = "正常";
        this.stateGroupCode = 1;
        this.projectName = "头等项目";
        this.deviceCount = 0;
    }

    public String getPlaceRegion() {
        return placeRegion;
    }

    public String getPlaceId() {
        return placeId;
    }

    public String getPlaceName() {
        return placeName;
    }

    public String getProjectName() {
        return projectName;
    }

    public String getPlaceAddress() {
        return placeAddress;
    }

    public int getDeviceCount() {
        return deviceCount;
    }

    public int getStateGroupCode() {
        return stateGroupCode;
    }

    public String getStateGroupName() {
        return stateGroupName;
    }

    public String getPlacePrincipalName() {
        return placePrincipalName;
    }

    public String getPlacePrincipalPhoneNumber() {
        return placePrincipalPhoneNumber;
    }

    public String getAreaName() {
        return areaName;
    }

    public void setAreaName(String areaName) {
        this.areaName = areaName;
    }

    public String getPlacePhoneNumber() {
        return placePhoneNumber;
    }

    public void setPlacePhoneNumber(String placePhoneNumber) {
        this.placePhoneNumber = placePhoneNumber;
    }

    public String getPlaceAdminName() {
        return placeAdminName;
    }

    public void setPlaceAdminName(String placeAdminName) {
        this.placeAdminName = placeAdminName;
    }

    public String getPlaceAdminPhoneNumber() {
        return placeAdminPhoneNumber;
    }

    public void setPlaceAdminPhoneNumber(String placeAdminPhoneNumber) {
        this.placeAdminPhoneNumber = placeAdminPhoneNumber;
    }

    public String getPicUrl() {
        return picUrl;
    }

    public void setPicUrl(String picUrl) {
        this.picUrl = picUrl;
    }

    public List<LinkmanListBean> getLinkmanList() {
        return linkmanList;
    }

    public void setLinkmanList(List<LinkmanListBean> linkmanList) {
        this.linkmanList = linkmanList;
    }

    public int getAloneDeviceCount() {
        return aloneDeviceCount;
    }

    public void setAloneDeviceCount(int aloneDeviceCount) {
        this.aloneDeviceCount = aloneDeviceCount;
    }

    public int getAdapterCount() {
        return adapterCount;
    }

    public void setAdapterCount(int adapterCount) {
        this.adapterCount = adapterCount;
    }

    public boolean isCanDelete() {
        return canDelete;
    }

    public static class LinkmanListBean {
        /**
         * id : 22973f0b-f902-4c2f-8ca8-e31c1252c355
         * placeId : 4eab5082-989c-4b61-b7f8-73615d78706a
         * linkmanName : 测试添加场所
         * linkmanPhoneNumber : 1364564564
         */

        private String id;
        private String placeId;
        private String placeIdX;
        private String linkmanName;
        private String linkmanPhoneNumber;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }
        public String getPlaceId() {
            return placeId;
        }
        public String getPlaceIdX() {
            return placeIdX;
        }

        public void setPlaceIdX(String placeIdX) {
            this.placeIdX = placeIdX;
        }

        public String getLinkmanName() {
            return linkmanName;
        }

        public void setLinkmanName(String linkmanName) {
            this.linkmanName = linkmanName;
        }

        public String getLinkmanPhoneNumber() {
            return linkmanPhoneNumber;
        }

        public void setLinkmanPhoneNumber(String linkmanPhoneNumber) {
            this.linkmanPhoneNumber = linkmanPhoneNumber;
        }
    }
}
