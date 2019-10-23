package com.zxycloud.zszw.model;

import com.zxycloud.common.utils.db.DbClass;
import com.zxycloud.common.utils.db.Ignore;
import com.zxycloud.common.utils.db.PrimaryKey;
import com.zxycloud.common.utils.db.Require;

/**
 * @author leiming
 * @date 2019/3/19.
 */
@DbClass
public class HistoryBean {
    @Ignore
    public static String QUERY_TYPE = "type";
    @Ignore
    public static String USER_ID = "userId";

    @PrimaryKey
    private long id;

    @SuppressWarnings("FieldCanBeLocal")
    @Require
    private int type;

    @Require
    private String searchItem;

    /**
     * 用于数据库工具解析
     */
    public HistoryBean() {

    }

    public HistoryBean(@HistoryTypeBean.HistoryType int type, String searchItem, String userId) {
        id = System.currentTimeMillis();
        this.type = type;
        this.searchItem = searchItem;
    }

    public String getSearchItem() {
        return searchItem;
    }
}
