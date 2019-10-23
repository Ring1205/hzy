package com.zxycloud.common.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RadialGradient;
import android.graphics.RectF;
import android.graphics.Region;
import android.graphics.Shader;
import android.os.Build;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import com.zxycloud.common.R;

public class LCardView extends FrameLayout {
    public static final int ADSORPTION = 0;
    public static final int LINEAR = 1;
    private int shadowFluidShape = ADSORPTION;
    private final int defaultShadowSize = 12;
    private final int defaultShadowStartAlpha = 10;
    private int elevation = 0;
    private int leftSize = defaultShadowSize, topSize = defaultShadowSize, rightSize = defaultShadowSize, bottomSize = defaultShadowSize;
    private int defaultShadowColor = Color.parseColor("#05000000");
    private int defaultCardBackgroundColor = Color.WHITE;
    private int[] colors = new int[]{defaultShadowColor, defaultShadowColor, Color.parseColor("#00000000"), Color.parseColor("#00000000")};
    private int shadowColor = defaultShadowColor;
    private int cardBackgroundColor = defaultCardBackgroundColor;
    private int cornerRadius = 0;
    private boolean elevationAffectShadowColor = false;
    private boolean elevationAffectShadowSize = false;
    private int leftTopCornerRadius = 0;
    private int rightTopCornerRadius = 0;
    private int rightBottomCornerRadius = 0;
    private int leftBottomCornerRadius = 0;
    private int viewWidth;
    private int viewHeight;
    private Path mPath = new Path();
    private Path highVerPath = new Path();
    private Path mContentPath = new Path();
    private Path mShadowPath = new Path();
    private Paint paint = new Paint();
    private Paint bgColorPaint = new Paint();
    private Paint bgPaint = new Paint();
    private Paint linePaint = new Paint();
    private Paint erasePaint = new Paint();
    private Paint pathPaint = new Paint();
    RadialGradient ltrg;
    RadialGradient rtrg;
    RadialGradient rbrg;
    RadialGradient lbrg;
    LinearGradient t, r, b, l;
    private int shadowAlpha = defaultShadowStartAlpha;
    float percent = 0.33f;
    int xOffset = 0;
    int yOffset = 0;

    public LCardView(@NonNull Context context) {
        this(context, null);
    }

    public LCardView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LCardView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setWillNotDraw(false);
        TypedArray typedArray = context.getTheme().obtainStyledAttributes(attrs, R.styleable.LCardView, defStyleAttr, 0);
        int indexCount = typedArray.getIndexCount();
        int allShadowSize = -1;
        for (int i = 0; i < indexCount; i++) {
            int index = typedArray.getIndex(i);
            if (index == R.styleable.LCardView_leftShadowWidth) {
                leftSize = typedArray.getDimensionPixelSize(index, defaultShadowSize);
            } else if (index == R.styleable.LCardView_topShadowHeight) {
                topSize = typedArray.getDimensionPixelSize(index, defaultShadowSize);
            } else if (index == R.styleable.LCardView_rightShadowWidth) {
                rightSize = typedArray.getDimensionPixelSize(index, defaultShadowSize);
            } else if (index == R.styleable.LCardView_bottomShadowHeight) {
                bottomSize = typedArray.getDimensionPixelSize(index, defaultShadowSize);
            } else if (index == R.styleable.LCardView_shadowSize) {
                allShadowSize = typedArray.getDimensionPixelSize(index, -1);
            } else if (index == R.styleable.LCardView_shadowColor) {
                shadowColor = typedArray.getColor(index, defaultShadowColor);
            } else if (index == R.styleable.LCardView_shadowStartAlpha) {
                shadowAlpha = typedArray.getInt(index, defaultShadowStartAlpha);
            } else if (index == R.styleable.LCardView_shadowFluidShape) {
                shadowFluidShape = typedArray.getInt(index, ADSORPTION);
            } else if (index == R.styleable.LCardView_cardBackgroundColor) {
                cardBackgroundColor = typedArray.getColor(index, defaultCardBackgroundColor);
            } else if (index == R.styleable.LCardView_cornerRadius) {
                cornerRadius = typedArray.getDimensionPixelSize(index, 0);
            } else if (index == R.styleable.LCardView_leftTopCornerRadius) {
                leftTopCornerRadius = typedArray.getDimensionPixelSize(index, 0);
            } else if (index == R.styleable.LCardView_leftBottomCornerRadius) {
                leftBottomCornerRadius = typedArray.getDimensionPixelSize(index, 0);
            } else if (index == R.styleable.LCardView_rightTopCornerRadius) {
                rightTopCornerRadius = typedArray.getDimensionPixelSize(index, 0);
            } else if (index == R.styleable.LCardView_rightBottomCornerRadius) {
                rightBottomCornerRadius = typedArray.getDimensionPixelSize(index, 0);
            } else if (index == R.styleable.LCardView_elevation) {
                elevation = typedArray.getDimensionPixelSize(index, 0);
            } else if (index == R.styleable.LCardView_elevationAffectShadowColor) {
                elevationAffectShadowColor = typedArray.getBoolean(index, false);
            } else if (index == R.styleable.LCardView_elevationAffectShadowSize) {
                elevationAffectShadowSize = typedArray.getBoolean(index, false);
            } else if (index == R.styleable.LCardView_xOffset) {
                xOffset = typedArray.getDimensionPixelSize(index, 0);
            } else if (index == R.styleable.LCardView_yOffset) {
                yOffset = typedArray.getDimensionPixelSize(index, 0);
            }
        }

