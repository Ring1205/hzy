package com.zxycloud.common.widget;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.StringRes;

import com.zxycloud.common.R;

public class BswProgressDialog extends ProgressDialog {
    private int messageId = 0;

    public BswProgressDialog(Context context, int theme) {
        super(context, theme);
    }

    public BswProgressDialog(Context context) {
        super(context);
    }

    public BswProgressDialog(Context context, int theme,
                             @StringRes int messageId) {
        super(context, theme);
        this.messageId = messageId;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.laod_progressbar_layout);
        //dialog弹出后点击物理返回键Dialog消失，但是点击屏幕不会消失
        this.setCanceledOnTouchOutside(false);

        //dialog弹出后点击屏幕或物理返回键，dialog都不消失
        //this.setCancelable(false);
    }
}