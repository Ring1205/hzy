package com.zxycloud.startup.ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.zxycloud.startup.R;
import com.zxycloud.startup.bean.AccountHistoryBean;
import com.zxycloud.startup.bean.PrivacyPolicyBean;
import com.zxycloud.startup.bean.ResultPrivacyPolicyBean;
import com.zxycloud.startup.bean.ResultProjectListBean;
import com.zxycloud.startup.bean.ResultRandomCodeBean;
import com.zxycloud.startup.bean.ResultSignInBean;
import com.zxycloud.startup.bean.ResultSystemSettingBean;
import com.zxycloud.startup.bean.SignInBean;
import com.zxycloud.startup.ui.base.BaseActivity;
import com.zxycloud.startup.utils.LoginAnimUtils;
import com.sarlmoclen.router.SRouter;
import com.sarlmoclen.router.SRouterRequest;
import com.sarlmoclen.router.SRouterResponse;
import com.sarlmoclen.router.forMonitor.MainActionName;
import com.zxycloud.common.base.BaseBean;
import com.zxycloud.common.utils.CommonUtils;
import com.zxycloud.common.utils.PopWinDownUtil;
import com.zxycloud.common.utils.SPUtils;
import com.zxycloud.common.utils.StringFormatUtils;
import com.zxycloud.common.utils.SystemUtil;
import com.zxycloud.common.utils.TimerUtils;
import com.zxycloud.common.utils.db.DbUtils;
import com.zxycloud.common.utils.netWork.ApiRequest;
import com.zxycloud.common.utils.netWork.NetBean;
import com.zxycloud.common.utils.netWork.NetUtils;
import com.zxycloud.common.widget.AnimButton;
import com.zxycloud.common.widget.BswRecyclerView.BswRecyclerView;
import com.zxycloud.common.widget.BswRecyclerView.ConvertViewCallBack;
import com.zxycloud.common.widget.BswRecyclerView.RecyclerViewHolder;
import com.zxycloud.common.widget.MyCheckBox;

import java.net.ConnectException;
import java.util.ArrayList;
import java.util.List;

/**
 * 登录页
 *
 * @author leiming
 * @date 2019/3/7 11:56
 */
public class LoginActivity extends BaseActivity {
    private final int CAPTCHA_SIGN_UP = 2;
    private final int CAPTCHA_PASSWORD = 3;

    /**
     * 根据布局不同重置点击事件的标示位
     * CLICK_TAG_SIGN_IN                ：登录
     * CLICK_TAG_SIGN_UP                ：注册
     * CLICK_TAG_SETTING                ：登录设置
     * CLICK_TAG_IMPROVE_INFORMATION    ：完善信息
     * CLICK_TAG_FORGET_PASSWORD        ：忘记密码
     * CLICK_TAG_SET_PASSWORD           ：密码设置
     */
    private final int CLICK_TAG_SIGN_IN = 11;
    private final int CLICK_TAG_SIGN_UP = 12;
    private final int CLICK_TAG_SETTING = 13;
    private final int CLICK_TAG_IMPROVE_INFORMATION = 14;
    private final int CLICK_TAG_FORGET_PASSWORD = 15;
    private final int CLICK_TAG_SET_PASSWORD = 16;

    /**
     * 下发登录注册的区分type：
     * NOTICE_TYPE_SIGN_UP：注册
     * NOTICE_TYPE_SIGN_IN：登录
     */
    private final Integer NOTICE_TYPE_SIGN_UP = 1;
    @SuppressWarnings("FieldCanBeLocal")
    private final Integer NOTICE_TYPE_SIGN_IN = 2;

    /**
     * 用于展示登录模块各功能的布局
     * item1、item2   具体展示布局的控件，用于切换的动画
     * itemDivider    Circular Reveal会消除背景，使用itemDivider做分割
     */
    @SuppressWarnings("FieldCanBeLocal")
    private FrameLayout item1, item2, itemDivider, frontItem;
    /**
     * signInView               登录View
     * signUpView               注册View
     * forgetPasswordView       忘记密码View
     * improveInformationView   完善信息View
     * setPasswordView          设置密码View
     * settingView              登录设置View
     * noticeView               提示View（切换页面时，被遮挡页面的显示的提示文本）
     */
    private View signInView, signUpView, forgetPasswordView, improveInformationView, setPasswordView, settingView, noticeView;

    private ImageView startUpSignUpImgCaptcha, passwordImgCaptcha;

    /**
     * zoomInView      显示View
     * zoomOutView     被遮挡View
     */
    private View zoomInView, zoomOutView;
    /**
     * noticeView中用于显示登录/注册的文本控件
     */
    private TextView noticeTv;

    /**
     * noticeTv的点击次数，对2取余决定展示item1还是item2
     */
    private int count = 1;
    /**
     * 登录模块动画工具类
     */
    private LoginAnimUtils animUtils;
    private NetUtils netUtils;
    private TextView signUpVerificationBtn, passwordVerificationBtn;

    private TimerUtils timerUtils;

    private StringFormatUtils stringFormatUtils;
    private PopWinDownUtil popWinDownUtil;
    private BswRecyclerView<AccountHistoryBean> historyRv;
    private DbUtils dbUtils;
    private EditText startUpSignInEtAccount;
    private EditText startUpSignInEtPassword;
    /**
     * 记住密码的checkbox
     */
    private CheckBox startUpSignInRememberCheckBox;
    /**
     * 登录账号的布局，用于PopWinDownUtil设置宽度
     */
    private View startUpSignInAccountLl;
    private TextInputLayout startUpImproveInformationInputLayoutPassword;
    private TextInputLayout startUpImproveInformationInputLayoutAccount;
    private TextInputLayout startUpSetPasswordTextInputEnter;
    private TextInputLayout startUpSetAccountTextInputLayoutStyle;
    private boolean isNoticeInside = false;
    /**
     * improveInformationButton ：注册，信息完善按钮
     * setPasswordButton        ：忘记密码，设置密码按钮
     * signInButton             ：登录按钮
     * forgetPasswordButton     ：忘记密码，动态码验证按钮
     * signUpButton             ：注册，动态码验证按钮
     */
    private AnimButton improveInformationButton, setPasswordButton, signInButton, forgetPasswordButton, signUpButton, settingButton;

    private final int IMPROVE_RESET_PASSWORD = 1;
    private final int IMPROVE_SIGN_UP_INFORMATION = 0;

    private int improveInformationType = IMPROVE_SIGN_UP_INFORMATION;
    private SPUtils spUtils;
    private MyCheckBox ipHttps;
    private EditText startUpSettingEtPort;

    @SuppressLint("InflateParams")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // 设置状态栏白底黑字
        SystemUtil.StatusBarLightMode(this);

        dbUtils = CommonUtils.getDbUtils(this);
        netUtils = NetUtils.getNewInstance(this);
        stringFormatUtils = CommonUtils.string();

        item1 = findViewById(R.id.item1);
        item2 = findViewById(R.id.item2);
        itemDivider = findViewById(R.id.item_divider);

        animUtils = new LoginAnimUtils(this, animStateListener, itemDivider);

