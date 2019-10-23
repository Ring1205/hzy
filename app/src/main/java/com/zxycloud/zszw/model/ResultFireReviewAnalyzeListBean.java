package com.zxycloud.zszw.model;

import com.zxycloud.zszw.model.bean.FireReviewAnalyzeBean;
import com.zxycloud.common.base.BaseBean;

import java.util.List;

/**
 * @author leiming
 * @date 2019/4/23.
 */
public class ResultFireReviewAnalyzeListBean extends BaseBean {
    private List<FireReviewAnalyzeBean> data;

    public List<FireReviewAnalyzeBean> getData() {
        return data;
    }
}
