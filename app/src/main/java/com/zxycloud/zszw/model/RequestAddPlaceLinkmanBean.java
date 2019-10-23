package com.zxycloud.zszw.model;

/**
 * @author leiming
 * @date 2019/3/26.
 */
@SuppressWarnings("FieldCanBeLocal")
public class RequestAddPlaceLinkmanBean {
    private String linkmanName;
    private String linkmanPhoneNumber;
    private String placeId;

    public RequestAddPlaceLinkmanBean(String placeId, String linkmanName, String linkmanPhoneNumber) {
        this.linkmanName = linkmanName;
        this.linkmanPhoneNumber = linkmanPhoneNumber;
        this.placeId = placeId;
    }
}
