package com.zxycloud.zszw.base;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.Toolbar;

import com.zxycloud.zszw.R;
import com.uuzuche.lib_zxing.activity.CaptureFragment;
import com.uuzuche.lib_zxing.activity.CodeUtils;
import com.zxycloud.common.utils.CommonUtils;
import com.zxycloud.common.utils.FileUtils;
import com.zxycloud.common.utils.PermissionUtils;

import java.util.List;

public abstract class BaseQRCodeFragment extends BaseBackFragment {
    public boolean isOpen = false;
    private CaptureFragment captureFragment;
    public static final int REQUEST_IMAGE = 112;

    /**
     * 初始化Toolbar
     *
     * @param titleId
     * @return
     */
    protected Toolbar setmToolbar(int titleId) {
        setToolbarTitle(R.id.toolbar_base, titleId).initToolbarNav();
        return mToolbar;
    }

    /**
     * 开关闪光灯
     */
    protected void openLight() {
        if (!isOpen)
            CodeUtils.isLightEnable(true);
        else
            CodeUtils.isLightEnable(false);

        isOpen = !isOpen;
    }

    /**
     * 打开相册识别
     */
    protected void openPhoto() {
        Intent inImg = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(inImg, REQUEST_IMAGE);
    }

    /**
     * 初始化Fragment
     *
     * @param fId
     */
    protected void setFragmentId(final int fId) {
        PermissionUtils.setRequestPermissions(this, new PermissionUtils.PermissionGrant() {
            @Override
            public Integer[] onPermissionGranted() {
                return new Integer[]{PermissionUtils.CODE_CAMERA, PermissionUtils.CODE_READ_EXTERNAL_STORAGE, PermissionUtils.CODE_WRITE_EXTERNAL_STORAGE};
            }

            @Override
            public void onRequestResult(List<String> deniedPermission) {
                if (CommonUtils.judgeListNull(deniedPermission) != 0)
                    finish();
            }
        });


        captureFragment = new CaptureFragment();
        captureFragment.setAnalyzeCallback(new CodeUtils.AnalyzeCallback() {
            @Override
            public void onAnalyzeSuccess(Bitmap mBitmap, String result) {
                onQrCodeSuccess(mBitmap, result);
            }

            @Override
            public void onAnalyzeFailed() {
                onQrCodeSuccess(null, null);
            }
        });

        getChildFragmentManager().beginTransaction().replace(fId, captureFragment).commit();
    }

    protected abstract void onQrCodeSuccess(Bitmap mBitmap, String result);

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_IMAGE:// 相册
                if (data != null) {
                    Uri uri = data.getData();
                    try {
                        CodeUtils.analyzeBitmap(FileUtils.getFilePathByUri(getContext(), uri), new CodeUtils.AnalyzeCallback() {
                            @Override
                            public void onAnalyzeSuccess(Bitmap mBitmap, String result) {
                                onQrCodeSuccess(mBitmap, result);
                            }

                            @Override
                            public void onAnalyzeFailed() {
                                onQrCodeSuccess(null, null);
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionUtils.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

}