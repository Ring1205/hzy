package com.zxycloud.zszw.fragment.mine;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.zxycloud.zszw.R;
import com.zxycloud.zszw.base.BaseMainFragment;
import com.zxycloud.zszw.fragment.common.SearchHistoryFragment;
import com.zxycloud.zszw.fragment.common.WebViewFragment;
import com.zxycloud.zszw.fragment.mine.other.AboutAppFragment;
import com.zxycloud.zszw.fragment.mine.other.ConsumerHotlineFragment;
import com.zxycloud.zszw.fragment.mine.other.KnowledgeListFragment;
import com.zxycloud.zszw.fragment.mine.other.QuestionListFragment;
import com.zxycloud.zszw.fragment.mine.profile.ProfileFragment;
import com.zxycloud.zszw.fragment.mine.profile.ReEmailAddsFragment;
import com.zxycloud.zszw.fragment.mine.profile.RePasswdFragment;
import com.zxycloud.zszw.listener.OnProjectObtainListener;
import com.zxycloud.zszw.model.HistoryTypeBean;
import com.zxycloud.startup.bean.PrivacyPolicyBean;
import com.zxycloud.startup.bean.ResultPrivacyPolicyBean;
import com.sarlmoclen.router.SRouter;
import com.sarlmoclen.router.SRouterRequest;
import com.sarlmoclen.router.forMonitor.LogoutActionName;
import com.zxycloud.common.base.BaseBean;
import com.zxycloud.common.utils.CommonUtils;
import com.zxycloud.common.utils.SPUtils;
import com.zxycloud.common.utils.netWork.ApiRequest;
import com.zxycloud.common.utils.netWork.NetBean;
import com.zxycloud.common.utils.netWork.NetUtils;
import com.zxycloud.common.utils.rxbus2.RxBusCode;
import com.zxycloud.common.utils.rxbus2.Subscribe;
import com.zxycloud.common.utils.rxbus2.ThreadMode;
import com.zxycloud.common.widget.AlertDialog;

import cn.jpush.android.api.JPushInterface;

public class MineFragment extends BaseMainFragment implements Toolbar.OnMenuItemClickListener, View.OnClickListener {
    private static final int REQ_MODIFY_FRAGMENT = 100;
    private TextView tvEmail, tvChosenProject;
    private TextView tvMineName, tvMineTelephone;
    private Toolbar mToolbar;

    public static MineFragment newInstance() {
        Bundle args = new Bundle();
        MineFragment fragment = new MineFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_tab_mine;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        mToolbar = findViewById(R.id.toolbar);
        mToolbar.setTitle(R.string.mine);
        mToolbar.inflateMenu(R.menu.quit);
        mToolbar.setOnMenuItemClickListener(this);

        tvMineName = findViewById(R.id.tv_mine_name);
        tvMineTelephone = findViewById(R.id.tv_mine_telephone);

        SPUtils spUtils = CommonUtils.getSPUtils(_mActivity);
        tvMineName.setText(spUtils.getString(SPUtils.USER_NAME));
        tvMineTelephone.setText(spUtils.getString(SPUtils.USER_PHONE, spUtils.getString(SPUtils.USER_EMAIL, "")));

        tvEmail = findViewById(R.id.tv_email);
        tvChosenProject = findViewById(R.id.tv_chosen_project);
        getProject(new OnProjectObtainListener() {
            @Override
            public void success(String projectId, String projectName) {
                tvChosenProject.setText(projectName);
            }
        });

        setOnClickListener(this, R.id.rl_mine_info, R.id.rl_change_password, R.id.rl_modify_mailbox, R.id.rl_common_problem, R.id.rl_safety_fire_knowledge, R.id.rl_consumer_hotline, R.id.rl_service_agreement, R.id.rl_switch_project, R.id.rl_about_us);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rl_mine_info:
                startFragment(ProfileFragment.newInstance());
                break;
            case R.id.rl_change_password:
                startFragment(RePasswdFragment.newInstance());
                break;
            case R.id.rl_modify_mailbox:
                startFragmentForResult(ReEmailAddsFragment.newInstance(tvEmail.getText().toString()), REQ_MODIFY_FRAGMENT);
                break;
            case R.id.rl_common_problem:
                startFragment(QuestionListFragment.newInstance());
                break;
            case R.id.rl_safety_fire_knowledge:
                startFragment(KnowledgeListFragment.newInstance());
                break;
            case R.id.rl_consumer_hotline:
                startFragment(ConsumerHotlineFragment.newInstance());
                break;
            case R.id.rl_service_agreement:
                NetUtils.getNewInstance(getContext()).request(new NetUtils.NetRequestCallBack<ResultPrivacyPolicyBean>() {
                    @Override
                    public void success(String action, ResultPrivacyPolicyBean bean, Object tag) {
                        if (bean.isSuccessful()) {
                            PrivacyPolicyBean policyBean = bean.getData();
                            startFragment(WebViewFragment.newInstance(R.string.start_up_string_privacy_policy, policyBean.getUrl()));
                        } else {
                            CommonUtils.toast(_mActivity, bean.getMessage());
                        }
                    }

                    @Override
                    public void error(String action, Throwable e, Object tag) {

                    }
                }, true, new ApiRequest<>(NetBean.actionGetPrivacyPolicy, ResultPrivacyPolicyBean.class));
                break;
            case R.id.rl_switch_project:
                startFragment(SearchHistoryFragment.getInstance(
                        HistoryTypeBean.TYPE_HISTORY_PROJECT_CHANGE,
                        CommonUtils.getSPUtils(getContext()).getString(SPUtils.USER_ID),
                        "", 0));
                break;
            case R.id.rl_about_us:
                startFragment(AboutAppFragment.getInstance());
                break;
        }
    }

    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.action_quit:
                final AlertDialog builder = new AlertDialog(getContext()).builder();
                builder.setTitle(R.string.confirm_exit).setMsg(R.string.user_exit)
