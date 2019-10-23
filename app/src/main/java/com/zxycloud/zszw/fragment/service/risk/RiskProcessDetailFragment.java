package com.zxycloud.zszw.fragment.service.risk;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.zxycloud.zszw.R;
import com.zxycloud.zszw.base.BaseBackFragment;
import com.zxycloud.zszw.event.type.VideoShowType;
import com.zxycloud.zszw.fragment.common.ImagePagerFragment;
import com.zxycloud.zszw.fragment.common.PlayVideoFragment;
import com.zxycloud.zszw.model.ResultRiskBean;
import com.zxycloud.zszw.model.bean.RiskBean;
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

import java.util.ArrayList;
import java.util.List;

/**
 * @author leiming
 * @date 2019/3/28.
 */
public class RiskProcessDetailFragment extends BaseBackFragment {
    private String riskProcessId;
    private TextView riskName, riskProjectName, riskState, riskDescription, riskImplementUser, riskImplementTime;
    private BswRecyclerView<String> riskShowImgRv;
    private RiskBean riskBean;
    private View itemVideoImg, itemVoiceImg;

    public static RiskProcessDetailFragment newInstance(String riskProcessId) {
        Bundle args = new Bundle();
        args.putString("riskProcessId", riskProcessId);
        RiskProcessDetailFragment fragment = new RiskProcessDetailFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_risk_process_detail;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        setOnClickListener(onClickListener, R.id.item_video_img, R.id.item_voice_img);

        riskProcessId = getArguments().getString("riskProcessId");

        riskName = findViewById(R.id.risk_name);
        riskProjectName = findViewById(R.id.risk_project_name);
        riskState = findViewById(R.id.risk_state);
        riskDescription = findViewById(R.id.risk_description);
        riskImplementUser = findViewById(R.id.risk_implement_user);
        riskImplementTime = findViewById(R.id.risk_implement_time);

        itemVideoImg = findViewById(R.id.item_video_img);
        itemVoiceImg = findViewById(R.id.item_voice_img);

        findViewById(R.id.risk_notice_info_ll).setVisibility(View.GONE);

        setToolbarTitle(R.string.string_risk_progress_detail_title);
        initToolbarNav();

        riskShowImgRv = findViewById(R.id.risk_show_img_rv);
        riskShowImgRv.initAdapter(R.layout.item_img_layout, convertViewCallBack)
                .setLayoutManager(LimitAnnotation.VERTICAL, 3);

        getRiskProcessDetail();
    }

    private void getRiskProcessDetail() {
        NetUtils.getNewInstance(_mActivity).request(new NetUtils.NetRequestCallBack<ResultRiskBean>() {
            @Override
            public void success(String action, ResultRiskBean bean, Object tag) {
                if (bean.isSuccessful()) {
                    riskBean = bean.getData();
                    riskName.setText(riskBean.getTitle());
                    riskProjectName.setText(riskBean.getProjectName());
                    riskState.setText(riskBean.getProcessResultName());
                    riskDescription.setText(riskBean.getDescription());
                    riskImplementUser.setText(riskBean.getProcessUserName());
                    riskImplementTime.setText(riskBean.getCreateTime());
                    riskShowImgRv.setData(riskBean.getImgUrls());

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
        }, true, new ApiRequest<>(NetBean.actionGetRiskProcessDetail, ResultRiskBean.class)
                .setRequestParams("id", riskProcessId));
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
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.item_video_img:
                    start(PlayVideoFragment.newInstance(VideoShowType.PLAY_TYPE_INSTALL_VIDEO, riskBean.getVideoUrl()));
                    break;

                case R.id.item_voice_img:
                    RecordPopupWindow recordPopupWindow = new RecordPopupWindow(getContext(), riskBean.getVoiceUrl());
                    recordPopupWindow.show(LayoutInflater.from(getContext()).inflate(R.layout.activity_base, null));
                    break;
            }
        }
    };
}