        typedArray.recycle();

        paint.setAntiAlias(true);
        paint.setDither(true);
        bgPaint.setAntiAlias(true);
        bgPaint.setDither(true);
        bgColorPaint.setAntiAlias(true);
        bgColorPaint.setDither(true);
        linePaint.setAntiAlias(true);
        linePaint.setDither(true);
        linePaint.setStyle(Paint.Style.STROKE);
        linePaint.setStrokeWidth(1);
        erasePaint.setStrokeWidth(1);
        erasePaint.setStyle(Paint.Style.STROKE);
        erasePaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));

        pathPaint.setDither(true);
        pathPaint.setAntiAlias(true);
        pathPaint.setColor(Color.WHITE);

        initColors(shadowColor);

        if (allShadowSize != -1) {
            leftSize = rightSize = bottomSize = topSize = allShadowSize;
        }

        if (elevationAffectShadowSize) {
            leftSize = rightSize = bottomSize = topSize = elevation + 12;
        }

        if (cornerRadius != 0) {
            leftTopCornerRadius = leftBottomCornerRadius = rightTopCornerRadius = rightBottomCornerRadius = cornerRadius;
        }

        if (xOffset > rightSize) {
            xOffset = rightSize;
        }
        if (xOffset < -leftSize) {
            xOffset = -leftSize;
        }
        if (yOffset > bottomSize) {
            yOffset = bottomSize;
        }
        if (yOffset < -topSize) {
            yOffset = -topSize;
        }

        super.setPadding(leftSize + xOffset,
                topSize + yOffset,
                rightSize - xOffset,
                bottomSize - yOffset);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        viewWidth = getMeasuredWidth();
        viewHeight = getMeasuredHeight();
        createDrawables();
    }

    private void judgeEdge() {
        if (leftSize > viewWidth / 4) {
            leftSize = viewWidth / 4;
        }
        if (rightSize > viewWidth / 4) {
            rightSize = viewWidth / 4;
        }
        if (topSize > viewHeight / 4) {
            topSize = viewHeight / 4;
        }
        if (topSize > viewHeight / 4) {
            topSize = viewHeight / 4;
        }
    }

    private void createDrawables() {
        judgeEdge();

        int min = Math.min(leftSize + leftTopCornerRadius, topSize + leftTopCornerRadius);
        if (min == 0) {
            ltrg = null;
        } else {
            float start = leftTopCornerRadius / (float) min;
            float center = (1 - start) * percent + start;
            float center2 = (1 - center) / 2 + center;
            ltrg = new RadialGradient(leftSize + leftTopCornerRadius, topSize + leftTopCornerRadius, min, colors, new float[]{start, center, center2, 1}, Shader.TileMode.CLAMP);
        }

        int min2 = Math.min(rightSize + rightTopCornerRadius, topSize + rightTopCornerRadius);
        if (min2 == 0) {
            rtrg = null;
        } else {
            float start = rightTopCornerRadius / (float) min2;
            float center = (1 - start) * percent + start;
            float center2 = (1 - center) / 2 + center;
            rtrg = new RadialGradient(viewWidth - rightSize - rightTopCornerRadius, topSize + rightTopCornerRadius, min2, colors, new float[]{start, center, center2, 1}, Shader.TileMode.CLAMP);
        }

        int min3 = Math.min(rightSize + rightBottomCornerRadius, bottomSize + rightBottomCornerRadius);
        if (min3 == 0) {
            rbrg = null;
        } else {
            float start = rightBottomCornerRadius / (float) min3;
            float center = (1 - start) * percent + start;
            float center2 = (1 - center) / 2 + center;
            rbrg = new RadialGradient(viewWidth - rightSize - rightBottomCornerRadius, viewHeight - bottomSize - rightBottomCornerRadius, min3, colors, new float[]{start, center, center2, 1}, Shader.TileMode.CLAMP);
        }

        int min4 = Math.min(leftSize + leftBottomCornerRadius, bottomSize + leftBottomCornerRadius);
        if (min4 == 0) {
            lbrg = null;
        } else {
            float start = leftBottomCornerRadius / (float) min4;
            float center = (1 - start) * percent + start;
            float center2 = (1 - center) / 2 + center;
            lbrg = new RadialGradient(leftSize + leftBottomCornerRadius, viewHeight - bottomSize - leftBottomCornerRadius, min4, colors, new float[]{start, center, center2, 1}, Shader.TileMode.CLAMP);
        }

        t = new LinearGradient(leftSize + leftTopCornerRadius, topSize, leftSize + leftTopCornerRadius, 0, colors, new float[]{0, percent, (1 - percent) / 2 + percent, 1}, Shader.TileMode.CLAMP);

        r = new LinearGradient(viewWidth - rightSize, topSize + rightTopCornerRadius, viewWidth, topSize + rightTopCornerRadius, colors, new float[]{0, percent, (1 - percent) / 2 + percent, 1}, Shader.TileMode.CLAMP);

        b = new LinearGradient(leftSize + leftBottomCornerRadius, viewHeight - bottomSize, leftSize + leftBottomCornerRadius, viewHeight, colors, new float[]{0, percent, (1 - percent) / 2 + percent, 1}, Shader.TileMode.CLAMP);

        l = new LinearGradient(leftSize, topSize + leftTopCornerRadius, 0, topSize + leftTopCornerRadius, colors, new float[]{0, percent, (1 - percent) / 2 + percent, 1}, Shader.TileMode.CLAMP);
    }

    private void measureContentPath() {
        mShadowPath.reset();
        float startX = leftSize;
        float startY = topSize + leftTopCornerRadius;
        mShadowPath.moveTo(startX, startY);
        mShadowPath.arcTo(new RectF(startX, topSize, leftTopCornerRadius * 2 + startX, topSize + leftTopCornerRadius * 2), 180, 90);
        mShadowPath.lineTo(viewWidth - rightSize - rightTopCornerRadius, topSize);
        mShadowPath.arcTo(new RectF(viewWidth - rightSize - rightTopCornerRadius * 2, topSize, viewWidth - rightSize, topSize + rightTopCornerRadius * 2), 270, 90);
        mShadowPath.lineTo(viewWidth - rightSize, viewHeight - bottomSize - rightBottomCornerRadius);
        mShadowPath.arcTo(new RectF(viewWidth - rightSize - rightBottomCornerRadius * 2, viewHeight - bottomSize - rightBottomCornerRadius * 2, viewWidth - rightSize, viewHeight - bottomSize), 0, 90);
        mShadowPath.lineTo(leftSize + leftBottomCornerRadius, viewHeight - bottomSize);
        mShadowPath.arcTo(new RectF(leftSize, viewHeight - bottomSize - leftBottomCornerRadius * 2, leftSize + leftBottomCornerRadius * 2, viewHeight - bottomSize), 90, 90);
        mShadowPath.close();

        mContentPath.reset();
        startX = leftSize + xOffset;
        startY = topSize + leftTopCornerRadius + yOffset;
        mContentPath.moveTo(startX, startY);
        mContentPath.arcTo(new RectF(startX, topSize + yOffset, leftTopCornerRadius * 2 + startX, topSize + leftTopCornerRadius * 2 + yOffset), 180, 90);
        mContentPath.lineTo(viewWidth - rightSize - rightTopCornerRadius + xOffset, topSize + yOffset);
        mContentPath.arcTo(new RectF(viewWidth - rightSize - rightTopCornerRadius * 2 + xOffset, topSize + yOffset, viewWidth - rightSize + xOffset, topSize + rightTopCornerRadius * 2 + yOffset), 270, 90);
        mContentPath.lineTo(viewWidth - rightSize + xOffset, viewHeight - bottomSize - rightBottomCornerRadius + yOffset);
        mContentPath.arcTo(new RectF(viewWidth - rightSize - rightBottomCornerRadius * 2 + xOffset, viewHeight - bottomSize - rightBottomCornerRadius * 2 + yOffset, viewWidth - rightSize + xOffset, viewHeight - bottomSize + yOffset), 0, 90);
        mContentPath.lineTo(leftSize + leftBottomCornerRadius + xOffset, viewHeight - bottomSize + yOffset);
        mContentPath.arcTo(new RectF(leftSize + xOffset, viewHeight - bottomSize - leftBottomCornerRadius * 2 + yOffset, leftSize + leftBottomCornerRadius * 2 + xOffset, viewHeight - bottomSize + yOffset), 90, 90);
        mContentPath.close();
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        canvas.save();
        int saveCount = canvas.saveLayer(0, 0, getWidth(), getHeight(), null, Canvas.ALL_SAVE_FLAG);
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.O_MR1) {
            pathPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.MULTIPLY));
            super.dispatchDraw(canvas);
            canvas.drawPath(mContentPath, pathPaint);
        } else {
            pathPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OUT));
            super.dispatchDraw(canvas);
            highVerPath.reset();
            highVerPath.addRect(0, 0, getWidth(), getHeight(), Path.Direction.CW);
            highVerPath.op(mContentPath, Path.Op.DIFFERENCE);
            canvas.drawPath(highVerPath, pathPaint);
        }
