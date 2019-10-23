package com.zxycloud.common.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

/**
 * 界面跳转类
 *
 * @author leiming
 * @date 2017/10/19
 */
public class JumpToUtils {
    /**
     * 跳转页面
     *
     * @param context        当前上下文
     * @param targetActivity 所跳转的目的Activity类
     */
    public static void jumpTo(Context context, Class<?> targetActivity) {
        jumpTo(context, targetActivity, null);
    }

    /**
     * 跳转页面
     *
     * @param context        当前上下文
     * @param targetActivity 所跳转的目的Activity类
     * @param bundle         跳转所携带的信息
     */
    public static void jumpTo(Context context, Class<?> targetActivity, Bundle bundle) {
        if (! new TimeUpUtils().isTimeUp(TimeUpUtils.TIME_UP_JUMP, System.currentTimeMillis())) {
            return;
        }
        Intent intent = new Intent(context, targetActivity);
        if (bundle != null) {
            intent.putExtra("bundle", bundle);
        }
        context.startActivity(intent);
    }

    /**
     * 跳转页面
     *
     * @param context        当前上下文
     * @param targetActivity 所跳转的目的Activity类
     */
    public static void receiverJumpTo(Context context, Class<?> targetActivity) {
        receiverJumpTo(context, targetActivity, null);
    }

    /**
     * 跳转页面
     *
     * @param context        当前上下文
     * @param targetActivity 所跳转的目的Activity类
     * @param bundle         跳转所携带的信息
     */
    public static void receiverJumpTo(Context context, Class<?> targetActivity, Bundle bundle) {
        if (! new TimeUpUtils().isTimeUp(TimeUpUtils.TIME_UP_JUMP, System.currentTimeMillis())) {
            return;
        }
        Intent intent = new Intent(context, targetActivity);
        if (bundle != null) {
            intent.putExtra("bundle", bundle);
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    /**
     * 跳转页面
     *
     * @param context        当前上下文
     * @param targetActivity 所跳转的Activity类
     * @param requestCode    请求码
     */
    public static void jumpTo(Context context, Class<?> targetActivity, int requestCode) {
        jumpTo(context, targetActivity, requestCode, null);
    }

    /**
     * 跳转页面
     *
     * @param context        当前上下文
     * @param targetActivity 所跳转的Activity类
     * @param bundle         跳转所携带的信息
     * @param requestCode    请求码
     */
    public static void jumpTo(Context context, Class<?> targetActivity, int requestCode, Bundle bundle) {
        if (! new TimeUpUtils().isTimeUp(TimeUpUtils.TIME_UP_JUMP, System.currentTimeMillis())) {
            return;
        }
        Intent intent = new Intent(context, targetActivity);
        if (bundle != null) {
            intent.putExtra("bundle", bundle);
        }
        ((Activity) context).startActivityForResult(intent, requestCode);
    }
}
