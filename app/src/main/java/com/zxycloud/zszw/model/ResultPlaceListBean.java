package com.zxycloud.zszw.model;

import com.zxycloud.zszw.model.bean.PlaceBean;
import com.zxycloud.common.base.BaseBean;

import java.util.List;

/**
 * @author leiming
 * @date 2019/3/20.
 */
public class ResultPlaceListBean extends BaseBean {
    private List<PlaceBean> data;

    public List<PlaceBean> getData() {
        return data;
    }
}
