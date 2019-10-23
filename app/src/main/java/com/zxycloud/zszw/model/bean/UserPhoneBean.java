package com.zxycloud.zszw.model.bean;

public class UserPhoneBean {
    /**
     * id : e956c898-dc4e-426c-8234-e6ca7db24b03
     * userAccount : zxy272
     * phoneNumber : 18888888888
     */

    private String id;
    private String userAccount;
    private String phoneNumber;
    private String userName;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserAccount() {
        return userAccount;
    }

    public void setUserAccount(String userAccount) {
        this.userAccount = userAccount;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
