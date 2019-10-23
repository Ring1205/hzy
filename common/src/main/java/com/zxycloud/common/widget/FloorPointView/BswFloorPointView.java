package com.zxycloud.common.widget.FloorPointView;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.RectF;
import android.support.annotation.DrawableRes;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.zxycloud.common.utils.CommonUtils;
import com.zxycloud.common.utils.GlideUtils;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.List;

/**
 * 在可缩放的背景图上标注点位
 * 此控件是根据点x/y位于地图百分比标注的，这也是现阶段比较准确的一种使用方式；
 * 若使用坐标的需注意，这里使用的底图承载控件是PhotoView，会根据屏幕大小约束图片，因此需要添加一个
 *
 * @author 半寿翁
 * @date 2018/8/28.
 */

public class BswFloorPointView extends RelativeLayout {
    public static final int KEEP_SIZE = 56;
    public static final int CHANGE_SIZE = 57;

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({KEEP_SIZE, CHANGE_SIZE})
    @interface SizeLimit {
    }


    /**
     * 图片相对屏幕宽高计算后的
     * <p>
     * cw：计算后的宽
     * ch：计算后的高
     */
    private double cw;
    private double ch;
    /**
     * 图片原始宽高
     * <p>
     * rw：原始宽
     * rh：原始高
     */
    private double rw;
    private double rh;

    private int imgResId = -1;

    private Context mContext;
    private boolean isPrepare = false;
    private int startL = 0;
    private int startR = 0;

    public PhotoView photoZoom;

    private List<PointBean> pointList;

    private boolean canMarker = false;

    @PointBean.PointPositionLimit
    private int positionLimit = PointBean.POSITION_CENTER;
    @PointBean.PointCoordinates
    private int pointCoordinates = PointBean.COORDINATES_RELATIVE;

    private String bgPath;

