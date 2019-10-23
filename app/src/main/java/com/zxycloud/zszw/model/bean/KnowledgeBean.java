package com.zxycloud.zszw.model.bean;

import com.zxycloud.zszw.base.BaseDataBean;

/**
 * @author leiming
 * @date 2019/3/18.
 */
public class KnowledgeBean extends BaseDataBean {
    /**
     * id : 8c981ba2-d527-4ef3-9d5b-344958855dd4
     * title : APP扫描TX3252网关，APP提示“没有获取主机权限”
     * url : http://192.168.32.133/notice/Question1.html
     * type : 1
     * language : zh
     * createTime : 1555295027000
     * brief : 发现火灾第一时间该怎么办?灭火三要素您知道吗？
     */

    private String id;
    private String title;
    private String url;
    private int type;
    private String language;
    private long createTime;
    private String brief;

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getUrl() {
        return url;
    }

    public int getType() {
        return type;
    }

    public String getLanguage() {
        return language;
    }

    public long getCreateTime() {
        return createTime;
    }

    public String getBrief() {
        return brief;
    }

    public void setBrief(String brief) {
        this.brief = brief;
    }
}