//        canvas.restoreToCount(saveCount);
        canvas.restore();
        pathPaint.setXfermode(null);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        measureContentPath();
        bgPaint.setColor(shadowColor);
        canvas.drawPath(mShadowPath, bgPaint);
        bgColorPaint.setColor(cardBackgroundColor);
        canvas.drawPath(mContentPath, bgColorPaint);

        //左上圆角
        int xRadius = leftSize + leftTopCornerRadius;
        int yRadius = topSize + leftTopCornerRadius;
        int min = Math.min(xRadius, yRadius);
        if (min != 0) {
            canvas.save();
            canvas.clipRect(0, 0, xRadius, yRadius);
            mPath.reset();
            mPath.addCircle(xRadius, yRadius, leftTopCornerRadius, Path.Direction.CCW);
            canvas.clipPath(mPath, Region.Op.DIFFERENCE);
            canvas.scale((float) xRadius / min, (float) yRadius / min, xRadius, yRadius);
            paint.setShader(ltrg);
            canvas.drawCircle(xRadius, yRadius, min, paint);
            canvas.restore();
        }

        //顶部阴影
//        t.draw(canvas);
        paint.setShader(t);
        canvas.drawRect(leftSize + leftTopCornerRadius, 0, viewWidth - rightSize - rightTopCornerRadius, topSize, paint);

        //右上圆角
        xRadius = rightSize + rightTopCornerRadius;
        yRadius = topSize + rightTopCornerRadius;
        min = Math.min(xRadius, yRadius);
        if (min != 0) {
            canvas.save();
            canvas.clipRect(viewWidth - xRadius, 0, viewWidth, yRadius);
            mPath.reset();
            mPath.addCircle(viewWidth - xRadius, yRadius, rightTopCornerRadius, Path.Direction.CCW);
            canvas.clipPath(mPath, Region.Op.DIFFERENCE);
            canvas.scale((float) xRadius / min, (float) yRadius / min, viewWidth - rightSize, topSize);
            paint.setShader(rtrg);
            canvas.drawCircle(viewWidth - xRadius, yRadius, min, paint);
            canvas.restore();
        }

        //右侧阴影