        signInView = LayoutInflater.from(this).inflate(R.layout.start_up_sign_in_layout, null);
        noticeView = LayoutInflater.from(this).inflate(R.layout.start_up_notice_tv_layout, null);

        noticeTv = noticeView.findViewById(R.id.start_up_notice_tv);
        noticeTv.setTag(NOTICE_TYPE_SIGN_UP);

        item1.addView(signInView);

        animUtils.startZoomIn(CLICK_TAG_SIGN_IN, item2, noticeView, 0);
        signInButton = signInView.findViewById(R.id.start_up_sign_in_btn);
        signInButton.setAnimationButtonListener(animationButtonClickListener);
        // 计时器
        timerUtils = new TimerUtils(60 * 1000, 1000, onBaseTimerCallBack);

        startUpSignInEtAccount = findViewById(R.id.start_up_sign_in_et_account);
        startUpSignInEtPassword = findViewById(R.id.start_up_sign_in_et_password);
        startUpSignInRememberCheckBox = findViewById(R.id.start_up_sign_in_remember_check_box);
        startUpSignInAccountLl = findViewById(R.id.start_up_sign_in_account_ll);

        getHistory(false);
    }

    /**
     * 获取历史登录账号
     *
     * @param showDialog 是否显示加载框
     */
    private void getHistory(final boolean showDialog) {
        final AccountHistoryBean firstHistoryBean = dbUtils.where(AccountHistoryBean.class).putParams("isFirst", true).getFirst();
        final List<AccountHistoryBean> historyBeans = dbUtils.where(AccountHistoryBean.class).putParams("isFirst", false).getAll();
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (null != firstHistoryBean) {
                    historyBeans.add(0, firstHistoryBean);
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (null != historyBeans && historyBeans.size() > 0) {
                            if (!showDialog) {
                                AccountHistoryBean accountHistoryBean = historyBeans.get(0);
                                startUpSignInEtAccount.setText(accountHistoryBean.getUserAccount());
                                startUpSignInEtAccount.setSelection(startUpSignInEtAccount.getText().length());
                                boolean isRememberPassword = accountHistoryBean.isRememberPassword();
                                startUpSignInRememberCheckBox.setSelected(isRememberPassword);
                                if (isRememberPassword) {
                                    startUpSignInEtPassword.setText(accountHistoryBean.getUserPassword());
                                    startUpSignInEtPassword.setSelection(startUpSignInEtPassword.getText().length());
                                }
                                return;
                            }
                            historyRv.setData(historyBeans);
                            popWinDownUtil.setWidth(startUpSignInAccountLl.getWidth());
                            popWinDownUtil.show();
                        } else {
                            if (showDialog)
                                CommonUtils.toast(LoginActivity.this, R.string.string_toast_history_account_none);
                        }
                    }
                });
            }
        });
    }

    @Override
    protected void onViewClick(View view) {
        if (null != popWinDownUtil && popWinDownUtil.isShowing()) {
            popWinDownUtil.hide();
            return;
        }
        int tag;
        final int id = view.getId();
        /*if (id == R.id.start_up_notice_tv) {                                                // 下方的注册/登录
            itemChange(view);
        } else*/
        if (id == R.id.start_up_sign_in_btn) {                                       // 启动登录验证
            signInButton = signInView.findViewById(R.id.start_up_sign_in_btn);
            signInButton.setAnimationButtonListener(animationButtonClickListener);
        } else if (id == R.id.start_up_sign_in_setting) {                                   // 首页登录设置时候的跳转入口
            noticeTv.setText(R.string.start_up_string_sign_in);
            noticeTv.setTag(NOTICE_TYPE_SIGN_IN);
            if (CommonUtils.isEmpty(settingView)) {
                settingView = LayoutInflater.from(this).inflate(R.layout.start_up_sign_in_setting_layout, null);
            }
            animUtils.startShow(CLICK_TAG_SETTING, view, count % 2 == 0 ? item2 : item1, settingView);
        } else if (id == R.id.start_up_sign_in_forget_password) {                           // 忘记密码时候的跳转入口
            noticeTv.setText(R.string.start_up_string_sign_in);
            noticeTv.setTag(NOTICE_TYPE_SIGN_IN);
            if (CommonUtils.isEmpty(forgetPasswordView)) {
                forgetPasswordView = LayoutInflater.from(this).inflate(R.layout.start_up_forget_password_layout, null);
            }
            animUtils.startShow(CLICK_TAG_FORGET_PASSWORD, view, count % 2 == 0 ? item2 : item1, forgetPasswordView);
        } else if (id == R.id.start_up_sign_up_img_captcha) {
            getCaptcha(CAPTCHA_SIGN_UP);
        } else if (id == R.id.start_up_sign_up_btn_verification) {
            if (Integer.parseInt(String.valueOf(view.getTag())) > 0) {
                return;
            }
            getVerification(CAPTCHA_SIGN_UP);
        } else if (id == R.id.start_up_forget_password_img_captcha) {
            getCaptcha(CAPTCHA_PASSWORD);
        } else if (id == R.id.start_up_forget_password_btn_verification) {
            if (Integer.parseInt(String.valueOf(view.getTag())) > 0) {
                return;
            }
            getVerification(CAPTCHA_PASSWORD);
        } else if (id == R.id.start_up_sign_in_history_account) {
            dbUtils.executeTransaction(new DbUtils.OnTransaction() {
                @Override
                public void execute(DbUtils dbUtils) {
                    getHistory(true);
                }
            });
        } else if (id == R.id.start_up_sign_in_remember_ll) {
            startUpSignInRememberCheckBox.setSelected(!startUpSignInRememberCheckBox.isSelected());
        } else if (id == R.id.start_up_sign_up_privacy_policy) {
            netUtils.request(new NetUtils.NetRequestCallBack<ResultPrivacyPolicyBean>() {
                @Override
                public void success(String action, ResultPrivacyPolicyBean bean, Object tag) {
                    if (bean.isSuccessful()) {
                        PrivacyPolicyBean policyBean = bean.getData();
                        Intent intent = new Intent(LoginActivity.this, UtilsActivity.class);
                        intent.putExtra("url", policyBean.getUrl());
                        startActivity(intent);
                    }
                }

                @Override
                public void error(String action, Throwable e, Object tag) {

                }
            }, true, new ApiRequest<>(NetBean.actionGetPrivacyPolicy, ResultPrivacyPolicyBean.class));
        }
    }

    /**
     * 获取图形验证码
     *
     * @param captchaTag 标签
     */
    private void getCaptcha(final int captchaTag) {
        netUtils.request(new NetUtils.NetRequestCallBack() {
            @Override
            public void success(String action, BaseBean baseBean, Object tag) {

            }

            @Override
            public void error(String action, Throwable e, Object tag) {
                if (e instanceof ConnectException) {
                    CommonUtils.toast(LoginActivity.this, R.string.error_toast_net);
                }
            }

            @Override
            public void success(String action, Bitmap bitmap, Object tag) {
                super.success(action, bitmap, tag);
                CommonUtils.glide().loadImageView(LoginActivity.this, bitmap, captchaTag == (CAPTCHA_SIGN_UP) ? startUpSignUpImgCaptcha : passwordImgCaptcha);
            }
        }, false, new ApiRequest<>(captchaTag == CAPTCHA_SIGN_UP ? NetBean.actionGetSignUpCaptcha : NetBean.actionGetPasswordCaptcha, ApiRequest.SPECIAL_GET_BITMAP)
                .setRequestType(ApiRequest.REQUEST_TYPE_GET)
                .setRequestParams("width", CommonUtils.measureScreen().dp2px(LoginActivity.this, 90))
                .setRequestParams("fontSize", CommonUtils.measureScreen().dp2px(LoginActivity.this, 25))
                .setRequestParams("height", CommonUtils.measureScreen().dp2px(LoginActivity.this, 40)));
    }

    /**
     * 获取短信验证码或手机验证码
     *
     * @param captchaTag 标签
     */
    private void getVerification(final int captchaTag) {
        final TextInputLayout tilPhone = findViewById(captchaTag == CAPTCHA_SIGN_UP ? R.id.start_up_sign_up_til_phone_or_email : R.id.start_up_forget_password_til_account);
        TextInputEditText titvPhone = findViewById(captchaTag == CAPTCHA_SIGN_UP ? R.id.start_up_sign_up_et_phone_or_email : R.id.start_up_forget_password_et_account);
        String phoneOrEmail = stringFormatUtils.getString(titvPhone);
        if (TextUtils.isEmpty(phoneOrEmail)) {
            tilPhone.setError(CommonUtils.string().getString(this, R.string.common_string_cannot_be_null));
            titvPhone.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    tilPhone.setError("");
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });
            ((AnimButton) findViewById(captchaTag == CAPTCHA_SIGN_UP ? R.id.start_up_sign_up_btn_next : R.id.start_up_forget_password_next)).reset();
            return;
        }

        int type = 0;
        if (CommonUtils.judge().isMailLegal(phoneOrEmail)) {
            type = 2;
        } else if (CommonUtils.judge().isChinaMobilePhoneLegal(phoneOrEmail)) {
            type = 1;
        }
        if (type == 0) {
            tilPhone.setError(CommonUtils.string().getString(this, R.string.common_string_format_error));
            titvPhone.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    tilPhone.setError("");
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });
            ((AnimButton) findViewById(captchaTag == CAPTCHA_SIGN_UP ? R.id.start_up_sign_up_btn_next : R.id.start_up_forget_password_next)).reset();
            return;
        }

        final TextInputLayout tilCaptcha = findViewById(captchaTag == CAPTCHA_SIGN_UP ? R.id.start_up_sign_up_til_captcha : R.id.start_up_forget_password_til_captcha);
        TextInputEditText titvCaptcha = findViewById(captchaTag == CAPTCHA_SIGN_UP ? R.id.start_up_sign_up_et_captcha : R.id.start_up_forget_password_et_captcha);
        String captcha = stringFormatUtils.getString(titvCaptcha);
        if (TextUtils.isEmpty(captcha)) {
            tilCaptcha.setError(CommonUtils.string().getString(this, R.string.common_string_cannot_be_null));
            titvCaptcha.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    tilCaptcha.setError("");
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });
            ((AnimButton) findViewById(captchaTag == CAPTCHA_SIGN_UP ? R.id.start_up_sign_up_btn_next : R.id.start_up_forget_password_next)).reset();
            return;
        }

        netUtils.request(new NetUtils.NetRequestCallBack() {
            @Override
            public void success(String action, BaseBean baseBean, Object tag) {
                if (baseBean.isSuccessful())
                    timerUtils.startTimer(captchaTag);
                else
                    CommonUtils.toast(LoginActivity.this, baseBean.getMessage());
            }

            @Override
            public void error(String action, Throwable e, Object tag) {
                if (e instanceof ConnectException) {
                    CommonUtils.toast(LoginActivity.this, R.string.error_toast_net);
                }
            }
        }, false, new ApiRequest<>(captchaTag == CAPTCHA_SIGN_UP ? NetBean.actionGetSignUpVerification : NetBean.actionGetPasswordVerification, BaseBean.class)
                .setRequestType(ApiRequest.REQUEST_TYPE_GET)
                .setRequestParams("captcha", captcha)
                .setRequestParams("account", phoneOrEmail)
                .setRequestParams("type", type));
    }

    /**
     * 启动动画
     */
    private void animStart(int tag) {
        count++;
        if (count % 2 == 0) {
            animUtils.startZoomIn(tag, item1, zoomInView);
            animUtils.startZoomOut(item2, zoomOutView);
        } else {
            animUtils.startZoomIn(tag, item2, zoomInView);
            animUtils.startZoomOut(item1, zoomOutView);
        }
    }

    /**
     * 不同页面切换逻辑实现
     */
    private void itemChange() {
        Integer type;
        try {
            type = (Integer) findViewById(R.id.start_up_notice_tv).getTag();
        } catch (NullPointerException e) {
            e.printStackTrace();
            return;
        }
        /*if (CommonUtils.isEmpty(signUpView)) {
            signUpView = LayoutInflater.from(this).inflate(R.layout.start_up_sign_up_layout, null);
        }
        if (CommonUtils.isEmpty(signInView)) {
            signUpView = LayoutInflater.from(this).inflate(R.layout.start_up_sign_in_layout, null);
        }
        zoomOutView = type.equals(NOTICE_TYPE_SIGN_UP) ? signUpView : signInView;
        noticeTv.setText(type.equals(NOTICE_TYPE_SIGN_UP) ? R.string.start_up_string_sign_in : R.string.start_up_string_forget_password);
        noticeTv.setTag(type.equals(NOTICE_TYPE_SIGN_UP) ? NOTICE_TYPE_SIGN_IN : NOTICE_TYPE_SIGN_UP);
        zoomInView = noticeView;
        animStart(type.equals(NOTICE_TYPE_SIGN_UP) ? CLICK_TAG_SIGN_UP : CLICK_TAG_SIGN_IN);
        signUpButton = signUpView.findViewById(R.id.start_up_sign_up_btn_next);
        signUpButton.setAnimationButtonListener(animationButtonClickListener);*/
        if (CommonUtils.isEmpty(forgetPasswordView)) {
            forgetPasswordView = LayoutInflater.from(this).inflate(R.layout.start_up_forget_password_layout, null);
        }
        if (CommonUtils.isEmpty(signInView)) {
            signInView = LayoutInflater.from(this).inflate(R.layout.start_up_sign_in_layout, null);
        }
        zoomOutView = type.equals(NOTICE_TYPE_SIGN_UP) ? forgetPasswordView : signInView;
        noticeTv.setText(type.equals(NOTICE_TYPE_SIGN_UP) ? R.string.start_up_string_sign_in : R.string.start_up_string_forget_password);
        noticeTv.setTag(type.equals(NOTICE_TYPE_SIGN_UP) ? NOTICE_TYPE_SIGN_IN : NOTICE_TYPE_SIGN_UP);
        zoomInView = noticeView;
        animStart(type.equals(NOTICE_TYPE_SIGN_UP) ? CLICK_TAG_FORGET_PASSWORD : CLICK_TAG_SIGN_IN);
        forgetPasswordButton = forgetPasswordView.findViewById(R.id.start_up_forget_password_next);
        forgetPasswordButton.setAnimationButtonListener(animationButtonClickListener);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int clickDistance = 200;
        // 根据顶层item的下边界距离判断点击范围
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (count % 2 == 0 && event.getRawY() < (item2.getBottom() + clickDistance) && event.getRawY() > item2.getBottom()) {
                    return isNoticeInside = true;
                } else if ((count % 2 == 1) && (event.getRawY() < (item1.getBottom() + clickDistance)) && (event.getRawY() > item2.getBottom())) {
                    return isNoticeInside = true;
                }
                break;

            case MotionEvent.ACTION_UP:
                if (count % 2 == 0 && event.getRawY() < (item2.getBottom() + clickDistance) && event.getRawY() > item2.getBottom() && isNoticeInside) {
                    itemChange();
                } else if (count % 2 == 1 && event.getRawY() < (item1.getBottom() + clickDistance) && event.getRawY() > item2.getBottom() && isNoticeInside) {
                    itemChange();
                }
                isNoticeInside = false;
                return true;
        }
        CommonUtils.log().i("touchX =" + event.getRawX()
                + " touchY " + event.getRawY()
                + " item1 " + item1.getBottom()
                + " item2 " + item2.getBottom()
                + " count " + count);
        return super.onTouchEvent(event);
    }

    private LoginAnimUtils.AnimStateListener animStateListener = new LoginAnimUtils.AnimStateListener() {

        @SuppressLint("InflateParams")
        @Override
        public void onAnimEnd(int tag) {
            switch (tag) {
                // 登录页加载完成
                case CLICK_TAG_SIGN_IN:
                    if (null == spUtils) {
                        spUtils = CommonUtils.getSPUtils(LoginActivity.this);
                    }
                    spUtils.remove(SPUtils.USER_ID, SPUtils.USER_NAME, SPUtils.USER_EMAIL, SPUtils.USER_PHONE, SPUtils.USER_PASSWORD);
                    setOnClickView(R.id.start_up_notice_tv, R.id.start_up_sign_in_setting, R.id.start_up_sign_in_forget_password);
                    setOnClickView(0L, R.id.start_up_sign_in_remember_ll, R.id.start_up_sign_in_history_account);
                    ((TextView) signInView.findViewById(R.id.start_up_sign_in_forget_password)).getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
                    if (null == popWinDownUtil) {
                        popWinDownUtil = new PopWinDownUtil(LayoutInflater.from(LoginActivity.this).inflate(R.layout.dialog_history_account_layout, null), startUpSignInEtAccount);
                        historyRv = popWinDownUtil.getView(R.id.history_rv);
                        historyRv.initAdapter(R.layout.item_history_account_layout, convertViewCallBack)
                                .setLayoutManager()
                                .setDecoration();
                    }
                    break;

                // 注册页加载完成
                case CLICK_TAG_SIGN_UP:
                    setOnClickView(R.id.start_up_notice_tv, R.id.start_up_sign_up_btn_verification, R.id.start_up_sign_up_img_captcha, R.id.start_up_sign_up_privacy_policy);
                    ((TextView) signUpView.findViewById(R.id.start_up_sign_up_privacy_policy)).getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
                    startUpSignUpImgCaptcha = findViewById(R.id.start_up_sign_up_img_captcha);
                    signUpVerificationBtn = findViewById(R.id.start_up_sign_up_btn_verification);
                    timerUtils.stop();
                    signUpVerificationBtn.setText(R.string.start_up_string_get_verification);
                    signUpVerificationBtn.setBackgroundResource(R.drawable.common_bg_corner_main_16);
                    signUpVerificationBtn.setTextColor(getResources().getColor(android.R.color.white));
                    signUpVerificationBtn.setTag(0);
                    getCaptcha(CAPTCHA_SIGN_UP);
                    break;

                // 登录设置页加载完成
                case CLICK_TAG_SETTING:
                    if (null == spUtils) {
                        spUtils = CommonUtils.getSPUtils(LoginActivity.this);
                    }
                    EditText startUpSettingEtUrl = findViewById(R.id.start_up_setting_et_url);
                    startUpSettingEtPort = findViewById(R.id.start_up_setting_et_port);
                    ipHttps = settingView.findViewById(R.id.ip_https);
                    ipHttps.setCheckBoxListener(new MyCheckBox.CheckBoxListener() {
                        @Override
                        public void onMoveStart() {

                        }

                        @Override
                        public void onMove() {

                        }

                        @Override
                        public void onMoveEnd(View view, boolean isChecked) {
                            startUpSettingEtPort.setText(isChecked ? "443" : "80");
                            startUpSettingEtPort.setSelection(startUpSettingEtPort.length());
                        }
                    });
                    String settingUrl = spUtils.getString(SPUtils.LOGIN_SETTING_URL);
                    String settingPort = spUtils.getString(SPUtils.LOGIN_SETTING_PORT);

                    boolean isHttps = spUtils.getBoolean(SPUtils.LOGIN_SETTING_HTTPS, true);
                    ipHttps.setChecked(isHttps);

                    if (TextUtils.isEmpty(settingPort) || TextUtils.isEmpty(settingUrl)) {
                        String completeUrl = NetBean.getHost();

                        String[] split = completeUrl
                                .replace("http:", "")
                                .replace("https:", "")
                                .replace("/", "")
                                .split(":");
                        String defaultPort = isHttps ? "443" : "80";

                        startUpSettingEtUrl.setText(split[0]);
                        if (split.length > 1) {
                            startUpSettingEtPort.setText(split[1]);
                        } else {
                            startUpSettingEtPort.setText(defaultPort);
                        }
                    } else {
                        startUpSettingEtUrl.setText(settingUrl);
                        startUpSettingEtPort.setText(settingPort);
                    }

                    startUpSettingEtUrl.setSelection(startUpSettingEtUrl.getText().length());
                    startUpSettingEtPort.setSelection(startUpSettingEtPort.getText().length());

                    setOnClickView(R.id.start_up_notice_tv);
                    settingButton = settingView.findViewById(R.id.start_up_setting_btn_complete);
                    settingButton.setAnimationButtonListener(animationButtonClickListener);
                    break;

                // 完善信息加载完成
                case CLICK_TAG_IMPROVE_INFORMATION:
                    setOnClickView(R.id.start_up_notice_tv);

                    clearEt(R.id.start_up_improve_information_et_account, R.id.start_up_improve_information_et_password, R.id.start_up_improve_information_et_re_enter_password);

                    improveInformationButton = improveInformationView.findViewById(R.id.start_up_improve_information_btn_complete);
                    improveInformationButton.setAnimationButtonListener(animationButtonClickListener);

                    if (null == startUpImproveInformationInputLayoutPassword) {
                        startUpImproveInformationInputLayoutPassword = findViewById(R.id.start_up_improve_information_input_layout_password);
                    }

                    if (null == startUpImproveInformationInputLayoutAccount) {
                        startUpImproveInformationInputLayoutAccount = findViewById(R.id.start_up_improve_information_input_layout_account);
                    }
                    break;

                // 忘记密码加载完成
                case CLICK_TAG_FORGET_PASSWORD:
                    setOnClickView(R.id.start_up_notice_tv, R.id.start_up_forget_password_btn_verification, R.id.start_up_forget_password_img_captcha);

                    clearEt(R.id.start_up_forget_password_et_account, R.id.start_up_forget_password_et_captcha, R.id.start_up_forget_password_et_verification);

                    forgetPasswordButton = forgetPasswordView.findViewById(R.id.start_up_forget_password_next);
                    forgetPasswordButton.setAnimationButtonListener(animationButtonClickListener);
                    passwordImgCaptcha = findViewById(R.id.start_up_forget_password_img_captcha);
                    passwordVerificationBtn = findViewById(R.id.start_up_forget_password_btn_verification);
                    timerUtils.stop();
                    passwordVerificationBtn.setText(R.string.start_up_string_get_verification);
                    passwordVerificationBtn.setBackgroundResource(R.drawable.common_bg_corner_main_16);
                    passwordVerificationBtn.setTextColor(getResources().getColor(android.R.color.white));
                    passwordVerificationBtn.setTag(0);
                    getCaptcha(CAPTCHA_PASSWORD);
                    break;

                // 完善信息加载完成
                case CLICK_TAG_SET_PASSWORD:
                    setOnClickView(R.id.start_up_notice_tv);

                    clearEt(R.id.start_up_set_account_et_enter, R.id.start_up_set_password_et_enter, R.id.start_up_set_password_et_re_enter);

                    setPasswordButton = setPasswordView.findViewById(R.id.start_up_set_password_complete);
                    setPasswordButton.setAnimationButtonListener(animationButtonClickListener);
                    if (null == startUpSetAccountTextInputLayoutStyle) {
                        startUpSetAccountTextInputLayoutStyle = findViewById(R.id.start_up_set_account_text_input_layout_style);
                    }

                    if (null == startUpSetPasswordTextInputEnter) {
                        startUpSetPasswordTextInputEnter = findViewById(R.id.start_up_set_password_text_input_enter);
                    }
                    break;
            }
        }

        @Override
        public void onAnimStart(int tag) {

        }
    };

    /**
     * 页面切换后清空原页面的文本
     *
     * @param idRes 待清空文本的输入框
     */
    private void clearEt(@IdRes int... idRes) {
        for (int id : idRes) {
            ((TextView) findViewById(id)).setText("");
        }
    }

    private AnimButton.OnAnimationButtonClickListener animationButtonClickListener = new AnimButton.OnAnimationButtonClickListener() {
        @Override
        public void onAnimationStart(final AnimButton btn) {
            btn.start();
            int id = btn.getId();
            if (id == R.id.start_up_sign_up_btn_next) {                                 // 注册页结束，进入完善信息页
                final TextInputLayout tilVerification = findViewById(R.id.start_up_sign_up_til_verification);
                TextInputEditText tietVerification = findViewById(R.id.start_up_sign_up_et_verification);
                String verification = stringFormatUtils.getString(tietVerification);
                if (TextUtils.isEmpty(verification)) {
                    tilVerification.setError(CommonUtils.string().getString(LoginActivity.this, R.string.common_string_cannot_be_null));
                    tietVerification.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                            tilVerification.setError("");
                        }

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {

                        }

                        @Override
                        public void afterTextChanged(Editable s) {

                        }
                    });
                    signUpButton.reset();
                    return;
                }
                netUtils.request(new NetUtils.NetRequestCallBack() {
                    @Override
                    public void success(String action, BaseBean baseBean, Object tag) {
                        if (baseBean.isSuccessful()) {
                            if (CommonUtils.isEmpty(improveInformationView)) {
                                improveInformationView = LayoutInflater.from(LoginActivity.this).inflate(R.layout.start_up_improve_information, null);
                            }
                            animUtils.startShow(CLICK_TAG_IMPROVE_INFORMATION, btn, count % 2 == 0 ? item2 : item1, improveInformationView);
                        } else {
                            CommonUtils.toast(getApplicationContext(), baseBean.getMessage());
                            signUpButton.reset();
                        }
                    }

                    @Override
                    public void error(String action, Throwable e, Object tag) {
                        signUpButton.reset();
                        if (e instanceof ConnectException) {
                            CommonUtils.toast(LoginActivity.this, R.string.error_toast_net);
                        }
                    }
                }, true, new ApiRequest<>(NetBean.actionGetSignUpRandomCode, ResultRandomCodeBean.class)
                        .setRequestParams("account", stringFormatUtils.getString((TextView) findViewById(R.id.start_up_sign_up_et_phone_or_email)))
                        .setRequestParams("verification", verification));
            } else if (id == R.id.start_up_forget_password_next) {
                final TextInputLayout tilAccount = findViewById(R.id.start_up_forget_password_til_account);
                TextInputEditText tietAccount = findViewById(R.id.start_up_forget_password_et_account);
                String account = stringFormatUtils.getString(tietAccount);
                if (TextUtils.isEmpty(account)) {
                    tilAccount.setError(CommonUtils.string().getString(LoginActivity.this, R.string.common_string_cannot_be_null));
                    tietAccount.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                            tilAccount.setError("");
                        }

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {

                        }

                        @Override
                        public void afterTextChanged(Editable s) {

                        }
                    });
                    forgetPasswordButton.reset();
                    return;
                }
                final TextInputLayout tilVerification = findViewById(R.id.start_up_forget_password_til_verification);
                TextInputEditText tietVerification = findViewById(R.id.start_up_forget_password_et_verification);
                String verification = stringFormatUtils.getString(tietVerification);
                if (TextUtils.isEmpty(verification)) {
                    tilVerification.setError(CommonUtils.string().getString(LoginActivity.this, R.string.common_string_cannot_be_null));
                    tietVerification.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                            tilVerification.setError("");
                        }

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {

                        }

                        @Override
                        public void afterTextChanged(Editable s) {

                        }
                    });
                    forgetPasswordButton.reset();
                    return;
                }
                netUtils.request(new NetUtils.NetRequestCallBack() {
                    @Override
                    public void success(String action, BaseBean baseBean, Object tag) {
                        if (baseBean.isSuccessful()) {
                            if (CommonUtils.isEmpty(setPasswordView)) {
                                setPasswordView = LayoutInflater.from(LoginActivity.this).inflate(R.layout.start_up_set_password_layout, null);
                            }
                            animUtils.startShow(CLICK_TAG_SET_PASSWORD, btn, count % 2 == 0 ? item2 : item1, setPasswordView);
                        } else {
                            CommonUtils.toast(getApplicationContext(), baseBean.getMessage());
                            forgetPasswordButton.reset();
                        }
                    }

                    @Override
                    public void error(String action, Throwable e, Object tag) {
                        forgetPasswordButton.reset();
                        if (e instanceof ConnectException) {
                            CommonUtils.toast(LoginActivity.this, R.string.error_toast_net);
                        }
                    }
                }, true, new ApiRequest<>(NetBean.actionGetSignUpRandomCode, ResultRandomCodeBean.class)
                        .setRequestParams("account", account)
                        .setRequestParams("verification", verification));
            } else if (id == R.id.start_up_sign_in_btn) {
                final String account = stringFormatUtils.getString((TextView) findViewById(R.id.start_up_sign_in_et_account));
                final String password = stringFormatUtils.getString((TextView) findViewById(R.id.start_up_sign_in_et_password));

                if (null == signInButton) {
                    signInButton = signInView.findViewById(R.id.start_up_sign_in_btn);
                }

                if (TextUtils.isEmpty(account) || TextUtils.isEmpty(password)) {
                    CommonUtils.toast(LoginActivity.this, R.string.string_toast_sign_in);
                    signInButton.reset();
                    return;
                }

                netUtils.request(new NetUtils.NetRequestCallBack<ResultSignInBean>() {
                    @Override
                    public void success(String action, ResultSignInBean resultSignInBean, Object tag) {
                        if (resultSignInBean.isSuccessful()) {
                            SignInBean signInBean = resultSignInBean.getData();
                            dbUtils.executeTransaction(new DbUtils.OnTransaction() {
                                @Override
                                public void execute(DbUtils dbUtils) {
                                    AccountHistoryBean accountHistoryBean = new AccountHistoryBean(account, password, startUpSignInRememberCheckBox.isSelected());
                                    dbUtils.update(accountHistoryBean);
                                }
                            });

                            if (null == spUtils) {
                                spUtils = CommonUtils.getSPUtils(LoginActivity.this);
                            }
                            spUtils.put(SPUtils.USER_ID, (signInBean.getUserId()))
                                    .put(SPUtils.USER_NAME, signInBean.getUserName())
                                    .put(SPUtils.USER_ACCOUNT, account)
                                    .put(SPUtils.USER_PASSWORD, password);
                            String userPhone = signInBean.getPhoneNumber();
                            String userEmail = signInBean.getEmail();
                            if (!TextUtils.isEmpty(userPhone)) {
                                spUtils.put(SPUtils.USER_PHONE, userPhone);
                            }
                            if (!TextUtils.isEmpty(userEmail)) {
                                spUtils.put(SPUtils.USER_EMAIL, userEmail);
                            }
                            if (signInBean.isChangePassword()) {
                                noticeTv.setText(R.string.start_up_string_sign_in);
                                noticeTv.setTag(NOTICE_TYPE_SIGN_IN);
                                if (CommonUtils.isEmpty(settingView)) {
                                    settingView = LayoutInflater.from(LoginActivity.this).inflate(R.layout.start_up_sign_in_setting_layout, null);
                                }
                                improveInformationType = IMPROVE_RESET_PASSWORD;
                                if (CommonUtils.isEmpty(improveInformationView)) {
                                    improveInformationView = LayoutInflater.from(LoginActivity.this).inflate(R.layout.start_up_improve_information, null);
                                }
                                animUtils.startShow(CLICK_TAG_IMPROVE_INFORMATION, btn, count % 2 == 0 ? item2 : item1, improveInformationView);
                            } else {
                                setProject(btn);
                            }
                        } else {
                            CommonUtils.toast(getApplicationContext(), resultSignInBean.getMessage());
                            signInButton.reset();
                        }
                    }

                    @Override
                    public void error(String action, Throwable e, Object tag) {
                        if (e instanceof ConnectException) {
                            CommonUtils.toast(LoginActivity.this, R.string.error_toast_net);
                        }
                        signInButton.reset();
                    }
                }, false, new ApiRequest<>(NetBean.actionSignIn, ResultSignInBean.class)
                        .setRequestType(ApiRequest.REQUEST_TYPE_POST)
                        .setRequestParams("account", account)
                        .setRequestParams("password", CommonUtils.getMD5Str(password)));
            } else if (id == R.id.start_up_improve_information_btn_complete) {          // 注册结束后，需返回登录页重新登录
                TextInputEditText etPassword = findViewById(R.id.start_up_improve_information_et_password);
                etPassword.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                        startUpImproveInformationInputLayoutPassword.setError("");
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                });
                String password = stringFormatUtils.getString(etPassword);
                String rePassword = stringFormatUtils.getString((TextView) findViewById(R.id.start_up_improve_information_et_re_enter_password));
