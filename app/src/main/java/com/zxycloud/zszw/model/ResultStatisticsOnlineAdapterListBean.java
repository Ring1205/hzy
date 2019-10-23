package com.zxycloud.zszw.model;

import com.zxycloud.zszw.model.bean.StatisticsOnlineAdapterBean;
import com.zxycloud.common.base.BaseBean;

import java.util.List;

/**
 * @author leiming
 * @date 2019/3/20.
 */
public class ResultStatisticsOnlineAdapterListBean extends BaseBean {
    private List<StatisticsOnlineAdapterBean> data;

    public List<StatisticsOnlineAdapterBean> getData() {
        return data;
    }
}
