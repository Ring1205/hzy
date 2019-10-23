package com.zxycloud.zszw.model.bean;

import java.util.Locale;

/**
 * @author leiming
 * @date 2019/3/25.
 */
public class LinkmanBean {
    /**
     * linkmanId :
     * linkmanName :
     * linkmanPhoneNumber :
     */

    public LinkmanBean(int i) {
        linkmanId = i + "";
        linkmanName = "张" + i;
        linkmanPhoneNumber = String.format(Locale.getDefault(), "%d%d%d%d%d%d%d%d%d%d%d", i, i, i, i, i, i, i, i, i, i, i);
    }

    private String linkmanId;
    private String linkmanName;
    private String linkmanPhoneNumber;

    public String getLinkmanId() {
        return linkmanId;
    }

    public String getLinkmanName() {
        return linkmanName;
    }

    public String getLinkmanPhoneNumber() {
        return linkmanPhoneNumber;
    }
}
