package com.zxycloud.zszw.model.JpushBean;

public class JPushRiskBean extends JPushBean {
    private final String REFRESH_YES = "yes";
    private final String REFRESH_NO = "no";

    /**
     * hiddenId : 82ddfada7011497ba432b8f58e988293
     * principal : in
     * refresh : yes
     * type : hidden
     */

    private String hiddenId;
    private String principal;
    private String refresh;
    private String type;

    public boolean isRefreshRedPoint() {
        return REFRESH_YES.equals(refresh);
    }
}
