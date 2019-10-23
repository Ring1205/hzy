package com.zxycloud.zszw.model;

import com.zxycloud.common.base.BaseBean;
import com.zxycloud.zszw.model.bean.PointStateBean;

import java.util.List;

public class ResultPatrolPointBean extends BaseBean {
    /**
     * data : {"id":"729f05ecf9f6490184c097ebf9e07f22","companyName":"中消云","patrolPointName":"巡查点故宫","patrolItemTypeName":"设备设施类巡查","equTypeName":"防火门","tagNumber":"taga111222333444","cardTypeName":"NFC","areaName":"故宫片区","address":"北京市故宫五角大楼4号楼","producedDate":"2014-03-25","openDate":"2014-03-25","durableYear":"2024-03-25","manufacturer":"中消云公司","patrolItemVOList":[{"id":"018e3c86fd77424f90a308f4c1ce9146","patrolItemName":"智能灭火器巡查项5","equTypeName":"灭火器","equType":"1"},{"id":"018e3c86fd77424f90a308f4c1ce9146","patrolItemName":"智能灭火器巡查项5","equTypeName":"灭火器","equType":"1"}],"equType":1,"patrolItemType":1,"cardType":1,"deviceCount":"1","areaId":""}
     */

    private DataBean data;

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }
    public static class DataBean {
        /**
         * id : 729f05ecf9f6490184c097ebf9e07f22
         * companyName : 中消云
         * patrolPointName : 巡查点故宫
         * patrolItemTypeName : 设备设施类巡查
         * equTypeName : 防火门
         * tagNumber : taga111222333444
         * cardTypeName : NFC
         * areaName : 故宫片区
         * address : 北京市故宫五角大楼4号楼
         * producedDate : 2014-03-25
         * openDate : 2014-03-25
         * durableYear : 2024-03-25
         * manufacturer : 中消云公司
         * patrolItemVOList : [{"id":"018e3c86fd77424f90a308f4c1ce9146","patrolItemName":"智能灭火器巡查项5","equTypeName":"灭火器","equType":"1"},{"id":"018e3c86fd77424f90a308f4c1ce9146","patrolItemName":"智能灭火器巡查项5","equTypeName":"灭火器","equType":"1"}]
         * equType : 1
         * patrolItemType : 1
         * cardType : 1
         * deviceCount : 1
         * companyId : 484de372-84f9-48e7-afce-5ccff1595a9f
         * areaId :
         */

        private String id;
        private String companyName;
        private String patrolPointName;
        private String patrolItemTypeName;
        private String equTypeName;
        private String tagNumber;
        private String cardTypeName;
        private String areaName;
        private String address;
        private String producedDate;
        private String openDate;
        private String durableYear;
        private String manufacturer;
        private int equType;
        private int patrolItemType;
        private int cardType;
        private int deviceCount;
        private String areaId;
        private List<PointStateBean> patrolItemVOList;
        private String companyId;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getCompanyName() {
            return companyName;
        }

        public void setCompanyName(String companyName) {
            this.companyName = companyName;
        }

        public String getPatrolPointName() {
            return patrolPointName;
        }

        public void setPatrolPointName(String patrolPointName) {
            this.patrolPointName = patrolPointName;
        }

        public String getPatrolItemTypeName() {
            return patrolItemTypeName;
        }

        public void setPatrolItemTypeName(String patrolItemTypeName) {
            this.patrolItemTypeName = patrolItemTypeName;
        }

        public String getEquTypeName() {
            return equTypeName;
        }

        public void setEquTypeName(String equTypeName) {
            this.equTypeName = equTypeName;
        }

        public String getTagNumber() {
            return tagNumber;
        }

        public void setTagNumber(String tagNumber) {
            this.tagNumber = tagNumber;
        }

        public String getCardTypeName() {
            return cardTypeName;
        }

        public void setCardTypeName(String cardTypeName) {
            this.cardTypeName = cardTypeName;
        }

        public String getAreaName() {
            return areaName;
        }

        public void setAreaName(String areaName) {
            this.areaName = areaName;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public String getProducedDate() {
            return producedDate;
        }

        public void setProducedDate(String producedDate) {
            this.producedDate = producedDate;
        }

        public String getOpenDate() {
            return openDate;
        }

        public void setOpenDate(String openDate) {
            this.openDate = openDate;
        }

        public String getDurableYear() {
            return durableYear;
        }

        public void setDurableYear(String durableYear) {
            this.durableYear = durableYear;
        }

        public String getManufacturer() {
            return manufacturer;
        }

        public void setManufacturer(String manufacturer) {
            this.manufacturer = manufacturer;
        }

        public int getEquType() {
            return equType;
        }

        public void setEquType(int equType) {
            this.equType = equType;
        }

        public int getPatrolItemType() {
            return patrolItemType;
        }

        public void setPatrolItemType(int patrolItemType) {
            this.patrolItemType = patrolItemType;
        }

        public int getCardType() {
            return cardType;
        }

        public void setCardType(int cardType) {
            this.cardType = cardType;
        }

        public int getDeviceCount() {
            return deviceCount;
        }

        public void setDeviceCount(int deviceCount) {
            this.deviceCount = deviceCount;
        }

        public String getAreaId() {
            return areaId;
        }

        public void setAreaId(String areaId) {
            this.areaId = areaId;
        }

        public List<PointStateBean> getPatrolItemVOList() {
            return patrolItemVOList;
        }

        public void setPatrolItemVOList(List<PointStateBean> patrolItemVOList) {
            this.patrolItemVOList = patrolItemVOList;
        }

        public String getCompanyId() {
            return companyId;
        }

        public void setCompanyId(String companyId) {
            this.companyId = companyId;
        }
    }
}
