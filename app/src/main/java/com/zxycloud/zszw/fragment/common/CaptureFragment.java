package com.zxycloud.zszw.fragment.common;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;

import com.zxycloud.zszw.R;
import com.zxycloud.zszw.base.BaseQRCodeFragment;

public class CaptureFragment extends BaseQRCodeFragment implements View.OnClickListener {
    public static final String SCAN_RESULT = "scanResult";

    public static CaptureFragment newInstance() {
        CaptureFragment fragment = new CaptureFragment();
        Bundle bundle = new Bundle();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.capture;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        setmToolbar(R.string.capture);

        setFragmentId(R.id.fl_capture);

        setOnClickListener(this, R.id.btn_photo, R.id.btn_light);

        findViewById(R.id.btn_input).setVisibility(View.GONE);
    }

    @Override
    protected void onQrCodeSuccess(Bitmap mBitmap, String result) {
        if (mBitmap != null && result != null) {
            Bundle bundleSearch = new Bundle();
            bundleSearch.putString(SCAN_RESULT, result.replace("\uFEFF", ""));
            setFragmentResult(RESULT_OK, bundleSearch);
            finish();
        }
    }

    @Override
    public void onClick(final View view) {
        switch (view.getId()) {
            case R.id.btn_photo:
                openPhoto();
                break;
            case R.id.btn_light:
                openLight();
                break;
        }
    }
}
