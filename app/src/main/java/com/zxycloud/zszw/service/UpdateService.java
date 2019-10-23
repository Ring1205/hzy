package com.zxycloud.zszw.service;

import android.app.DownloadManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;

import com.zxycloud.zszw.R;
import com.zxycloud.common.utils.CommonUtils;

import java.lang.ref.WeakReference;

/**
 * apk更新服务（暂未启用）
 *
 * @author leiming
 * @date 2017/10/11
 */
public class UpdateService extends Service {
    public static final String APP_PATH = "appPath";

    /**
     *  安卓系统下载类 
     **/
    DownloadManager manager;

    /**
     *  接收下载完的广播 
     **/
    DownloadCompleteReceiver receiver;

    /**
     * 文件下载对应的ID
     */
    private long downloadId;
    /**
     * APP Name字符串对应的ID
     */
    private int appNameID;
    /**
     * APP 桌面图标对应的图片ID
     */
    private int iconID;

    /**
     * 下载意图
     */
    private PendingIntent updatePendingIntent;
    /**
     * 通知栏
     */
    private NotificationCompat.Builder builderNotification;
    /**
     * 通知栏管理者
     */
    private NotificationManager updateNotificationManager;
    private Context context;
    private MyHandler myHandler;

    /**
     *  初始化下载器 
     **/
    private void initDownManager(Intent intent) {
        manager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);

        receiver = new DownloadCompleteReceiver();

        //设置下载地址  
        DownloadManager.Request down = new DownloadManager.Request(Uri.parse(intent.getStringExtra(APP_PATH)));

