package com.zxycloud.zszw.fragment.service.risk;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.zxycloud.zszw.R;
import com.zxycloud.zszw.base.BaseBackFragment;
import com.zxycloud.zszw.event.JPushEvent;
import com.zxycloud.zszw.event.RiskEvent;
import com.zxycloud.zszw.event.type.RiskShowType;
import com.zxycloud.zszw.event.type.VideoShowType;
import com.zxycloud.zszw.fragment.common.ImagePagerFragment;
import com.zxycloud.zszw.fragment.common.PlayVideoFragment;
import com.zxycloud.zszw.fragment.common.SearchHistoryFragment;
import com.zxycloud.zszw.model.HistoryTypeBean;
import com.zxycloud.zszw.model.ResultCanRiskDistributionBean;
import com.zxycloud.zszw.model.ResultRiskBean;
import com.zxycloud.zszw.model.bean.RiskBean;
import com.zxycloud.common.base.BaseBean;
import com.zxycloud.common.utils.CommonUtils;
import com.zxycloud.common.utils.GlideUtils;
import com.zxycloud.common.utils.netWork.ApiRequest;
import com.zxycloud.common.utils.netWork.NetBean;
import com.zxycloud.common.utils.netWork.NetUtils;
import com.zxycloud.common.widget.BswRecyclerView.BswRecyclerView;
import com.zxycloud.common.widget.BswRecyclerView.ConvertViewCallBack;
import com.zxycloud.common.widget.BswRecyclerView.LimitAnnotation;
import com.zxycloud.common.widget.BswRecyclerView.RecyclerViewHolder;
import com.zxycloud.common.widget.RecordPopupWindow;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

/**
 * @author leiming
 * @date 2019/3/28.
 */
public class RiskDetailFragment extends BaseBackFragment {
    private final int DISTRIBUTION_RISK = 5;

    private String riskId;
    private int showType = RiskShowType.SHOW_TYPE_REPORT;
    private TextView riskName, riskState, riskImplementUser, riskImplementTime, riskProjectName, riskAddress, riskDescription, riskReportLevel, riskReportSource, riskReportUser, riskReportTime, riskNoticeInfo;
    private BswRecyclerView<String> riskShowImgRv;
    private RiskBean riskBean;
    private View riskDescriptionLl, riskImgLl, riskVideoLl, riskVoiceLl, riskNoticeInfoLl;
    private View itemVideoImg, itemVoiceImg;
    private Toolbar mToolbar;

    public static RiskDetailFragment newInstance(@RiskShowType.showType int showType, String riskId) {
        Bundle args = new Bundle();
        args.putString("riskId", riskId);
        args.putInt("showType", showType);
        RiskDetailFragment fragment = new RiskDetailFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_risk_detail;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        setOnClickListener(onClickListener, R.id.item_video_img, R.id.item_voice_img, R.id.risk_distribution);

        showType = getArguments().getInt("showType");
        riskId = getArguments().getString("riskId");

        riskName = findViewById(R.id.risk_name);
        riskState = findViewById(R.id.risk_state);
        riskImplementUser = findViewById(R.id.risk_implement_user);
        riskImplementTime = findViewById(R.id.risk_implement_time);
        riskProjectName = findViewById(R.id.risk_project_name);
        riskAddress = findViewById(R.id.risk_address);
        riskReportLevel = findViewById(R.id.risk_report_level);
        riskReportSource = findViewById(R.id.risk_report_source);
        riskDescription = findViewById(R.id.risk_description);
        riskReportUser = findViewById(R.id.risk_report_user);
        riskReportTime = findViewById(R.id.risk_report_time);
        riskNoticeInfo = findViewById(R.id.risk_notice_info);

        riskDescriptionLl = findViewById(R.id.risk_description_ll);
        riskImgLl = findViewById(R.id.risk_img_ll);
        riskVideoLl = findViewById(R.id.risk_video_ll);
        riskVoiceLl = findViewById(R.id.risk_voice_ll);
        riskNoticeInfoLl = findViewById(R.id.risk_notice_info_ll);

        itemVideoImg = findViewById(R.id.item_video_img);
        itemVoiceImg = findViewById(R.id.item_voice_img);

        mToolbar = findViewById(R.id.toolbar);
        setToolbarTitle(R.string.string_risk_detail_title);
        if (showType == RiskShowType.SHOW_TYPE_REPORT) {
            riskNoticeInfoLl.setVisibility(View.GONE);
        } else if (showType == RiskShowType.SHOW_TYPE_TO_DO || showType == RiskShowType.SHOW_TYPE_RECORD) {
            riskNoticeInfoLl.setVisibility(View.VISIBLE);
        } else {
            riskNoticeInfoLl.setVisibility(View.VISIBLE);
        }
        initToolbarNav();
        setToolbarMenu(R.menu.menu_risk_progress, itemClickListener);

        riskShowImgRv = findViewById(R.id.risk_show_img_rv);
        riskShowImgRv.initAdapter(R.layout.item_img_layout, convertViewCallBack)
                .setLayoutManager(LimitAnnotation.VERTICAL, 3);

        getRiskDetail();
        if (showType != RiskShowType.SHOW_TYPE_REPORT)
            judgeRiskCanDistribution();
    }

