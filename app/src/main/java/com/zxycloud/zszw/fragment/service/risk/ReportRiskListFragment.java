package com.zxycloud.zszw.fragment.service.risk;

import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.SparseBooleanArray;
import android.view.MenuItem;
import android.view.View;

import com.zxycloud.zszw.R;
import com.zxycloud.zszw.base.BaseBackFragment;
import com.zxycloud.zszw.base.MyBaseFragment;
import com.zxycloud.zszw.event.JPushEvent;
import com.zxycloud.zszw.event.RiskEvent;
import com.zxycloud.zszw.event.type.RiskShowType;
import com.zxycloud.zszw.fragment.service.records.RecordStatisticsFragment;
import com.zxycloud.zszw.listener.NetRequestListener;
import com.zxycloud.zszw.model.ResultRiskListBean;
import com.zxycloud.zszw.model.bean.RiskBean;
import com.zxycloud.common.utils.CommonUtils;
import com.zxycloud.common.utils.SPUtils;
import com.zxycloud.common.utils.netWork.ApiRequest;
import com.zxycloud.common.utils.netWork.NetBean;
import com.zxycloud.common.widget.BswRecyclerView.BswRecyclerView;
import com.zxycloud.common.widget.BswRecyclerView.ConvertViewCallBack;
import com.zxycloud.common.widget.BswRecyclerView.LimitAnnotation;
import com.zxycloud.common.widget.BswRecyclerView.RecyclerViewHolder;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReportRiskListFragment extends BaseBackFragment {
    private int showType = RiskShowType.SHOW_TYPE_REPORT;
    public static boolean isUpdate = false;

    private int pageSize = 20;
    private int pageIndex = 1;

    private BswRecyclerView<RiskBean> riskRv;
    private DrawerLayout riskScreenDrawer;

    private Map<String, Integer> stateImgMap;

    List<String> sourceCodeList = new ArrayList<>();
    List<String> hiddenLevelList = new ArrayList<>();
    List<String> processResultList = new ArrayList<>();
    List<RiskSelectItemBean> sourceRiskList = new ArrayList<>();
    List<RiskSelectItemBean> levelRiskList = new ArrayList<>();
    List<RiskSelectItemBean> stateResultList = new ArrayList<>();
    private BswRecyclerView<RiskSelectItemBean> rvRiskResource;
    private BswRecyclerView<RiskSelectItemBean> rvRiskLevel;
    private BswRecyclerView<RiskSelectItemBean> rvRiskState;

    private SparseBooleanArray riskSelectResetState = new SparseBooleanArray();

    public static ReportRiskListFragment newInstance(@RiskShowType.showType int showType) {
        Bundle args = new Bundle();
        args.putInt("showType", showType);
        ReportRiskListFragment fragment = new ReportRiskListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_report_risk_list;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        setOnClickListener(onClickListener, R.id.risk_screen_reset
                , R.id.risk_screen_confirm);

        showType = getArguments().getInt("showType");

        if (RiskShowType.SHOW_TYPE_RECORD == showType) {
            findViewById(R.id.AppBarLayout).setVisibility(View.GONE);
        } else if (showType == RiskShowType.SHOW_TYPE_REPORT) {
            setToolbarTitle(R.string.string_service_risk_report);
            setToolbarMenu(R.menu.menu_risk_report, itemClickListener);

            findViewById(R.id.ll_risk_source).setVisibility(View.GONE);
            findViewById(R.id.ll_risk_state).setVisibility(View.VISIBLE);
        } else if (showType == RiskShowType.SHOW_TYPE_TO_DO) {
            setToolbarTitle(R.string.string_risk_to_do_title);
            setToolbarMenu(R.menu.menu_risk_screen, itemClickListener);

            findViewById(R.id.ll_risk_source).setVisibility(View.VISIBLE);
            findViewById(R.id.ll_risk_state).setVisibility(View.VISIBLE);
        } else {
            setToolbarTitle(R.string.string_risk_done_title);
            setToolbarMenu(R.menu.menu_risk_screen, itemClickListener);

            findViewById(R.id.ll_risk_source).setVisibility(View.VISIBLE);
            findViewById(R.id.ll_risk_state).setVisibility(View.GONE);
        }
        initToolbarNav();

        initRiskScreenArray();
        initSelectItem(ITEM_TYPE_ALL);

        riskScreenDrawer = findViewById(R.id.risk_screen_drawer);

        riskRv = findViewById(R.id.risk_rv);
        riskRv.initAdapter(R.layout.item_risk_list_layout, riskCallBack)
                .setLayoutManager();

        rvRiskResource = findViewById(R.id.rv_risk_resource);
        rvRiskResource.initAdapter(R.layout.item_risk_select_layout, riskSelectResourceBack)
                .setLayoutManager(LimitAnnotation.VERTICAL, 2)
                .setData(sourceRiskList);
        rvRiskLevel = findViewById(R.id.rv_risk_level);
        rvRiskLevel.initAdapter(R.layout.item_risk_select_layout, riskSelectLevelBack)
                .setLayoutManager(LimitAnnotation.VERTICAL, 2)
                .setData(levelRiskList);
        rvRiskState = findViewById(R.id.rv_risk_state);
        rvRiskState.initAdapter(R.layout.item_risk_select_layout, riskSelectStateBack)
                .setLayoutManager(LimitAnnotation.VERTICAL, 2)
                .setData(stateResultList);

        stateImgMap = new HashMap<>();
        stateImgMap.put("0", R.mipmap.ic_risk_state_no_rectification);
        stateImgMap.put("1", R.mipmap.ic_risk_state_rectification);
        stateImgMap.put("2", R.mipmap.ic_risk_state_pending_trial);
        stateImgMap.put("3", R.mipmap.ic_risk_state_qualified);
        stateImgMap.put("4", R.mipmap.ic_risk_state_unqualified);

        getRiskList();
    }

    private final int ITEM_TYPE_ALL = 1;
    private final int ITEM_TYPE_STATE = 2;
    private final int ITEM_TYPE_LEVEL = 3;
    private final int ITEM_TYPE_SOURCE = 4;

    private void initSelectItem(int itemType) {
        riskSelectResetState.put(ITEM_TYPE_SOURCE, true);
        riskSelectResetState.put(ITEM_TYPE_LEVEL, true);
        riskSelectResetState.put(ITEM_TYPE_STATE, true);
        if (RiskShowType.SHOW_TYPE_RECORD == showType) {
            if (itemType == ITEM_TYPE_ALL || itemType == ITEM_TYPE_SOURCE)
                sourceCodeList.add(RiskSelectItemBean.RISK_SELECT_ALL);
            if (itemType == ITEM_TYPE_ALL || itemType == ITEM_TYPE_LEVEL)
                hiddenLevelList.add(RiskSelectItemBean.RISK_SELECT_ALL);
            if (itemType == ITEM_TYPE_ALL || itemType == ITEM_TYPE_STATE) {
                processResultList.add(RiskSelectItemBean.RISK_STATE_NO_RECTIFICATION);
                processResultList.add(RiskSelectItemBean.RISK_STATE_RECTIFICATION);
                processResultList.add(RiskSelectItemBean.RISK_STATE_PENDING_ACCEPTANCE);
                processResultList.add(RiskSelectItemBean.RISK_STATE_PENDING_QUALIFIED);
                processResultList.add(RiskSelectItemBean.RISK_STATE_PENDING_UNQUALIFIED);
            }
        } else if (RiskShowType.SHOW_TYPE_REPORT == showType) {
            if (itemType == ITEM_TYPE_ALL || itemType == ITEM_TYPE_SOURCE)
                sourceCodeList.add(RiskSelectItemBean.RISK_RESOURCE_SUPERVISE);
            if (itemType == ITEM_TYPE_ALL || itemType == ITEM_TYPE_LEVEL)
                hiddenLevelList.add(RiskSelectItemBean.RISK_SELECT_ALL);
            if (itemType == ITEM_TYPE_ALL || itemType == ITEM_TYPE_STATE)
                processResultList.add(RiskSelectItemBean.RISK_SELECT_ALL);
        } else if (RiskShowType.SHOW_TYPE_TO_DO == showType) {
            if (itemType == ITEM_TYPE_ALL || itemType == ITEM_TYPE_SOURCE)
                sourceCodeList.add(RiskSelectItemBean.RISK_SELECT_ALL);
            if (itemType == ITEM_TYPE_ALL || itemType == ITEM_TYPE_LEVEL)
                hiddenLevelList.add(RiskSelectItemBean.RISK_SELECT_ALL);
            if (itemType == ITEM_TYPE_ALL || itemType == ITEM_TYPE_STATE) {
                processResultList.add(RiskSelectItemBean.RISK_STATE_NO_RECTIFICATION);
                processResultList.add(RiskSelectItemBean.RISK_STATE_RECTIFICATION);
                processResultList.add(RiskSelectItemBean.RISK_STATE_PENDING_ACCEPTANCE);
                processResultList.add(RiskSelectItemBean.RISK_STATE_PENDING_UNQUALIFIED);
            }
        } else {
            if (ITEM_TYPE_ALL == itemType || ITEM_TYPE_SOURCE == itemType)
                sourceCodeList.add(RiskSelectItemBean.RISK_SELECT_ALL);
            if (ITEM_TYPE_ALL == itemType || ITEM_TYPE_LEVEL == itemType)
                hiddenLevelList.add(RiskSelectItemBean.RISK_SELECT_ALL);
            if (ITEM_TYPE_ALL == itemType || ITEM_TYPE_STATE == itemType)
                processResultList.add(RiskSelectItemBean.RISK_STATE_PENDING_QUALIFIED);
        }
    }

    /**
     * 初始化隐患筛选列表
     */
    private void initRiskScreenArray() {
        switch (showType) {
            case RiskShowType.SHOW_TYPE_REPORT:
                sourceRiskList.add(new RiskSelectItemBean(R.string.string_risk_resource_supervise, false, RiskSelectItemBean.RISK_RESOURCE_SUPERVISE));

                levelRiskList.add(new RiskSelectItemBean(R.string.array_risk_level_1, false, RiskSelectItemBean.RISK_LEVEL_1));
                levelRiskList.add(new RiskSelectItemBean(R.string.array_risk_level_2, false, RiskSelectItemBean.RISK_LEVEL_2));
                levelRiskList.add(new RiskSelectItemBean(R.string.array_risk_level_3, false, RiskSelectItemBean.RISK_LEVEL_3));

                stateResultList.add(new RiskSelectItemBean(R.string.array_risk_state_no_rectification, false, RiskSelectItemBean.RISK_STATE_NO_RECTIFICATION));
                stateResultList.add(new RiskSelectItemBean(R.string.array_risk_state_rectification, false, RiskSelectItemBean.RISK_STATE_RECTIFICATION));
                stateResultList.add(new RiskSelectItemBean(R.string.array_risk_state_pending_acceptance, false, RiskSelectItemBean.RISK_STATE_PENDING_ACCEPTANCE));
                stateResultList.add(new RiskSelectItemBean(R.string.array_risk_state_pending_qualified, false, RiskSelectItemBean.RISK_STATE_PENDING_QUALIFIED));
                stateResultList.add(new RiskSelectItemBean(R.string.array_risk_state_pending_unqualified, false, RiskSelectItemBean.RISK_STATE_PENDING_UNQUALIFIED));
                break;

            case RiskShowType.SHOW_TYPE_RECORD:

                break;

            case RiskShowType.SHOW_TYPE_TO_DO:
                sourceRiskList.add(new RiskSelectItemBean(R.string.string_risk_resource_supervise, false, RiskSelectItemBean.RISK_RESOURCE_SUPERVISE));
                sourceRiskList.add(new RiskSelectItemBean(R.string.string_risk_resource_patrol, false, RiskSelectItemBean.RISK_RESOURCE_PATROL));
                sourceRiskList.add(new RiskSelectItemBean(R.string.string_risk_resource_social, false, RiskSelectItemBean.RISK_RESOURCE_SOCIAL));

                levelRiskList.add(new RiskSelectItemBean(R.string.array_risk_level_1, false, RiskSelectItemBean.RISK_LEVEL_1));
                levelRiskList.add(new RiskSelectItemBean(R.string.array_risk_level_2, false, RiskSelectItemBean.RISK_LEVEL_2));
                levelRiskList.add(new RiskSelectItemBean(R.string.array_risk_level_3, false, RiskSelectItemBean.RISK_LEVEL_3));

                stateResultList.add(new RiskSelectItemBean(R.string.array_risk_state_no_rectification, false, RiskSelectItemBean.RISK_STATE_NO_RECTIFICATION));
                stateResultList.add(new RiskSelectItemBean(R.string.array_risk_state_rectification, false, RiskSelectItemBean.RISK_STATE_RECTIFICATION));
                stateResultList.add(new RiskSelectItemBean(R.string.array_risk_state_pending_acceptance, false, RiskSelectItemBean.RISK_STATE_PENDING_ACCEPTANCE));
                stateResultList.add(new RiskSelectItemBean(R.string.array_risk_state_pending_unqualified, false, RiskSelectItemBean.RISK_STATE_PENDING_UNQUALIFIED));
                break;

            case RiskShowType.SHOW_TYPE_DONE:
                sourceRiskList.add(new RiskSelectItemBean(R.string.string_risk_resource_supervise, false, RiskSelectItemBean.RISK_RESOURCE_SUPERVISE));
                sourceRiskList.add(new RiskSelectItemBean(R.string.string_risk_resource_patrol, false, RiskSelectItemBean.RISK_RESOURCE_PATROL));
                sourceRiskList.add(new RiskSelectItemBean(R.string.string_risk_resource_social, false, RiskSelectItemBean.RISK_RESOURCE_SOCIAL));

                levelRiskList.add(new RiskSelectItemBean(R.string.array_risk_level_1, false, RiskSelectItemBean.RISK_LEVEL_1));
                levelRiskList.add(new RiskSelectItemBean(R.string.array_risk_level_2, false, RiskSelectItemBean.RISK_LEVEL_2));
                levelRiskList.add(new RiskSelectItemBean(R.string.array_risk_level_3, false, RiskSelectItemBean.RISK_LEVEL_3));

                stateResultList.add(new RiskSelectItemBean(R.string.array_risk_state_pending_qualified, false, RiskSelectItemBean.RISK_STATE_PENDING_QUALIFIED));
                break;
        }
    }

    private void resetRiskScreenArray() {
        for (RiskSelectItemBean itemBean : sourceRiskList) {
            itemBean.setSelect(false);
        }
        rvRiskResource.notifyDataSetChanged();
        for (RiskSelectItemBean itemBean : levelRiskList) {
            itemBean.setSelect(false);
        }
        rvRiskLevel.notifyDataSetChanged();
        for (RiskSelectItemBean itemBean : stateResultList) {
            itemBean.setSelect(false);
        }
        rvRiskState.notifyDataSetChanged();

        sourceCodeList.clear();
        hiddenLevelList.clear();
        processResultList.clear();

        initSelectItem(ITEM_TYPE_ALL);

        getRiskList();
    }

    @org.greenrobot.eventbus.Subscribe(sticky = true, threadMode = org.greenrobot.eventbus.ThreadMode.MAIN)
    public void onPushRefresh(JPushEvent event) {
        getRiskList();
    }

    @SuppressWarnings("unchecked")
    private void getRiskList() {
        final ApiRequest<ResultRiskListBean> apiRequest = netWork().apiRequest(showType == RiskShowType.SHOW_TYPE_REPORT ? NetBean.actionRiskList : NetBean.actionRiskNoticeList, ResultRiskListBean.class, ApiRequest.REQUEST_TYPE_POST, R.id.load_layout)
                .setRequestParams("pageSize", pageSize)
                .setRequestParams("pageIndex", pageIndex)
                .setRequestParams("sourceCodeList", sourceCodeList)
                .setRequestParams("hiddenLevelList", hiddenLevelList)
                .setRequestParams("processResultList", processResultList);

        String projectId = CommonUtils.getSPUtils(_mActivity).getString(SPUtils.PROJECT_ID);
        if (!TextUtils.isEmpty(projectId)) {
            apiRequest.setRequestParams("projectId", projectId);
        }
        netWork().setRefreshListener(R.id.refresh_layout, true, true, new NetRequestListener<ResultRiskListBean>() {
            @Override
            public void success(String action, ResultRiskListBean baseBean, Object tag) {
                if (baseBean.isSuccessful()) {
                    List<RiskBean> linkmanBeans = baseBean.getData();
                    int currentIndex = (int) apiRequest.getRequestParams().get("pageIndex");
                    riskRv.setData(linkmanBeans, currentIndex, pageSize);
                    if (currentIndex == 1) {
                        riskRv.scrollToPosition(0);
                    }
                }
            }
        }, apiRequest);
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onRiskProcess(RiskEvent event) {
        getRiskList();
        EventBus.getDefault().removeStickyEvent(event);
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden && isUpdate) {
            isUpdate = false;
            getRiskList();
        }
    }

    private ConvertViewCallBack<RiskBean> riskCallBack = new ConvertViewCallBack<RiskBean>() {
        @Override
        public void convert(RecyclerViewHolder holder, final RiskBean riskBean, int position, int layoutTag) {
            holder.setText(R.id.risk_title, riskBean.getTitle())
                    .setText(R.id.risk_report_source, riskBean.getSourceCodeName())
                    .setText(R.id.risk_report_level, riskBean.getHiddenLevelName())
                    .setText(R.id.risk_report_user, riskBean.getCreateUserName())
                    .setText(R.id.risk_report_time, riskBean.getCreateTime())
                    .setText(R.id.risk_description, riskBean.getDescription())
                    .setVisibility(R.id.risk_description, TextUtils.isEmpty(riskBean.getDescription()) ? View.GONE : View.VISIBLE);
            int imgSize = CommonUtils.judgeListNull(riskBean.getImgUrls());
            holder.setTextWithDrawables(R.id.risk_report_img
                    , imgSize + ""
                    , imgSize == 0 ? R.mipmap.ic_risk_item_img_select_no : R.mipmap.ic_risk_item_img_selected
                    , 0
                    , 0
                    , 0);

            int videoSize = TextUtils.isEmpty(riskBean.getVideoUrl()) ? 0 : 1;
            holder.setTextWithDrawables(R.id.risk_report_video
                    , videoSize + ""
                    , videoSize == 0 ? R.mipmap.ic_risk_item_video_select_no : R.mipmap.ic_risk_item_video_selected
                    , 0
                    , 0
                    , 0);

            int voiceSize = TextUtils.isEmpty(riskBean.getVoiceUrl()) ? 0 : 1;
            holder.setTextWithDrawables(R.id.risk_report_voice
                    , voiceSize + ""
                    , voiceSize == 0 ? R.mipmap.ic_risk_item_voice_select_no : R.mipmap.ic_risk_item_voice_selected
                    , 0
                    , 0
                    , 0);

            holder.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MyBaseFragment fragment = (MyBaseFragment) getParentFragment();
                    if (null != fragment && fragment instanceof RecordStatisticsFragment)
                        fragment.start(RiskDetailFragment.newInstance(showType, riskBean.getId()));
                    else
                        start(RiskDetailFragment.newInstance(showType, riskBean.getId()));
                }
            });
            Integer imgRes = stateImgMap.get(riskBean.getProcessResult());
            if (null != imgRes) {
                holder.setImageRes(R.id.item_alert_state, imgRes);
            }
        }

        @Override
        public void loadingFinished() {

        }
    };

    private ConvertViewCallBack<RiskSelectItemBean> riskSelectResourceBack = new ConvertViewCallBack<RiskSelectItemBean>() {
        boolean isChanged = false;

        @Override
        public void convert(RecyclerViewHolder holder, final RiskSelectItemBean riskBean, int position, int layoutTag) {
            holder.setText(R.id.item_risk_select, getResources().getColor(riskBean.isSelect() ? android.R.color.white : R.color.common_color_text), riskBean.getItemStringRes())
                    .setBackground(R.id.item_risk_select, riskBean.isSelect() ? R.drawable.common_bg_corner_main_16 : R.drawable.common_bg_corner_enable_light_16)
                    .setClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (riskSelectResetState.get(ITEM_TYPE_SOURCE))
                                sourceCodeList.clear();
                            riskBean.setSelect(!riskBean.isSelect());
                            String riskSelectTag = riskBean.getItemSelectTag();
                            if (riskBean.isSelect()) {
                                riskSelectResetState.put(ITEM_TYPE_SOURCE, false);
                                sourceCodeList.remove(RiskSelectItemBean.RISK_SELECT_ALL);
                                sourceCodeList.add(riskSelectTag);
                            } else {
                                sourceCodeList.remove(riskSelectTag);
                                if (sourceCodeList.size() == 0) {
                                    isChanged = false;
                                    initSelectItem(ITEM_TYPE_SOURCE);
                                }
                            }
                            rvRiskResource.notifyDataSetChanged();
                            getRiskList();
                        }
                    });
        }

        @Override
        public void loadingFinished() {

        }
    };

    private ConvertViewCallBack<RiskSelectItemBean> riskSelectLevelBack = new ConvertViewCallBack<RiskSelectItemBean>() {

        @Override
        public void convert(RecyclerViewHolder holder, final RiskSelectItemBean riskBean, int position, int layoutTag) {
            holder.setText(R.id.item_risk_select, getResources().getColor(riskBean.isSelect() ? android.R.color.white : R.color.common_color_text), riskBean.getItemStringRes())
                    .setBackground(R.id.item_risk_select, riskBean.isSelect() ? R.drawable.common_bg_corner_main_16 : R.drawable.common_bg_corner_enable_light_16)
                    .setClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (riskSelectResetState.get(ITEM_TYPE_LEVEL))
                                hiddenLevelList.clear();
                            riskBean.setSelect(!riskBean.isSelect());
                            String riskSelectTag = riskBean.getItemSelectTag();
                            if (riskBean.isSelect()) {
                                riskSelectResetState.put(ITEM_TYPE_LEVEL, false);
                                hiddenLevelList.remove(RiskSelectItemBean.RISK_SELECT_ALL);
                                hiddenLevelList.add(riskSelectTag);
                            } else {
                                hiddenLevelList.remove(riskSelectTag);
                                if (hiddenLevelList.size() == 0) {
                                    initSelectItem(ITEM_TYPE_LEVEL);
                                }
                            }
                            rvRiskLevel.notifyDataSetChanged();
                            getRiskList();
                        }
                    });
        }

        @Override
        public void loadingFinished() {

        }
    };

    private ConvertViewCallBack<RiskSelectItemBean> riskSelectStateBack = new ConvertViewCallBack<RiskSelectItemBean>() {

        @Override
        public void convert(RecyclerViewHolder holder, final RiskSelectItemBean riskBean, int position, int layoutTag) {
            holder.setText(R.id.item_risk_select, getResources().getColor(riskBean.isSelect() ? android.R.color.white : R.color.common_color_text), riskBean.getItemStringRes())
                    .setBackground(R.id.item_risk_select, riskBean.isSelect() ? R.drawable.common_bg_corner_main_16 : R.drawable.common_bg_corner_enable_light_16)
                    .setClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (riskSelectResetState.get(ITEM_TYPE_STATE))
                                processResultList.clear();
                            riskBean.setSelect(!riskBean.isSelect());
                            String riskSelectTag = riskBean.getItemSelectTag();
                            if (riskBean.isSelect()) {
                                riskSelectResetState.put(ITEM_TYPE_STATE, false);
                                processResultList.remove(RiskSelectItemBean.RISK_SELECT_ALL);
                                processResultList.add(riskSelectTag);
                            } else {
                                processResultList.remove(riskSelectTag);
                                if (processResultList.size() == 0) {
                                    initSelectItem(ITEM_TYPE_STATE);
                                }
                            }
                            rvRiskState.notifyDataSetChanged();
                            getRiskList();
                        }
                    });
        }

        @Override
        public void loadingFinished() {

        }
    };

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            int id = view.getId();
            switch (id) {
                case R.id.risk_screen_reset:
                    resetRiskScreenArray();
                    break;

                case R.id.risk_screen_confirm:
                    riskScreenDrawer.closeDrawer(GravityCompat.END);
                    break;
            }
        }
    };

    private Toolbar.OnMenuItemClickListener itemClickListener = new Toolbar.OnMenuItemClickListener() {
        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            if (riskScreenDrawer.isDrawerOpen(GravityCompat.END)) {// 若抽屉开启，则点击时优先关闭抽屉，再次点击执行目标操作
                riskScreenDrawer.closeDrawer(GravityCompat.END);
                return true;
            }
            switch (menuItem.getItemId()) {
                case R.id.risk_report:
                    MyBaseFragment fragment = (MyBaseFragment) getParentFragment();
                    if (null != fragment && fragment instanceof RecordStatisticsFragment)
                        fragment.start(RiskBaseReportFragment.newInstance());
                    else
                        start(RiskBaseReportFragment.newInstance());
                    break;

                case R.id.risk_screen:
                    riskScreenDrawer.openDrawer(GravityCompat.END);
                    break;
            }
            return true;
        }
    };

    /**
     * 抽屉搜索列表Bean
     */
    class RiskSelectItemBean {
        static final String RISK_SELECT_ALL = "-1";

        static final String RISK_RESOURCE_PATROL = "1";
        static final String RISK_RESOURCE_SOCIAL = "2";
        static final String RISK_RESOURCE_SUPERVISE = "3";

        static final String RISK_LEVEL_1 = "1";
        static final String RISK_LEVEL_2 = "2";
        static final String RISK_LEVEL_3 = "3";

        static final String RISK_STATE_NO_RECTIFICATION = "0";
        static final String RISK_STATE_RECTIFICATION = "1";
        static final String RISK_STATE_PENDING_ACCEPTANCE = "2";
        static final String RISK_STATE_PENDING_QUALIFIED = "3";
        static final String RISK_STATE_PENDING_UNQUALIFIED = "4";

        private int itemStringRes;
        private boolean isSelect;
        private String itemSelectTag;

        public RiskSelectItemBean(int itemStringRes, boolean isSelect, String itemSelectTag) {
            this.itemStringRes = itemStringRes;
            this.isSelect = isSelect;
            this.itemSelectTag = itemSelectTag;
        }

        public void setSelect(boolean select) {
            isSelect = select;
        }

        public int getItemStringRes() {
            return itemStringRes;
        }

        public boolean isSelect() {
            return isSelect;
        }

        public String getItemSelectTag() {
            return itemSelectTag;
        }
    }
}
