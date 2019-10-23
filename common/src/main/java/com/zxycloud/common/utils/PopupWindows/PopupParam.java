package com.zxycloud.common.utils.PopupWindows;

import android.support.annotation.FloatRange;
import android.support.annotation.IntDef;
import android.view.Gravity;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class PopupParam {
    public static final int DEFAULT_NUM = 99999999;

    public static final int RELATIVE_VIEW = 0x01;
    public static final int RELATIVE_SCREEN = 0x02;

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({RELATIVE_VIEW, RELATIVE_SCREEN})
    @interface RelativeReference {
    }

    public static final int SHOW_POSITION_NO_GRAVITY = Gravity.NO_GRAVITY;
    public static final int SHOW_POSITION_BOTTOM = Gravity.BOTTOM;
    public static final int SHOW_POSITION_TOP = Gravity.TOP;
    public static final int SHOW_POSITION_RIGHT = Gravity.END;
    public static final int SHOW_POSITION_LEFT = Gravity.START;

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({SHOW_POSITION_NO_GRAVITY, SHOW_POSITION_BOTTOM, SHOW_POSITION_TOP, SHOW_POSITION_RIGHT, SHOW_POSITION_LEFT})
    @interface ShowPosition {
    }

    /**
     * 位置是相对点击控件还是相对屏幕
     * <p>
     * {@link PopupParam#RELATIVE_VIEW} 相对被点击控件
     * {@link PopupParam#RELATIVE_SCREEN} 相对屏幕
     */
    private int relativeReference = RELATIVE_VIEW;
    /**
     * 相对于参考项的位置关系（如在被点击控件上方）
     * <p>
     * {@link PopupParam#SHOW_POSITION_NO_GRAVITY} 没有相对关系——左上角
     * {@link PopupParam#SHOW_POSITION_BOTTOM} 底部
     * {@link PopupParam#SHOW_POSITION_TOP} 顶部
     * {@link PopupParam#SHOW_POSITION_RIGHT} 右侧
     * {@link PopupParam#SHOW_POSITION_LEFT} 左侧
     */
    private int showPosition = SHOW_POSITION_NO_GRAVITY;

    /**
     * 弹窗宽度
     * <p>
     * 若需设置，默认相对屏幕是取屏幕宽度，相对被点击控件是取控件宽度
     */
    private int popWindowWidth = DEFAULT_NUM;

    /**
     * 是否设置弹窗宽度
     */
    private boolean isSetWindowWidth = true;

    /**
     * 弹窗高度
     * <p>
     * 若需设置，默认相对屏幕是取屏幕高度，相对被点击控件是取控件高度
     */
    private int popWindowHeight = DEFAULT_NUM;

    /**
     * 是否设置弹窗高度
     */
    private boolean isSetWindowHeight = false;

    /**
     * 顶部微调留白，
     * <p>
     * 默认:
     * 被点击控件为左下角纵坐标
     * 屏幕为屏幕左上角(0)
     */
    private int marginTop = DEFAULT_NUM;
    /**
     * 左侧微调留白
     * <p>
     * 默认:
     * 被点击控件为左下角横坐标
     * 屏幕为屏幕左上角(0)
     */
    private int marginLeft = DEFAULT_NUM;

    private float alpha = 0.8f;

    public PopupParam(@RelativeReference int relativeReference, @ShowPosition int... showPositions) {
        this.relativeReference = relativeReference;
        for (int position : showPositions)
            this.showPosition = this.showPosition | position;
    }

    public int getRelativeReference() {
        return relativeReference;
    }

    public int getShowPosition() {
        return showPosition;
    }

    public int getPopWindowWidth() {
        return popWindowWidth;
    }

    /**
     * 设置弹窗宽度
     *
     * @param popWindowWidth 弹窗宽度
     * @return 当前位置设置类
     */
    public PopupParam setPopWindowWidth(int popWindowWidth) {
        isSetWindowWidth = true;
        this.popWindowWidth = popWindowWidth;
        return this;
    }

    /**
     * 设置弹窗宽度，使用默认宽度{@link PopupParam#popWindowWidth}
     *
     * @return 当前位置设置类
     */
    public PopupParam setPopWindowWidth() {
        isSetWindowWidth = true;
        return this;
    }

    public int getPopWindowHeight() {
        return popWindowHeight;
    }

    /**
     * 设置弹窗高度
     *
     * @param popWindowHeight 弹窗高度
     * @return 当前位置设置类
     */
    public PopupParam setPopWindowHeight(int popWindowHeight) {
        this.popWindowHeight = popWindowHeight;
        isSetWindowHeight = true;
        return this;
    }

    /**
     * 设置弹窗宽度，使用默认宽度{@link PopupParam#popWindowWidth}
     *
     * @return 当前位置设置类
     */
    public PopupParam setPopWindowHeight() {
        isSetWindowHeight = true;
        return this;
    }

    public PopupParam setWindowWidth(boolean setWindowWidth) {
        isSetWindowWidth = setWindowWidth;
        return this;
    }

    public PopupParam setWindowHeight(boolean setWindowHeight) {
        isSetWindowHeight = setWindowHeight;
        return this;
    }

    public int getMarginTop() {
        return marginTop;
    }

    public PopupParam setMarginTop(int marginTop) {
        this.marginTop = marginTop;
        return this;
    }

    public int getMarginLeft() {
        return marginLeft;
    }

    public PopupParam setMarginLeft(int marginLeft) {
        this.marginLeft = marginLeft;
        return this;
    }

    public boolean isSetWindowWidth() {
        return isSetWindowWidth;
    }

    public boolean isSetWindowHeight() {
        return isSetWindowHeight;
    }

    public float getAlpha() {
        return alpha;
    }

    /**
     * 设置弹窗透明度
     *
     * @param alpha 透明度
     */
    public void setAlpha(@FloatRange(from = 0, to = 1) float alpha) {
        this.alpha = alpha;
    }
}
