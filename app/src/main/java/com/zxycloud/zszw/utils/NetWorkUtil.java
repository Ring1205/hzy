package com.zxycloud.zszw.utils;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;

import com.zxycloud.zszw.R;
import com.zxycloud.zszw.event.type.ShowLoadType;
import com.zxycloud.zszw.listener.NetRequestListener;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.zxycloud.common.base.BaseBean;
import com.zxycloud.common.utils.CommonUtils;
import com.zxycloud.common.utils.SPUtils;
import com.zxycloud.common.utils.TimeUpUtils;
import com.zxycloud.common.utils.netWork.ApiRequest;
import com.zxycloud.common.utils.netWork.NetUtils;

import java.net.ConnectException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import ezy.ui.layout.LoadingLayout;

public class NetWorkUtil<T extends BaseBean> {
    private Context mContext;
    private View layoutView;
    private RefreshLayout mRefreshLayout;
    private HashMap<String, Integer> mPagerNumbers = new HashMap<>();

    private int request = -1;
    private int mPageSize = 10;// 默认分页Size数为10
    private LoadingLayout loading;
    private List<String> actionUrl = new ArrayList<>();
    private NetRequestListener mCallBack;
    private ApiRequest[] mApiRequests;
    private OnLayoutRefreshListener onLayoutRefreshListener;

