package com.zxycloud.zszw.model;

import com.zxycloud.zszw.model.bean.StatisticsFireReviewInfoBean;
import com.zxycloud.common.base.BaseBean;

import java.util.List;

/**
 * @author leiming
 * @date 2019/3/20.
 */
public class ResultStatisticsFireReviewInfoListBean extends BaseBean {
    private List<StatisticsFireReviewInfoBean> data;

    public List<StatisticsFireReviewInfoBean> getData() {
        return data;
    }
}
