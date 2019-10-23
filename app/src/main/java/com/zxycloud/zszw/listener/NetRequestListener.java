package com.zxycloud.zszw.listener;

import com.zxycloud.common.base.BaseBean;

public interface NetRequestListener<TT extends BaseBean> {
    void success(String action, TT baseBean, Object tag);
}
