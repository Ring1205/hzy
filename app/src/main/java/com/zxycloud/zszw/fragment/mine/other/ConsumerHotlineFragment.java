package com.zxycloud.zszw.fragment.mine.other;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;

import com.zxycloud.zszw.R;
import com.zxycloud.zszw.base.BaseBackFragment;
import com.zxycloud.common.utils.CommonUtils;
import com.zxycloud.common.utils.PermissionUtils;

import java.util.List;

public class ConsumerHotlineFragment extends BaseBackFragment implements View.OnClickListener {

    public static ConsumerHotlineFragment newInstance() {
        return new ConsumerHotlineFragment();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_consumer_hotline;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        setToolbarTitle(R.string.consumer_hotline).initToolbarNav();
        setOnClickListener(this, R.id.consumer_hotline_call);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.consumer_hotline_call:
                PermissionUtils.setRequestPermissions(_mActivity, new PermissionUtils.PermissionGrant() {
                    @Override
                    public Integer[] onPermissionGranted() {
                        return new Integer[]{PermissionUtils.CODE_CALL_PHONE};
                    }

                    @Override
                    public void onRequestResult(List<String> deniedPermission) {
                        if (CommonUtils.judgeListNull(deniedPermission) == 0) {
                            // 跳转到拨号页面
                            Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:4000220119"));
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                        } else {
                            CommonUtils.toast(_mActivity, R.string.common_permission_call);
                        }
                    }
                });
                break;
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionUtils.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
