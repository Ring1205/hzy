package com.zxycloud.zszw.model;

import com.zxycloud.zszw.model.bean.StatisticsAlarmFalseBean;
import com.zxycloud.common.base.BaseBean;

import java.util.List;

/**
 * @author leiming
 * @date 2019/3/20.
 */
public class ResultStatisticsAlarmFalseListBean extends BaseBean {
    private List<StatisticsAlarmFalseBean> data;

    public List<StatisticsAlarmFalseBean> getData() {
        return data;
    }
}
