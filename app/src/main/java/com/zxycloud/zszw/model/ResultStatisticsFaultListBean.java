package com.zxycloud.zszw.model;

import com.zxycloud.zszw.model.bean.StatisticsFaultBean;
import com.zxycloud.common.base.BaseBean;

import java.util.List;

/**
 * @author leiming
 * @date 2019/3/20.
 */
public class ResultStatisticsFaultListBean extends BaseBean {
    private List<StatisticsFaultBean> data;

    public List<StatisticsFaultBean> getData() {
        return data;
    }
}
