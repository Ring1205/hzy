package com.zxycloud.zszw.model;

import com.zxycloud.zszw.model.bean.VideoBean;
import com.zxycloud.common.base.BaseBean;

import java.util.List;

/**
 * @author leiming
 * @date 2019/3/25.
 */
public class ResultVideoListBean extends BaseBean {
    private List<VideoBean> data;

    public List<VideoBean> getData() {
        return data;
    }
}