        // 设置允许使用的网络类型，这里是移动网络和wifi都可以
        down.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE | DownloadManager.Request.NETWORK_WIFI);

        // 下载时，通知栏是否可见
        down.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE);

        /*如果我们希望下载的文件可以被系统的Downloads应用扫描到并管理，
         我们需要调用Request对象的setVisibleInDownloadsUi方法，传递参数true.*/
        down.setVisibleInDownloadsUi(true);

        // 设置下载后文件存放的位置
        down.setDestinationInExternalFilesDir(this, Environment.DIRECTORY_DOWNLOADS, "nofire-online.apk");

        // 将下载请求放入队列
        downloadId = manager.enqueue(down);

        //注册下载广播
        registerReceiver(receiver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        context = getApplicationContext();
        appNameID = R.string.app_name;
        iconID = R.mipmap.ic_launcher;
        // 调用下载
        initDownManager(intent);
        // 调用通知
        initNotification();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (receiver != null) {
            unregisterReceiver(receiver);
        }
    }

    private void getProgress() {
        DownloadManager.Query query = new DownloadManager.Query().setFilterById(downloadId);
        Cursor cursor = null;
        try {
            cursor = manager.query(query);
            if (cursor != null && cursor.moveToFirst()) {
                //已经下载文件大小
                int downloadSize = cursor.getInt(cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR));
                //下载文件的总大小
                int fileSize = cursor.getInt(cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_TOTAL_SIZE_BYTES));
                //下载状态
                int downloadStatus = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS));

                if (downloadStatus == DownloadManager.STATUS_FAILED) {
                    onFailNotification();
                } else if (downloadStatus == DownloadManager.STATUS_SUCCESSFUL) {
                    onSuccessNotification();
                }

                int progress = (downloadSize * 100 / fileSize);

                //实时更新通知
                builderNotification.setSmallIcon(iconID).
                        setContentTitle(getResources().getString(appNameID)).
                        setContentIntent(updatePendingIntent).
                        setAutoCancel(true).
                        setTicker(CommonUtils.string().getString(context, R.string.update_service_begin_update)).
                        setDefaults(0).
                        setContentText(CommonUtils.string().getString(context, R.string.update_service_download_speed_of_progress)).
                        build();
                builderNotification.setProgress(100, progress, false);
                updateNotificationManager.notify(iconID, builderNotification.build());

                if (progress < 100) {
                    myHandler.sendEmptyMessageDelayed(0, 500);
                }
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    /**
     * 下载失败通知用户重新下载
     */
    private void onFailNotification() {
        builderNotification.setSmallIcon(iconID).
                setContentTitle(CommonUtils.string().getString(context, R.string.update_service_download_update_failed_and_download_again)).
                setAutoCancel(true).
                setTicker(CommonUtils.string().getString(context, R.string.update_service_download_update_failed)).
                setDefaults(1).
                setProgress(100, 0, false).
                setContentText(CommonUtils.string().getString(context, R.string.update_service_download_speed_of_progress)).
                build();
        updateNotificationManager.notify(iconID, this.builderNotification.build());
    }

    /**
     * 下载成功通知用户重新下载
     */
    private void onSuccessNotification() {
        builderNotification.setSmallIcon(iconID).
                setContentTitle(CommonUtils.string().getString(context, R.string.update_service_download_update_completed)).
                setContentIntent(updatePendingIntent).
                setAutoCancel(true).
                setTicker(CommonUtils.string().getString(context, R.string.update_service_download_update_completed)).
                setDefaults(1).
                setProgress(100, 100, false).
                setContentText(CommonUtils.string().getString(context, R.string.update_service_download_speed_of_progress)).
                build();
        updateNotificationManager.notify(iconID, this.builderNotification.build());
    }

    /**
     * 初始化通知
     */
    private void initNotification() {
        Intent updateCompletingIntent = new Intent();
        updateCompletingIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        updateCompletingIntent.setClass(this.getApplication().getApplicationContext(), UpdateService.class);
        updatePendingIntent = PendingIntent.getActivity(this, this.appNameID, updateCompletingIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        updateNotificationManager = (NotificationManager) this.getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION_CODES.O <= Build.VERSION.SDK_INT) {
            String channelId = "jzxfyun.net";
            String channelName = "中消云";
            NotificationChannel channel = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_MIN);
            // 开启指示灯，如果设备有的话
            channel.enableLights(true);
            // 设置指示灯颜色
            channel.setLightColor(ContextCompat.getColor(context, R.color.colorPrimary));
            // 是否在久按桌面图标时显示此渠道的通知
            channel.setShowBadge(true);
            // 设置是否应在锁定屏幕上显示此频道的通知
            channel.setLockscreenVisibility(NotificationCompat.VISIBILITY_PRIVATE);
            // 设置绕过免打扰模式
            channel.setBypassDnd(true);
            updateNotificationManager.createNotificationChannel(channel);
//            Android Studio 3.0 API 26.0 后的Notification
            builderNotification = new NotificationCompat.Builder(context, channelId);
        } else {
            builderNotification = new NotificationCompat.Builder(context);
        }
        builderNotification.setSmallIcon(this.iconID).
                setContentTitle(this.getResources().getString(this.appNameID)).
                setContentIntent(updatePendingIntent).
                setAutoCancel(true).
                setTicker(CommonUtils.string().getString(context, R.string.update_service_begin_update)).
                setDefaults(1).
                setProgress(100, 0, false).
                setContentText(CommonUtils.string().getString(context, R.string.update_service_download_speed_of_progress)).
                build();
        updateNotificationManager.notify(this.iconID, this.builderNotification.build());


        myHandler = new MyHandler(this);
        myHandler.sendEmptyMessageDelayed(0, 500);
    }

    /**
     * 下载完成接受者
     */
    class DownloadCompleteReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {

            //判断是否下载完成
            if (intent.getAction().equals(DownloadManager.ACTION_DOWNLOAD_COMPLETE)) {
                long downId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);

                installAPK(manager.getUriForDownloadedFile(downId));
            }
        }

        private void installAPK(Uri apk) {
            try {
                //通过Intent安装APK文件
                Intent intents = new Intent();
                intents.setAction("android.intent.action.VIEW");
                intents.addCategory("android.intent.category.DEFAULT");
                intents.setDataAndType(apk, "application/vnd.android.package-archive");
                intents.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intents.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//            android.os.Process.killProcess(android.os.Process.myPid());
                // 如果不加上这句的话在apk安装完成之后点击单开会崩溃
                startActivity(intents);
            } catch (ActivityNotFoundException exception) {
                exception.printStackTrace();
            }
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private static class MyHandler extends Handler {
        WeakReference<UpdateService> weakReference;

        MyHandler(UpdateService service) {
            weakReference = new WeakReference<>(service);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            UpdateService service = weakReference.get();
            if (null != service) {
                service.getProgress();
            }
        }
    }
}
