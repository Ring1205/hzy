package com.zxycloud.zszw.base;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;

import com.zxycloud.zszw.R;
import com.zxycloud.zszw.fragment.common.SearchHistoryFragment;
import com.zxycloud.zszw.listener.OnNavigationListener;
import com.zxycloud.zszw.listener.OnProjectObtainListener;
import com.zxycloud.zszw.model.HistoryTypeBean;
import com.zxycloud.common.utils.CommonUtils;
import com.zxycloud.common.utils.SPUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by YoKeyword on 16/2/7.
 */
public abstract class BaseBackFragment extends MyBaseFragment {
    protected Toolbar mToolbar;
    private OnNavigationListener navigationListener;
    private static List<String> closeFragmentTag = new ArrayList<>();
    private static List<BaseBackFragment> openFragment = new ArrayList<>();

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setParallaxOffset(0.5f);
    }

    protected BaseBackFragment setToolbarTitle(String title) {
        mToolbar = findViewById(R.id.toolbar);
        mToolbar.setTitle(title);
        return this;
    }
    protected BaseBackFragment setToolbarTitle(int title) {
        mToolbar = findViewById(R.id.toolbar);
        mToolbar.setTitle(title);
        return this;
    }

    protected BaseBackFragment setToolbarTitle(@IdRes int tid, int title) {
        mToolbar = findViewById(tid);
        mToolbar.setTitle(title);
        return this;
    }

    public BaseBackFragment setToolbarMenu(int menu, Toolbar.OnMenuItemClickListener listener) {
        mToolbar.inflateMenu(menu);
        mToolbar.setOnMenuItemClickListener(listener);
        return this;
    }

    public BaseBackFragment initToolbarNav() {
        if (mToolbar != null){
            mToolbar.setNavigationIcon(R.mipmap.ic_arrow_back_white_24dp);
            mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (navigationListener == null || navigationListener.addNavigationBack())
                        _mActivity.onBackPressed();
                }
            });
        }
        return this;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        openFragment.add(this);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        openFragment.remove(this);
    }

    /**
     * 设置关闭返回的判断标签
     *
     * @param closeTag 被关闭的标签
     */
    protected void setCloseTag(String closeTag) {
        closeFragmentTag.add(closeTag.concat(getName()));
    }

    /**
     * 返回到标签对应Fragment（不关闭标签对应的Fragment）
     *
     * @param closeTag 关闭标签
     */
    protected void backToTag(String closeTag) {
        int size = openFragment.size();
        if (size == 0) {
            return;
        }
        for (int i = size - 1; i >= 0; i--) {
            BaseBackFragment fragment = openFragment.get(i);
            if (closeFragmentTag.contains(closeTag.concat(fragment.getName()))) {
                break;
            }
            fragment.finish();
        }
    }

    /**
     * 返回到标签对应Fragment（不关闭标签对应的Fragment）
     *
     * @param closeTag 关闭标签
     */
    protected void backEndTag(String closeTag) {
        int size = openFragment.size();
        if (size == 0) {
            return;
        }
        for (int i = size - 1; i >= 0; i--) {
            BaseBackFragment fragment = openFragment.get(i);
            fragment.finish();
            if (closeFragmentTag.contains(closeTag.concat(fragment.getName()))) {
                return;
            }
        }
    }

    /**
     * 此处可以做退出判断，确认是否退出
     *
     * @return
     */
    protected void addNavigationBack(OnNavigationListener navigationListener) {
        this.navigationListener = navigationListener;
    }

    public void getProject(OnProjectObtainListener listener) {
        String pId = CommonUtils.getSPUtils(_mActivity).getString(SPUtils.PROJECT_ID);
        String pName = CommonUtils.getSPUtils(_mActivity).getString(SPUtils.PROJECT_NAME);
        if (TextUtils.isEmpty(pId) && TextUtils.isEmpty(pId))
            start(SearchHistoryFragment.getInstance(
                    HistoryTypeBean.TYPE_HISTORY_PROJECT_CHANGE,
                    CommonUtils.getSPUtils(getContext()).getString(SPUtils.USER_ID),
                    "", 0));
        else
            listener.success(pId, pName);
    }
}
