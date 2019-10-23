package com.zxycloud.startup.bean;

import com.zxycloud.common.base.BaseBean;

import java.util.List;

/**
 * @author leiming
 * @date 2019/3/22.
 */
public class ResultProjectListBean extends BaseBean {
    private List<ProjectBean> data;

    public List<ProjectBean> getData() {
        return data;
    }

   public class ProjectBean {
        /**
         * projectId :
         * projectName :
         * type : 1
         * typeName : 商场、宾馆、公共娱乐场所等公众聚集场所
         * countryCode : 000156
         * countryName : 中国
         * firstLevelRegionCode : 110000
         * secondLevelRegionCode : 110000
         * thirdLevelRegionCode : 110105
         * firstLevelRegionName : 北京市
         * secondLevelRegionName : 北京市
         * thirdLevelRegionName : 朝阳区
         * postalCode :
         * address : 北京市朝阳区来广营
         * projectMonitorRegion : 监控范围
         * principalName : 张三
         * principalPhoneNumber : 15009258370
         * adminId : 123455
         * dutyPhoneNumber : 15009258370
         * gcj02Latitude : 1
         * gcj02Longitude : 1
         * wgs84Latitude : 0
         * wgs84Longitude : 0
         * phoneNotified : 1
         * messageNotified : 1
         * property : 0
         * title : 单位测试
         * logoUrl : http://127.0.0.1:8080
         * mealId : 12344555
         * serviceMonth : 1
         * urgent : 0
         * urgentReason :
         * auditMethod : 3
         */
        private String projectId;
        private String projectName;
        private int type;
        private String typeName;
        private String countryCode;
        private String countryName;
        private String firstLevelRegionCode;
        private String secondLevelRegionCode;
        private String thirdLevelRegionCode;
        private String firstLevelRegionName;
        private String secondLevelRegionName;
        private String thirdLevelRegionName;
        private String postalCode;
        private String address;
        private String projectMonitorRegion;
        private String principalName;
        private String principalPhoneNumber;
        private String adminId;
        private String dutyPhoneNumber;
        private int gcj02Latitude;
        private int gcj02Longitude;
        private int wgs84Latitude;
        private int wgs84Longitude;
        private int phoneNotified;
        private int messageNotified;
        private int property;
        private String title;
        private String logoUrl;
        private String mealId;
        private int serviceMonth;
        private int urgent;
        private String urgentReason;
        private int auditMethod;

        public String getProjectId() {
            return projectId;
        }

        public String getProjectName() {
            return projectName;
        }


        public int getType() {
            return type;
        }

        public String getTypeName() {
            return typeName;
        }

        public String getCountryCode() {
            return countryCode;
        }

        public String getCountryName() {
            return countryName;
        }

        public String getFirstLevelRegionCode() {
            return firstLevelRegionCode;
        }

        public String getSecondLevelRegionCode() {
            return secondLevelRegionCode;
        }

        public String getThirdLevelRegionCode() {
            return thirdLevelRegionCode;
        }

        public String getFirstLevelRegionName() {
            return firstLevelRegionName;
        }

        public String getSecondLevelRegionName() {
            return secondLevelRegionName;
        }

        public String getThirdLevelRegionName() {
            return thirdLevelRegionName;
        }

        public String getPostalCode() {
            return postalCode;
        }

        public String getAddress() {
            return address;
        }

        public String getProjectMonitorRegion() {
            return projectMonitorRegion;
        }

        public String getPrincipalName() {
            return principalName;
        }

        public String getPrincipalPhoneNumber() {
            return principalPhoneNumber;
        }

        public String getAdminId() {
            return adminId;
        }

        public String getDutyPhoneNumber() {
            return dutyPhoneNumber;
        }

        public int getGcj02Latitude() {
            return gcj02Latitude;
        }

        public int getGcj02Longitude() {
            return gcj02Longitude;
        }

        public int getWgs84Latitude() {
            return wgs84Latitude;
        }

        public int getWgs84Longitude() {
            return wgs84Longitude;
        }

        public int getPhoneNotified() {
            return phoneNotified;
        }

        public int getMessageNotified() {
            return messageNotified;
        }

        public int getProperty() {
            return property;
        }

        public String getTitle() {
            return title;
        }

        public String getLogoUrl() {
            return logoUrl;
        }

        public String getMealId() {
            return mealId;
        }

        public int getServiceMonth() {
            return serviceMonth;
        }

        public int getUrgent() {
            return urgent;
        }

        public String getUrgentReason() {
            return urgentReason;
        }

        public int getAuditMethod() {
            return auditMethod;
        }
    }
}
