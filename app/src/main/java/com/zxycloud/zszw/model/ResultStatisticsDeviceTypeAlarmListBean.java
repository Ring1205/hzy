package com.zxycloud.zszw.model;

import com.zxycloud.zszw.model.bean.StatisticsDeviceTypeAlarmBean;
import com.zxycloud.common.base.BaseBean;

import java.util.List;

/**
 * @author leiming
 * @date 2019/3/20.
 */
public class ResultStatisticsDeviceTypeAlarmListBean extends BaseBean {
    private List<StatisticsDeviceTypeAlarmBean> data;

    public List<StatisticsDeviceTypeAlarmBean> getData() {
        return data;
    }
}
