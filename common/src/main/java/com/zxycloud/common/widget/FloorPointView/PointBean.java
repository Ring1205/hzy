package com.zxycloud.common.widget.FloorPointView;

import android.support.annotation.DrawableRes;
import android.support.annotation.FloatRange;
import android.support.annotation.IntDef;
import android.widget.ImageView;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @author 半寿翁
 * @date 2018/8/28.
 */

public class PointBean {
    /**
     * 横纵坐标是相对坐标还是绝对坐标
     * <p>
     * COORDINATES_RELATIVE 相对坐标：0~1
     * COORDINATES_ABSOLUTE 绝对坐标：在图片上的实际x、y值
     */
    public static final int COORDINATES_RELATIVE = 31;
    public static final int COORDINATES_ABSOLUTE = 32;

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({COORDINATES_RELATIVE, COORDINATES_ABSOLUTE})
    public @interface PointCoordinates {
    }

    /**
     * 展示图片在相对坐标点的位置
     * <p>
     * POSITION_BELOW   点位下
     * POSITION_ABOVE   点位上
     * POSITION_CENTER  图片中心是点位
     * POSITION_LEFT    点位左
     * POSITION_RIGHT   点位右
     */
    public static final int POSITION_BELOW = 21;
    public static final int POSITION_ABOVE = 22;
    public static final int POSITION_CENTER = 23;
    public static final int POSITION_LEFT = 24;
    public static final int POSITION_RIGHT = 25;

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({POSITION_BELOW, POSITION_ABOVE, POSITION_CENTER, POSITION_LEFT, POSITION_RIGHT})
    public @interface PointPositionLimit {
    }

    private double x;
    private double y;
    private String path;
    private int imgRes;
    private ImageView pointView;
    private final int positionLimit;
    private int pointCoordinates;

    private int halfHeight = 0;
    private int halfWidth = 0;

    /**
     * @param x    横坐标
     * @param y    纵坐标
     * @param path 显示文件的路径
     */
    public PointBean(double x, double y, String path, @PointPositionLimit int positionLimit, @PointCoordinates int pointCoordinates) {
        this.x = x;
        this.y = y;
        this.path = path;
        this.positionLimit = positionLimit;
        this.pointCoordinates = pointCoordinates;
    }

    /**
     * @param x      横坐标
     * @param y      纵坐标
     * @param imgRes 显示文件的路径
     */
    public PointBean(double x, double y, @DrawableRes int imgRes, @PointPositionLimit int positionLimit, @PointCoordinates int pointCoordinates) {
        this.x = x;
        this.y = y;
        this.imgRes = imgRes;
        this.positionLimit = positionLimit;
        this.pointCoordinates = pointCoordinates;
    }

    public double getX(double rx) {
        switch (pointCoordinates) {
            case COORDINATES_RELATIVE:
                return x;

            case COORDINATES_ABSOLUTE:
                return x / rx;
        }
        return x;
    }

    public double getY(double ry) {
        switch (pointCoordinates) {
            case COORDINATES_RELATIVE:
                return y;

            case COORDINATES_ABSOLUTE:
                return y / ry;
        }
        return y;
    }

    public String getPath() {
        return path;
    }

    public int getImgRes() {
        return imgRes;
    }

    ImageView getPointView() {
        return pointView;
    }

    void setPointView(ImageView pointView) {
        this.pointView = pointView;
    }

    public PointBean setHalfHeight(int halfHeight) {
        this.halfHeight = halfHeight;
        return this;
    }

    public void setHalfWidth(int halfWidth) {
        this.halfWidth = halfWidth;
    }

    public void resetXY(@FloatRange(from = 0, to = 1) double x, @FloatRange(from = 0, to = 1) double y) {
        this.x = x;
        this.y = y;
        this.pointCoordinates = COORDINATES_RELATIVE;
    }

    int getHalfWidth() {
        return halfWidth;
    }

    int getHalfHeight() {
        return halfHeight;
    }

    public int getPositionLimit() {
        return positionLimit;
    }

    public int getPointCoordinates() {
        return pointCoordinates;
    }
}
