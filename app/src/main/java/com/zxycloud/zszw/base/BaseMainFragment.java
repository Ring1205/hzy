package com.zxycloud.zszw.base;

import android.text.TextUtils;
import android.widget.Toast;

import com.zxycloud.zszw.MainFragment;
import com.zxycloud.zszw.R;
import com.zxycloud.zszw.listener.OnProjectObtainListener;
import com.zxycloud.zszw.model.ResultProjectListBean;
import com.zxycloud.common.base.fragment.ISupportFragment;
import com.zxycloud.common.base.fragment.SupportFragment;
import com.zxycloud.common.utils.CommonUtils;
import com.zxycloud.common.utils.SPUtils;
import com.zxycloud.common.utils.netWork.ApiRequest;
import com.zxycloud.common.utils.netWork.NetBean;
import com.zxycloud.common.utils.netWork.NetUtils;

/**
 * 懒加载
 * Created by YoKeyword on 16/6/5.
 */
public abstract class BaseMainFragment extends MyBaseFragment {
    // 再点一次退出程序时间设置
    private static final long WAIT_TIME = 2000L;
    private long TOUCH_TIME = 0;

    /**
     * 处理回退事件
     *
     * @return
     */
    @Override
    public boolean onBackPressedSupport() {
        if (System.currentTimeMillis() - TOUCH_TIME < WAIT_TIME) {
            _mActivity.finish();
        } else {
            TOUCH_TIME = System.currentTimeMillis();
            Toast.makeText(_mActivity, R.string.press_again_exit, Toast.LENGTH_SHORT).show();
        }
        return true;
    }

    /**
     * 主页面跳转到次级页面
     *
     * @param targetFragment
     */
    public void startFragment(SupportFragment targetFragment) {
        ((MainFragment) getParentFragment()).start(targetFragment);
    }

    /**
     * 主页面跳转到次级页面并返回值
     *
     * @param toFragment
     * @param requestCode
     */
    public void startFragmentForResult(ISupportFragment toFragment, int requestCode) {
        ((MainFragment) getParentFragment()).startForResult(toFragment, requestCode);
    }

    public void getProject(final OnProjectObtainListener listener) {
        String pId = CommonUtils.getSPUtils(_mActivity).getString(SPUtils.PROJECT_ID);
        String pName = CommonUtils.getSPUtils(_mActivity).getString(SPUtils.PROJECT_NAME);
        if (TextUtils.isEmpty(pId) && TextUtils.isEmpty(pName))
            new NetUtils(_mActivity).request(new NetUtils.NetRequestCallBack<ResultProjectListBean>() {
                @Override
                public void success(String action, ResultProjectListBean baseBean, Object tag) {
                    if (baseBean.isSuccessful() && baseBean.getData() != null && !baseBean.getData().isEmpty()) {
                        CommonUtils.getSPUtils(getContext())
                                .put(SPUtils.PROJECT_ID, baseBean.getData().get(0).getProjectId())
                                .put(SPUtils.PROJECT_NAME, baseBean.getData().get(0).getProjectName());
                        listener.success(baseBean.getData().get(0).getProjectId(), baseBean.getData().get(0).getProjectName());
                    }
                }

                @Override
                public void error(String action, Throwable e, Object tag) {}
            }, false, netWork().apiRequest(NetBean.actionGetProjectList, ResultProjectListBean.class, ApiRequest.REQUEST_TYPE_POST)
                    .setRequestParams("pageSize", 10)
                    .setRequestParams("pageIndex", 1));
        else
            listener.success(pId, pName);
    }
}
