package com.zxycloud.zszw.fragment.common;

import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;

import com.zxycloud.zszw.base.BaseBackFragment;
import com.zxycloud.common.R;
import com.zxycloud.common.utils.CommonUtils;
import com.zxycloud.common.utils.GlideUtils;
import com.zxycloud.common.widget.FloorPointView.PhotoView;

import java.util.ArrayList;
import java.util.List;

/**
 * @author leiming
 * @date 2019/3/30.
 */
public class ImagePagerFragment extends BaseBackFragment {

    private ViewPager pager;
    private int pagerPosition;
    private List<String> paths;

    public static ImagePagerFragment newInstance(int pagerPosition, ArrayList<String> paths) {
        Bundle args = new Bundle();
        args.putInt("pagerPosition", pagerPosition);
        args.putStringArrayList("paths", paths);
        ImagePagerFragment fragment = new ImagePagerFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_image_pager;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        setToolbarTitle(R.string.picture_preview);
        initToolbarNav();

        Bundle args = getArguments();
        pagerPosition = args.getInt("pagerPosition");
        paths = args.getStringArrayList("paths");

        pager = findViewById(R.id.pager);
        pager.setAdapter(adapter);
        pager.setCurrentItem(pagerPosition);
    }

    private PagerAdapter adapter = new PagerAdapter() {
        GlideUtils utils = CommonUtils.glide();

        @Override
        public int getCount() {
            return CommonUtils.judgeListNull(paths);
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public View instantiateItem(ViewGroup container, int position) {
            PhotoView photoView = new PhotoView(container.getContext());
            utils.loadImageView(_mActivity, paths.get(position), photoView);
            // Now just add PhotoView to ViewPager and return it
            container.addView(photoView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            return photoView;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }
    };
}
