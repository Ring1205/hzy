package com.zxycloud.zszw.model;

import com.zxycloud.zszw.model.bean.ProcessStateBean;
import com.zxycloud.common.base.BaseBean;

import java.util.List;

public class ResultProcessStateListBean extends BaseBean {
    private List<ProcessStateBean> data;

    public List<ProcessStateBean> getData() {
        return data;
    }
}
