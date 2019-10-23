package com.zxycloud.zszw.fragment.service.install.device;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.google.gson.Gson;
import com.zxycloud.zszw.R;
import com.zxycloud.zszw.adapter.BasePagerAdapter;
import com.zxycloud.zszw.base.BaseBackFragment;
import com.zxycloud.zszw.model.bean.DeviceBean;
import com.zxycloud.common.utils.CommonUtils;

import java.util.ArrayList;
import java.util.List;

public class DeviceAssignmentFragment extends BaseBackFragment implements Toolbar.OnMenuItemClickListener, View.OnClickListener {
    private FloatingActionButton fabCheckAll;
    private ViewPager mViewPager;
    private TabLayout mTab;
    private List<DeviceBean> deviceBeanList = new ArrayList<>();
    private Boolean[] checkedFabs;
    private BasePagerAdapter pagerAdapter;
    private int page;

    public static DeviceAssignmentFragment newInstance(String adapterName, String placeId, String placeName, String picUrl) {
        DeviceAssignmentFragment fragment = new DeviceAssignmentFragment();
        Bundle bundle = new Bundle();
        bundle.putString("adapterName", adapterName);
        bundle.putString("placeId", placeId);
        bundle.putString("placeName", placeName);
        bundle.putString("picUrl", picUrl);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.device_assignment;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        setToolbarTitle(R.string.device_assign).initToolbarNav().setToolbarMenu(R.menu.next, this);

        mViewPager = findViewById(R.id.viewPager);
        mTab = findViewById(R.id.tab_assign);
        fabCheckAll = findViewById(R.id.fab_check_all);

        checkedFabs = new Boolean[]{false, false};
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {
                // 滑动监听，i:点击滑动的页面，v:滑动的页面占屏幕百分比，i1:滑动的页面的屏幕像素
            }

            @Override
            public void onPageSelected(int i) {
                // 完成切换后的页面
                page = i;
                fabChecked(checkedFabs[i]);
            }

            @Override
            public void onPageScrollStateChanged(int i) {
                // 当页面停止的时候该参数为0，页面开始滑动的时候变成了1，当手指从屏幕上抬起变为了2（无论页面是否从1跳到了2），当页面静止后又变成了0
            }
        });
        setOnClickListener(this, R.id.fab_check_all);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.fab_check_all:
                fabChecked(!checkedFabs[page]);
                ((PagerAllocatedFragment) pagerAdapter.getItem(page)).isCheckAll(checkedFabs[page]);
                break;
        }
    }

    public void fabChecked(Boolean b) {
        checkedFabs[page] = b;
        if (b)
            fabCheckAll.setImageResource(R.drawable.ic_close_black_24dp);
        else
            fabCheckAll.setImageResource(R.drawable.ic_done_all_black_24dp);
    }

    @Override
    public void onLazyInitView(@Nullable Bundle savedInstanceState) {
        super.onLazyInitView(savedInstanceState);
        mTab.setupWithViewPager(mViewPager);
        pagerAdapter = new BasePagerAdapter(getChildFragmentManager(),
                getString(R.string.device_unallocated), getString(R.string.device_allocated))
                .setFragments(PagerAllocatedFragment.newInstance(PagerAllocatedFragment.UNALLOCATED, getArguments().getString("adapterName")),
                        PagerAllocatedFragment.newInstance(PagerAllocatedFragment.ALLOCATED, getArguments().getString("adapterName")));
        mViewPager.setAdapter(pagerAdapter);
    }

    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {
        if (deviceBeanList.size() != 0)
            startForResult(DeviceInstallListFragment.newInstance(new Gson().toJson(deviceBeanList),
                    getArguments().getString("placeId"),
                    getArguments().getString("placeName"),
                    getArguments().getString("picUrl")), 1030);
        else
            CommonUtils.toast(getContext(), R.string.hint_check_device);
        return true;
    }

    public void setDeviceBean(Boolean isChecked, DeviceBean bean) {
        if (isChecked)
            deviceBeanList.add(bean);
        else
            deviceBeanList.remove(bean);
    }

    public void clearDeviceList() {
        deviceBeanList.clear();
    }

    boolean isb;
    public void fabAnimator(boolean b) {
        if (isb == b)
            return;
        float y = getContext().getResources().getDisplayMetrics().heightPixels - fabCheckAll.getTop();
        ValueAnimator animator = ObjectAnimator.ofFloat(fabCheckAll, "translationY", b ? 0 : y, b ? y : 0);
        animator.setDuration(500).start();
        isb = b;
    }

    @Override
    public void onFragmentResult(int requestCode, int resultCode, Bundle data) {
        super.onFragmentResult(requestCode, resultCode, data);
        if (requestCode == 1030 && resultCode == RESULT_OK && data != null) {
            if (data.getBoolean("isSucceed"))
                finish();
        }
    }

}
