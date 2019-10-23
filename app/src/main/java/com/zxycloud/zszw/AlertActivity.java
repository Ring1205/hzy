package com.zxycloud.zszw;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;

import com.zxycloud.zszw.event.type.AlertShowType;
import com.zxycloud.zszw.fragment.home.message.AlertDetailFragment;
import com.zxycloud.zszw.service.AlertService;
import com.zxycloud.common.base.fragment.SupportActivity;
import com.zxycloud.common.base.fragment.anim.DefaultHorizontalAnimator;
import com.zxycloud.common.base.fragment.anim.FragmentAnimator;
import com.zxycloud.common.utils.CommonUtils;
import com.zxycloud.common.utils.SystemUtil;

/**
 * @author leiming
 * @date 2019/4/10.
 */
public class AlertActivity extends SupportActivity {

    private AlertService alertService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);

        Intent intent = getIntent();
        Bundle bundle = intent.getBundleExtra("bundle");
        int stateCode = bundle.getInt("stateCode", CommonUtils.STATE_CODE_FIRE);

        Intent serviceIntent = new Intent(AlertActivity.this, AlertService.class);
        serviceIntent.putExtra("stateCode", stateCode);
        boolean isBind = bindService(serviceIntent, conn, Context.BIND_AUTO_CREATE);
        CommonUtils.log().i("isBind = " + isBind);

        // 设置状态栏白底黑字
        SystemUtil.StatusBarLightMode(this);

        AlertDetailFragment detailFragment = AlertDetailFragment.newInstance(AlertShowType.ALERT_HAPPENING, bundle.getInt("stateCode", CommonUtils.STATE_CODE_FIRE), bundle.getString("alertBean"));

        detailFragment.setVoiceControlListener(voiceControlListener);

        loadRootFragment(R.id.activity_base_fl
                , detailFragment);
    }

    private ServiceConnection conn = new ServiceConnection() {
        /** 获取服务对象时的操作 */
        public void onServiceConnected(ComponentName name, IBinder service) {
            // TODO Auto-generated method stub
            alertService = ((AlertService.ServiceBinder) service).getService();

        }

        /** 无法获取到服务对象时的操作 */
        public void onServiceDisconnected(ComponentName name) {
            // TODO Auto-generated method stub
            alertService = null;
        }

    };
    @Override
    public FragmentAnimator onCreateFragmentAnimator() {
        // 设置横向(和安卓4.x动画相同)
        return new DefaultHorizontalAnimator();
    }

    protected void onDestroy() {
        super.onDestroy();
        if (null != alertService)
            this.unbindService(conn);
    }

    private AlertDetailFragment.VoiceControlListener voiceControlListener = new AlertDetailFragment.VoiceControlListener() {
        @Override
        public void stopVoice() {
            try {
                AlertActivity.this.unbindService(conn);
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            }
        }
    };
}