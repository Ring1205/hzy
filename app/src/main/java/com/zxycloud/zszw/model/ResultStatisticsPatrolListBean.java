package com.zxycloud.zszw.model;

import com.zxycloud.zszw.model.bean.StatisticsPatrolInfoBean;
import com.zxycloud.common.base.BaseBean;

import java.util.List;

/**
 * @author leiming
 * @date 2019/3/20.
 */
public class ResultStatisticsPatrolListBean extends BaseBean {
    private List<StatisticsPatrolInfoBean> data;

    public List<StatisticsPatrolInfoBean> getData() {
        return data;
    }
}
