package com.zxycloud.common.widget.ASRDigitalDialog;

import android.media.MediaPlayer;
import android.util.Log;

import java.io.IOException;

public class UPlayer implements IVoiceManager {

    private final String TAG = UPlayer.class.getName();
    private String path;

    private MediaPlayer mPlayer;
    private boolean isStop;

    public UPlayer(String path, MediaPlayer.OnCompletionListener listener) {
        this.path = path;
        mPlayer = new MediaPlayer();
        mPlayer.setOnCompletionListener(listener);
    }

    @Override
    public boolean start() {
        //设置要播放的文件
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    mPlayer.setDataSource(path);
                    mPlayer.prepare();
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.e(TAG, "prepare() failed");
                }
                stopJudge();
            }
        }).start();
        mPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                //播放
                mPlayer.start();
            }
        });
        return false;
    }

    private void stopJudge() {
        if (isStop) {
            mPlayer.stop();
            mPlayer.release();
            mPlayer = null;
        }
    }

    @Override
    public boolean stop() {
        isStop = true;
        if (mPlayer.isPlaying()) {
            mPlayer.stop();
            mPlayer.release();
            mPlayer = null;
        }
        return false;
    }

}