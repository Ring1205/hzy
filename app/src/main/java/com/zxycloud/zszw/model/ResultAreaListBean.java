package com.zxycloud.zszw.model;

import com.zxycloud.zszw.model.bean.AreaBean;
import com.zxycloud.common.base.BaseBean;

import java.util.List;

/**
 * @author leiming
 * @date 2019/3/20.
 */
public class ResultAreaListBean extends BaseBean {
    private List<AreaBean> data;

    public List<AreaBean> getData() {
        return data;
    }
}
