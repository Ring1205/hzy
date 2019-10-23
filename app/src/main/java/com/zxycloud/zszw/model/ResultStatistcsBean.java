package com.zxycloud.zszw.model;

import com.zxycloud.zszw.model.bean.StatistcsBean;
import com.zxycloud.common.base.BaseBean;

public class ResultStatistcsBean extends BaseBean {
    /**
     * data : {"patrolTaskCount":32,"patrolPointCount":6,"firstAreaCount":18,"placeCount":9,"devicesCount":29,"malfunctionCount":6,"userCount":1}
     */

    private StatistcsBean data;

    public StatistcsBean getData() {
        return data;
    }

    public void setData(StatistcsBean data) {
        this.data = data;
    }

}