//                TODO: 2019/8/26 第一次登录时修改密码，重新填写用户名，根据新需求，已经将这部分隐藏，后续有需求重新放开即可
//                final String nickName = stringFormatUtils.getString((TextView) findViewById(R.id.start_up_improve_information_et_account));
                if (/*TextUtils.isEmpty(nickName) || */TextUtils.isEmpty(password)) {
                    startUpImproveInformationInputLayoutPassword.setError(CommonUtils.string().getString(LoginActivity.this, R.string.data_incomplete));
                    improveInformationButton.reset();
                    return;
                }

                String account = spUtils.getString(SPUtils.USER_ACCOUNT);
                if (null != account && password.contains(account)) {
                    improveInformationButton.reset();
                    startUpImproveInformationInputLayoutPassword.setError(CommonUtils.string().getString(LoginActivity.this, R.string.string_toast_password_contains_account));
                    return;
                }

                if (TextUtils.isEmpty(password) || password.length() < 8 || password.length() > 15 || !CommonUtils.judge().isPassword(password)) {
                    improveInformationButton.reset();
                    startUpImproveInformationInputLayoutPassword.setError(CommonUtils.string().getString(LoginActivity.this, R.string.hint_passwd_ps));
                    return;
                }

                if (password.equals(rePassword)) {
                    switch (improveInformationType) {
                        case IMPROVE_SIGN_UP_INFORMATION:
                            netUtils.request(new NetUtils.NetRequestCallBack() {
                                @Override
                                public void success(String action, BaseBean baseBean, Object tag) {
                                    improveInformationType = IMPROVE_SIGN_UP_INFORMATION;
                                    if (baseBean.isSuccessful()) {
                                        noticeTv.setText(R.string.start_up_string_forget_password);
                                        noticeTv.setTag(NOTICE_TYPE_SIGN_UP);
                                        animUtils.startShow(CLICK_TAG_SIGN_IN, btn, count % 2 == 0 ? item2 : item1, signInView);
                                    } else {
                                        improveInformationButton.reset();
                                        CommonUtils.toast(getApplicationContext(), baseBean.getMessage());
                                    }
                                }

                                @Override
                                public void error(String action, Throwable e, Object tag) {
                                    if (e instanceof ConnectException) {
                                        CommonUtils.toast(LoginActivity.this, R.string.error_toast_net);
                                    }
                                    improveInformationButton.reset();
                                }
                            }, true, new ApiRequest<>(NetBean.actionSignUp, BaseBean.class));
                            break;

                        case IMPROVE_RESET_PASSWORD:
                            netUtils.request(new NetUtils.NetRequestCallBack() {
                                @Override
                                public void success(String action, BaseBean baseBean, Object tag) {
                                    improveInformationType = IMPROVE_SIGN_UP_INFORMATION;
                                    if (baseBean.isSuccessful()) {
//                                        spUtils.put(SPUtils.USER_NAME, nickName);
                                        setProject(btn);
                                    } else {
                                        improveInformationButton.reset();
                                        CommonUtils.toast(getApplicationContext(), baseBean.getMessage());
                                    }
                                }

                                @Override
                                public void error(String action, Throwable e, Object tag) {
                                    if (e instanceof ConnectException) {
                                        CommonUtils.toast(LoginActivity.this, R.string.error_toast_net);
                                    }
                                    improveInformationButton.reset();
                                }
                            }, true, new ApiRequest<>(NetBean.actionUpdateUserPasswordAndNickName, BaseBean.class)
                                    .setRequestType(ApiRequest.REQUEST_TYPE_POST)
//                                    .setRequestParams("nickName", nickName)
                                    .setRequestParams("password", CommonUtils.getMD5Str(password)));
                            break;
                    }
                } else {
                    btn.reset();
                    CommonUtils.toast(getApplicationContext(), R.string.string_toast_password_not_match);
                }
            } else if (id == R.id.start_up_setting_btn_complete) {                      // 登录设置结束后，返回登录页
                final TextInputLayout startUpSettingTilUrl = findViewById(R.id.start_up_setting_til_url);
                TextInputEditText startUpSettingEtUrl = findViewById(R.id.start_up_setting_et_url);
                startUpSettingEtUrl.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                        startUpSettingTilUrl.setError("");
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                });
                String regionUrl = stringFormatUtils.getString(startUpSettingEtUrl)
                        .replace("https://", "")    // 去掉用户手动填写的Http/https，避免与选择框冲突
                        .replace("http://", "");

                if (TextUtils.isEmpty(regionUrl)) {
                    btn.reset();
                    startUpSettingTilUrl.setError(CommonUtils.string().getString(LoginActivity.this, R.string.common_string_cannot_be_null));
                    return;
                }

                if (!CommonUtils.judge().isUrlLegal(regionUrl)) {
                    btn.reset();
                    startUpSettingTilUrl.setError(CommonUtils.string().getString(LoginActivity.this, R.string.common_string_format_error));
                    return;
                }

                final String port = stringFormatUtils.getString((TextView) findViewById(R.id.start_up_setting_et_port));

                int urlSubPosition = regionUrl.indexOf(":");          // 若用户在地址部分手动输入端口号也要去掉
                if (urlSubPosition > 0) {
                    regionUrl = regionUrl.substring(0, urlSubPosition);
                }
                if (!TextUtils.isEmpty(regionUrl)) {
                    final boolean isHttps = ipHttps.isChecked();

                    final String currentPort = TextUtils.isEmpty(port) ? isHttps ? "443" : "80" : port;

                    final String url = (isHttps ? "https://" : "http://").concat(regionUrl).concat(":").concat(currentPort);

                    final String finalRegionUrl = regionUrl;
                    netUtils.request(new NetUtils.NetRequestCallBack<ResultSystemSettingBean>() {
                        @Override
                        public void success(String action, ResultSystemSettingBean bean, Object tag) {
                            CommonUtils.toast(LoginActivity.this, R.string.success_operation);

                            if (null == spUtils) {
                                spUtils = CommonUtils.getSPUtils(LoginActivity.this);
                            }
                            spUtils.put(SPUtils.LOGIN_COMPLETE_URL, url)
                                    .put(SPUtils.LOGIN_SETTING_PORT, currentPort)
                                    .put(SPUtils.LOGIN_SETTING_URL, finalRegionUrl)
                                    .put(SPUtils.LOGIN_SETTING_HTTPS, isHttps);

                            NetBean.save(LoginActivity.this, bean.getApp());

                            noticeTv.setText(R.string.start_up_string_forget_password);
                            noticeTv.setTag(NOTICE_TYPE_SIGN_UP);
                            animUtils.startShow(CLICK_TAG_SIGN_IN, btn, count % 2 == 0 ? item2 : item1, signInView);
                            settingButton.reset();
                        }

                        @Override
                        public void error(String action, Throwable e, Object tag) {
                            startUpSettingTilUrl.setError(CommonUtils.string().getString(LoginActivity.this, R.string.common_string_url_error));
                            settingButton.reset();
                        }
                    }, true, new ApiRequest<>(url, ResultSystemSettingBean.class)
                            .setApiType(ApiRequest.API_TYPE_LOGIN_SETTING));
                } else {
                    btn.reset();
                    CommonUtils.toast(getApplicationContext(), R.string.data_incomplete);
                }
            } else if (id == R.id.start_up_set_password_complete) {                     // 密码修改结束后，返回登录页

                TextInputEditText startUpSetPasswordEtEnter = findViewById(R.id.start_up_set_password_et_enter);
                TextInputEditText startUpSetAccountEtEnter = findViewById(R.id.start_up_set_account_et_enter);

                startUpSetPasswordEtEnter.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                        startUpSetPasswordTextInputEnter.setError("");
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                });
                startUpSetAccountEtEnter.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                        startUpSetAccountTextInputLayoutStyle.setError("");
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                });

                String password = stringFormatUtils.getString(startUpSetPasswordEtEnter);
                String rePassword = stringFormatUtils.getString((TextView) findViewById(R.id.start_up_set_password_et_re_enter));

                String account = stringFormatUtils.getString(startUpSetAccountEtEnter);

                if (TextUtils.isEmpty(account)) {
                    setPasswordButton.reset();
                    startUpSetAccountTextInputLayoutStyle.setError(CommonUtils.string().getString(LoginActivity.this, R.string.common_string_cannot_be_null));
                    return;
                }

                if (TextUtils.isEmpty(password) || password.length() < 8 || password.length() > 15 || !CommonUtils.judge().isPassword(password)) {
                    setPasswordButton.reset();
                    startUpSetPasswordTextInputEnter.setError(CommonUtils.string().getString(LoginActivity.this, R.string.hint_passwd_ps));
                    return;
                }

                if (password.contains(account)) {
                    setPasswordButton.reset();
                    startUpSetPasswordTextInputEnter.setError(CommonUtils.string().getString(LoginActivity.this, R.string.string_toast_password_contains_account));
                    return;
                }

                if (password.equals(rePassword)) {
                    netUtils.request(new NetUtils.NetRequestCallBack() {
                        @Override
                        public void success(String action, BaseBean baseBean, Object tag) {
                            if (baseBean.isSuccessful()) {
                                noticeTv.setText(R.string.start_up_string_forget_password);
                                noticeTv.setTag(NOTICE_TYPE_SIGN_UP);
                                animUtils.startShow(CLICK_TAG_SIGN_IN, btn, count % 2 == 0 ? item2 : item1, signInView);
                            } else {
                                CommonUtils.toast(getApplicationContext(), baseBean.getMessage());
                                setPasswordButton.reset();
                            }
                        }

                        @Override
                        public void error(String action, Throwable e, Object tag) {
                            if (e instanceof ConnectException) {
                                CommonUtils.toast(LoginActivity.this, R.string.error_toast_net);
                            }
                            setPasswordButton.reset();
                        }
                    }, true, new ApiRequest<>(NetBean.actionResetPasswordByPhone, BaseBean.class)
                            .setRequestParams("password", CommonUtils.getMD5Str(password))
                            .setRequestParams("userAccount", account));
                } else {
                    btn.reset();
                    startUpSetPasswordTextInputEnter.setError(CommonUtils.string().getString(LoginActivity.this, R.string.string_toast_password_not_match));
                }
            }
        }

        @Override
        public void onAnimationFinish(final AnimButton btn) {

        }

        @Override
        public void onAnimationCancel(AnimButton btn) {

        }

        private void setProject(final View btn) {
            netUtils.request(new NetUtils.NetRequestCallBack<ResultProjectListBean>() {
                @Override
                public void success(String action, ResultProjectListBean baseBean, Object tag) {
                    if (baseBean.isSuccessful() && baseBean.getData() != null && !baseBean.getData().isEmpty())
                        CommonUtils.getSPUtils(LoginActivity.this)
                                .put(SPUtils.PROJECT_ID, baseBean.getData().get(0).getProjectId())
                                .put(SPUtils.PROJECT_NAME, baseBean.getData().get(0).getProjectName());
                    toMain(btn);
                }

                @Override
                public void error(String action, Throwable e, Object tag) {
                    toMain(btn);
                }
            }, false, new ApiRequest<>(NetBean.actionGetProjectList, ResultProjectListBean.class)
                    .setRequestType(ApiRequest.REQUEST_TYPE_POST)
                    .setRequestParams("pageSize", 100)
                    .setRequestParams("pageIndex", 1));

        }
    };

    private void toMain(View btn) {
        int xc = (btn.getLeft() + btn.getRight()) / 2;
        int yc = (btn.getTop() + btn.getBottom()) / 2;
        SRouterResponse mSRouterResponse = SRouter.getInstance().sendMessage(
                LoginActivity.this, SRouterRequest.creat()
                        .action(MainActionName.name)
                        .data("fromX", xc)
                        .data("fromY", yc));
        CommonUtils.log().i(mSRouterResponse.getResult());
        finish();
    }

    private TimerUtils.OnBaseTimerCallBack onBaseTimerCallBack = new TimerUtils.OnBaseTimerCallBack() {

        @Override
        public void onTick(Object tag, long millisUntilFinished) {
            int timeToFinish = (int) millisUntilFinished / 1000;
            if (tag.equals(CAPTCHA_SIGN_UP)) {
                signUpVerificationBtn.setText(String.valueOf(timeToFinish));
                signUpVerificationBtn.setBackgroundResource(R.drawable.common_bg_corner_enable_16);
                signUpVerificationBtn.setTextColor(getResources().getColor(R.color.common_color_text));
                signUpVerificationBtn.setTag(timeToFinish);
            } else {
                passwordVerificationBtn.setText(String.valueOf(timeToFinish));
                passwordVerificationBtn.setBackgroundResource(R.drawable.common_bg_corner_enable_16);
                passwordVerificationBtn.setTextColor(getResources().getColor(R.color.common_color_text));
                passwordVerificationBtn.setTag(timeToFinish);
            }
        }

        @Override
        public void onFinish(Object tag) {
            if (tag.equals(CAPTCHA_SIGN_UP)) {
                signUpVerificationBtn.setText(R.string.start_up_string_get_verification);
                signUpVerificationBtn.setBackgroundResource(R.drawable.common_bg_corner_main_16);
                signUpVerificationBtn.setTextColor(getResources().getColor(android.R.color.white));
                signUpVerificationBtn.setTag(0);
            } else {
                passwordVerificationBtn.setText(R.string.start_up_string_get_verification);
                passwordVerificationBtn.setBackgroundResource(R.drawable.common_bg_corner_main_16);
                passwordVerificationBtn.setTextColor(getResources().getColor(android.R.color.white));
                passwordVerificationBtn.setTag(0);
            }
        }
    };

    private ConvertViewCallBack<AccountHistoryBean> convertViewCallBack = new ConvertViewCallBack<AccountHistoryBean>() {
        @Override
        public void convert(RecyclerViewHolder holder, final AccountHistoryBean accountHistoryBean, final int position, int layoutTag) {
            if (accountHistoryBean.isFirst()) {
                holder.setVisibility(R.id.history_account_left, View.GONE)
                        .setImageRes(R.id.history_account_right, R.mipmap.ic_top_revoke)
                        .setOnClickListener(R.id.history_account_right, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                accountHistoryBean.setFirst(false);
                                dbUtils.executeTransaction(new DbUtils.OnTransaction() {
                                    @Override
                                    public void execute(DbUtils dbUtils) {
                                        dbUtils.update(accountHistoryBean);
                                        getHistory(true);
                                    }
                                });
                            }
                        });
            } else {
                holder.setVisibility(R.id.history_account_left, View.VISIBLE)
                        .setImageRes(R.id.history_account_right, R.mipmap.ic_delete_text)
                        .setOnClickListener(R.id.history_account_right, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dbUtils.executeTransaction(new DbUtils.OnTransaction() {
                                    @Override
                                    public void execute(DbUtils dbUtils) {
                                        CommonUtils.log().i(position + " ****** " + accountHistoryBean.getUserAccount());
                                        dbUtils.delete(accountHistoryBean);
                                    }
                                });
                                historyRv.removeItem(position);
                            }
                        })
                        .setOnClickListener(R.id.history_account_left, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                final AccountHistoryBean historyBean = historyRv.getItem(0);
                                if (historyBean.isFirst()) {
                                    historyBean.setFirst(false);
                                } else {
                                    accountHistoryBean.setFirst(true);
                                }

                                dbUtils.executeTransaction(new DbUtils.OnTransaction() {
                                    @Override
                                    public void execute(DbUtils dbUtils) {
                                        List<AccountHistoryBean> historyBeans = new ArrayList<>();
                                        historyBeans.add(historyBean);
                                        historyBeans.add(accountHistoryBean);
                                        dbUtils.update(historyBeans);
                                        getHistory(true);
                                    }
                                });
                            }
                        });
            }
            holder.setText(R.id.history_account, accountHistoryBean.getUserAccount())
                    .setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            startUpSignInEtAccount.setText(accountHistoryBean.getUserAccount());
                            startUpSignInEtAccount.setSelection(startUpSignInEtAccount.getText().length());
                            boolean isRememberPassword = accountHistoryBean.isRememberPassword();
                            startUpSignInRememberCheckBox.setSelected(isRememberPassword);
                            if (isRememberPassword) {
                                startUpSignInEtPassword.setText(accountHistoryBean.getUserPassword());
                                startUpSignInEtPassword.setSelection(startUpSignInEtPassword.getText().length());
                            }
                            popWinDownUtil.hide();
                        }
                    });

        }

        @Override
        public void loadingFinished() {

        }
    };
}
