package com.zxycloud.startup.ui;

import android.os.Bundle;

import com.zxycloud.startup.R;
import com.zxycloud.common.base.fragment.SupportActivity;
import com.zxycloud.common.utils.SystemUtil;

/**
 * 引导页
 *
 * @author leiming
 * @date 2019/3/7 11:55
 */ 
public class GuideActivity extends SupportActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide);

        // 设置状态栏白底黑字
        SystemUtil.StatusBarLightMode(this);
    }
}