//                        .setHint(R.string.logout_user, new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        builder.setGone().setTitle(R.string.confirm_cancellation)
//                                .setMsg(R.string.canellation_specification)
//                                .setNegativeButton(R.string.dialog_no, null)
//                                .setPositiveButton(R.string.dialog_yes, new View.OnClickListener() {
//                                    @Override
//                                    public void onClick(View v) {
//                                        netWork().setRequestListener(new NetRequestListener() {
//                                            @Override
//                                            public void success(String action, BaseBean baseBean, Object tag) {
//                                                if (baseBean.isSuccessful()) {
//                                                    SRouter.getInstance().sendMessage(_mActivity, SRouterRequest.creat().action(LogoutActionName.name));
//                                                    _mActivity.finish();
//                                                }
//                                            }
//                                        }, netWork().apiRequest(NetBean.actionLogout, BaseBean.class, ApiRequest.REQUEST_TYPE_GET));
//                                    }
//                                }).show();
//                    }
//                })
                        .setNegativeButton(R.string.dialog_no, null).setPositiveButton(R.string.dialog_yes, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!JPushInterface.isPushStopped(_mActivity))
                            JPushInterface.stopPush(_mActivity);
                        new NetUtils(getContext()).request(new NetUtils.NetRequestCallBack() {
                            @Override
                            public void success(String action, BaseBean baseBean, Object tag) {
                                NetUtils.getNewInstance(_mActivity).removeCookies();
                                CommonUtils.getSPUtils(_mActivity)
                                        .remove(SPUtils.USER_EMAIL
                                                , SPUtils.USER_ID
                                                , SPUtils.USER_NAME
                                                , SPUtils.USER_PHONE);
                            }

                            @Override
                            public void error(String action, Throwable e, Object tag) {
                                NetUtils.getNewInstance(_mActivity).removeCookies();
                                CommonUtils.getSPUtils(_mActivity)
                                        .remove(SPUtils.USER_EMAIL
                                                , SPUtils.USER_ID
                                                , SPUtils.USER_NAME
                                                , SPUtils.USER_PHONE);
                            }
                        }, false,new ApiRequest<>(NetBean.actionSignOut, BaseBean.class));
                        SRouter.getInstance().sendMessage(_mActivity, SRouterRequest.creat().action(LogoutActionName.name));
                        _mActivity.finish();
                        CommonUtils.toast(_mActivity, R.string.toast_logout);
                    }
                }).show();
                break;
        }
        return true;
    }

    @Override
    public void onFragmentResult(int requestCode, int resultCode, Bundle data) {
        super.onFragmentResult(requestCode, resultCode, data);
        if (requestCode == REQ_MODIFY_FRAGMENT && resultCode == RESULT_OK && data != null) {
            tvEmail.setText(R.string.modify_mailbox);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        CommonUtils.registerRxBus(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        CommonUtils.unRegisterRxBus(this);
    }

    @Subscribe(code = RxBusCode.RX_PROJECT_CHANGED, threadMode = ThreadMode.MAIN)
    public void onProjectChanged() {
        tvChosenProject.setText(CommonUtils.getSPUtils(_mActivity).getString(SPUtils.PROJECT_NAME, CommonUtils.string().getString(_mActivity, R.string.all_project)));
    }
}
