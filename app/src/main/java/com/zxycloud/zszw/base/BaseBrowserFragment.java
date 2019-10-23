package com.zxycloud.zszw.base;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ProgressBar;

import com.zxycloud.zszw.R;
import com.tencent.smtt.sdk.CookieSyncManager;
import com.tencent.smtt.sdk.WebChromeClient;
import com.tencent.smtt.sdk.WebSettings;
import com.tencent.smtt.sdk.WebSettings.LayoutAlgorithm;
import com.tencent.smtt.sdk.WebView;

public abstract class BaseBrowserFragment extends BaseBackFragment {
    private WebView mWebView;
    protected Toolbar mToolbar;
    private ProgressBar mPageLoadingProgressBar = null;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_webview;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        mWebView = findViewById(R.id.webView);

        WebSettings webSetting = mWebView.getSettings();
        webSetting.setAllowFileAccess(true);
        webSetting.setLayoutAlgorithm(LayoutAlgorithm.NARROW_COLUMNS);
        webSetting.setSupportZoom(true);
        webSetting.setBuiltInZoomControls(true);
        webSetting.setUseWideViewPort(true);
        webSetting.setSupportMultipleWindows(false);
        // webSetting.setLoadWithOverviewMode(true);
        webSetting.setAppCacheEnabled(true);
        // webSetting.setDatabaseEnabled(true);
        webSetting.setDomStorageEnabled(true);
        webSetting.setJavaScriptEnabled(true);
        webSetting.setGeolocationEnabled(true);
        webSetting.setAppCacheMaxSize(Long.MAX_VALUE);
        webSetting.setAppCachePath(_mActivity.getDir("appcache", 0).getPath());
        webSetting.setDatabasePath(_mActivity.getDir("databases", 0).getPath());
        webSetting.setGeolocationDatabasePath(_mActivity.getDir("geolocation", 0)
                .getPath());
        // webSetting.setPageCacheCapacity(IX5WebSettings.DEFAULT_CACHE_CAPACITY);
        webSetting.setPluginState(WebSettings.PluginState.ON_DEMAND);
        // webSetting.setRenderPriority(WebSettings.RenderPriority.HIGH);
        // webSetting.setPreFectch(true);
        CookieSyncManager.createInstance(_mActivity);
        CookieSyncManager.getInstance().sync();

        mWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView webView, int i) {
                super.onProgressChanged(webView, i);
                if (null != mPageLoadingProgressBar) {
                    if (i < 100) {
                        mPageLoadingProgressBar.setVisibility(View.VISIBLE);
                        mPageLoadingProgressBar.setProgress(i);
                    } else {
                        mPageLoadingProgressBar.setVisibility(View.GONE);
                    }
                }
            }
        });

        initProgressBar();

        initBrowserData();
    }

    private void initProgressBar() {
        mPageLoadingProgressBar = findViewById(R.id.progressBar1);// new
        mPageLoadingProgressBar.setMax(100);
        mPageLoadingProgressBar.setProgressDrawable(this.getResources()
                .getDrawable(R.drawable.color_progressbar));
    }

    /**
     * 初始化布局
     */
    protected abstract void initBrowserData();

    protected void loadUrl(String url) {
        mWebView.loadUrl(url);
    }

    @Override
    public void finish() {
        if (mWebView != null && mWebView.canGoBack()) {
            mWebView.goBack();
            if (Integer.parseInt(android.os.Build.VERSION.SDK) >= 16)
                changGoForwardButton(mWebView);
        } else
            super.finish();
    }

    private void changGoForwardButton(WebView view) {
        if (view.canGoBack())
            view.goBack();
    }

    @Override
    public void onResume() {
        super.onResume();
        mWebView.onResume();
        mWebView.getSettings().setJavaScriptEnabled(true);
    }
}
