package com.zxycloud.zszw.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.zxycloud.zszw.AlertActivity;
import com.zxycloud.zszw.MainActivity;
import com.zxycloud.zszw.event.JPushEvent;
import com.zxycloud.zszw.fragment.home.message.AlertDetailFragment;
import com.zxycloud.zszw.model.JpushBean.JPushRiskBean;
import com.zxycloud.zszw.model.JpushBean.ResultJPushBean;
import com.zxycloud.common.utils.CommonUtils;
import com.zxycloud.common.utils.JumpToUtils;
import com.zxycloud.common.utils.Logger;
import com.zxycloud.common.utils.rxbus2.RxBus;
import com.zxycloud.common.utils.rxbus2.RxBusCode;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.Iterator;
import java.util.Locale;

import cn.jpush.android.api.JPushInterface;

/**
 * 极光推送接受者
 *
 * @author leiming
 * @date 2017/10/11
 */
public class JPushReceiver extends BroadcastReceiver {
    private final String TAG = "registrationId";
    private MyHandler myHandler;
    private SaveContext saveContext;
    private Logger logger;

    @Override
    public void onReceive(final Context context, Intent intent) {
        if (null == myHandler) {
            myHandler = new MyHandler(this);
        }

        logger = CommonUtils.log();

        logger.i(TAG, "[MyReceiver]");
        try {
            final Bundle bundle = intent.getExtras();
            logger.i(TAG, String.format("[MyReceiver] onReceive - %s, extras: %s", intent.getAction(), printBundle(bundle)));

            // 极光推送接收类
            final ResultJPushBean bean;
            // 推送状态判断
            if (JPushInterface.ACTION_REGISTRATION_ID.equals(intent.getAction())) {
                String regId = bundle.getString(JPushInterface.EXTRA_REGISTRATION_ID);
                logger.i(TAG, String.format("[MyReceiver] 接收Registration Id : %s", regId));
                CommonUtils.setRegistrationId(regId);
                //send the Registration Id to your server...
            } else if (JPushInterface.ACTION_MESSAGE_RECEIVED.equals(intent.getAction())) // 消息推送
            {
//                receivingNotification(context, bundle);
                logger.i(TAG, String.format("[MyReceiver] 接收到推送下来的自定义消息: %s", bundle.getString(JPushInterface.EXTRA_MESSAGE)));
                logger.i(TAG, String.format("[MyReceiver] 接收到推送下来的自定义消息: %s", bundle.getString(JPushInterface.EXTRA_CONTENT_TYPE)));
                String extraExtra = bundle.getString(JPushInterface.EXTRA_EXTRA);
                if (!TextUtils.isEmpty(extraExtra)) {
                    bean = new Gson().fromJson(extraExtra, ResultJPushBean.class);
                    if (null != bean && (bean.getMType().equals("fire") || bean.getMType().equals("prefire"))) {
                        if (!CommonUtils.hasFragment(AlertDetailFragment.class.getSimpleName())) {
                            logger.i(getClass().getSimpleName(), "start AlertActivity");
                            saveContext = new SaveContext(context);
                            // 防止AlertActivity被MainActivity覆盖，延迟1500s开启
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        Thread.sleep(1);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    } finally {
                                        Message message = myHandler.obtainMessage();
                                        message.obj = new Gson().toJson(bean.getPushRecord());
                                        message.arg1 = bean.getMType().equals("fire") ? CommonUtils.STATE_CODE_FIRE : CommonUtils.STATE_CODE_PREFIRE;
                                        myHandler.handleMessage(message);
                                    }
                                }
                            }).start();
                        }/*if (App.getInstance().isBackground() && ! BaseActivity.hasActivity(AlertActivity.class)) {
                            Bundle bundleFire = new Bundle();
                            bundleFire.putSerializable("ResultJPushBean", bean);
                            logger.i(getClass().getSimpleName(), "start AlertActivity");
                            JumpToUtils.receiverJumpTo(context, AlertActivity.class, bundleFire);
                        } else if (! Const.isServiceRunning(context, AlertService.class)) {
                            context.startService(new Intent(context, AlertService.class));
                            EventBus.getDefault().post(new JPushEvent(bean));
                            BaseNetActivity.resultJPushBean = bean;
                        }*/
                    } else {
                        CommonUtils.threadPoolExecute(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    Thread.sleep(1500);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                } finally {
                                    RxBus.get().send(RxBusCode.RX_JPUSH, null);
                                }
                            }
                        });
                    }
                }
                processCustomMessage(context, bundle);
                CommonUtils.threadPoolExecute(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(1500);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        } finally {
                            EventBus.getDefault().postSticky(new JPushEvent(null));
                        }
                    }
                });
            } else if (JPushInterface.ACTION_NOTIFICATION_RECEIVED.equals(intent.getAction())) // 火警推送
            {
                CommonUtils.threadPoolExecute(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(1500);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        } finally {
                            String extraExtra = bundle.getString(JPushInterface.EXTRA_EXTRA);
                            try {
                                JPushRiskBean jPushRiskBean = new Gson().fromJson(extraExtra, JPushRiskBean.class);
                                EventBus.getDefault().postSticky(new JPushEvent(jPushRiskBean));
                            } catch (Exception e) {
                                e.printStackTrace();
                                EventBus.getDefault().postSticky(new JPushEvent(null));
                            }
                        }
                    }
                });
            } else if (JPushInterface.ACTION_NOTIFICATION_OPENED.equals(intent.getAction()))// 通知点击
            {
                if (!CommonUtils.isRunningForeground(context)) {
                    Intent intentFault = new Intent(context, MainActivity.class);
                    intentFault.addCategory(Intent.CATEGORY_LAUNCHER);
                    intentFault.setAction(Intent.ACTION_MAIN);
                    intentFault.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
                    context.startActivity(intentFault);
                } else {
                    JumpToUtils.jumpTo(context, MainActivity.class);
                }
            } else if (JPushInterface.ACTION_RICHPUSH_CALLBACK.equals(intent.getAction())) {
                logger.i(TAG, String.format("[MyReceiver] 用户收到到RICH PUSH CALLBACK: %s", bundle.getString(JPushInterface.EXTRA_EXTRA)));
                //在这里根据 JPushInterface.EXTRA_EXTRA 的内容处理代码，比如打开新的Activity， 打开一个网页等..
            } else if (JPushInterface.ACTION_CONNECTION_CHANGE.equals(intent.getAction())) {
                boolean connected = intent.getBooleanExtra(JPushInterface.EXTRA_CONNECTION_CHANGE, false);
                logger.w(TAG, String.format("[MyReceiver]%s connected state change to %s", intent.getAction(), connected));
            } else {
                logger.i(TAG, String.format("[MyReceiver] Unhandled intent - %s", intent.getAction()));
            }
        } catch (Exception e) {
            logger.i(TAG, String.format("[MyReceiver]%s", e.toString()));
        }
    }

    /**
     * 向MainActivity发送信息
     *
     * @param context 上下文
     * @param bundle  所要推送的数据
     */

    private void processCustomMessage(Context context, Bundle bundle) {
        if (CommonUtils.isRunningForeground(context)) {
            String message = bundle.getString(JPushInterface.EXTRA_MESSAGE);
            String extras = bundle.getString(JPushInterface.EXTRA_EXTRA);
            Intent msgIntent = new Intent(CommonUtils.MESSAGE_RECEIVED_ACTION);
            msgIntent.putExtra(CommonUtils.KEY_MESSAGE, message);
            if (!TextUtils.isEmpty(extras)) {
                try {
                    JSONObject extraJson = new JSONObject(extras);
                    if (extraJson.length() > 0) {
                        msgIntent.putExtra(CommonUtils.KEY_EXTRAS, extras);
                    }
                } catch (JSONException e) {
                    logger.i(TAG, e.getMessage());
                }

            }
            LocalBroadcastManager.getInstance(context).sendBroadcast(msgIntent);
        }
    }

    /**
     * 打印所有的 intent extra 数据
     */
    private String printBundle(Bundle bundle) {
        StringBuilder sb = new StringBuilder();
        for (String key : bundle.keySet()) {
            switch (key) {
                case JPushInterface.EXTRA_NOTIFICATION_ID:
                    sb.append(String.format(Locale.CHINA, "\nkey:%s, value:%d", key, bundle.getInt(key)));
                    break;
                case JPushInterface.EXTRA_CONNECTION_CHANGE:
                    sb.append(String.format("\nkey:%s, value:%s", key, bundle.getBoolean(key)));
                    break;
                case JPushInterface.EXTRA_EXTRA:
                    if (TextUtils.isEmpty(bundle.getString(JPushInterface.EXTRA_EXTRA))) {
                        logger.i(TAG, "This message has no Extra data");
                        continue;
                    }
                    try {
                        JSONObject json = new JSONObject(bundle.getString(JPushInterface.EXTRA_EXTRA));
                        Iterator<String> it = json.keys();

                        while (it.hasNext()) {
                            String myKey = it.next();
                            sb.append(String.format("\nkey:%s, value: [%s - %s]", key, myKey, json.optString(myKey)));
                        }
                    } catch (JSONException e) {
                        logger.e(TAG, "Get message extra JSON error!");
                    }

                    break;
                default:
                    sb.append(String.format("\nkey:%s, value:%s", key, bundle.getString(key)));
                    break;
            }
        }
        return sb.toString();
    }

    class SaveContext {
        private Context context;

        SaveContext(Context c) {
            this.context = c;
        }

        public Context getContext() {
            return context;
        }
    }

    /**
     * 音频播放定时器
     */
    private static class MyHandler extends Handler {
        WeakReference<JPushReceiver> recordingUtilsWeakReference;

        MyHandler(JPushReceiver jPushReceiver) {
            recordingUtilsWeakReference = new WeakReference<>(jPushReceiver);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            JPushReceiver jPushReceiver = recordingUtilsWeakReference.get();
            if (null != jPushReceiver) {
                if (CommonUtils.hasActivity(AlertActivity.class)) {
                    return;
                }
                Bundle bundle = new Bundle();
                bundle.putString("alertBean", (String) msg.obj);
                bundle.putInt("stateCode", msg.arg1);
                JumpToUtils.receiverJumpTo(jPushReceiver.saveContext.getContext(), AlertActivity.class, bundle);
            }
        }
    }
}
