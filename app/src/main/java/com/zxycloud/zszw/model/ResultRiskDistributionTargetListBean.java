package com.zxycloud.zszw.model;

import com.zxycloud.zszw.model.bean.RiskDistributionTargetBean;
import com.zxycloud.common.base.BaseBean;
import com.zxycloud.common.utils.CommonUtils;

import java.util.List;

/**
 * @author leiming
 * @date 2019/3/20.
 */
public class ResultRiskDistributionTargetListBean extends BaseBean {
    private List<RiskDistributionTargetBean> data;

    public List<RiskDistributionTargetBean> getData() {
        return data;
    }

    public boolean isDataNull() {
        return CommonUtils.judgeListNull(data) == 0;
    }
}
