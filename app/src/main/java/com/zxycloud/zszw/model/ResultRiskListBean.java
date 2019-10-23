package com.zxycloud.zszw.model;

import com.zxycloud.zszw.model.bean.RiskBean;
import com.zxycloud.common.base.BaseBean;

import java.util.List;

/**
 * @author leiming
 * @date 2019/3/20.
 */
public class ResultRiskListBean extends BaseBean {
    private List<RiskBean> data;

    public List<RiskBean> getData() {
        return data;
    }
}
