package com.zxycloud.zszw.event;

import com.zxycloud.zszw.model.JpushBean.JPushBean;

/**
 * @author leiming
 * @date 2019/4/8.
 */
public class JPushEvent {

    private JPushBean jPushBean;

    public JPushEvent(JPushBean jPushBean) {
        this.jPushBean = jPushBean;
    }

    public JPushBean getjPushBean() {
        return jPushBean;
    }
}
