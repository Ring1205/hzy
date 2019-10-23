package com.zxycloud.zszw.model;

import com.zxycloud.zszw.model.bean.LinkmanBean;
import com.zxycloud.common.base.BaseBean;

import java.util.List;

/**
 * @author leiming
 * @date 2019/3/26.
 */
public class ResultLinkmanListBean extends BaseBean {
    private List<LinkmanBean> data;

    public List<LinkmanBean> getData() {
        return data;
    }
}
