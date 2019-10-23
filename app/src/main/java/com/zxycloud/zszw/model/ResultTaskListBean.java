package com.zxycloud.zszw.model;

import com.zxycloud.zszw.model.bean.TaskBean;
import com.zxycloud.common.base.BaseBean;

import java.util.List;

public class ResultTaskListBean extends BaseBean {
    private List<TaskBean> data;

    public List<TaskBean> getData(){
        return data;
    }
}
