package com.zxycloud.zszw.model;

import com.zxycloud.zszw.model.bean.PointStateBean;
import com.zxycloud.common.base.BaseBean;

import java.util.List;

public class ResultPointStateBean extends BaseBean {
    public List<PointStateBean> getData() {
        return data;
    }

    public void setData(List<PointStateBean> data) {
        this.data = data;
    }

    private List<PointStateBean> data;
}
