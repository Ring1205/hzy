package com.zxycloud.startup.utils;

import android.animation.Animator;
import android.app.Activity;
import android.os.Build;
import android.os.Handler;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.FrameLayout;

import com.zxycloud.common.utils.CommonUtils;

/**
 * @author leiming
 * @date 2019/3/8.
 */
public class LoginAnimUtils {
    /**
     * Circular Reveal会消除背景，使用itemDivider做分割
     */
    private final FrameLayout itemDivider;
    private AnimationSet animationZoomOut;
    private AnimationSet animationZoomIn;

    private float smallSizeX = 0.8f;
    private float bigSizeX = 1f;
    private float smallSizeY = 0.8f;
    private float bigSizeY = 1f;

    private Activity mActivity;
    private AnimStateListener animStateListener;
    private Handler handler;
    private Animator animator;

    public LoginAnimUtils(Activity mActivity, AnimStateListener animStateListener, FrameLayout itemDivider) {
        this.mActivity = mActivity;
        this.animStateListener = animStateListener;
        this.itemDivider = itemDivider;
    }

    public void startZoomOut(final FrameLayout frameView, final View layoutView) {
        if (CommonUtils.isEmpty(animationZoomOut)) {
            animationZoomOut = new AnimationSet(true);
            ScaleAnimation scaleAnimation = new ScaleAnimation(smallSizeX, bigSizeX, smallSizeY, bigSizeY
                    , Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 1.4f);
            AlphaAnimation showAnimation = new AlphaAnimation(0.8f, 1f);
            animationZoomOut.addAnimation(scaleAnimation);
            animationZoomOut.addAnimation(showAnimation);
            animationZoomOut.setDuration(1000);
            animationZoomOut.setFillAfter(true);
        }
        frameView.removeAllViews();
        frameView.addView(layoutView);
        frameView.startAnimation(animationZoomOut);
        CommonUtils.threadPoolExecute(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    mActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            itemDivider.bringToFront();
                            frameView.bringToFront();
                        }
                    });
                }
            }
        });
    }

    public void startShow(final int tag, View clickView, final FrameLayout frameView, final View layoutView) {
        if (CommonUtils.isEmpty(handler)) {
            handler = new Handler();
        }
        int xc = (clickView.getLeft() + clickView.getRight()) / 2;
        int yc = (clickView.getTop() + clickView.getBottom()) / 2;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            animator = ViewAnimationUtils.createCircularReveal(frameView, xc, yc, 0, 1111);
            animator.setDuration(1000);
            animator.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            frameView.removeAllViews();
                            frameView.addView(layoutView);

                        }
                    }, 200);
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    animStateListener.onAnimEnd(tag);
                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });
            animator.start();
        } else {
            AlphaAnimation hideAnimation = new AlphaAnimation(1f, 0f);
            hideAnimation.setDuration(500);
            hideAnimation.setFillAfter(true);
            hideAnimation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    frameView.removeAllViews();
                    frameView.addView(layoutView);
                    AlphaAnimation showAnimation = new AlphaAnimation(1f, 0f);
                    showAnimation.setDuration(0);
                    layoutView.startAnimation(showAnimation);
                    showAnimation = new AlphaAnimation(0, 1f);
                    showAnimation.setDuration(500);
                    showAnimation.setFillAfter(true);
                    layoutView.startAnimation(showAnimation);
                    animStateListener.onAnimEnd(tag);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            frameView.getChildAt(0).startAnimation(hideAnimation);
        }
    }

    public void startZoomIn(int tag, final FrameLayout frameView, final View layoutView) {
        startZoomIn(tag, frameView, layoutView, 1000);
    }

    public void startZoomIn(final int tag, final FrameLayout frameView, final View layoutView, int duration) {
        if (CommonUtils.isEmpty(animationZoomIn)) {
            animationZoomIn = new AnimationSet(true);
            ScaleAnimation scaleAnimation = new ScaleAnimation(bigSizeX, smallSizeX, bigSizeY, smallSizeY
                    , Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 1.4f);
            AlphaAnimation showAnimation = new AlphaAnimation(1f, 0.8f);
            animationZoomIn.addAnimation(scaleAnimation);
            animationZoomIn.addAnimation(showAnimation);
            animationZoomIn.setFillAfter(true);
        }
        animationZoomIn.setDuration(duration);
        frameView.startAnimation(animationZoomIn);
        animationZoomIn.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                animStateListener.onAnimStart(tag);
                itemDivider.setAlpha(0);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                frameView.removeAllViews();
                frameView.addView(layoutView);
                animStateListener.onAnimEnd(tag);
                itemDivider.setAlpha(1);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    public interface AnimStateListener {
        void onAnimEnd(int tag);

        void onAnimStart(int tag);
    }
}
