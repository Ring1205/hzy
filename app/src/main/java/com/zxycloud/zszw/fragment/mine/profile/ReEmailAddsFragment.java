package com.zxycloud.zszw.fragment.mine.profile;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.zxycloud.common.CommonApp;
import com.zxycloud.zszw.R;
import com.zxycloud.zszw.base.BaseBackFragment;
import com.zxycloud.common.base.BaseBean;
import com.zxycloud.common.utils.CommonUtils;
import com.zxycloud.common.utils.TimerUtils;
import com.zxycloud.common.utils.netWork.ApiRequest;
import com.zxycloud.common.utils.netWork.NetBean;
import com.zxycloud.common.utils.netWork.NetUtils;

public class ReEmailAddsFragment extends BaseBackFragment implements Toolbar.OnMenuItemClickListener, View.OnClickListener {
    private static String EMAIL_TITLE = "email_title";
    private Toolbar mToolbar;
    private TextInputEditText resetEmailEtEmail, resetEmailEtCaptcha, resetEmailEtVerification;
    private ImageView resetEmailImgCaptcha;
    private Button resetEmailBtnVerification;
    private NetUtils netUtils;
    private TimerUtils timerUtils;
    private long timerFinish;
    private TextInputLayout resetEmailTilEmail, resetEmailTilCaptcha, resetEmailTilVerification;

