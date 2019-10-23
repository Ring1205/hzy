package com.zxycloud.zszw.model;

import android.support.annotation.MenuRes;
import android.support.annotation.StringRes;

public class IconBean {
    @StringRes
    private int nameID;
    @MenuRes
    private int iconID;

    public IconBean(int nameID, int iconID) {
        this.nameID = nameID;
        this.iconID = iconID;
    }

    public int getNameID() {
        return nameID;
    }

    public void setNameID(int nameID) {
        this.nameID = nameID;
    }

    public int getIconID() {
        return iconID;
    }

    public void setIconID(int iconID) {
        this.iconID = iconID;
    }
}
