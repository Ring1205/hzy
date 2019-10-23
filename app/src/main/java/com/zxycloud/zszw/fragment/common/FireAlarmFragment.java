package com.zxycloud.zszw.fragment.common;

import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.zxycloud.zszw.R;
import com.zxycloud.zszw.base.BaseMapViewFragment;

public class FireAlarmFragment extends BaseMapViewFragment implements Toolbar.OnMenuItemClickListener, View.OnClickListener {
    private CardView cvReviewLayout;

    public static FireAlarmFragment newInstance() {
        Bundle args = new Bundle();
        FireAlarmFragment fragment = new FireAlarmFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.alarm_fire;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        setToolbarTitle(R.string.fire_alarm).setToolbarMenu(R.menu.quit, this);
        cvReviewLayout = findViewById(R.id.cv_review_layout);
        initMapView(savedInstanceState);

        setOnClickListener(this, R.id.tv_fire_point, R.id.tv_fire_video, R.id.tv_fire_device);
    }
    @Override
    public void onClick(View view) {
        super.onClick(view);
        switch (view.getId()) {
            case R.id.tv_fire_point:
                addMarkerInScreenCenter();
                break;
            case R.id.tv_fire_video:
                cvReviewLayout.setVisibility(View.GONE);
                initSearch();
                break;
            case R.id.tv_fire_device:
                break;
        }
    }

    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.action_quit:
                finish();
                break;
        }
        return true;
    }

}
