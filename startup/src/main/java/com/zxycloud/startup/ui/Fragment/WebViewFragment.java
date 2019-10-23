package com.zxycloud.startup.ui.Fragment;

import android.os.Bundle;
import android.support.annotation.StringRes;

/**
 * @author leiming
 * @date 2019/4/16.
 */
public class WebViewFragment extends BaseBrowserFragment {
    private String url;

    public static WebViewFragment newInstance(@StringRes int titleId, String url) {
        Bundle args = new Bundle();
        args.putString("url", url);
        args.putInt("titleId", titleId);
        WebViewFragment fragment = new WebViewFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected void initBrowserData() {
        Bundle bundle = getArguments();
        setToolbarTitle(bundle.getInt("titleId")).initToolbarNav();
        url = bundle.getString("url");
        loadUrl(url);
    }

}
