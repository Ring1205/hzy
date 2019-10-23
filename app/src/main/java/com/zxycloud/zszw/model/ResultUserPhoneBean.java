package com.zxycloud.zszw.model;

import com.zxycloud.zszw.model.bean.UserPhoneBean;
import com.zxycloud.common.base.BaseBean;

import java.util.List;

public class ResultUserPhoneBean extends BaseBean {
    /**
     * data : [{"id":"e956c898-dc4e-426c-8234-e6ca7db24b03","userAccount":"zxy272","phoneNumber":"18888888888"}]
     * pageIndex : 1
     * pageSize : 1
     */

    private int pageIndex;
    private int pageSize;
    private List<UserPhoneBean> data;

    public int getPageIndex() {
        return pageIndex;
    }

    public void setPageIndex(int pageIndex) {
        this.pageIndex = pageIndex;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public List<UserPhoneBean> getData() {
        return data;
    }

    public void setData(List<UserPhoneBean> data) {
        this.data = data;
    }
}