//        r.draw(canvas);
        paint.setShader(r);
        canvas.drawRect(viewWidth - rightSize, topSize + rightTopCornerRadius, viewWidth, viewHeight - bottomSize - rightBottomCornerRadius, paint);

        //右下圆角阴影
        xRadius = rightSize + rightBottomCornerRadius;
        yRadius = bottomSize + rightBottomCornerRadius;
        min = Math.min(xRadius, yRadius);
        if (min != 0) {
            canvas.save();
            canvas.clipRect(viewWidth - xRadius, viewHeight - yRadius, viewWidth, viewHeight);
            mPath.reset();
            mPath.addCircle(viewWidth - xRadius, viewHeight - yRadius, rightBottomCornerRadius, Path.Direction.CCW);
            canvas.clipPath(mPath, Region.Op.DIFFERENCE);
            canvas.scale((float) xRadius / min, (float) yRadius / min, viewWidth - xRadius, viewHeight - yRadius);
            paint.setShader(rbrg);
            canvas.drawCircle(viewWidth - xRadius, viewHeight - yRadius, min, paint);
            canvas.restore();
        }

        //底部阴影
//        b.draw(canvas);
        paint.setShader(b);
        canvas.drawRect(leftSize + leftBottomCornerRadius, viewHeight - bottomSize, viewWidth - rightSize - rightBottomCornerRadius, viewHeight, paint);

        xRadius = leftSize + leftBottomCornerRadius;
        yRadius = bottomSize + leftBottomCornerRadius;
        min = Math.min(xRadius, yRadius);
        if (min != 0) {
            canvas.save();
            canvas.clipRect(0, viewHeight - yRadius, xRadius, viewHeight);
            mPath.reset();
            mPath.addCircle(xRadius, viewHeight - yRadius, leftBottomCornerRadius, Path.Direction.CCW);
            canvas.clipPath(mPath, Region.Op.DIFFERENCE);
            canvas.scale((float) xRadius / min, (float) yRadius / min, xRadius, viewHeight - yRadius);
            paint.setShader(lbrg);
            canvas.drawCircle(xRadius, viewHeight - yRadius, min, paint);
            canvas.restore();
        }

        //左侧阴影
