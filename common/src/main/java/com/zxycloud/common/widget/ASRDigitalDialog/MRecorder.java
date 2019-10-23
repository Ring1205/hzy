package com.zxycloud.common.widget.ASRDigitalDialog;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;

import com.csf.lame4android.utils.FLameUtils;
import com.zxycloud.common.utils.TimerUtils;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class MRecorder implements IVoiceManager {
    private String path;
    private short[] mBuffer;
    int SAMPLE_RATE = 16000;
    private boolean mIsRecording = false;
    private AudioRecord recorder;//录音对象
    private TimerUtils timerUtils;//定时器
    //定时器监听
    private TimerUtils.OnBaseTimerCallBack callBack = new TimerUtils.OnBaseTimerCallBack() {
        @Override
        public void onTick(Object tag, long millisUntilFinished) {

        }

        @Override
        public void onFinish(Object tag) {
        }
    };

    public MRecorder(String path) {
        this.path = path;
        timerUtils = new TimerUtils(60 * 1000, 1000, callBack);
    }

    /**
     * 开始录音
     *
     * @return boolean
     */
    @Override
    public boolean start() {
        timerUtils.start();//开启定时器
        startRecord();//开始录音
        mIsRecording = true;//开启录音线程

        new Thread(new Runnable() {
            @Override
            public void run() {
                DataOutputStream output = null;
                try {
                    output = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(path)));
                    while (mIsRecording) {
                        int readSize = recorder.read(mBuffer, 0, mBuffer.length);
                        for (int i = 0; i < readSize; i++)
                            output.writeShort(mBuffer[i]);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (output != null) {
                        try {
                            output.flush();
                        } catch (IOException e) {
                            e.printStackTrace();
                        } finally {
                            try {
                                output.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
        }).start();

        return false;
    }

    private void startRecord() {
        int bufferSize = AudioRecord.getMinBufferSize(SAMPLE_RATE, AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_16BIT);
        mBuffer = new short[bufferSize];
        recorder = new AudioRecord(MediaRecorder.AudioSource.MIC, SAMPLE_RATE, AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_16BIT, bufferSize);
        recorder.startRecording();
    }

    /**
     * 停止录音
     *
     * @return boolean
     */
    @Override
    public boolean stop() {
        if (!mIsRecording) {
            return true;
        }
        timerUtils.stop();//停止定时器
        mIsRecording = false;//关闭录音线程
        if (recorder != null)//停止录音
            recorder.stop();
        if (timerUtils.getMillisUntilFinished() < 2) {
            //录音时间不够
            return false;
        } else {
            // 开个线程进行转码 raw转mp3
            new Thread(new Runnable() {
                @Override
                public void run() {
                    FLameUtils fLameUtils = new FLameUtils(1, SAMPLE_RATE, 128);
                    File mp3File = new File(path.replace(".raw", ".mp3"));
                    fLameUtils.raw2mp3(path, mp3File.getAbsolutePath());
                }
            }).start();
            return true;
        }
    }

}