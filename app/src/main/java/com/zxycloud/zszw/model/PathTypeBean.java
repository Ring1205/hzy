package com.zxycloud.zszw.model;

import android.text.TextUtils;

public class PathTypeBean {

    public static final String SPLIT = "PathTypeBean&&&&PathTypeBean";

    String type;
    String dataPath;
    String loadUrl;

    public PathTypeBean() {
    }

    public PathTypeBean(String type, String dataPath, String loadUrl) {
        this.type = type;
        this.dataPath = dataPath;
        this.loadUrl = loadUrl;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDataPath() {
        return TextUtils.isEmpty(dataPath) ? null : dataPath.split(SPLIT)[0];
    }

    public String getVideoImgPath() {
        String[] result = dataPath.split(SPLIT);
        return result.length > 1 ? result[1] : "";
    }

    public void setDataPath(String dataPath) {
        this.dataPath = dataPath;
    }

    public String getLoadUrl() {
        return loadUrl;
    }

    public void setLoadUrl(String loadUrl) {
        this.loadUrl = loadUrl;
    }
}
