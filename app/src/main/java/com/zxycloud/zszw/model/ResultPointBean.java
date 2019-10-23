package com.zxycloud.zszw.model;

import com.zxycloud.zszw.model.bean.PointBean;
import com.zxycloud.common.base.BaseBean;

public class ResultPointBean extends BaseBean {
    private PointBean data;

    public PointBean getData() {
        return data;
    }

    public void setData(PointBean data) {
        this.data = data;
    }
}
