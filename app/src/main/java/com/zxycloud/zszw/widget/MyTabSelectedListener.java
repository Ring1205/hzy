package com.zxycloud.zszw.widget;

import android.content.Context;
import android.support.design.widget.TabLayout;
import android.util.TypedValue;
import android.widget.TextView;

import com.zxycloud.zszw.R;

public class MyTabSelectedListener implements TabLayout.OnTabSelectedListener {
    Context context;
    float nor = 17,pit = 15;

    public MyTabSelectedListener(Context context) {
        this.context = context;
    }

    public MyTabSelectedListener(Context context, float nor, float pit) {
        this.context = context;
        this.nor = nor;
        this.pit = pit;
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        TextView tv_tab = tab.getCustomView().findViewById(R.id.tv_tab);
        tv_tab.setTextSize(TypedValue.COMPLEX_UNIT_DIP, nor);
        tv_tab.setTextColor(context.getResources().getColor(R.color.common_color_text));
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {
        TextView tv_tab = tab.getCustomView().findViewById(R.id.tv_tab);
        tv_tab.setTextSize(TypedValue.COMPLEX_UNIT_DIP, pit);
        tv_tab.setTextColor(context.getResources().getColor(R.color.common_color_text_level_3));
    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }
}
