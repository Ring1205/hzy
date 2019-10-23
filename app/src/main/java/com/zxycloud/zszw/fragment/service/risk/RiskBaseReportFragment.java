package com.zxycloud.zszw.fragment.service.risk;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.SparseArray;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.Gson;
import com.zxycloud.zszw.R;
import com.zxycloud.zszw.base.BaseBackFragment;
import com.zxycloud.zszw.event.type.CloseType;
import com.zxycloud.zszw.fragment.common.SearchHistoryFragment;
import com.zxycloud.zszw.model.HistoryTypeBean;
import com.zxycloud.zszw.model.ResultProjectListBean;
import com.zxycloud.zszw.model.bean.ProjectBean;
import com.zxycloud.zszw.model.request.RequestRiskReportBean;
import com.zxycloud.common.utils.CommonUtils;
import com.zxycloud.common.utils.SPUtils;
import com.zxycloud.common.utils.netWork.ApiRequest;
import com.zxycloud.common.utils.netWork.NetBean;
import com.zxycloud.common.utils.netWork.NetUtils;
import com.zxycloud.common.widget.BswRecyclerView.BswRecyclerView;
import com.zxycloud.common.widget.BswRecyclerView.ConvertViewCallBack;
import com.zxycloud.common.widget.BswRecyclerView.RecyclerViewHolder;
import com.zxycloud.common.widget.CustomPopupWindows;

import java.util.Arrays;
import java.util.List;

public class RiskBaseReportFragment extends BaseBackFragment {
    private static final int SWITCH_PROJECT = 1;
    private SparseArray<String> sparseArray;

    private TextView riskBaseTitleTv, riskBaseLevelTv, riskBaseProjectTv, riskBaseAddressTv, riskBaseLevelShow, riskBaseProjectShow;
    private EditText riskBaseAddressEt, riskBaseTitleEt;

    private RequestRiskReportBean riskReportBean;
    private CustomPopupWindows customPopupWindows;
    private String[] riskLevel;
    private Toolbar mToolbar;
    private String projectId;