    private Timer timer;
    private String lastAction;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1) {
                if (timer != null)
                    timer.cancel();
                timer = null;
                setRequestListener(lastAction);
            }
        }
    };

    public NetWorkUtil(Context context, View layout) {
        this.mContext = context;
        this.layoutView = layout;
    }

    public <V extends View> V findViewById(int layout) {
        return layoutView.findViewById(layout);
    }

    public NetWorkUtil setOnLayoutRefreshListener(OnLayoutRefreshListener onLayoutRefreshListener) {
        this.onLayoutRefreshListener = onLayoutRefreshListener;
        return this;
    }

    /**
     * 无需显示网络请求状态的ApiRequest
     *
     * @param url  网络url 如：NetBean.actionSignIn
     * @param clz  请求返回的Bean类 如：ResultListBean.class
     * @param type 网络请求类型 如：NetUtils.GET
     * @return ApiRequest
     */
    public ApiRequest<T> apiRequest(String url, Class<? extends BaseBean> clz, @ApiRequest.NetRequestType int type) {
        if (type != 0)
            return new ApiRequest<>(url, (Class<T>) clz).setRequestType(type);
        else
            return new ApiRequest<>(url, (Class<T>) clz);
    }

    /**
     * 显示网络请求状态的ApiRequest
     *
     * @param loadingId 可显示请求状态的控件的Id 建议传入ezy.ui.layout.LoadingLayout控件，传入其他控件ID无法进行下拉刷新等操作
     */
    public ApiRequest<T> apiRequest(String url, Class<? extends BaseBean> clz, @ApiRequest.NetRequestType int type, @IdRes int loadingId) {
        actionUrl.add(url);
        showLoading(loadingId, ShowLoadType.SHOW_LOADING);
        return apiRequest(url, clz, type);
    }

    /**
     * 显示网络请求状态的ApiRequest
     *
     * @param loading 显示请求状态的控件LoadingLayout 如：showLoading(ViewId, ShowLoadType.SHOW_LOADING)，可对LoadingLayout进行其他操作，如点击刷新、文字提示、状态样式
     */
    public ApiRequest<T> apiRequest(String url, Class<? extends BaseBean> clz, @ApiRequest.NetRequestType int type, LoadingLayout loading) {
        actionUrl.add(url);
        this.loading = loading;
        return apiRequest(url, clz, type);
    }

    /**
     * 基础网络请求
     * 第一次调用会缓存请求对象，后面再次调用相当于临时请求
     *
     * @param callBack
     * @param apiRequest
     */
    public ApiRequest<T>[] setRequestListener(NetRequestListener callBack, ApiRequest<T>... apiRequest) {
        for (ApiRequest<T> request : apiRequest)
            if (request != null && request.getRequestParams() != null) {
                String projectId = (String) request.getRequestParams().get("projectId");
                String pId = CommonUtils.getSPUtils(mContext).getString(SPUtils.PROJECT_ID);
                if (!TextUtils.isEmpty(projectId) && !projectId.equals(pId))
                    request.setRequestParams("projectId", pId);
            }

        mCallBack = callBack;
        if (mApiRequests == null)
            mApiRequests = apiRequest;

        new NetUtils(mContext).request(new NetUtils.NetRequestCallBack() {
            @Override
            public void success(String action, BaseBean baseBean, Object tag) {
                // 如果设置了页面状态，根据请求的code 显示页面状态默认
                if (baseBean.isSuccessful()) {
                    if (actionUrl.contains(action) && loading != null)
                        loading.showContent();
                } else {
                    // 空页面，返回massage
                    if (actionUrl.contains(action) && loading != null) {
                        loading.showEmpty();
                        loading.setEmptyText(baseBean.getMessage());
                    }
                }
                /**
                 // 如果设置了刷新手势，关闭手势动画
                 if (mRefreshLayout != null) {
                 mRefreshLayout.finishRefresh(true);
                 mRefreshLayout.finishLoadMore(true);
                 }*/
                // 如果该请求为分页请求，获取页面总数
                if (mPagerNumbers.get(action) != null) {
                    mPagerNumbers.put(action, baseBean.getTotalPages());
                    if (baseBean.getTotalRecords() == 0)
                        loading.showEmpty();
                }
                // 返回成功请求结果
                mCallBack.success(action, baseBean, tag);
            }

            @Override
            public void error(String action, Throwable e, Object tag) {
                // 如果设置了网络错误页面，设置点击刷新
                if (actionUrl.contains(action) && loading != null) {
                    View.OnClickListener listener = new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (loading != null)
                                loading.showLoading();
                            for (ApiRequest request : mApiRequests)
                                if (request.getRequestType() != 0 && actionUrl.contains(request.getAction()))
                                    setRequestListener(mCallBack, request);
                        }
                    };
                    if (e instanceof ConnectException) {
                        loading.showError();
                        loading.setRetryListener(listener);
                    } else {
                        loading.showEmpty();
                        loading.setOnClickListener(listener);
                    }
                }
                /**
                 // 如果设置了刷新手势，关闭手势动画
                 if (mRefreshLayout != null) {
                 mRefreshLayout.finishRefresh(false);
                 mRefreshLayout.finishLoadMore(false);
                 }*/
            }
        }, false, apiRequest);
        return apiRequest;
    }

    /**
     * 添加网络请求到缓存区
     * 注意：在原有的监听中添加请求对象
     *
     * @param apiRequest
     * @return
     */
    public ApiRequest<T>[] addRequestListener(ApiRequest<T>... apiRequest) {
        if (mApiRequests != null)
            mApiRequests = CommonUtils.concatArray(mApiRequests, apiRequest);
        else
            mApiRequests = apiRequest;
        return mCallBack != null ? setRequestListener(mCallBack, apiRequest) : apiRequest;
    }

    /**
     * 网络请求，带下拉刷新或上拉加载更多功能
     *
     * @param refreshId   刷新控件com.scwang.smartrefresh.layout.api.RefreshLayout的id
     * @param isRefresh   是否开启下拉刷新
     * @param isLoadMore  是否开启上拉加载更多
     * @param callBack    成功返回
     * @param apiRequests
     */
    public void setRefreshListener(@IdRes int refreshId, boolean isRefresh, boolean isLoadMore, final NetRequestListener callBack, final ApiRequest... apiRequests) {
        mRefreshLayout = findViewById(refreshId);
        mRefreshLayout.setEnableLoadMore(isLoadMore);
        mRefreshLayout.setEnableRefresh(isRefresh);
        mRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                if (callBack != null && apiRequests != null) {
                    mRefreshLayout.finishRefresh(1200, true);
                    loadPageIndex(true, callBack, apiRequests);
                }
                if (null != onLayoutRefreshListener)
                    onLayoutRefreshListener.onRefresh(refreshLayout);
            }
        });
        mRefreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                if (callBack != null && apiRequests != null) {
                    mRefreshLayout.finishLoadMore(800);
                    loadPageIndex(false, callBack, apiRequests);
                }
                if (null != onLayoutRefreshListener)
                    onLayoutRefreshListener.onLoadMore(refreshLayout);
            }
        });
        for (ApiRequest mApiRequest : apiRequests)
            if (mApiRequest.getRequestParams() != null && mApiRequest.getRequestParams().get("pageIndex") != null && mPagerNumbers.get(mApiRequest.getAction()) == null)
                mPagerNumbers.put(mApiRequest.getAction(), 0);

        mApiRequests = apiRequests;
        setRequestListener(callBack, apiRequests);
    }

    /**
     * 根据是否有列表设置分页加载
     *
     * @param b           判断手势，下拉为true，上拉为false
     * @param apiRequests
     */
    private void loadPageIndex(boolean b, NetRequestListener callBack, ApiRequest... apiRequests) {
        for (ApiRequest apiRequest : apiRequests)
            if (apiRequest.getRequestParams() != null) {
                Integer pageIndex = (Integer) apiRequest.getRequestParams().get("pageIndex");
                Integer pageSize = (Integer) apiRequest.getRequestParams().get("pageSize");
                if (pageIndex != null && pageSize != null) {
                    if (mPageSize == 10 && mPageSize != pageSize)
                        mPageSize = pageSize;
                    if (request != -1 && pageSize > mPageSize) {
                        apiRequest.setRequestParams("pageIndex", pageIndex * pageSize / mPageSize);
                        apiRequest.setRequestParams("pageSize", mPageSize);
                    }
                    pageIndex = b ? 1 : ++pageIndex;
                    apiRequest.setTag(pageIndex);// 将pageIndex存储到Tag中
                    if (mPagerNumbers.get(apiRequest.getAction()) != 0 && pageIndex > mPagerNumbers.get(apiRequest.getAction())) {
                        mRefreshLayout.finishLoadMoreWithNoMoreData();
                        CommonUtils.toast(mContext, R.string.all_loaded);
                        return;
                    } else {
                        mRefreshLayout.resetNoMoreData();// 开启上拉手势
                    }
                    apiRequest.setRequestParams("pageIndex", pageIndex);
                }
            }
        setRequestListener(callBack, apiRequests);
    }

    /**
     * 单独加载请求列表中的一个，可以改参数
     *
     * @param action
     */
    public ApiRequest<T> setRequestListener(@NonNull String action) {
        if (mApiRequests != null)
            for (ApiRequest mApiRequest : mApiRequests) {
                if (action.equals(mApiRequest.getAction())) {
                    setRequestListener(mCallBack, mApiRequest);
                    return mApiRequest;
                }
            }
        return new ApiRequest<>(null, null);
    }

    /**
     * 防止频繁请求，同时保证返回最后一次请求结果
     *
     * @param action
     * @param intervalTime 同一个请求每次间隔的时间
     * @return
     */
    public ApiRequest<T> setRequestListener(@NonNull String action, long intervalTime) {
        if (mApiRequests != null)
            if (action.equals(lastAction) && !CommonUtils.timeUpUtils().isTimeDown(TimeUpUtils.TIME_UP_JUMP, 800L)) {
                for (ApiRequest mApiRequest : mApiRequests) {
                    if (action.equals(mApiRequest.getAction())) {
                        if (timer != null)
                            timer.cancel();
                        timer = null;
                        timer = new Timer();
                        timer.schedule(new TimerTask() {
                            @Override
                            public void run() {
                                Message msg = handler.obtainMessage();
                                msg.what = 1;
                                handler.sendMessage(msg);
                            }
                        }, intervalTime, 9527);
                        return mApiRequest;
                    }
                }
            } else {
                lastAction = action;
                return setRequestListener(action);
            }
        return new ApiRequest<>(null, null);
    }

    /**
     * 加载与反馈页面
     *
     * @param loadId
     * @param type
     */
    public LoadingLayout showLoading(@IdRes int loadId, @ShowLoadType.showType int type) {
        if (findViewById(loadId) instanceof LoadingLayout) {
            loading = findViewById(loadId);
        } else {
            loading = LoadingLayout.wrap(findViewById(loadId));
        }
        return showLoading(type);
    }

    public LoadingLayout showLoading(@ShowLoadType.showType int type) {
        if (loading != null) {
            switch (type) {
                case ShowLoadType.SHOW_LOADING:
                    loading.showLoading();
                    break;
                case ShowLoadType.SHOW_EMPTY:
                    loading.showEmpty();
                    break;
                case ShowLoadType.SHOW_ERROR:
                    loading.showError();
                    break;
                case ShowLoadType.SHOW_CONTENT:
                    loading.showContent();
                    break;
            }
        }
        return loading;
    }

    /**
     * 加载已有的网络请求
     */
    public void loading() {
        if (mApiRequests != null && mCallBack != null) {
            for (int i = 0; i < mApiRequests.length; i++)
                if (mApiRequests[i].getRequestParams().get("pageIndex") != null && mApiRequests[i].getRequestParams().get("pageSize") != null)
                    request = i;

            if (request != -1) {
                Integer pageIndex = (Integer) mApiRequests[request].getRequestParams().get("pageIndex");
                Integer pageSize = (Integer) mApiRequests[request].getRequestParams().get("pageSize");
                mApiRequests[request].setRequestParams("pageIndex", 1);
                mApiRequests[request].setRequestParams("pageSize", pageIndex * pageSize);
            }
            setRequestListener(mCallBack, mApiRequests);
        }
    }

    public RefreshLayout getRefreshLayout() {
        return mRefreshLayout;
    }

    public interface OnLayoutRefreshListener {
        void onRefresh(RefreshLayout refreshLayout);

        void onLoadMore(RefreshLayout refreshLayout);
    }
}
