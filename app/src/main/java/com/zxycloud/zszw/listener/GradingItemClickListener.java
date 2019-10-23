package com.zxycloud.zszw.listener;

import com.zxycloud.common.base.BaseBean;

public interface GradingItemClickListener {
    /**
     * 展开子Item
     * @param bean
     */
    void onExpandChildren(BaseBean bean);

    /**
     * 隐藏子Item
     * @param bean
     */
    void onHideChildren(BaseBean bean);
}
