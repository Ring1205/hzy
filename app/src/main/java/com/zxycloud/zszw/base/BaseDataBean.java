package com.zxycloud.zszw.base;

import com.zxycloud.common.utils.CommonUtils;
import com.zxycloud.common.utils.StringFormatUtils;

import java.io.Serializable;

/**
 * @author leiming
 * @date 2019/3/18.
 */
public class BaseDataBean implements Serializable {
    protected StringFormatUtils formatUtils;

    public BaseDataBean() {
        formatUtils = CommonUtils.string();
    }
}
