package com.zxycloud.startup.ui.Fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.zxycloud.startup.R;

/**
 * Created by YoKeyword on 16/2/7.
 */
public abstract class BaseBackFragment extends MyBaseFragment {
    protected Toolbar mToolbar;
    private OnNavigationListener navigationListener;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setParallaxOffset(0.5f);
    }

    protected BaseBackFragment setToolbarTitle(int title) {
        mToolbar = findViewById(R.id.toolbar);
        mToolbar.setTitle(title);
        return this;
    }

    public BaseBackFragment setToolbarMenu(int menu, Toolbar.OnMenuItemClickListener listener) {
        mToolbar.inflateMenu(menu);
        mToolbar.setOnMenuItemClickListener(listener);
        return this;
    }

    public BaseBackFragment initToolbarNav() {
        if (mToolbar != null){
//        toolbar.setNavigationIcon(R.drawable.icon_back);
            mToolbar.setNavigationIcon(R.mipmap.ic_arrow_back_white_24dp);
            mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (navigationListener == null || navigationListener.addNavigationBack())
                        _mActivity.onBackPressed();
                }
            });
        }
        return this;
    }

    /**
     * 此处可以做退出判断，确认是否退出
     *
     * @return
     */
    protected void addNavigationBack(OnNavigationListener navigationListener) {
        this.navigationListener = navigationListener;
    }
}
