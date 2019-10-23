package com.zxycloud.common.utils;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.IdRes;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.PopupWindow;

import com.zxycloud.common.R;

/**
 * Created by cuijunling on 2016/11/1.
 */
public class PopWinDownUtil {
    private View contentView;
    private View relayView;
    private PopupWindow popupWindow;

    public PopWinDownUtil(View contentView, View relayView) {
        this.contentView = contentView;
        this.relayView = relayView;
        init();
    }

    public void init() {
        //内容，高度，宽度
        popupWindow = new PopupWindow(contentView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        //动画效果
        popupWindow.setAnimationStyle(R.style.AnimationTopFade);
        //菜单背景色
        ColorDrawable dw = new ColorDrawable(Color.TRANSPARENT);
        popupWindow.setBackgroundDrawable(dw);
        popupWindow.setOutsideTouchable(true);
        //关闭事件
        popupWindow.setInputMethodMode(PopupWindow.INPUT_METHOD_NEEDED);
        popupWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                if (onDismissLisener != null) {
                    onDismissLisener.onDismiss();
                }
            }
        });
    }

    public void show() {
        if (popupWindow != null)
            popupWindow.showAsDropDown(relayView);
    }

    public void hide() {
        if (popupWindow != null && popupWindow.isShowing())
            popupWindow.dismiss();
    }

    private OnDismissLisener onDismissLisener;

    public void setOnDismissListener(OnDismissLisener onDismissLisener) {
        this.onDismissLisener = onDismissLisener;
    }

    public interface OnDismissLisener {
        void onDismiss();
    }

    public boolean isShowing() {
        return popupWindow.isShowing();
    }

    public void setWidth(int width) {
        popupWindow.setWidth(width);
    }

    public <T extends View> T getView(@IdRes int id) {
        return contentView.findViewById(id);
    }
}
