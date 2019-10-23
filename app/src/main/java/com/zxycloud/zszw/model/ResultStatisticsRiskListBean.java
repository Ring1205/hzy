package com.zxycloud.zszw.model;

import com.zxycloud.zszw.model.bean.StatisticsRiskInfoBean;
import com.zxycloud.common.base.BaseBean;

import java.util.List;

/**
 * @author leiming
 * @date 2019/3/20.
 */
public class ResultStatisticsRiskListBean extends BaseBean {
    private List<StatisticsRiskInfoBean> data;

    public List<StatisticsRiskInfoBean> getData() {
        return data;
    }
}
