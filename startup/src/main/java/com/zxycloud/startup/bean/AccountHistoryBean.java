package com.zxycloud.startup.bean;

import com.zxycloud.common.utils.db.DbClass;
import com.zxycloud.common.utils.db.PrimaryKey;
import com.zxycloud.common.utils.db.Require;

/**
 * @author leiming
 * @date 2019/4/1.
 */
@DbClass
public class AccountHistoryBean {
    @PrimaryKey
    private String userAccount;

    @Require
    private String userPassword;

    @Require
    private boolean rememberPassword;

    @Require
    private boolean isFirst;

    public AccountHistoryBean() {

    }

    public AccountHistoryBean(String userAccount, String userPassword, boolean rememberPassword) {
        this.userAccount = userAccount;
        this.userPassword = userPassword;
        this.rememberPassword = rememberPassword;
    }

    public String getUserAccount() {
        return userAccount;
    }

    public String getUserPassword() {
        return userPassword;
    }

    public boolean isRememberPassword() {
        return rememberPassword;
    }

    public void setFirst(boolean first) {
        isFirst = first;
    }

    public boolean isFirst() {
        return isFirst;
    }
}
