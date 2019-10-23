package com.zxycloud.zszw.model;

import com.zxycloud.zszw.model.bean.RecentStateBean;
import com.zxycloud.common.base.BaseBean;

import java.util.List;

/**
 * @author leiming
 * @date 2019/4/13.
 */
public class ResultRecentStateListBean extends BaseBean {
    private List<RecentStateBean> data;

    public List<RecentStateBean> getData() {
        return data;
    }
}