    public static ReEmailAddsFragment newInstance(String title) {
        Bundle args = new Bundle();
        ReEmailAddsFragment fragment = new ReEmailAddsFragment();
        args.putString(EMAIL_TITLE, title);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.mine_email;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        setOnClickListener(this, R.id.reset_email_btn_verification, R.id.reset_email_img_captcha);

        netUtils = NetUtils.getNewInstance(_mActivity);

        // 计时器
        timerUtils = new TimerUtils(60 * 1000, 1000, onBaseTimerCallBack);

        if (getArguments() != null && !getArguments().getString(EMAIL_TITLE).isEmpty())
            setToolbarTitle(R.string.modify_mailbox).initToolbarNav().setToolbarMenu(R.menu.save, this);
        else
            setToolbarTitle(R.string.add_email_adds).initToolbarNav().setToolbarMenu(R.menu.save, this);

        resetEmailBtnVerification = findViewById(R.id.reset_email_btn_verification);
        resetEmailTilEmail = findViewById(R.id.reset_email_til_email);
        resetEmailEtEmail = findViewById(R.id.reset_email_et_email);
        resetEmailTilCaptcha = findViewById(R.id.reset_email_til_captcha);
        resetEmailImgCaptcha = findViewById(R.id.reset_email_img_captcha);
        resetEmailEtCaptcha = findViewById(R.id.reset_email_et_captcha);

        resetEmailEtEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                resetEmailTilEmail.setError("");
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (CommonUtils.judge().isMailLegal(String.valueOf(s)) && CommonUtils.string().getString(resetEmailEtCaptcha).length() == 4) {
                    resetEmailBtnVerification.setBackgroundResource(R.drawable.common_bg_corner_main_16);
                } else {
                    resetEmailBtnVerification.setBackgroundResource(R.drawable.common_bg_corner_enable_16);
                }
            }
        });

        resetEmailEtCaptcha.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                resetEmailTilCaptcha.setError("");
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 4 && CommonUtils.judge().isMailLegal(CommonUtils.string().getString(resetEmailEtEmail))) {
                    resetEmailBtnVerification.setBackgroundResource(R.drawable.common_bg_corner_main_16);
                } else {
                    resetEmailBtnVerification.setBackgroundResource(R.drawable.common_bg_corner_enable_16);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        resetEmailTilVerification = findViewById(R.id.reset_email_til_verification);
        resetEmailEtVerification = findViewById(R.id.reset_email_et_verification);
        resetEmailEtVerification.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                resetEmailTilVerification.setError("");
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        getCaptcha();
    }

    /**
     * 获取图形验证码
     */
    private void getCaptcha() {
        netUtils.request(netRequestCallBack, false, new ApiRequest<>(NetBean.actionGetUpdateEmailCaptcha, ApiRequest.SPECIAL_GET_BITMAP)
                .setRequestParams("width", CommonUtils.measureScreen().dp2px(_mActivity, 90))
                .setRequestParams("fontSize", CommonUtils.measureScreen().dp2px(_mActivity, 25))
                .setRequestParams("height", CommonUtils.measureScreen().dp2px(_mActivity, 40)));
    }

    /**
     * 获取动态验证码
     */
    private void getVerification() {
        String email = CommonUtils.string().getString(resetEmailEtEmail);
        String captcha = CommonUtils.string().getString(resetEmailEtCaptcha);
        if (TextUtils.isEmpty(captcha)) {
            resetEmailTilCaptcha.setError(CommonUtils.string().getString(_mActivity, R.string.common_string_cannot_be_null));
            return;
        }
        if (CommonUtils.judge().isMailLegal(email)) {
            netUtils.request(netRequestCallBack, false, new ApiRequest<>(NetBean.actionGetUpdateEmailVerification, BaseBean.class)
                    .setRequestParams("account", email)
                    .setRequestParams("captcha", captcha));
        } else {
            resetEmailTilEmail.setError(_mActivity.getResources().getText(R.string.common_string_format_error));
        }
    }

    /**
     * 更新邮箱
     */
    private void update() {
        String verification = CommonUtils.string().getString(resetEmailEtVerification);
        String email = CommonUtils.string().getString(resetEmailEtEmail);

        if (TextUtils.isEmpty(verification)) {
            resetEmailTilVerification.setError(CommonUtils.string().getString(_mActivity, R.string.common_string_cannot_be_null));
            return;
        } else if (TextUtils.isEmpty(email)){
            CommonUtils.toast(getContext(), R.string.toast_email_null);
            return;
        }

        if (CommonUtils.judge().isMailLegal(email)) {
            netUtils.request(netRequestCallBack, false, new ApiRequest<>(NetBean.actionGetUpdateEmail, BaseBean.class)
                    .setRequestParams("account", CommonUtils.string().getString(resetEmailEtEmail))
                    .setRequestParams("verification", verification));
        } else {
            resetEmailTilEmail.setError(_mActivity.getResources().getText(R.string.common_string_format_error));
        }
    }

    private Toolbar.OnMenuItemClickListener menuItemClickListener = new Toolbar.OnMenuItemClickListener() {
        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            update();
            return true;
        }
    };

    private NetUtils.NetRequestCallBack netRequestCallBack = new NetUtils.NetRequestCallBack() {
        @Override
        public void success(String action, BaseBean baseBean, Object tag) {
            if (baseBean.isSuccessful()) {
                switch (action) {
                    case NetBean.actionGetUpdateEmailVerification:
                        timerUtils.startTimer(null);
                        break;

                    case NetBean.actionGetUpdateEmail:
                        finish();
                        CommonUtils.toast(_mActivity, baseBean.getMessage());
                        break;
                }
            } else {
                CommonUtils.toast(_mActivity, baseBean.getMessage());
            }
        }

        @Override
        public void error(String action, Throwable e, Object tag) {
            CommonUtils.log().e(e);
        }

        @Override
        public void success(String action, Bitmap bitmap, Object tag) {
            super.success(action, bitmap, tag);
            if (action.equals(NetBean.actionGetUpdateEmailCaptcha)) {
                CommonUtils.glide().loadImageView(_mActivity, bitmap, resetEmailImgCaptcha);
            }
        }
    };

    private TimerUtils.OnBaseTimerCallBack onBaseTimerCallBack = new TimerUtils.OnBaseTimerCallBack() {

        @Override
        public void onTick(Object tag, long millisUntilFinished) {
            timerFinish = millisUntilFinished;
            resetEmailBtnVerification.setText(String.valueOf((int) (millisUntilFinished / 1000)));
            resetEmailBtnVerification.setBackgroundResource(R.drawable.common_bg_corner_enable_16);
        }

        @Override
        public void onFinish(Object tag) {
            timerFinish = 0;
            resetEmailBtnVerification.setText(com.zxycloud.startup.R.string.start_up_string_get_verification);
            resetEmailBtnVerification.setBackgroundResource(R.drawable.common_bg_corner_main_16);
        }
    };

    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.action_save:
                Bundle bundle = new Bundle();
                bundle.putString(ProfileFragment.EMAIL_ADDRESS, resetEmailEtEmail.getText().toString());
                setFragmentResult(RESULT_OK, bundle);
                update();
                break;
        }
        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.reset_email_btn_verification:
                if (timerFinish > 0) {
                    return;
                }
                getVerification();
                break;

            case R.id.reset_email_img_captcha:
                getCaptcha();
                break;
        }
    }
}