    @org.greenrobot.eventbus.Subscribe(sticky = true, threadMode = org.greenrobot.eventbus.ThreadMode.MAIN)
    public void onPushRefresh(JPushEvent event) {
        getRiskDetail();
        if (showType != RiskShowType.SHOW_TYPE_REPORT)
            judgeRiskCanDistribution();
    }

    private void judgeRiskCanDistribution() {
        NetUtils.getNewInstance(_mActivity).request(new NetUtils.NetRequestCallBack<ResultCanRiskDistributionBean>() {
            @Override
            public void success(String action, ResultCanRiskDistributionBean distributionBean, Object tag) {
                if (distributionBean.isSuccessful()) {
                    findViewById(R.id.risk_distribution).setVisibility(distributionBean.getData() == 1 ? View.VISIBLE : View.GONE);
                }
            }

            @Override
            public void error(String action, Throwable e, Object tag) {

            }
        }, false, new ApiRequest<>(NetBean.actionGetRiskCanDistribution, ResultCanRiskDistributionBean.class)
                .setRequestParams("hiddenId", riskId));
    }

    private void getRiskDetail() {
        NetUtils.getNewInstance(_mActivity).request(new NetUtils.NetRequestCallBack<ResultRiskBean>() {
            @Override
            public void success(String action, ResultRiskBean bean, Object tag) {
                if (bean.isSuccessful()) {
                    riskBean = bean.getData();
                    riskName.setText(riskBean.getTitle());
                    riskState.setText(riskBean.getProcessResultName());
                    riskImplementUser.setText(riskBean.getProcessUserName());
                    riskImplementTime.setText(riskBean.getProcessTime());
                    riskProjectName.setText(riskBean.getProjectName());
                    riskAddress.setText(riskBean.getHiddenAddress());
                    riskDescription.setText(riskBean.getDescription());
                    riskReportLevel.setText(riskBean.getHiddenLevelName());
                    riskReportSource.setText(riskBean.getSourceCodeName());
                    riskReportUser.setText(riskBean.getCreateUserName());
                    riskReportTime.setText(riskBean.getCreateTime());
                    riskShowImgRv.setData(riskBean.getImgUrls());
                    if (showType != RiskShowType.SHOW_TYPE_REPORT) {
                        riskNoticeInfo.setText(String.format(CommonUtils.string().getString(_mActivity, R.string.string_space_span), riskBean.getInfo()));
                    }

                    // TODO: 2019/5/24 当前需求调整为若不存在要显示标题，故隐藏，需求更改的时候放开即可
//                    if (TextUtils.isEmpty(riskBean.getDescription())) {
//                        riskDescriptionLl.setVisibility(View.GONE);
//                    }
//                    if (TextUtils.isEmpty(riskBean.getVideoUrl())) {
//                        riskVideoLl.setVisibility(View.GONE);
//                    }
//                    if (TextUtils.isEmpty(riskBean.getVoiceUrl())) {
//                        riskVoiceLl.setVisibility(View.GONE);
//                    }
//                    if (CommonUtils.judgeListNull(riskBean.getImgUrls()) == 0) {
//                        riskImgLl.setVisibility(View.GONE);
//                    }

                    if (TextUtils.isEmpty(riskBean.getVideoUrl())) {
                        itemVideoImg.setVisibility(View.GONE);
                    }

                    if (TextUtils.isEmpty(riskBean.getVoiceUrl())) {
                        itemVoiceImg.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void error(String action, Throwable e, Object tag) {

            }
        }, true, new ApiRequest<>(NetBean.actionRiskDetail, ResultRiskBean.class)
                .setRequestParams("id", riskId)
                .setRequestParams("note", showType == RiskShowType.SHOW_TYPE_REPORT ? 0 : 1));
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onRiskProcess(RiskEvent event) {
        getRiskDetail();
        judgeRiskCanDistribution();
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
    public void onFragmentResult(int requestCode, int resultCode, Bundle data) {
        super.onFragmentResult(requestCode, resultCode, data);
        if (requestCode == DISTRIBUTION_RISK && resultCode == RESULT_OK) {
            NetUtils.getNewInstance(_mActivity).request(new NetUtils.NetRequestCallBack() {
                @Override
                public void success(String action, BaseBean baseBean, Object tag) {
                    if (baseBean.isSuccessful()) {
                        judgeRiskCanDistribution();
                    }
                    CommonUtils.toast(_mActivity, baseBean.getMessage());
                }

                @Override
                public void error(String action, Throwable e, Object tag) {

                }
            }, true, new ApiRequest<>(NetBean.actionGetRiskDistribution, BaseBean.class)
                    .setRequestType(ApiRequest.REQUEST_TYPE_POST)
                    .setRequestParams("id", riskId)
                    .setRequestParams("areaOrPlaceId", data.getString("areaId")));
        }
    }

    private ConvertViewCallBack<String> convertViewCallBack = new ConvertViewCallBack<String>() {
        GlideUtils glideUtils = CommonUtils.glide();

        @Override
        public void convert(RecyclerViewHolder holder, final String s, final int position, int layoutTag) {
            glideUtils.loadImageView(_mActivity, s, holder.getImageView(R.id.item_img_show));
            holder.setVisibility(R.id.item_img_add, View.GONE)
                    .setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            List<String> paths = riskBean.getImgUrls();
                            if (CommonUtils.judgeListNull(paths) > 0) {
                                start(ImagePagerFragment.newInstance(position, (ArrayList<String>) paths));
                            }
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
            switch (view.getId()) {
                case R.id.item_video_img:
                    start(PlayVideoFragment.newInstance(VideoShowType.PLAY_TYPE_INSTALL_VIDEO, riskBean.getVideoUrl()));
                    break;

                case R.id.item_voice_img:
                    RecordPopupWindow recordPopupWindow = new RecordPopupWindow(getContext(), riskBean.getVoiceUrl());
                    recordPopupWindow.show(LayoutInflater.from(getContext()).inflate(R.layout.activity_base, null));
                    break;

                case R.id.risk_distribution:
                    startForResult(SearchHistoryFragment.getInstance(HistoryTypeBean.TYPE_HISTORY_RISK_DISTRIBUTION, riskBean.getProjectId(), null, mToolbar.getHeight()), DISTRIBUTION_RISK);
                    break;
            }

        }
    };

    private Toolbar.OnMenuItemClickListener itemClickListener = new Toolbar.OnMenuItemClickListener() {
        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            switch (menuItem.getItemId()) {
                case R.id.risk_progress:
                    start(RiskProgressListFragment.newInstance(riskId, showType));
                    break;
            }
            return true;
        }
    };
}
