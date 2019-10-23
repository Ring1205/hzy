package com.zxycloud.zszw.fragment.common;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.AppBarLayout;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.zxycloud.zszw.R;
import com.zxycloud.zszw.base.BaseBackFragment;
import com.zxycloud.zszw.base.BaseDataBean;
import com.zxycloud.zszw.event.type.AlertShowType;
import com.zxycloud.zszw.event.type.RiskShowType;
import com.zxycloud.zszw.event.type.ShowLoadType;
import com.zxycloud.zszw.fragment.home.message.AlertDetailFragment;
import com.zxycloud.zszw.fragment.home.shortcut.area.AreaDetailFragment;
import com.zxycloud.zszw.fragment.home.shortcut.device.DeviceDetailsFragment;
import com.zxycloud.zszw.fragment.home.shortcut.place.PlaceDetailFragment;
import com.zxycloud.zszw.fragment.service.risk.RiskDetailFragment;
import com.zxycloud.zszw.fragment.service.unit.UnitDetailsFragment;
import com.zxycloud.zszw.listener.NetRequestListener;
import com.zxycloud.zszw.model.HistoryBean;
import com.zxycloud.zszw.model.HistoryTypeBean;
import com.zxycloud.zszw.model.ResultAreaListBean;
import com.zxycloud.zszw.model.ResultDeviceListBean;
import com.zxycloud.zszw.model.ResultPlaceListBean;
import com.zxycloud.zszw.model.ResultProjectListBean;
import com.zxycloud.zszw.model.ResultRecordListBean;
import com.zxycloud.zszw.model.ResultRiskDistributionTargetListBean;
import com.zxycloud.zszw.model.ResultRiskListBean;
import com.zxycloud.zszw.model.bean.AreaBean;
import com.zxycloud.zszw.model.bean.DeviceBean;
import com.zxycloud.zszw.model.bean.PlaceBean;
import com.zxycloud.zszw.model.bean.ProjectBean;
import com.zxycloud.zszw.model.bean.RecordBean;
import com.zxycloud.zszw.model.bean.RiskBean;
import com.zxycloud.zszw.model.bean.RiskDistributionTargetBean;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.zxycloud.common.base.BaseBean;
import com.zxycloud.common.base.fragment.ISupportActivity;
import com.zxycloud.common.base.fragment.anim.DefaultHorizontalAnimator;
import com.zxycloud.common.base.fragment.anim.DefaultVerticalAnimator;
import com.zxycloud.common.utils.CommonUtils;
import com.zxycloud.common.utils.SPUtils;
import com.zxycloud.common.utils.db.DbUtils;
import com.zxycloud.common.utils.netWork.ApiRequest;
import com.zxycloud.common.utils.netWork.NetBean;
import com.zxycloud.common.utils.rxbus2.RxBusCode;
import com.zxycloud.common.widget.BswRecyclerView.BswRecyclerView;
import com.zxycloud.common.widget.BswRecyclerView.ConvertViewCallBack;
import com.zxycloud.common.widget.BswRecyclerView.LimitAnnotation;
import com.zxycloud.common.widget.BswRecyclerView.RecyclerViewHolder;
import com.zxycloud.common.widget.LCardView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author leiming
 * @date 2019/3/19.
 */
public class SearchHistoryFragment extends BaseBackFragment implements View.OnClickListener {
    private static final int FOR_CAPTURE = 2;
    public static String SEARCH_RESULT = "searchResult";
    private BswRecyclerView<BaseDataBean> commonSearchRv;
    private BswRecyclerView<HistoryBean> commonHistoryRv;
    private int historyType;

    private DbUtils mDbUtils;
    private Toolbar toolbar;
    private TextView searchHistoryDeleteChange;

    private int pageSize = 20;
    private int pageIndex = 1;

