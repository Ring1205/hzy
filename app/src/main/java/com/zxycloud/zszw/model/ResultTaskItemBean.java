package com.zxycloud.zszw.model;

import com.zxycloud.zszw.model.bean.PointStateBean;
import com.zxycloud.common.base.BaseBean;

import java.util.List;

public class ResultTaskItemBean extends BaseBean {
    /**
     * data : {"id":"39419a8c195a4c55bcd40b808be97b45","patrolPointName":"巡查点望京","tagNumber":"taga111222333444","address":"北京市故宫五角大楼4号楼","taskItemVOList":[{"id":"5a665de47f344fbda7ff9545bd4dfcdb","resultState":0,"patrolItemName":"智能灭火器巡查项7","equType":1,"equTypeName":"灭火器"},{"id":"5a665de47f344fbda7ff9545bd4dfcdb","resultState":0,"patrolItemName":"智能灭火器巡查项7","equType":1,"equTypeName":"灭火器"},{"id":"5a665de47f344fbda7ff9545bd4dfcdb","resultState":0,"patrolItemName":"智能灭火器巡查项7","equType":1,"equTypeName":"灭火器"}],"patrolDate":"2019-04-01 11:02:53","des":"结果描述","videoUrl":"http://192.168.32.106:8000/static/images/upload/2019-04-16/693074ec-07d6-4155-bd08-62ef829a3972.mp4","voiceUrl":"http://192.168.32.106:8000/static/images/upload/2019-04-16/3855dff9-71be-466b-b158-36bef2130727.mp3","imgUrls":["路径1","路径2","路径3"]}
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
         * id : 39419a8c195a4c55bcd40b808be97b45
         * patrolPointName : 巡查点望京
         * tagNumber : taga111222333444
         * address : 北京市故宫五角大楼4号楼
         * taskItemVOList : [{"id":"5a665de47f344fbda7ff9545bd4dfcdb","resultState":0,"patrolItemName":"智能灭火器巡查项7","equType":1,"equTypeName":"灭火器"},{"id":"5a665de47f344fbda7ff9545bd4dfcdb","resultState":0,"patrolItemName":"智能灭火器巡查项7","equType":1,"equTypeName":"灭火器"},{"id":"5a665de47f344fbda7ff9545bd4dfcdb","resultState":0,"patrolItemName":"智能灭火器巡查项7","equType":1,"equTypeName":"灭火器"}]
         * patrolDate : 2019-04-01 11:02:53
         * des : 结果描述
         * videoUrl : http://192.168.32.106:8000/static/images/upload/2019-04-16/693074ec-07d6-4155-bd08-62ef829a3972.mp4
         * voiceUrl : http://192.168.32.106:8000/static/images/upload/2019-04-16/3855dff9-71be-466b-b158-36bef2130727.mp3
         * imgUrls : ["路径1","路径2","路径3"]
         */

        private String id;
        private String patrolPointName;
        private String tagNumber;
        private String address;
        private String patrolDate;
        private String des;
        private String videoUrl;
        private String voiceUrl;
        private List<PointStateBean> taskItemVOList;
        private List<String> imgUrls;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getPatrolPointName() {
            return patrolPointName;
        }

        public void setPatrolPointName(String patrolPointName) {
            this.patrolPointName = patrolPointName;
        }

        public String getTagNumber() {
            return tagNumber;
        }

        public void setTagNumber(String tagNumber) {
            this.tagNumber = tagNumber;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public String getPatrolDate() {
            return patrolDate;
        }

        public void setPatrolDate(String patrolDate) {
            this.patrolDate = patrolDate;
        }

        public String getDes() {
            return des;
        }

        public void setDes(String des) {
            this.des = des;
        }

        public String getVideoUrl() {
            return videoUrl;
        }

        public void setVideoUrl(String videoUrl) {
            this.videoUrl = videoUrl;
        }

        public String getVoiceUrl() {
            return voiceUrl;
        }

        public void setVoiceUrl(String voiceUrl) {
            this.voiceUrl = voiceUrl;
        }

        public List<PointStateBean> getTaskItemVOList() {
            return taskItemVOList;
        }

        public void setTaskItemVOList(List<PointStateBean> taskItemVOList) {
            this.taskItemVOList = taskItemVOList;
        }

        public List<String> getImgUrls() {
            return imgUrls;
        }

        public void setImgUrls(List<String> imgUrls) {
            this.imgUrls = imgUrls;
        }

    }
}
