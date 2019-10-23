package com.zxycloud.zszw.fragment.service.users;

import android.os.Bundle;

import com.zxycloud.zszw.R;
import com.zxycloud.zszw.base.BaseBackFragment;

public class UserinfoFragment extends BaseBackFragment {

    public static UserinfoFragment newInstance() {
        Bundle args = new Bundle();
        UserinfoFragment fragment = new UserinfoFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.user_info;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        setToolbarTitle(R.string.user_info).initToolbarNav();
    }
}