    private String areaId = "";
    private String placeId = "";
    private String deviceSystemCode = "";
    private String userId = "";
    private ArrayList<Integer> stateArray;
    private View searchHistoryDeleteAll;
    private TextView searchHistoryDeleteTv;
    private EditText etSearchKey;
    private ProjectBean allProject;
    private LCardView historyCard;
    private int marginTop;
    private SmartRefreshLayout refreshLayout;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            netWork().showLoading(R.id.load_layout, ShowLoadType.SHOW_CONTENT);
        }
    };

    private ApiRequest apiRequest;
    private String riskProjectId;

    /**
     * 单例
     *
     * @param historyType 历史类型
     * @param areaId      区域Id
     * @param placeId     场所Id（若historyType为TYPE_HISTORY_DEVICE_SYSTEM时，为设备系统编码）
     * @param marginTop   距离顶部距离
     * @return SearchHistoryFragment
     */
    public static SearchHistoryFragment getInstance(@HistoryTypeBean.HistoryType int historyType, String areaId, String placeId, int marginTop) {
        Bundle bundle = new Bundle();
        bundle.putInt("historyType", historyType);
        bundle.putString("areaId", areaId);
        bundle.putString("placeId", placeId);
        bundle.putInt("marginTop", marginTop);
        SearchHistoryFragment fragment = new SearchHistoryFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    /**
     * 单例
     *
     * @param historyType 历史类型
     * @param marginTop   距离顶部距离
     * @return SearchHistoryFragment
     */
    public static SearchHistoryFragment getInstance(@HistoryTypeBean.HistoryType int historyType, Integer[] stateArray, int marginTop) {
        Bundle bundle = new Bundle();
        bundle.putInt("historyType", historyType);
        bundle.putIntegerArrayList("stateArray", new ArrayList<>(Arrays.asList(stateArray)));
        bundle.putInt("marginTop", marginTop);
        SearchHistoryFragment fragment = new SearchHistoryFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    /**
     * 单例
     *
     * @param historyType 历史类型
     * @param marginTop   距离顶部距离
     * @return SearchHistoryFragment
     */
    public static SearchHistoryFragment getInstance(@HistoryTypeBean.HistoryType int historyType, int marginTop) {
        Bundle bundle = new Bundle();
        bundle.putInt("historyType", historyType);
        bundle.putInt("marginTop", marginTop);
        SearchHistoryFragment fragment = new SearchHistoryFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.search_history;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void initView(Bundle savedInstanceState) {
        if (null != commonHistoryRv || null != commonSearchRv) {
            return;
        }

        Bundle bundle = getArguments();
        historyType = bundle.getInt("historyType", HistoryTypeBean.TYPE_HISTORY_PLACE_DETAIL);
        if (historyType == HistoryTypeBean.TYPE_HISTORY_RECORD) {
            stateArray = bundle.getIntegerArrayList("stateArray");
        } else if (historyType == HistoryTypeBean.TYPE_HISTORY_RISK_DISTRIBUTION) {
            riskProjectId = bundle.getString("areaId", "");
        } else {
            areaId = bundle.getString("areaId", "");
            if (historyType == HistoryTypeBean.TYPE_HISTORY_DEVICE_SYSTEM) {
                deviceSystemCode = bundle.getString("placeId", "");
            } else {
                placeId = bundle.getString("placeId", "");
            }
        }
        marginTop = bundle.getInt("marginTop", 0);
        toolbar = findViewById(R.id.toolbar);
        toolbar.setPadding(0, 0, 0, 0);
        AppBarLayout.LayoutParams toolbarLp = new AppBarLayout.LayoutParams(AppBarLayout.LayoutParams.MATCH_PARENT, CommonUtils.measureScreen().dp2px(_mActivity, 50));
        toolbar.setLayoutParams(toolbarLp);
        if (historyType == HistoryTypeBean.TYPE_HISTORY_PROJECT_CHANGE_ADD_ALL ||
                historyType == HistoryTypeBean.TYPE_HISTORY_PROJECT_CHANGE ||
                historyType == HistoryTypeBean.TYPE_HISTORY_PROJECT_RESULT ||
                historyType == HistoryTypeBean.TYPE_HISTORY_PROJECT_RESULT_ALL ||
                historyType == HistoryTypeBean.TYPE_HISTORY_PROJECT_DETAIL) {
            toolbar.setTitle(R.string.common_string_switch_project);
        } else if (historyType == HistoryTypeBean.TYPE_HISTORY_RISK_DISTRIBUTION) {
            toolbar.setTitle(R.string.common_string_distribution);
        } else {
            toolbar.setTitle(R.string.common_string_search);
        }
        toolbar.inflateMenu(R.menu.menu_close);
        toolbar.setOnMenuItemClickListener(itemClickListener);
        toolbar.setOnTouchListener(new View.OnTouchListener() {
            float startY = 0;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        startY = event.getY();
                        return true;

                    case MotionEvent.ACTION_UP:
                        if (event.getY() - startY > 100) {
                            finish();
                        }
                        break;
                }
                return false;
            }
        });

        etSearchKey = findViewById(R.id.et_search_key);
        etSearchKey.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // 如果有搜索框有文本，则展示待搜索列表；若搜索框为空，则显示搜索历史
                pageIndex = 1;
                if (s.length() > 0
                        || historyType == HistoryTypeBean.TYPE_HISTORY_PROJECT_CHANGE
                        || historyType == HistoryTypeBean.TYPE_HISTORY_PROJECT_RESULT
                        || historyType == HistoryTypeBean.TYPE_HISTORY_PROJECT_RESULT_ALL
                        || historyType == HistoryTypeBean.TYPE_HISTORY_PROJECT_CHANGE_ADD_ALL
                        || historyType == HistoryTypeBean.TYPE_HISTORY_RISK_DISTRIBUTION) {
                    getRv(false);
                    getList(s.toString());
                } else {
                    getRv(true);
                    getHistory();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        searchHistoryDeleteChange = findViewById(R.id.search_history_delete_change);
        searchHistoryDeleteChange.setTag(true);
        searchHistoryDeleteAll = findViewById(R.id.search_history_delete_all);
        searchHistoryDeleteTv = findViewById(R.id.search_history_delete_tv);
        refreshLayout = findViewById(R.id.refresh_layout);

        searchHistoryDeleteTv.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });

        setOnClickListener(this, R.id.search_history_delete_all, R.id.search_history_delete_change, R.id.et_search_clear);

        if (null == mDbUtils) {
            mDbUtils = CommonUtils.getDbUtils(_mActivity);
        }

        // TODO: 2019/5/9 暂时无法禁止头部点击事件，先统一遮挡头部
//        if (marginTop == 0) {
        marginTop = CommonUtils.measureScreen().dp2px(_mActivity, 20);
//        }

        userId = CommonUtils.getSPUtils(_mActivity).getString(SPUtils.USER_ID);

        historyCard = findViewById(R.id.history_card);
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
        lp.setMargins(0, marginTop, 0, 0);
        historyCard.setLayoutParams(lp);
        historyCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CommonUtils.log().i("背景点击了");
            }
        });
        findViewById(R.id.load_layout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CommonUtils.log().i("背景点击了");
            }
        });


        if (historyType == HistoryTypeBean.TYPE_HISTORY_PROJECT_CHANGE
                || historyType == HistoryTypeBean.TYPE_HISTORY_PROJECT_RESULT
                || historyType == HistoryTypeBean.TYPE_HISTORY_PROJECT_RESULT_ALL
                || historyType == HistoryTypeBean.TYPE_HISTORY_PROJECT_CHANGE_ADD_ALL
                || historyType == HistoryTypeBean.TYPE_HISTORY_PROJECT_DETAIL
                || historyType == HistoryTypeBean.TYPE_HISTORY_RISK_DISTRIBUTION) {
            toolbar.setOnMenuItemClickListener(itemClickListener);

            allProject = new ProjectBean(_mActivity, ProjectBean.ALL_PROJECT_ID);

            getRv(false);
            getList("");
        } else {
            getRv(true);
            if (historyType == HistoryTypeBean.TYPE_HISTORY_DEVICE_STATE
                    || historyType == HistoryTypeBean.TYPE_HISTORY_DEVICE_PLACE
                    || historyType == HistoryTypeBean.TYPE_HISTORY_DEVICE_SYSTEM
                    || historyType == HistoryTypeBean.TYPE_HISTORY_DEVICE_ADAPTER) {
                toolbar.inflateMenu(R.menu.menu_capture);
                toolbar.setOnMenuItemClickListener(itemClickListener);
            }
            getHistory();
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(800);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    mHandler.sendMessage(new Message());
                }
            }
        }).start();
    }

    /**
     * 获取列表
     *
     * @param isHistory 是否展示历史
     */
    private void getRv(boolean isHistory) {
        searchHistoryDeleteTv.setText(getSearchHistoryText(isHistory));
        if (isHistory) {
            searchHistoryDeleteChange.setVisibility(View.VISIBLE);
            searchHistoryDeleteChange.setTag(false);
            deleteStateChange();
            refreshLayout.setEnableLoadMore(false);
            refreshLayout.setEnableRefresh(false);
            if (null == commonHistoryRv) {
                commonHistoryRv = findViewById(R.id.common_history_rv);
                commonHistoryRv.initAdapter(R.layout.item_search_history_layout, historyCallBack)
                        .setLayoutManager(LimitAnnotation.VERTICAL, 2)
                        .setDecoration();
            }
            commonHistoryRv.setVisibility(View.VISIBLE);
            if (null == commonSearchRv) {
                return;
            }
            commonSearchRv.setVisibility(View.GONE);
        } else {
            searchHistoryDeleteChange.setVisibility(View.GONE);
            searchHistoryDeleteAll.setVisibility(View.GONE);
            refreshLayout.setEnableLoadMore(true);
            refreshLayout.setEnableRefresh(true);
            if (null == commonSearchRv) {
                commonSearchRv = findViewById(R.id.common_search_rv);
                commonSearchRv.initAdapter(R.layout.item_search_history_layout, convertViewCallBack)
                        .setLayoutManager()
                        .setDecoration();
            }
            commonSearchRv.setVisibility(View.VISIBLE);
            if (null == commonHistoryRv) {
                return;
            }
            commonHistoryRv.setVisibility(View.GONE);
        }
    }

    private int getSearchHistoryText(boolean isHistory) {
        if (isHistory) {
            return R.string.common_string_search_history;
        } else {
            switch (historyType) {
                case HistoryTypeBean.TYPE_HISTORY_PLACE_DETAIL:
                case HistoryTypeBean.TYPE_HISTORY_PLACE_CHANGE:
                    return R.string.search_place_list;
                case HistoryTypeBean.TYPE_HISTORY_DEVICE_SYSTEM:
                case HistoryTypeBean.TYPE_HISTORY_DEVICE_PLACE:
                case HistoryTypeBean.TYPE_HISTORY_DEVICE_STATE:
                case HistoryTypeBean.TYPE_HISTORY_DEVICE_ADAPTER:
                    return R.string.search_device_list;
                case HistoryTypeBean.TYPE_HISTORY_AREA:
                case HistoryTypeBean.TYPE_HISTORY_RISK_DISTRIBUTION:
                    return R.string.search_area_list;
                case HistoryTypeBean.TYPE_HISTORY_RECORD:
                    return R.string.search_record_list;
                case HistoryTypeBean.TYPE_HISTORY_PROJECT_CHANGE_ADD_ALL:
                case HistoryTypeBean.TYPE_HISTORY_PROJECT_CHANGE:
                case HistoryTypeBean.TYPE_HISTORY_PROJECT_RESULT:
                case HistoryTypeBean.TYPE_HISTORY_PROJECT_RESULT_ALL:
                case HistoryTypeBean.TYPE_HISTORY_PROJECT_DETAIL:
                    return R.string.search_project_list;

                case HistoryTypeBean.TYPE_HISTORY_RISK:
                    return R.string.search_risk_list;

                default:
                    return R.string.search_list;
            }
        }
    }

    /**
     * 获取带展示历史列表
     */
    private void getHistory() {
        mDbUtils.executeTransaction(new DbUtils.OnTransaction() {
            @Override
            public void execute(DbUtils dbUtils) {
                final List<HistoryBean> historyBeans = dbUtils.where(HistoryBean.class)
                        .putParams(HistoryBean.QUERY_TYPE, historyType)
                        .putParams(HistoryBean.USER_ID, userId)
                        .getAll();
                _mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        commonHistoryRv.setData(historyBeans);
                    }
                });
            }
        });
    }

    /**
     * 网络请求模糊搜索结果
     *
     * @param searchString 模糊搜索字段
     */
    @SuppressWarnings("unchecked")
    private void getList(String searchString) {
        String projectId = CommonUtils.getSPUtils(_mActivity).getString(SPUtils.PROJECT_ID);
        switch (historyType) {
            case HistoryTypeBean.TYPE_HISTORY_PLACE_DETAIL:
            case HistoryTypeBean.TYPE_HISTORY_PLACE_CHANGE:
                apiRequest = netWork().apiRequest(NetBean.actionPostPlaceList, ResultPlaceListBean.class, ApiRequest.REQUEST_TYPE_POST, R.id.load_layout)
                        .setRequestParams("pageSize", pageSize)
                        .setRequestParams("pageIndex", pageIndex)
                        .setRequestParams("placeName", searchString);
                if (!TextUtils.isEmpty(areaId)) {
                    apiRequest.setRequestParams("areaId", areaId);
                }
                if (!TextUtils.isEmpty(projectId)) {
                    apiRequest.setRequestParams("projectId", projectId);
                }
                netWork().setRefreshListener(R.id.refresh_layout, false, true, netRequestListener, apiRequest);
                break;

            case HistoryTypeBean.TYPE_HISTORY_DEVICE_ADAPTER:
                apiRequest = netWork().apiRequest(NetBean.actionPostAdapterDeviceList, ResultDeviceListBean.class, ApiRequest.REQUEST_TYPE_POST, R.id.load_layout)
                        .setRequestParams("pageSize", pageSize)
                        .setRequestParams("pageIndex", pageIndex)
                        .setRequestParams("searchKeyName", searchString);
                if (!TextUtils.isEmpty(areaId)) {
                    apiRequest.setRequestParams("areaId", areaId);
                }
                if (!TextUtils.isEmpty(placeId)) {
                    apiRequest.setRequestParams("placeId", placeId);
                }
                if (!TextUtils.isEmpty(projectId)) {
                    apiRequest.setRequestParams("projectId", projectId);
                }
                netWork().setRefreshListener(R.id.refresh_layout, true, true, netRequestListener, apiRequest);
                break;

            case HistoryTypeBean.TYPE_HISTORY_DEVICE_STATE:
                apiRequest = netWork().apiRequest(NetBean.actionPostDeviceList, ResultDeviceListBean.class, ApiRequest.REQUEST_TYPE_POST, R.id.load_layout)
                        .setRequestParams("pageSize", pageSize)
                        .setRequestParams("pageIndex", pageIndex)
                        .setRequestParams("searchKeyName", searchString);
                if (!TextUtils.isEmpty(areaId)) {
                    apiRequest.setRequestParams("areaId", areaId);
                }
                if (!TextUtils.isEmpty(placeId)) {
                    apiRequest.setRequestParams("placeId", placeId);
                }
                if (!TextUtils.isEmpty(projectId)) {
                    apiRequest.setRequestParams("projectId", projectId);
                }
                netWork().setRefreshListener(R.id.refresh_layout, true, true, netRequestListener, apiRequest);
                break;

            case HistoryTypeBean.TYPE_HISTORY_DEVICE_SYSTEM:
                apiRequest = netWork().apiRequest(NetBean.actionGetDeviceListBySystemId, ResultDeviceListBean.class, ApiRequest.REQUEST_TYPE_POST, R.id.load_layout)
                        .setRequestParams("pageSize", pageSize)
                        .setRequestParams("pageIndex", pageIndex)
                        .setRequestParams("searchKeyName", searchString)
                        .setRequestParams("deviceSystemCode", deviceSystemCode);
                if (!TextUtils.isEmpty(projectId)) {
                    apiRequest.setRequestParams("projectId", projectId);
                }
                netWork().setRefreshListener(R.id.refresh_layout, true, true, netRequestListener, apiRequest);
                break;

            case HistoryTypeBean.TYPE_HISTORY_DEVICE_PLACE:
                apiRequest = netWork().apiRequest(NetBean.actionPostPlaceDeviceList, ResultDeviceListBean.class, ApiRequest.REQUEST_TYPE_POST, R.id.load_layout)
                        .setRequestParams("pageSize", pageSize)
                        .setRequestParams("pageIndex", pageIndex)
                        .setRequestParams("searchKeyName", searchString);
                if (!TextUtils.isEmpty(areaId)) {
                    apiRequest.setRequestParams("areaId", areaId);
                }
                if (!TextUtils.isEmpty(placeId)) {
                    apiRequest.setRequestParams("placeId", placeId);
                }
                if (!TextUtils.isEmpty(projectId)) {
                    apiRequest.setRequestParams("projectId", projectId);
                }
                netWork().setRefreshListener(R.id.refresh_layout, true, true, netRequestListener, apiRequest);
                break;

            case HistoryTypeBean.TYPE_HISTORY_AREA:
                apiRequest = netWork().apiRequest(NetBean.actionPostAreaList, ResultAreaListBean.class, ApiRequest.REQUEST_TYPE_POST, R.id.load_layout)
                        .setRequestParams("pageSize", pageSize)
                        .setRequestParams("pageIndex", pageIndex)
                        .setRequestParams("areaName", searchString);
                if (!TextUtils.isEmpty(projectId)) {
                    apiRequest.setRequestParams("projectId", projectId);
                }
                netWork().setRefreshListener(R.id.refresh_layout, true, true, netRequestListener, apiRequest);
                break;

            case HistoryTypeBean.TYPE_HISTORY_RISK_DISTRIBUTION:
                apiRequest = netWork().apiRequest(NetBean.actionGetRiskDistributionAreaList, ResultRiskDistributionTargetListBean.class, ApiRequest.REQUEST_TYPE_POST, R.id.load_layout)
                        .setRequestParams("pageSize", pageSize)
                        .setRequestParams("pageIndex", pageIndex)
                        .setRequestParams("areaName", searchString)
                        .setRequestParams("projectId", riskProjectId);
                netWork().setRefreshListener(R.id.refresh_layout, false, true, netRequestListener, apiRequest);
                break;

            case HistoryTypeBean.TYPE_HISTORY_RECORD:
                apiRequest = netWork().apiRequest(NetBean.actionGetRecordList, ResultRecordListBean.class, ApiRequest.REQUEST_TYPE_POST, R.id.load_layout)
                        .setRequestParams("pageSize", pageSize)
                        .setRequestParams("pageIndex", pageIndex)
                        .setRequestParams("stateGroupCodeList", stateArray)
                        .setRequestParams("placeName", searchString);
                netWork().setRefreshListener(R.id.refresh_layout, true, true, netRequestListener, apiRequest);
                break;

            case HistoryTypeBean.TYPE_HISTORY_PROJECT_CHANGE:
            case HistoryTypeBean.TYPE_HISTORY_PROJECT_RESULT_ALL:
            case HistoryTypeBean.TYPE_HISTORY_PROJECT_CHANGE_ADD_ALL:
            case HistoryTypeBean.TYPE_HISTORY_PROJECT_DETAIL:
            case HistoryTypeBean.TYPE_HISTORY_PROJECT_RESULT:
                apiRequest = netWork().apiRequest(NetBean.actionGetProjectList, ResultProjectListBean.class, ApiRequest.REQUEST_TYPE_POST, R.id.load_layout)
                        .setRequestParams("pageSize", pageSize)
                        .setRequestParams("pageIndex", pageIndex)
                        .setRequestParams("projectName", searchString);
                netWork().setRefreshListener(R.id.refresh_layout, true, true, netRequestListener, apiRequest);
                break;

            case HistoryTypeBean.TYPE_HISTORY_RISK:
                apiRequest = netWork().apiRequest(NetBean.actionRiskNoticeList, ResultRiskListBean.class, ApiRequest.REQUEST_TYPE_POST, R.id.load_layout)
                        .setRequestParams("pageSize", pageSize)
                        .setRequestParams("pageIndex", pageIndex)
                        .setRequestParams("title", searchString)
                        .setRequestParams("processResult", -1)
                        .setRequestParams("hiddenLevel", -1)
                        .setRequestParams("sourceCode", -1);
                if (!TextUtils.isEmpty(projectId)) {
                    apiRequest.setRequestParams("projectId", projectId);
                }
                netWork().setRefreshListener(R.id.refresh_layout, false, true, netRequestListener, apiRequest);
                break;
        }
    }

    @Override
    public boolean onBackPressedSupport() {
        _mActivity.setResult(Activity.RESULT_CANCELED);
        return false;
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.search_history_delete_all) {
            mDbUtils.executeTransaction(new DbUtils.OnTransaction() {
                @Override
                public void execute(DbUtils dbUtils) {
                    dbUtils.delete(commonHistoryRv.getData());
                    _mActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            commonHistoryRv.setData(null);
                        }
                    });
                }
            });
        } else if (id == R.id.search_history_delete_change) {
            deleteStateChange();
        } else if (id == R.id.et_search_clear) {
            etSearchKey.setText("");
        }
    }

    /**
     * 切换搜索历史列表的编辑状态
     */
    private void deleteStateChange() {
        boolean deleteState = (Boolean) searchHistoryDeleteChange.getTag();
        if (deleteState) {
            searchHistoryDeleteChange.setText(R.string.common_string_save);
            searchHistoryDeleteChange.setCompoundDrawables(null, null, null, null);
            searchHistoryDeleteAll.setVisibility(View.VISIBLE);
        } else {
            Drawable drawable = getResources().getDrawable(R.mipmap.common_ic_search_history_manager);
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
            searchHistoryDeleteChange.setCompoundDrawables(null, null, drawable, null);
            searchHistoryDeleteChange.setText("");
            searchHistoryDeleteAll.setVisibility(View.GONE);
        }
        searchHistoryDeleteChange.setTag(!deleteState);
        if (null != commonHistoryRv)
            commonHistoryRv.notifyDataSetChanged();
    }

    @Override
    public void onFragmentResult(int requestCode, int resultCode, Bundle data) {
        super.onFragmentResult(requestCode, resultCode, data);
        if (requestCode == FOR_CAPTURE && resultCode == RESULT_OK && null != data) {
            String searchString = data.getString(CaptureFragment.SCAN_RESULT, "");
            etSearchKey.setText(searchString);
        }
    }

    private Toolbar.OnMenuItemClickListener itemClickListener = new Toolbar.OnMenuItemClickListener() {
        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            int id = menuItem.getItemId();
            if (id == R.id.capture) {
                startForResult(CaptureFragment.newInstance(), FOR_CAPTURE);
            } else if (id == R.id.close) {
                finish();
            }
            return true;
        }
    };

    private ConvertViewCallBack<BaseDataBean> convertViewCallBack = new ConvertViewCallBack<BaseDataBean>() {
        @Override
        public void convert(RecyclerViewHolder holder, final BaseDataBean dataBean, int position, int layoutTag) {
            switch (historyType) {
                case HistoryTypeBean.TYPE_HISTORY_AREA:
                    final String areaName = ((AreaBean) dataBean).getAreaName();
                    holder.setText(R.id.tv_item_search_history, areaName)
                            .setVisibility(R.id.tv_item_search_history_delete, View.GONE)
                            .setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    final AreaBean areaBean = (AreaBean) dataBean;
                                    saveSearchString();
                                    startWithPop(AreaDetailFragment.newInstance(areaBean.getAreaId()));
                                }
                            });
                    ((TextView) holder.getView(R.id.tv_item_search_history)).setSingleLine(true);
                    break;

                case HistoryTypeBean.TYPE_HISTORY_RISK_DISTRIBUTION:
                    final String distributionAreaName = ((RiskDistributionTargetBean) dataBean).getAreaOrPlaceName();
                    holder.setText(R.id.tv_item_search_history, distributionAreaName)
                            .setVisibility(R.id.tv_item_search_history_delete, View.GONE)
                            .setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    final RiskDistributionTargetBean areaBean = (RiskDistributionTargetBean) dataBean;
                                    saveSearchString();
                                    Bundle bundle = new Bundle();
                                    bundle.putString("areaName", areaBean.getAreaOrPlaceName());
                                    bundle.putString("areaId", areaBean.getAreaOrPlaceId());
                                    setFragmentResult(RESULT_OK, bundle);
                                    finish();
                                }
                            });
                    ((TextView) holder.getView(R.id.tv_item_search_history)).setSingleLine(true);
                    break;

                case HistoryTypeBean.TYPE_HISTORY_PLACE_DETAIL:
                    final PlaceBean placeBeanDetail = (PlaceBean) dataBean;
                    final String placeNameDetail = placeBeanDetail.getPlaceName();
                    holder.setText(R.id.tv_item_search_history, placeNameDetail)
                            .setVisibility(R.id.tv_item_search_history_delete, View.GONE)
                            .setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    saveSearchString();
                                    startWithPop(PlaceDetailFragment.newInstance(placeBeanDetail.getPlaceId()));
                                }
                            });
                    ((TextView) holder.getView(R.id.tv_item_search_history)).setSingleLine(true);
                    break;

                case HistoryTypeBean.TYPE_HISTORY_PLACE_CHANGE:
                    final PlaceBean placeBeanChange = (PlaceBean) dataBean;
                    final String placeNameChange = placeBeanChange.getPlaceName();
                    holder.setText(R.id.tv_item_search_history, placeNameChange)
                            .setVisibility(R.id.tv_item_search_history_delete, View.GONE)
                            .setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    saveSearchString();
                                    Bundle bundle = new Bundle();
                                    bundle.putString("placeName", placeNameChange);
                                    bundle.putString("placeId", placeBeanChange.getPlaceId());
                                    setFragmentResult(RESULT_OK, bundle);
                                    finish();
                                }
                            });
                    ((TextView) holder.getView(R.id.tv_item_search_history)).setSingleLine(true);
                    break;

                case HistoryTypeBean.TYPE_HISTORY_PROJECT_RESULT:
                case HistoryTypeBean.TYPE_HISTORY_PROJECT_RESULT_ALL:
                    final ProjectBean projectBeanResult = (ProjectBean) dataBean;
                    final String projectNameResult = projectBeanResult.getProjectName();
                    holder.setText(R.id.tv_item_search_history, projectNameResult)
                            .setVisibility(R.id.tv_item_search_history_delete, View.GONE)
                            .setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    saveSearchString();
                                    Bundle bundle = new Bundle();
                                    bundle.putString("projectName", projectNameResult);
                                    bundle.putString("projectId", projectBeanResult.getProjectId());
                                    setFragmentResult(RESULT_OK, bundle);
                                    finish();
                                }
                            });
                    ((TextView) holder.getView(R.id.tv_item_search_history)).setSingleLine(true);
                    break;

                case HistoryTypeBean.TYPE_HISTORY_PROJECT_CHANGE_ADD_ALL:
                case HistoryTypeBean.TYPE_HISTORY_PROJECT_CHANGE:
                    final ProjectBean projectBeanChange = (ProjectBean) dataBean;
                    final String projectNameChange = projectBeanChange.getProjectName();
                    holder.setText(R.id.tv_item_search_history, projectNameChange)
                            .setVisibility(R.id.tv_item_search_history_delete, View.GONE)
                            .setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    saveSearchString();
                                    String projectId = projectBeanChange.getProjectId();
                                    if (ProjectBean.ALL_PROJECT_ID.equals(projectId)) {
                                        CommonUtils.getSPUtils(_mActivity)
                                                .put(SPUtils.PROJECT_NAME, projectNameChange)
                                                .remove(SPUtils.PROJECT_ID);
                                    } else {
                                        CommonUtils.getSPUtils(_mActivity)
                                                .put(SPUtils.PROJECT_ID, projectId)
                                                .put(SPUtils.PROJECT_NAME, projectNameChange);
                                    }
                                    CommonUtils.getRxBus().send(RxBusCode.RX_PROJECT_CHANGED);
                                    finish();
                                }
                            });
                    ((TextView) holder.getView(R.id.tv_item_search_history)).setSingleLine(true);
                    break;

                case HistoryTypeBean.TYPE_HISTORY_PROJECT_DETAIL:
                    final ProjectBean projectBeanDetail = (ProjectBean) dataBean;
                    final String projectNameDetail = projectBeanDetail.getProjectName();
                    holder.setText(R.id.tv_item_search_history, projectNameDetail)
                            .setVisibility(R.id.tv_item_search_history_delete, View.GONE)
                            .setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    saveSearchString();
                                    startWithPop(UnitDetailsFragment.newInstance(projectBeanDetail.getProjectId()));
                                }
                            });
                    ((TextView) holder.getView(R.id.tv_item_search_history)).setSingleLine(true);
                    break;

                case HistoryTypeBean.TYPE_HISTORY_DEVICE_ADAPTER:
                case HistoryTypeBean.TYPE_HISTORY_DEVICE_STATE:
                case HistoryTypeBean.TYPE_HISTORY_DEVICE_SYSTEM:
                case HistoryTypeBean.TYPE_HISTORY_DEVICE_PLACE:
                    final DeviceBean deviceBean = (DeviceBean) dataBean;
                    String showText;
                    if (deviceBean.getDeviceFlag() == 2) {
                        showText = deviceBean.getDeviceTypeName().concat("(").concat(deviceBean.getAdapterName()).concat(")\n").concat(((DeviceBean) dataBean).getDeviceInstallLocation());
                    } else {
                        showText = deviceBean.getDeviceTypeName().concat("(").concat(deviceBean.getDeviceNumber()).concat(")\n").concat(((DeviceBean) dataBean).getDeviceInstallLocation());
                    }
                    holder.setText(R.id.tv_item_search_history, showText)
                            .setVisibility(R.id.tv_item_search_history_delete, View.GONE)
                            .setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    saveSearchString();
                                    startWithPop(DeviceDetailsFragment.newInstance(deviceBean.getDeviceId(), deviceBean.getAdapterName()));
                                }
                            });
                    ((TextView) holder.getView(R.id.tv_item_search_history)).setSingleLine(false);
                    break;

                case HistoryTypeBean.TYPE_HISTORY_RECORD:
                    final RecordBean recordBean = (RecordBean) dataBean;
                    final String recordShowString = recordBean.getPlaceName().concat("\n").concat(CommonUtils.date().format(recordBean.getReceiveTime()));
                    holder.setText(R.id.tv_item_search_history, recordShowString)
                            .setVisibility(R.id.tv_item_search_history_delete, View.GONE)
                            .setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if (recordBean.getStateGroupCode() == CommonUtils.STATE_CODE_FIRE
                                            || recordBean.getStateGroupCode() == CommonUtils.STATE_CODE_PREFIRE) {
                                        saveSearchString();
                                        startWithPop(AlertDetailFragment.newInstance(AlertShowType.ALERT_HISTORY, recordBean.getStateGroupCode(), recordBean.getRecordId()));
                                    } else {
                                        finish();
                                    }
                                }
                            });
                    ((TextView) holder.getView(R.id.tv_item_search_history)).setSingleLine(false);
                    break;

                case HistoryTypeBean.TYPE_HISTORY_RISK:
                    final RiskBean riskBean = (RiskBean) dataBean;
                    final String riskShowString = riskBean.getTitle();
                    holder.setText(R.id.tv_item_search_history, riskShowString)
                            .setVisibility(R.id.tv_item_search_history_delete, View.GONE)
                            .setClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    startWithPop(RiskDetailFragment.newInstance(RiskShowType.SHOW_TYPE_RECORD, riskBean.getId()));
                                }
                            });
                    ((TextView) holder.getView(R.id.tv_item_search_history)).setSingleLine(false);
                    break;
            }
        }

        @Override
        public void loadingFinished() {

        }

        private void saveSearchString() {
            final String searchString = CommonUtils.string().getString(etSearchKey);
            mDbUtils.executeTransaction(new DbUtils.OnTransaction() {
                @Override
                public void execute(DbUtils dbUtils) {
                    HistoryBean historyBean = dbUtils.where(HistoryBean.class).putParams("searchItem", searchString).getFirst();
                    if (null == historyBean) {
                        dbUtils.update(new HistoryBean(historyType, searchString, userId));
                    } else {
                        dbUtils.update(historyBean);
                    }
                }
            });
        }
    };

    private ConvertViewCallBack<HistoryBean> historyCallBack = new ConvertViewCallBack<HistoryBean>() {
        @Override
        public void convert(RecyclerViewHolder holder, final HistoryBean historyBean, int position, int layoutTag) {
            holder.setText(R.id.tv_item_search_history, historyBean.getSearchItem())
                    .setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            etSearchKey.setText(historyBean.getSearchItem());
                        }
                    })
                    .setVisibility(R.id.tv_item_search_history_delete, (Boolean) searchHistoryDeleteChange.getTag() ? View.GONE : View.VISIBLE)
                    .setOnClickListener(R.id.tv_item_search_history_delete, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mDbUtils.executeTransaction(new DbUtils.OnTransaction() {
                                @Override
                                public void execute(DbUtils dbUtils) {
                                    dbUtils.delete(historyBean);
                                    final List<HistoryBean> historyBeans = dbUtils.where(HistoryBean.class)
                                            .putParams(HistoryBean.QUERY_TYPE, historyType)
                                            .putParams(HistoryBean.USER_ID, userId)
                                            .getAll();
                                    _mActivity.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            commonHistoryRv.setData(historyBeans);
                                        }
                                    });
                                }
                            });
                        }
                    });
            ((TextView) holder.getView(R.id.tv_item_search_history)).setSingleLine(true);
        }

        @Override
        public void loadingFinished() {

        }
    };

    private NetRequestListener netRequestListener = new NetRequestListener<BaseBean>() {
        @Override
        public void success(String action, BaseBean baseBean, Object tag) {
            List<BaseDataBean> dataBeans = new ArrayList<>();
            if (baseBean.isSuccessful()) {
                switch (historyType) {
                    case HistoryTypeBean.TYPE_HISTORY_PLACE_DETAIL:
                    case HistoryTypeBean.TYPE_HISTORY_PLACE_CHANGE:
                        dataBeans.addAll(((ResultPlaceListBean) baseBean).getData());
                        break;

                    case HistoryTypeBean.TYPE_HISTORY_DEVICE_ADAPTER:
                    case HistoryTypeBean.TYPE_HISTORY_DEVICE_PLACE:
                    case HistoryTypeBean.TYPE_HISTORY_DEVICE_STATE:
                    case HistoryTypeBean.TYPE_HISTORY_DEVICE_SYSTEM:
                        dataBeans.addAll(((ResultDeviceListBean) baseBean).getData());
                        break;

                    case HistoryTypeBean.TYPE_HISTORY_AREA:
                        dataBeans.addAll(((ResultAreaListBean) baseBean).getData());
                        break;

                    case HistoryTypeBean.TYPE_HISTORY_RISK_DISTRIBUTION:
                        dataBeans.addAll(((ResultRiskDistributionTargetListBean) baseBean).getData());
                        break;

                    case HistoryTypeBean.TYPE_HISTORY_RECORD:
                        dataBeans.addAll(((ResultRecordListBean) baseBean).getData());
                        break;

                    case HistoryTypeBean.TYPE_HISTORY_PROJECT_CHANGE_ADD_ALL:
                    case HistoryTypeBean.TYPE_HISTORY_PROJECT_RESULT_ALL:
                        if (pageIndex == 1) {
                            dataBeans.add(allProject);
                        }
                    case HistoryTypeBean.TYPE_HISTORY_PROJECT_CHANGE:
                    case HistoryTypeBean.TYPE_HISTORY_PROJECT_RESULT:
                    case HistoryTypeBean.TYPE_HISTORY_PROJECT_DETAIL:
                        dataBeans.addAll(((ResultProjectListBean) baseBean).getData());
                        break;
                }
                pageIndex = (int) apiRequest.getRequestParams("pageIndex");
                commonSearchRv.setData(dataBeans, pageIndex, pageSize);
            } else {
                CommonUtils.toast(_mActivity, baseBean.getMessage());
            }
        }
    };

    // 设置出入页面的动画
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        ((ISupportActivity) _mActivity).setFragmentAnimator(new DefaultVerticalAnimator());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ((ISupportActivity) _mActivity).setFragmentAnimator(new DefaultHorizontalAnimator());
    }
}
