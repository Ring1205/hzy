package com.zxycloud.zszw.service;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.telephony.TelephonyManager;

import com.zxycloud.zszw.AlertActivity;
import com.zxycloud.zszw.R;
import com.zxycloud.zszw.event.PhoneEvent;
import com.zxycloud.common.utils.CommonUtils;
import com.zxycloud.common.utils.PermissionUtils;
import com.zxycloud.common.utils.TimerUtils;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;

/**
 * 数据初始化服务
 *
 * @author leiming
 * @date 2017/10/11
 */
public class AlertService extends Service implements TimerUtils.OnBaseTimerCallBack {

    /**
     * 定时器工具
     */
    private TimerUtils timerUtils;

    private static MediaPlayer mediaPlayer;
    private AudioManager mAudioManager;
    private int maxMusic;
    private int currentMusic;
    private int maxSystem;
    private int currentSystem;
    /**
     * 震动
     */
    private Vibrator vibrator;
    /**
     * 闪光灯
     */
    private Camera camera;
    private Camera.Parameters parameter;
    private boolean isFlash;
    private NotificationManager notificationManager;
    private int times;
    private Context context;

    private int stateCode;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {

        stateCode = intent.getIntExtra("stateCode", CommonUtils.STATE_CODE_FIRE);

        context = getApplicationContext();

        // 初始化音频管理器
        initAudioManager();
        startAlert();
        timerUtils = new TimerUtils(1000 * 60 * 3, 1000, this);
        timerUtils.start();
        return null;
    }

    /**
     * 开始警报效果
     */
    private void startAlert() {
        // 播放警报铃声
        initMediaPlayer();
        // 开启震动
        shake();
        // 闪光灯权限判定，有相机权限则开启，没有则不开起
        if (PermissionUtils.hasPermission(getApplicationContext(), PermissionUtils.CODE_CAMERA)) {
            flash();
        }
        // 系统音量以及音乐音量均设置为最大音量
        if (PermissionUtils.hasPermission(getApplicationContext(), PermissionUtils.CODE_PROCESS_OUTGOING_CALLS)) {
//            notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N
//                    && null != notificationManager && ! notificationManager.isNotificationPolicyAccessGranted()) {
//                Intent intent = new Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS);
//                getApplicationContext().startActivity(intent);
//                return;
//            }
            setMaxVoice();
        }
    }

