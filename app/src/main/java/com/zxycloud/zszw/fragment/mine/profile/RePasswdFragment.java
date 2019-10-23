package com.zxycloud.zszw.fragment.mine.profile;

import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;

import com.zxycloud.zszw.R;
import com.zxycloud.zszw.base.BaseBackFragment;
import com.zxycloud.zszw.listener.NetRequestListener;
import com.zxycloud.common.base.BaseBean;
import com.zxycloud.common.utils.CommonUtils;
import com.zxycloud.common.utils.SPUtils;
import com.zxycloud.common.utils.netWork.ApiRequest;
import com.zxycloud.common.utils.netWork.NetBean;

import static android.text.InputType.TYPE_CLASS_TEXT;
import static android.text.InputType.TYPE_TEXT_VARIATION_NORMAL;
import static android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD;

public class RePasswdFragment extends BaseBackFragment implements Toolbar.OnMenuItemClickListener, View.OnClickListener {
    private boolean isChecked;
    private EditText confirmNewPwd, newPwd, oldPwd;
    private TextInputLayout tilConfirmPwd;
    private TextInputLayout tilOldPwd;
    private TextInputLayout tilNewPwd;

    public static RePasswdFragment newInstance() {
        RePasswdFragment fragment = new RePasswdFragment();
        fragment.setArguments(new Bundle());
        return fragment;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.mine_passwd;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        setToolbarTitle(R.string.modify_password).initToolbarNav().setToolbarMenu(R.menu.save, this);

        confirmNewPwd = findViewById(R.id.edit_confirm_new_pwd);
        newPwd = findViewById(R.id.edit_new_pwd);
        oldPwd = findViewById(R.id.edit_old_pwd);

        tilConfirmPwd = findViewById(R.id.til_confirm_pwd);
        tilNewPwd = findViewById(R.id.til_new_pwd);
        tilOldPwd = findViewById(R.id.til_old_pwd);

        confirmNewPwd.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                tilConfirmPwd.setError("");
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        newPwd.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                tilNewPwd.setError("");
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        oldPwd.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                tilOldPwd.setError("");
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        setOnClickListener(this, R.id.tv_forget_password, R.id.radio_display_password);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_forget_password://忘记密码

                break;
            case R.id.radio_display_password:
                setInputType(isChecked, oldPwd, newPwd, confirmNewPwd);
                if (isChecked) {
                    ((RadioButton) (view)).setChecked(false);
                }
                isChecked = !isChecked;
                break;
        }
    }

    private void setInputType(Boolean isChecked, EditText... edits) {
        for (EditText edit : edits) {
            if (!isChecked) {
                edit.setInputType(TYPE_CLASS_TEXT | TYPE_TEXT_VARIATION_NORMAL);
            } else {
                edit.setInputType(TYPE_CLASS_TEXT | TYPE_TEXT_VARIATION_PASSWORD);
            }
            edit.setSelection(edit.getText().toString().length());
        }
    }

    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {
        String oldPassword = CommonUtils.string().getString(oldPwd);
        if (TextUtils.isEmpty(oldPassword)) {
            ((TextInputLayout) findViewById(R.id.til_old_pwd)).setError(CommonUtils.string().getString(_mActivity, R.string.toast_cont_null));
            return false;
        }
        String newPassword = CommonUtils.string().getString(newPwd);
        String newConfirmPassword = CommonUtils.string().getString(confirmNewPwd);

        CommonUtils.log().i(CommonUtils.judge().isPassword(newPassword) + " newPassword = " + newPassword);

        if (TextUtils.isEmpty(newPassword) || newPassword.length() < 8 || newPassword.length() > 15 || !CommonUtils.judge().isPassword(newPassword)) {
            ((TextInputLayout) findViewById(R.id.til_new_pwd)).setError(CommonUtils.string().getString(_mActivity, com.zxycloud.startup.R.string.hint_passwd_ps));
            return false;
        } else if (!newPassword.equals(newConfirmPassword)) {
            tilNewPwd.setError(CommonUtils.string().getString(_mActivity, com.zxycloud.startup.R.string.string_toast_password_not_match));
        } else {
            String account = CommonUtils.getSPUtils(_mActivity).getString(SPUtils.USER_ACCOUNT);
            if (null != account && newPassword.contains(account)) {
                tilNewPwd.setError(CommonUtils.string().getString(_mActivity, com.zxycloud.startup.R.string.string_toast_password_contains_account));
                return true;
            }
            if (oldPassword.equals(newPassword)) {
                tilNewPwd.setError(CommonUtils.string().getString(_mActivity, com.zxycloud.startup.R.string.string_toast_password_equals_new));
                return true;
            }
            netWork().setRequestListener(new NetRequestListener() {
                @Override
                public void success(String action, BaseBean baseBean, Object tag) {
                    if (baseBean.isSuccessful()) {
                        finish();
                        CommonUtils.toast(_mActivity, R.string.success_operation);
                    } else {
                        CommonUtils.toast(_mActivity, baseBean.getMessage());
                    }
                }
            }, new ApiRequest<>(NetBean.actionResetPasswordByOld, BaseBean.class)
                    .setRequestParams("oldPassword", CommonUtils.getMD5Str(oldPassword))
                    .setRequestParams("newPassword", CommonUtils.getMD5Str(newPassword)));
        }
        return true;
    }
}
