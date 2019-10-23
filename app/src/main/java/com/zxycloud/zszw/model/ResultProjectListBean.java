package com.zxycloud.zszw.model;

import com.zxycloud.zszw.model.bean.ProjectBean;
import com.zxycloud.common.base.BaseBean;

import java.util.List;

/**
 * @author leiming
 * @date 2019/3/22.
 */
public class ResultProjectListBean extends BaseBean {
    private List<ProjectBean> data;

    public List<ProjectBean> getData() {
        return data;
    }
}
