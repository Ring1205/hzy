package com.zxycloud.zszw.model.request;

import android.support.annotation.DrawableRes;
import android.text.TextUtils;

/**
 * @author leiming
 * @date 2018/6/21.
 */

public class ImageAddBean {
    private int imgResId;
    private String imgPath;
    private boolean isFromCapture = false;

    public ImageAddBean(@DrawableRes int imgResId) {
        this.imgResId = imgResId;
    }

    public ImageAddBean(String imgPath, boolean isFromCapture) {
        this.imgPath = imgPath;
        this.isFromCapture = isFromCapture;
    }

    public int getImgResId() {
        return imgResId;
    }

    public String getImgPath() {
        return TextUtils.isEmpty(imgPath) ? "" : imgPath;
    }

    public String getImgPath(String divideString) {
        if (TextUtils.isEmpty(divideString) || !imgPath.contains(divideString))
            return TextUtils.isEmpty(imgPath) ? "" : imgPath;
        else {
            return TextUtils.isEmpty(imgPath) ? "" : imgPath.substring(0, imgPath.indexOf(divideString));
        }
    }


    public boolean isFromCapture() {
        return isFromCapture;
    }
}
