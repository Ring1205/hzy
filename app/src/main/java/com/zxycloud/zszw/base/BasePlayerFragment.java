package com.zxycloud.zszw.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import com.zxycloud.common.widget.player.BDCloudVideoView;
import com.zxycloud.common.widget.player.SimpleMediaController;

public abstract class BasePlayerFragment extends BaseBackFragment {
    private Toolbar mToolbar;

    protected RelativeLayout mViewHolder = null;
    protected BDCloudVideoView mVV = null;
    protected SimpleMediaController mediaController = null;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        _mActivity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    protected void startPlayer(String rtmp) {
        /*
         * 设置ak
         */
        BDCloudVideoView.setAK("");

        mVV = new BDCloudVideoView(_mActivity);
        mVV.setVideoPath(rtmp);
//        if (SharedPrefsStore.isPlayerFitModeCrapping(getApplicationContext())) {
//        mVV.setVideoScalingMode(BDCloudVideoView.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING);
//        } else {
        mVV.setVideoScalingMode(BDCloudVideoView.VIDEO_SCALING_MODE_SCALE_TO_FIT);
//        }

        RelativeLayout.LayoutParams rllp = new RelativeLayout.LayoutParams(- 1, - 1);
        rllp.addRule(RelativeLayout.CENTER_IN_PARENT);
        mViewHolder.addView(mVV, rllp);

        /**
         * 注册listener
         *//*
        mVV.setOnPreparedListener(this);
        mVV.setOnCompletionListener(this);
        mVV.setOnErrorListener(this);
        mVV.setOnInfoListener(this);
        mVV.setOnBufferingUpdateListener(this);
        mVV.setOnPlayerStateListener(this);*/

        mediaController.setMediaPlayerControl(mVV);

        mVV.start();
    }

    @Override
    public void onStart() {
        super.onStart();
        if (mVV != null) {
            mVV.enterForeground();
        }
    }

    @Override
    public void onStop() {
        if (mVV != null) {
            mVV.enterBackground();
        }
        super.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mVV != null) {
            mVV.stopPlayback();
        }
    }
}
