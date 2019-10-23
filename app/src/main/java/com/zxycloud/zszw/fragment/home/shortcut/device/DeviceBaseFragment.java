package com.zxycloud.zszw.fragment.home.shortcut.device;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.design.widget.CustomTabLayout;
import android.support.v4.view.ViewPager;
import android.util.TypedValue;
import android.widget.TextView;

import com.zxycloud.zszw.R;
import com.zxycloud.zszw.base.BaseBackFragment;
import com.zxycloud.zszw.event.type.DeviceShowType;
import com.zxycloud.zszw.fragment.home.shortcut.place.PlaceDetailFragment;
import com.zxycloud.zszw.widget.MyTabSelectedListener;
import com.zxycloud.common.base.adapter.TabAdapter;
import com.zxycloud.common.base.fragment.ISupportFragment;

/**
 * 当需要展示多状态tab的设备列表时的ViewPager
 */
public class DeviceBaseFragment extends BaseBackFragment {
    private String placeId;

    private CustomTabLayout deviceTab;
    private ViewPager devicePager;
    private int[] titles = {R.string.place_adapter, R.string.place_device};

    private DeviceViewPagerFragment[] fragments;

    public static DeviceBaseFragment newInstance(String placeId, String placeName) {
        Bundle args = new Bundle();
        args.putString("placeId", placeId);
        args.putString("placeName", placeName);
        DeviceBaseFragment fragment = new DeviceBaseFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.layout_custom_tab;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void initView(Bundle savedInstanceState) {
        Bundle args = getArguments();
        placeId = args.getString("placeId");
        fragments = new DeviceViewPagerFragment[]{
                DeviceViewPagerFragment.newInstance(DeviceShowType.SHOW_TYPE_ADAPTER_LIST, placeId)
                , DeviceViewPagerFragment.newInstance(DeviceShowType.SHOW_TYPE_INDEPENDENT_DEVICE_LIST, placeId)};

        deviceTab = findViewById(R.id.tab_titles);
        devicePager = findViewById(R.id.pager_context);

        devicePager.setAdapter(new TabAdapter(_mActivity, getChildFragmentManager(), fragments, titles));
        deviceTab.setupWithViewPager(devicePager);
        deviceTab.addOnTabSelectedListener(new MyTabSelectedListener(getContext(), 16, 14));

        deviceTab.post(new Runnable() {
            @Override
            public void run() {
                _mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        TextView tv_tab = deviceTab.getTabAt(0).getCustomView().findViewById(R.id.tv_tab);
                        tv_tab.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16f);
                        tv_tab.setTextColor(getResources().getColor(R.color.common_color_text));
                    }
                });
            }
        });

    }

    public void startFragment(ISupportFragment fragment) {
        ((PlaceDetailFragment) getParentFragment()).start(fragment);
    }
}
