package com.zxycloud.zszw.receiver;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.zxycloud.zszw.event.PhoneEvent;
import com.zxycloud.common.utils.rxbus2.RxBus;
import com.zxycloud.common.utils.rxbus2.RxBusCode;

/**
 * Created by leiming on 2018/1/30.
 */

public class PhoneBroadcastReceiver extends BroadcastReceiver {

    private static final String TAG = "PhoneBroadcastReceiver";
    private static String mIncomingNumber = null;

    @Override
    public void onReceive(Context context, Intent intent) {
//        // 如果是拨打电话
        if (intent.getAction().equals(Intent.ACTION_NEW_OUTGOING_CALL)) {
            String phoneNumber = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);
            RxBus.get().send(RxBusCode.RX_CALLING, new PhoneEvent(PhoneEvent.callOut));
            Log.i(TAG, "call OUT:" + phoneNumber);
        } else {
            // 如果是来电
            TelephonyManager tManager = (TelephonyManager) context
                    .getSystemService(Service.TELEPHONY_SERVICE);

            RxBus.get().send(RxBusCode.RX_CALLING, new PhoneEvent(tManager.getCallState()));

            /*switch (tManager.getCallState()) {
                case TelephonyManager.CALL_STATE_RINGING:
                    mIncomingNumber = intent.getStringExtra("incoming_number");
                    Log.i(TAG, "RINGING :" + mIncomingNumber);
                    break;
                case TelephonyManager.CALL_STATE_OFFHOOK:
                    Log.i(TAG, "incoming ACCEPT :" + mIncomingNumber);
                    break;
                case TelephonyManager.CALL_STATE_IDLE:
                    Log.i(TAG, "incoming IDLE");
                    break;
                default:
                    Log.i(TAG, "incoming default");
                    break;
            }*/
        }
    }
}