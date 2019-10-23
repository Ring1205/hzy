package com.zxycloud.common.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.CycleInterpolator;
import android.view.animation.Interpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.zxycloud.common.R;
import com.zxycloud.common.utils.CommonUtils;

/**
 * @author leiming
 * @date 2017/10/11
 */
public class MyCheckBox extends RelativeLayout implements OnClickListener {

    private boolean isChecked;
    private View mViewBall;
    private int width;
    private int border;
    private CheckBoxListener listener;
    private MyCheckBox box;

    public MyCheckBox(Context context, AttributeSet attrs) {
        super(context, attrs);
        mViewBall = new ImageView(context);
        mViewBall.setBackgroundResource(R.mipmap.cb_ball);
        setBackgroundResource(R.mipmap.cb_not_selected);
        addView(mViewBall);
        setOnClickListener(this);
        width = CommonUtils.measureScreen().dp2px(context, 33);
        box = this;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        border = (mViewBall.getMeasuredHeight() - getMeasuredHeight()) / 2;
        if (isChecked) {
            scrollTo(-width - border, border);
        } else {
            scrollTo(border, border);
        }
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean isChecked) {
        this.isChecked = isChecked;
        if (isChecked) {
            scrollTo(-width - border, border);
            setBackgroundResource(R.mipmap.cb_selected);
        } else {
            scrollTo(border, border);
            setBackgroundResource(R.mipmap.cb_not_selected);
        }
    }

    public void hasClicked() {
        mViewBall.post(new SmoothRollRunnable());
    }

    @Override
    public void onClick(View v) {
        hasClicked();
    }

    private class SmoothRollRunnable implements Runnable {

        private int mDeltaValue;
        private long mStartTime;
        private int duration = 500;
        private Interpolator mInterpolator;

        SmoothRollRunnable() {
            mInterpolator = new CycleInterpolator(0.25f);
        }

        @Override
        public void run() {
            if (mDeltaValue == 0) {
                if (listener != null) {
                    listener.onMoveStart();
                }
                mDeltaValue++;
                mStartTime = System.currentTimeMillis();
            } else {
                if (listener != null) {
                    listener.onMove();
                }
                setClickable(false);
                long normalizedTime = (1000 * (System.currentTimeMillis() - mStartTime))
                        / duration;
                mDeltaValue = Math.round(width
                        * mInterpolator
                        .getInterpolation(normalizedTime / 1000f));
                scrollTo(isChecked ? mDeltaValue - width + border : -mDeltaValue - border, border);
                if (mDeltaValue > width / 2) {
                    if (isChecked) {
                        setBackgroundResource(R.mipmap.cb_not_selected);
                    } else {
                        setBackgroundResource(R.mipmap.cb_selected);
                    }
                }
            }
            if (mDeltaValue < width) {
                mViewBall.postDelayed(this, 10);
            } else {
                isChecked = !isChecked;
                if (listener != null) {
                    listener.onMoveEnd(box, isChecked);
                }
                setClickable(true);
            }
        }
    }

    public interface CheckBoxListener {
        void onMoveStart();

        void onMove();

        void onMoveEnd(View view, boolean isChecked);
    }

    public void setCheckBoxListener(CheckBoxListener listener) {
        this.listener = listener;
    }

}