    /**
     * 警报铃声
     */
    private void initMediaPlayer() {
        if (mediaPlayer != null) {
            mediaPlayer.reset();
        }
        try {
            Uri setDataSourceUri = Uri.parse("android.resource://com.zxycloud.monitor/" + (stateCode == CommonUtils.STATE_CODE_FIRE ? R.raw.fire_alarm : R.raw.prefire_alarm));
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setDataSource(this, setDataSourceUri);
            mediaPlayer.prepare();
            mediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                @Override
                public boolean onError(MediaPlayer mp, int what, int extra) {
                    if (CommonUtils.notEmpty(mp) && mp.isPlaying()) {
                        mp.stop();
                    }
                    CommonUtils.log().e(getClass().getSimpleName(), "what = " + what + "  ");
                    return false;
                }
            });
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setLooping(true);
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    try {
                        mp.release();
                        mp.stop();
                    } catch (IllegalStateException e) {
                        CommonUtils.log().e(getClass().getSimpleName(), e);
                    }

                }
            });
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    try {
                        if (CommonUtils.notEmpty(mp) && mp.isPlaying()) {
                            mp.pause();
                        }
                        mp.start();
                    } catch (Exception e) {
                        CommonUtils.log().e(getClass().getSimpleName(), e);
                    }
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Thread.sleep(1000);
                            } catch (InterruptedException e) {
                                CommonUtils.log().e(getClass().getSimpleName(), e);
                            } finally {
                                mAudioManager.setSpeakerphoneOn(true);
                            }
                        }
                    }).start();
                }
            });
        } catch (IOException e) {
            CommonUtils.log().e(getClass().getSimpleName(), e);
        }
    }

    /**
     * 开启震动
     */
    private void shake() {
        //设置震动周期，数组表示时间：等待+执行，单位是毫秒，下面操作代表:等待300，执行100，等待300，执行1000，
        //后面的数字如果为-1代表不重复，之执行一次，其他代表会重复，0代表从数组的第0个位置开始
        if (vibrator == null) {
            vibrator = (Vibrator) getApplication().getSystemService(Service.VIBRATOR_SERVICE);
        }
        vibrator.vibrate(new long[]{300, 1000}, 0);
    }

    /**
     * 开启闪光灯频闪
     */
    private void flash() {
        isFlash = true;
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (isFlash) {
                    openFlashLight();
                    try {
                        Thread.sleep(300);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    offFlashLight();
                    try {
                        Thread.sleep(300);
                    } catch (InterruptedException e) {
                        CommonUtils.log().i("InterruptedException", e);
                    }
                }
            }
        }).start();
    }

    /**
     * 打开闪光灯
     */
    public void openFlashLight() {
        try {
            if (camera == null) {
                camera = Camera.open();
                int textureId = 0;
                camera.setPreviewTexture(new SurfaceTexture(textureId));
                camera.startPreview();
            }
            parameter = camera.getParameters();
            parameter.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
            camera.setParameters(parameter);
        } catch (Exception e) {
            CommonUtils.log().i("Exception", e);
        }
    }

    /**
     * 关闭闪光灯
     */
    public void offFlashLight() {
        try {
            if (camera != null) {
                parameter = camera.getParameters();
                parameter.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                camera.setParameters(parameter);
                camera.stopPreview();
                camera.release();
                camera = null;
            }
        } catch (RuntimeException e) {
            e.printStackTrace();
        }
    }

    /**
     * 初始化媒体管理器
     */
    private void initAudioManager() {
        try {
            mAudioManager = (AudioManager) getSystemService(Service.AUDIO_SERVICE);
            mAudioManager.setMode(AudioManager.MODE_IN_COMMUNICATION);

            maxMusic = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
            currentMusic = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
            maxSystem = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_SYSTEM);
            currentSystem = mAudioManager.getStreamVolume(AudioManager.STREAM_SYSTEM);
        } catch (SecurityException e) {
            CommonUtils.log().e(getClass().getSimpleName(), e);
        }
    }

    /**
     * 设置最大音量
     */
    private void setMaxVoice() {
        try {
            mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, maxMusic, 0);
            mAudioManager.setStreamVolume(AudioManager.STREAM_SYSTEM, maxSystem, 0);
        } catch (SecurityException e) {
            CommonUtils.log().e(getClass().getSimpleName(), e);
        }
    }

    /**
     * 还原之前音量
     */
    private void setCurrentVoice() {
        try {
            mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, currentMusic, 0);
            mAudioManager.setStreamVolume(AudioManager.STREAM_SYSTEM, currentSystem, 0);
        } catch (SecurityException e) {
            CommonUtils.log().e(getClass().getSimpleName(), e);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopAlert();
    }

    private void stopAlert() {
        // 恢复默认音量
        if (PermissionUtils.hasPermission(getApplicationContext(), PermissionUtils.CODE_PROCESS_OUTGOING_CALLS)) {
            if (!(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N
                    && null != notificationManager && !notificationManager.isNotificationPolicyAccessGranted())) {
                setCurrentVoice();
                // 还原为正常媒体播放模式
                mAudioManager.setMode(AudioManager.MODE_NORMAL);
            }
        }

        try {
            // 媒体播放器停止
            if (CommonUtils.notEmpty(mediaPlayer) && mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
            }
        } catch (IllegalStateException e) {
            CommonUtils.log().e(getClass().getSimpleName(), e);
        }
        // 关闭闪光灯
        isFlash = false;
        // 取消震动
        if (vibrator != null) {
            vibrator.cancel();
        }
    }

    public class ServiceBinder extends Binder {
        public AlertService getService() {
            return AlertService.this;
        }
    }

    @Override
    public void onTick(Object tag, long millisUntilFinished) {

    }

    @Override
    public void onFinish(Object tag) {
        if (CommonUtils.hasActivity(AlertActivity.class)) {
            CommonUtils.closeActivity(AlertActivity.class);
        }
        stopSelf();
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onMessageEvent(PhoneEvent event) {
        switch (event.getPhoneState()) {
            case TelephonyManager.CALL_STATE_RINGING:
                stopAlert();
                break;
            case TelephonyManager.CALL_STATE_OFFHOOK:
                break;

            case TelephonyManager.CALL_STATE_IDLE:
                startAlert();
                break;

            default:
                stopAlert();
                break;
        }
    }
}
