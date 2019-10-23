package com.zxycloud.zszw.model.request;

import com.zxycloud.zszw.model.ChannelBean;
import com.zxycloud.common.base.BaseBean;
import com.zxycloud.common.utils.CommonUtils;

import java.util.List;

/**
 * @author leiming
 * @date 2019/3/20.
 */
public class ResultChannelListBean extends BaseBean {
    private List<ChannelBean> data;

    public List<ChannelBean> getData() {
        return data;
    }

    public boolean isDataNull() {
        return CommonUtils.judgeListNull(data) == 0;
    }
}
