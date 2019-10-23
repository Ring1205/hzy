package com.zxycloud.zszw.model;

import com.zxycloud.zszw.model.bean.SystemBean;
import com.zxycloud.common.base.BaseBean;

import java.util.List;

public class ResultSystemListBean extends BaseBean {
    private List<SystemBean> data;

    public List<SystemBean> getData() {
        return data;
    }
}
