package com.zxycloud.zszw.base;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.zxycloud.zszw.R;
import com.zxycloud.zszw.fragment.common.CaptureFragment;
import com.zxycloud.common.utils.netWork.ApiRequest;

public abstract class BaseSearchListFragment extends BaseBackFragment implements Toolbar.OnMenuItemClickListener {
    private int request_code = 3166;
    private EditText etSearchKey;
    private String searchString;

    protected BaseBackFragment setSearchToolbar(boolean isShowScan, int title) {
        findViewById(R.id.iv_scan).setVisibility(isShowScan ? View.VISIBLE : View.GONE);
        return setToolbarTitle(title).initToolbarNav().setToolbarMenu(R.menu.menu_search, this);
    }

    protected BaseBackFragment setAddSearchToolbar(boolean isShowScan, int title, Toolbar.OnMenuItemClickListener listener) {
        findViewById(R.id.iv_scan).setVisibility(isShowScan ? View.VISIBLE : View.GONE);
        return setToolbarTitle(title).initToolbarNav().setToolbarMenu(R.menu.add, listener).setToolbarMenu(R.menu.menu_search, this);
    }

    public void initSearchEditText(final String action, final String param) {
        etSearchKey = findViewById(R.id.et_search);
        etSearchKey.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                ApiRequest apiRequest = netWork().setRequestListener(action, 800L).setRequestParams(param, s.toString().trim());
                if (apiRequest.getRequestParams().get("pageIndex") != null)
                    apiRequest.setRequestParams("pageIndex", 1).setTag(1);

                if (netWork().getRefreshLayout() != null)
                    netWork().getRefreshLayout().resetNoMoreData();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startForResult(CaptureFragment.newInstance(), request_code);
            }
        }, R.id.iv_scan);
    }

    public void initSearchEditText(TextWatcher watcher) {
        etSearchKey = findViewById(R.id.et_search);
        etSearchKey.addTextChangedListener(watcher);

        setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startForResult(CaptureFragment.newInstance(), request_code);
            }
        }, R.id.iv_scan);
    }



    @Override
    public void onFragmentResult(int requestCode, int resultCode, Bundle data) {
        super.onFragmentResult(requestCode, resultCode, data);
        if (requestCode == request_code && resultCode == RESULT_OK && null != data) {
            String searchStr = data.getString(CaptureFragment.SCAN_RESULT, "");
            etSearchKey.setText(searchStr.trim());
            etSearchKey.setSelection(searchStr.trim().length());
        }
    }

    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.action_message:
                if (findViewById(R.id.include_search).getVisibility() != View.VISIBLE) {
                    menuItem.setIcon(R.mipmap.common_ic_title_finish);
                    findViewById(R.id.include_search).setVisibility(View.VISIBLE);
                    etSearchKey.setText(searchString != null ? searchString : "");
                    etSearchKey.setSelection(etSearchKey.getText().length());
                } else {
                    menuItem.setIcon(R.mipmap.ic_search);
                    findViewById(R.id.include_search).setVisibility(View.GONE);
                    searchString = etSearchKey.getText().toString();
                    etSearchKey.setText("");
                }
                break;
        }
        return false;
    }

}
