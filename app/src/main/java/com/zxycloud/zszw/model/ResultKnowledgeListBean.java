package com.zxycloud.zszw.model;

import com.zxycloud.zszw.model.bean.KnowledgeBean;
import com.zxycloud.common.base.BaseBean;

import java.util.List;

/**
 * @author leiming
 * @date 2019/3/20.
 */
public class ResultKnowledgeListBean extends BaseBean {
    private List<KnowledgeBean> data;

    public List<KnowledgeBean> getData() {
        return data;
    }
}
