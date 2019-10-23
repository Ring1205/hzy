package com.zxycloud.zszw.model;

import com.zxycloud.zszw.model.bean.StatisticsFaultDeviceBean;
import com.zxycloud.common.base.BaseBean;

import java.util.List;

/**
 * @author leiming
 * @date 2019/3/20.
 */
public class ResultStatisticsFaultDeviceListBean extends BaseBean {
    private List<StatisticsFaultDeviceBean> data;

    public List<StatisticsFaultDeviceBean> getData() {
        return data;
    }
}
