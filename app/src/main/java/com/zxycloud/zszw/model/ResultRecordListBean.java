package com.zxycloud.zszw.model;

import com.zxycloud.zszw.model.bean.RecordBean;
import com.zxycloud.common.base.BaseBean;

import java.util.List;

/**
 * @author leiming
 * @date 2019/3/20.
 */
public class ResultRecordListBean extends BaseBean {
    private List<RecordBean> data;

    public List<RecordBean> getData() {
        return data;
    }
}