    public static RiskBaseReportFragment newInstance() {
        return new RiskBaseReportFragment();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_risk_report_base;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        setOnClickListener(onClickListener, R.id.risk_base_level_show, R.id.risk_base_project_show, R.id.risk_base_next);

        setCloseTag(CloseType.RISK_REPORT_TAG);

        setToolbarTitle(R.string.string_risk_title);
        initToolbarNav();

        riskBaseTitleTv = findViewById(R.id.risk_base_title_tv);
        riskBaseLevelTv = findViewById(R.id.risk_base_level_tv);
        riskBaseProjectTv = findViewById(R.id.risk_base_project_tv);
        riskBaseAddressTv = findViewById(R.id.risk_base_address_tv);
        riskBaseLevelShow = findViewById(R.id.risk_base_level_show);
        riskBaseProjectShow = findViewById(R.id.risk_base_project_show);

        riskBaseAddressEt = findViewById(R.id.risk_base_address_et);
        riskBaseAddressEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (riskBaseAddressEt.getText().toString().length() >= 20) {
                    CommonUtils.toast(getContext(), R.string.toast_information_limit);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        riskBaseTitleEt = findViewById(R.id.risk_base_title_et);
        riskBaseTitleEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (riskBaseTitleEt.getText().toString().length() >= 35) {
                    CommonUtils.toast(getContext(), R.string.toast_information_limit);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        sparseArray = CommonUtils.string().formatStringLength(_mActivity,
                R.string.string_risk_title_input,
                R.string.string_risk_level,
                R.string.string_risk_project,
                R.string.string_risk_address
        );

        riskBaseTitleTv.setText(sparseArray.get(R.string.string_risk_title_input));
        riskBaseLevelTv.setText(sparseArray.get(R.string.string_risk_level));
        riskBaseProjectTv.setText(sparseArray.get(R.string.string_risk_project));
        riskBaseAddressTv.setText(sparseArray.get(R.string.string_risk_address));

        riskLevel = getResources().getStringArray(R.array.risk_level_array);

        riskBaseLevelShow.setText(riskLevel[0]);
        riskBaseLevelShow.setTag(1);

        customPopupWindows = CustomPopupWindows.getInstance(_mActivity, riskBaseLevelShow, R.layout.popup_list_match_parent_layout);

        showProject();
    }

    private void showProject() {
        SPUtils spUtils = CommonUtils.getSPUtils(_mActivity);
        projectId = spUtils.getString(SPUtils.PROJECT_ID);
        if (TextUtils.isEmpty(projectId)) {
            getProjectList();
        } else {
            riskBaseProjectShow.setText(spUtils.getString(SPUtils.PROJECT_NAME));
        }
    }

    private void getProjectList() {
        SPUtils spUtils = CommonUtils.getSPUtils(_mActivity);
        String spProjectId = spUtils.getString(SPUtils.PROJECT_ID, ProjectBean.ALL_PROJECT_ID);
        if (ProjectBean.ALL_PROJECT_ID.equals(projectId)) {
            projectId = spProjectId;
            riskBaseProjectShow.setText(spUtils.getString(SPUtils.PROJECT_NAME));
            return;
        }

        ApiRequest<ResultProjectListBean> apiRequest = new ApiRequest<>(NetBean.actionGetProjectList, ResultProjectListBean.class)
                .setRequestType(ApiRequest.REQUEST_TYPE_POST)
                .setRequestParams("pageSize", 1)
                .setRequestParams("pageIndex", 1);
        NetUtils.getNewInstance(_mActivity).request(new NetUtils.NetRequestCallBack<ResultProjectListBean>() {
            @Override
            public void success(String action, ResultProjectListBean bean, Object tag) {
                if (bean.isSuccessful()) {
                    List<ProjectBean> data = bean.getData();
                    if (data.size() > 0) {
                        ProjectBean projectBean = data.get(0);
                        projectId = projectBean.getProjectId();
                        riskBaseProjectShow.setText(projectBean.getProjectName());
                    }
                }
            }

            @Override
            public void error(String action, Throwable e, Object tag) {

            }
        }, true, apiRequest);
    }

    @Override
    public void onFragmentResult(int requestCode, int resultCode, Bundle data) {
        super.onFragmentResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            riskBaseProjectShow.setText(data.getString("projectName"));
            projectId = data.getString("projectId");
        }
    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.risk_base_level_show:
                    customPopupWindows.showPopu(new CustomPopupWindows.InitCustomPopupListener() {
                        @Override
                        public void initPopup(CustomPopupWindows.PopupHolder holder) {
                            BswRecyclerView<String> popupRv = holder.getView(R.id.popup_rv);
                            popupRv.initAdapter(R.layout.item_popup_text_layout, new ConvertViewCallBack<String>() {
                                @Override
                                public void convert(RecyclerViewHolder holder, String s, final int position, int layoutTag) {
                                    holder.setText(R.id.tv_popup_text, s)
                                            .setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    customPopupWindows.dismiss();
                                                    riskBaseLevelShow.setText(riskLevel[position]);
                                                    riskBaseLevelShow.setTag(position + 1);
                                                }
                                            });
                                }

                                @Override
                                public void loadingFinished() {

                                }
                            }).setLayoutManager()
                                    .setDecoration()
                                    .setData(Arrays.asList(riskLevel));
                        }
                    });
                    break;

                case R.id.risk_base_project_show:
                    extraTransaction().startForResultDontHideSelf(SearchHistoryFragment.getInstance(HistoryTypeBean.TYPE_HISTORY_PROJECT_RESULT, "", "", 0), SWITCH_PROJECT);
                    break;

                case R.id.risk_base_next:
                    String title = CommonUtils.string().getString(riskBaseTitleEt);
                    String address = CommonUtils.string().getString(riskBaseAddressEt);
                    if (TextUtils.isEmpty(title) || TextUtils.isEmpty(address) || TextUtils.isEmpty(projectId)) {
                        CommonUtils.toast(_mActivity, R.string.data_incomplete);
                        return;
                    }
                    riskReportBean = new RequestRiskReportBean((Integer) riskBaseLevelShow.getTag(), projectId, RequestRiskReportBean.SOURCE_CODE_MONITOR, title);
                    riskReportBean.setHiddenAddress(address);
                    start(RiskDataReportFragment.newInstance(new Gson().toJson(riskReportBean)));
                    break;
            }
        }
    };
}
