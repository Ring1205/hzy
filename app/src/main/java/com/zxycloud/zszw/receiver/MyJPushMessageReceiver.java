package com.zxycloud.zszw.receiver;

import android.content.Context;

import com.zxycloud.common.utils.CommonUtils;

import cn.jpush.android.api.JPushMessage;
import cn.jpush.android.service.JPushMessageReceiver;

/**
 * 极光推送接受者
 *
 * @author leiming
 * @date 2017/10/11
 */
public class MyJPushMessageReceiver extends JPushMessageReceiver {
    private final String TAG = "registrationId";
    @Override
    public void onTagOperatorResult(Context context, JPushMessage jPushMessage) {
        super.onTagOperatorResult(context, jPushMessage);
        CommonUtils.log().i(TAG, "[onTagOperatorResult]");
    }
    @Override
    public void onCheckTagOperatorResult(Context context, JPushMessage jPushMessage){
        super.onCheckTagOperatorResult(context, jPushMessage);
        CommonUtils.log().i(TAG, "[onCheckTagOperatorResult]");
    }
    @Override
    public void onAliasOperatorResult(Context context, JPushMessage jPushMessage) {
        super.onAliasOperatorResult(context, jPushMessage);
        CommonUtils.log().i(TAG, "[onAliasOperatorResult]");
    }
}