//        l.draw(canvas);
        paint.setShader(l);
        canvas.drawRect(0, topSize + leftTopCornerRadius, leftSize, viewHeight - bottomSize - leftBottomCornerRadius, paint);

    }

    @Override
    public void setPadding(int left, int top, int right, int bottom) {
        //NO OP
    }

    @Override
    public void setPaddingRelative(int start, int top, int end, int bottom) {
        //NO OP
    }

    private boolean adjustEdgeSize() {
        if (leftSize != rightSize || leftSize != bottomSize || leftSize != topSize) {
            leftSize = rightSize = topSize = bottomSize = defaultShadowSize;
            return true;
        }
        return false;
    }

    public void setLeftTopCornerRadius(int leftTopCornerRadius) {
        this.leftTopCornerRadius = leftTopCornerRadius;
        this.leftTopCornerRadius = Math.min(this.leftTopCornerRadius, (viewWidth - leftSize - rightSize) / 2);
        this.leftTopCornerRadius = Math.min(this.leftTopCornerRadius, (viewHeight - topSize - bottomSize) / 2);
        if (adjustEdgeSize()) {
            super.setPadding(leftSize + xOffset,
                    topSize + yOffset,
                    rightSize - xOffset,
                    bottomSize - yOffset);
        }
        createDrawables();
        invalidate();
    }


    public void setRightTopCornerRadius(int rightTopCornerRadius) {
        this.rightTopCornerRadius = rightTopCornerRadius;
        this.rightTopCornerRadius = Math.min(this.rightTopCornerRadius, (viewWidth - leftSize - rightSize) / 2);
        this.rightTopCornerRadius = Math.min(this.rightTopCornerRadius, (viewHeight - topSize - bottomSize) / 2);
        if (adjustEdgeSize()) {
            super.setPadding(leftSize + xOffset,
                    topSize + yOffset,
                    rightSize - xOffset,
                    bottomSize - yOffset);
        }
        createDrawables();
        invalidate();
    }

    public void setRightBottomCornerRadius(int rightBottomCornerRadius) {
        this.rightBottomCornerRadius = rightBottomCornerRadius;
        this.rightBottomCornerRadius = Math.min(this.rightBottomCornerRadius, (viewWidth - leftSize - rightSize) / 2);
        this.rightBottomCornerRadius = Math.min(this.rightBottomCornerRadius, (viewHeight - topSize - bottomSize) / 2);
        if (adjustEdgeSize()) {
            super.setPadding(leftSize + xOffset,
                    topSize + yOffset,
                    rightSize - xOffset,
                    bottomSize - yOffset);
        }
        createDrawables();
        invalidate();
    }

    public void setLeftBottomCornerRadius(int leftBottomCornerRadius) {
        this.leftBottomCornerRadius = leftBottomCornerRadius;
        this.leftBottomCornerRadius = Math.min(this.leftBottomCornerRadius, (viewWidth - leftSize - rightSize) / 2);
        this.leftBottomCornerRadius = Math.min(this.leftBottomCornerRadius, (viewHeight - topSize - bottomSize) / 2);
        if (adjustEdgeSize()) {
            super.setPadding(leftSize + xOffset,
                    topSize + yOffset,
                    rightSize - xOffset,
                    bottomSize - yOffset);
        }
        createDrawables();
        invalidate();
    }

    private void zeroCorner() {
        leftTopCornerRadius = 0;
        rightTopCornerRadius = 0;
        rightBottomCornerRadius = 0;
        leftBottomCornerRadius = 0;
    }

    public void setLeftShadowSize(int leftShadowSize) {
        elevationAffectShadowSize = false;
        this.leftSize = leftShadowSize;
        zeroCorner();
        if (xOffset < -leftSize) {
            xOffset = -leftSize;
        }
        super.setPadding(leftSize, topSize + yOffset,
                rightSize - xOffset,
                bottomSize - yOffset);
        createDrawables();
        invalidate();
    }

    public void setRightShadowSize(int rightShadowSize) {
        elevationAffectShadowSize = false;
        this.rightSize = rightShadowSize;
        zeroCorner();
        if (xOffset > rightSize) {
            xOffset = rightSize;
        }
        super.setPadding(getPaddingLeft(), getPaddingTop(), rightSize - xOffset, getPaddingBottom());
        createDrawables();
        invalidate();
    }

    public void setTopShadowSize(int topShadowSize) {
        elevationAffectShadowSize = false;
        this.topSize = topShadowSize;
        zeroCorner();
        if (yOffset < -topSize) {
            yOffset = -topSize;
        }
        super.setPadding(getPaddingLeft(), topSize + yOffset, getPaddingRight(), getPaddingBottom());
        createDrawables();
        invalidate();
    }

    public void setBottomShadowSize(int bottomShadowSize) {
        elevationAffectShadowSize = false;
        this.bottomSize = bottomShadowSize;
        zeroCorner();
        if (yOffset > bottomSize) {
            yOffset = bottomSize;
        }
        super.setPadding(getPaddingLeft(), getPaddingTop(), getPaddingRight(), bottomSize - yOffset);
        createDrawables();
        invalidate();
    }

    public void setShadowColor(@ColorInt int color) {
        initColors(color);
        createDrawables();
        invalidate();
    }

    private void initColors(@ColorInt int color) {
        int red = Color.red(color);
        int green = Color.green(color);
        int blue = Color.blue(color);
        shadowColor = Color.argb(elevationAffectShadowColor ? (elevation + 10) : shadowAlpha, red, green, blue);
        if (shadowFluidShape == ADSORPTION) {
            colors[0] = shadowColor;
            colors[1] = Color.argb(Color.alpha(shadowColor) / 4, red, green, blue);
            colors[2] = Color.argb(Color.alpha(shadowColor) / 8, red, green, blue);
            colors[3] = Color.argb(0, red, green, blue);
        } else {
            colors[0] = shadowColor;
            colors[1] = Color.argb((int) (Color.alpha(shadowColor) * 0.67), red, green, blue);
            colors[2] = Color.argb((int) (Color.alpha(shadowColor) * 0.33), red, green, blue);
            colors[3] = Color.argb(0, red, green, blue);
        }
    }

    //0~ 255
    public void setShadowAlpha(int alpha) {
        this.shadowAlpha = alpha;
        setShadowColor(shadowColor);
    }

    public void setElevationAffectShadowColor(boolean elevationAffectShadowColor) {
        if (this.elevationAffectShadowColor != elevationAffectShadowColor) {
            this.elevationAffectShadowColor = elevationAffectShadowColor;
            initColors(shadowColor);
            createDrawables();
            invalidate();
        }
    }

    public void setElevationAffectShadowSize(boolean elevationAffectShadowSize) {
        if (this.elevationAffectShadowSize != elevationAffectShadowSize) {
            this.elevationAffectShadowSize = elevationAffectShadowSize;
            if (elevationAffectShadowSize) {
                leftSize = rightSize = bottomSize = topSize = elevation + 12;
                super.setPadding(leftSize + xOffset,
                        topSize + yOffset,
                        rightSize - xOffset,
                        bottomSize - yOffset);
            }
            createDrawables();
            invalidate();
        }
    }

    public void setElevation(int elevation) {
        this.elevation = elevation;
        if (elevationAffectShadowColor) {
            initColors(shadowColor);
        }
        if (elevationAffectShadowSize) {
            leftSize = rightSize = bottomSize = topSize = elevation + 12;
            judgeEdge();
            super.setPadding(leftSize + xOffset,
                    topSize + yOffset,
                    rightSize - xOffset,
                    bottomSize - yOffset);
        }
        createDrawables();
        invalidate();
    }

    public void setCornerRadius(int radius) {
        this.cornerRadius = radius;
        leftTopCornerRadius = leftBottomCornerRadius = rightTopCornerRadius = rightBottomCornerRadius = cornerRadius;
        if (adjustEdgeSize()) {
            super.setPadding(leftSize + xOffset,
                    topSize + yOffset,
                    rightSize - xOffset,
                    bottomSize - yOffset);
        }
        createDrawables();
        invalidate();
    }

    public void setShadowSize(int shadowSize) {
        if (elevationAffectShadowSize) {
            leftSize = rightSize = bottomSize = topSize = elevation + 12;
        } else {
            leftSize = rightSize = bottomSize = topSize = shadowSize;
        }
        if (xOffset > rightSize) {
            xOffset = rightSize;
        }
        if (xOffset < -leftSize) {
            xOffset = -leftSize;
        }
        if (yOffset > bottomSize) {
            yOffset = bottomSize;
        }
        if (yOffset < -topSize) {
            yOffset = -topSize;
        }
        super.setPadding(leftSize + xOffset,
                topSize + yOffset,
                rightSize - xOffset,
                bottomSize - yOffset);
        createDrawables();
        invalidate();
    }

    public int getCardElevation() {
        return elevation;
    }

    public int getLeftShadowSize() {
        return leftSize;
    }

    public int getTopShadowSize() {
        return topSize;
    }

    public int getRightShadowSize() {
        return rightSize;
    }

    public int getBottomShadowSize() {
        return bottomSize;
    }

    public int getShadowColor() {
        return shadowColor;
    }

    public int getCardBackgroundColor() {
        return cardBackgroundColor;
    }

    public int getCornerRadius() {
        return cornerRadius;
    }

    public boolean isElevationAffectShadowColor() {
        return elevationAffectShadowColor;
    }

    public boolean isElevationAffectShadowSize() {
        return elevationAffectShadowSize;
    }

    public int getLeftTopCornerRadius() {
        return leftTopCornerRadius;
    }

    public int getRightTopCornerRadius() {
        return rightTopCornerRadius;
    }

    public int getRightBottomCornerRadius() {
        return rightBottomCornerRadius;
    }

    public int getLeftBottomCornerRadius() {
        return leftBottomCornerRadius;
    }

    public int getShadowAlpha() {
        return shadowAlpha;
    }

    public void setCardBackgroundColor(int cardBackgroundColor) {
        this.cardBackgroundColor = cardBackgroundColor;
        invalidate();
    }

    public int getXOffset() {
        return xOffset;
    }

    public void setXOffset(int xOffset) {
        if (xOffset > rightSize) {
            xOffset = rightSize;
        }
        if (xOffset < -leftSize) {
            xOffset = -leftSize;
        }
        this.xOffset = xOffset;
        super.setPadding(leftSize + xOffset,
                topSize + yOffset,
                rightSize - xOffset,
                bottomSize - yOffset);
        createDrawables();
        invalidate();
    }

    public int getYOffset() {
        return yOffset;
    }

    public void setYOffset(int yOffset) {
        if (yOffset > bottomSize) {
            yOffset = bottomSize;
        }
        if (yOffset < -topSize) {
            yOffset = -topSize;
        }
        this.yOffset = yOffset;
        super.setPadding(leftSize + xOffset,
                topSize + yOffset,
                rightSize - xOffset,
                bottomSize - yOffset);
        createDrawables();
        invalidate();
    }

    public void setShadowFluidShape(int shape) {
        if (shape != ADSORPTION && shape != LINEAR) {
            return;
        }
        this.shadowFluidShape = shape;
        initColors(shadowColor);
        createDrawables();
        postInvalidate();
    }
}
