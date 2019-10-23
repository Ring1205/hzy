package com.zxycloud.startup.routerAction;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.zxycloud.startup.ui.LoginActivity;
import com.sarlmoclen.router.SAction;

import java.util.HashMap;
import java.util.Map;

/**
 * @author leiming
 * @date 2019/3/11.
 */
public class LogoutAction extends SAction {
    @Override
    public Object startAction(Context context, HashMap<String, Object> requestData) {
        if (context instanceof Activity) {
            Intent i = new Intent(context, LoginActivity.class);
            Bundle bundle = new Bundle();
            for (Map.Entry<String, Object> entry : requestData.entrySet()) {
                bundle.putString(entry.getKey(), String.valueOf(entry.getValue()));
            }
            i.putExtra("bundle", bundle);
            context.startActivity(i);
        } else {
            Intent i = new Intent(context, LoginActivity.class);
            Bundle bundle = new Bundle();
            for (Map.Entry<String, Object> entry : requestData.entrySet()) {
                bundle.putString(entry.getKey(), String.valueOf(entry.getValue()));
            }
            i.putExtra("bundle", bundle);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(i);
        }
        return "arrive one success!";
    }
}