    private ViewGroup.LayoutParams lpM = new LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT);

    private ViewGroup.LayoutParams lpW = new LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT);

    private ViewGroup.LayoutParams lp30;
    private RectF rectTemp;

    private int size = KEEP_SIZE;

    public BswFloorPointView(Context context) {
        this(context, null);
    }

    public BswFloorPointView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BswFloorPointView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        photoZoom = new PhotoView(context);
        photoZoom.setOnMatrixChangeListener(onMatrixChangedListener);

        lp30 = new LayoutParams(
                Util.dp2px(mContext, 30),
                Util.dp2px(mContext, 30));
    }

    private void moveGif(RectF rect) {
        int moveL = (int) rect.left;
        int moveT = (int) rect.top;
        int moveR = (int) rect.right;

        double startLength = startR - startL;
        double moveLength = moveR - moveL;

        double multiple;
        if (moveLength == 0 || startLength == 0) {
            multiple = 1;
        } else {
            multiple = moveLength / startLength;
        }
        setGifLayout(multiple, moveL, moveT);
    }

    public BswFloorPointView setCanMarker(boolean canMarker) {
        this.canMarker = canMarker;
        return this;
    }

    public BswFloorPointView setImgResId(@DrawableRes int imgResId) {
        this.imgResId = imgResId;
        return this;
    }

    public BswFloorPointView setPositionLimit(@PointBean.PointPositionLimit int positionLimit) {
        this.positionLimit = positionLimit;
        return this;
    }

    public BswFloorPointView setPointCoordinates(@PointBean.PointCoordinates int pointCoordinates) {
        this.pointCoordinates = pointCoordinates;
        return this;
    }

    private void setGifLayout(double multiple, double moveL, double moveT) {
        if (CommonUtils.judgeListNull(pointList) == 0) {
            return;
        }
        for (PointBean pointBean : pointList) {
            double halfHeight;
            double halfWidth;
            switch (size) {
                case CHANGE_SIZE:
                    halfHeight = pointBean.getHalfHeight() * multiple;
                    halfWidth = pointBean.getHalfWidth() * multiple;
                    break;

                case KEEP_SIZE:
                    halfHeight = pointBean.getHalfHeight();
                    halfWidth = pointBean.getHalfWidth();
                    break;

                default:
                    halfHeight = pointBean.getHalfHeight();
                    halfWidth = pointBean.getHalfWidth();
                    break;
            }
            if (halfHeight == 0 || halfWidth == 0 || cw == 0 || ch == 0) {
                return;
            }
            double xTemp = (pointBean.getX(rw) * cw * multiple + moveL);
            double yTemp = (pointBean.getY(rh) * ch * multiple + moveT);
            switch (pointBean.getPositionLimit()) {
                case PointBean.POSITION_CENTER:
                    pointBean.getPointView().layout((int) (xTemp - halfWidth), (int) (yTemp - halfHeight), (int) (xTemp + halfWidth), (int) (yTemp + halfHeight));
                    pointBean.getPointView().setVisibility(VISIBLE);
                    break;

                case PointBean.POSITION_ABOVE:
                    pointBean.getPointView().layout((int) (xTemp - halfWidth), (int) (yTemp - halfHeight * 2), (int) (xTemp + halfWidth), (int) (yTemp));
                    pointBean.getPointView().setVisibility(VISIBLE);
                    break;

                case PointBean.POSITION_BELOW:
                    pointBean.getPointView().layout((int) (xTemp - halfWidth), (int) yTemp, (int) (xTemp + halfWidth), (int) (yTemp + halfHeight * 2));
                    pointBean.getPointView().setVisibility(VISIBLE);
                    break;

                case PointBean.POSITION_LEFT:
                    pointBean.getPointView().layout((int) (xTemp - halfWidth * 2), (int) (yTemp - halfHeight), (int) xTemp, (int) (yTemp + halfHeight));
                    pointBean.getPointView().setVisibility(VISIBLE);
                    break;

                case PointBean.POSITION_RIGHT:
                    pointBean.getPointView().layout((int) xTemp, (int) (yTemp - halfHeight), (int) (xTemp + halfWidth * 2), (int) (yTemp + halfHeight));
                    pointBean.getPointView().setVisibility(VISIBLE);
                    break;

                default:
                    pointBean.getPointView().layout((int) (xTemp - halfWidth), (int) (yTemp - halfHeight), (int) (xTemp + halfWidth), (int) (yTemp + halfHeight));
                    pointBean.getPointView().setVisibility(VISIBLE);
                    break;
            }
        }
    }

    public BswFloorPointView setPointList(List<PointBean> pointList) throws NullPointerException {
        removeAllViews();
        this.pointList = new ArrayList<>();
        for (PointBean pointBean : pointList) {
            pointBean.setPointView(new ImageView(mContext));
            this.pointList.add(pointBean);
        }
        this.pointList = pointList;
        return this;
    }

    public BswFloorPointView setFloorBackground(String bgPath) {
        this.bgPath = bgPath;
        return this;
    }

    /**
     * 设置图片尺寸随背景图变化、或是固定尺寸
     *
     * @param size 尺寸情况
     */
    public BswFloorPointView setSize(@SizeLimit int size) {
        this.size = size;
        return this;
    }

    public void paint() {
        photoZoom.addOnLayoutChangeListener(new OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                cw = CommonUtils.measureView().getWidth(photoZoom);
                updateList();
            }
        });
        addView(photoZoom, lpM);
        SimpleTarget simpleTarget = new SimpleTarget<Bitmap>(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL) {
            @Override
            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                photoZoom.setImageBitmap(resource);
                rw = resource.getWidth();
                rh = resource.getHeight();
                updateList();
            }
        };
        GlideUtils.loadImageView(mContext, bgPath, simpleTarget);

        if (CommonUtils.judgeListNull(pointList) > 0) {
            for (int i = 0; i < pointList.size(); i++) {
                final PointBean pointBean = pointList.get(i);
                final ImageView imageView = pointBean.getPointView();
                removeView(imageView);
                addView(imageView, lp30);
                int imgRes = pointBean.getImgRes();
                final int finalI = i;
                SimpleTarget target = new SimpleTarget<Bitmap>(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL) {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        imageView.setImageBitmap(resource);
                        pointList.get(finalI)
                                .setHalfHeight(resource.getHeight() / 2)
                                .setHalfWidth(resource.getWidth() / 2);
                    }
                };
                if (imgRes == 0) {
                    GlideUtils.loadImageView(mContext, pointBean.getPath(), target);
                } else {
                    GlideUtils.loadImageView(mContext, imgRes, target);
                }
            }
        }

        if (canMarker) {
            photoZoom.setOnPhotoTapListener(onPhotoTapListener);
        }
    }

    private void updateList() {
        // 防止获取ch时除数为零
        if (cw * rw == 0)
            return;
        // 计算ch
        if (cw == 0 || ch == 0)
            ch = rh / rw * cw;
        // 只有当cw、ch都有值时刷新
        if (cw * ch == 0)
            return;
        CommonUtils.threadPoolExecute(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(25);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    ((Activity) mContext).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            photoZoom.update();
                        }
                    });
                }
            }
        });
    }

    private OnPhotoTapListener onPhotoTapListener = new OnPhotoTapListener() {
        @Override
        public void onPhotoTap(ImageView view, float x, float y) {
            if (CommonUtils.judgeListNull(pointList) == 0) {
                pointList = new ArrayList<>();
                final ImageView imageView = new ImageView(mContext);
                imageView.setVisibility(INVISIBLE);
                addView(imageView, lp30);
                final PointBean pointBean = new PointBean(x, y, imgResId, positionLimit, PointBean.COORDINATES_RELATIVE);
                SimpleTarget target = new SimpleTarget<Bitmap>(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL) {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        imageView.setImageBitmap(resource);
                        pointBean.setHalfWidth(resource.getWidth() / 2);
                        pointBean.setHalfHeight(resource.getHeight() / 2);
                        pointBean.setPointView(imageView);
                        pointList.add(pointBean);
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    Thread.sleep(50);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                } finally {
                                    ((Activity) mContext).runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            moveGif(rectTemp);
                                        }
                                    });
                                }
                            }
                        }).start();
                    }
                };
                GlideUtils.loadImageView(mContext, imgResId, target);

            } else {
                pointList.get(0).resetXY(x, y);
                moveGif(rectTemp);
            }
        }
    };

    private OnMatrixChangedListener onMatrixChangedListener = new OnMatrixChangedListener() {

        @Override
        public void onMatrixChanged(RectF rect) {
            rectTemp = rect;
            if (isPrepare) {
                moveGif(rect);
            } else {
                startL = (int) rect.left;
                startR = (int) rect.right;
                if (startR != 0) {
                    isPrepare = true;
                    setGifLayout(1, 0, 0);
                }
            }
        }
    };

    public List<Size> getSizeList() {
        List<Size> sizeList = new ArrayList<>();
        if (CommonUtils.judgeListNull(pointList) > 0) {
            for (PointBean pointBean : pointList) {
                sizeList.add(new Size(rw, rh, pointBean.getX(rw), pointBean.getY(rh), pointCoordinates));
            }
        }
        return sizeList;
    }

    /**
     * 是否已添加点位
     *
     * @return
     */
    public boolean isMarked() {
        return CommonUtils.judgeListNull(pointList) > 0;
    }

    public Size getSize() {
        PointBean pointBean;
        if (CommonUtils.judgeListNull(pointList) > 0) {
            pointBean = pointList.get(0);
            return new Size(rw, rh, pointBean.getX(rw), pointBean.getY(rh), pointCoordinates);
        } else {
            return null;
        }
    }

    public void update() {
        photoZoom.update();
    }

    public class Size {
        private final double width;
        private final double height;
        private final double px, py;
        private final int pointCoordinates;

        Size(double width, double height, double px, double py, int pointCoordinates) {
            this.width = width;
            this.height = height;
            this.px = px;
            this.py = py;
            this.pointCoordinates = pointCoordinates;
        }

        /**
         * 获取相对横坐标
         *
         * @return 横坐标
         */
        public double getPx() {
            switch (pointCoordinates) {
                case PointBean.COORDINATES_ABSOLUTE:
                    return px * rw;

                case PointBean.COORDINATES_RELATIVE:
                    return px;
            }
            return px;
        }

        /**
         * 获取相对纵坐标
         *
         * @return 纵坐标
         */
        public double getPy() {
            switch (pointCoordinates) {
                case PointBean.COORDINATES_ABSOLUTE:
                    return py * rh;

                case PointBean.COORDINATES_RELATIVE:
                    return py;
            }
            return py;
        }

        /**
         * 获取图片高
         *
         * @return 图片高
         */
        public double getHeight() {
            return height;
        }

        /**
         * 获取图片宽
         *
         * @return 图片宽
         */
        public double getWidth() {
            return width;
        }
    }
}
