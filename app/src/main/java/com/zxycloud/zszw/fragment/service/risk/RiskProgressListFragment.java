package com.zxycloud.zszw.fragment.service.risk;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.zxycloud.zszw.R;
import com.zxycloud.zszw.base.BaseBackFragment;
import com.zxycloud.zszw.event.JPushEvent;
import com.zxycloud.zszw.event.RiskEvent;
import com.zxycloud.zszw.event.type.RiskShowType;
import com.zxycloud.zszw.listener.NetRequestListener;
import com.zxycloud.zszw.model.ResultCanRiskProcessBean;
import com.zxycloud.zszw.model.ResultProcessStateListBean;
import com.zxycloud.zszw.model.ResultRiskListBean;
import com.zxycloud.zszw.model.bean.ProcessStateBean;
import com.zxycloud.zszw.model.bean.RiskBean;
import com.zxycloud.common.utils.CommonUtils;
import com.zxycloud.common.utils.PopupWindows.CustomPopupWindows;
import com.zxycloud.common.utils.PopupWindows.PopupParam;
import com.zxycloud.common.utils.netWork.ApiRequest;
import com.zxycloud.common.utils.netWork.NetBean;
import com.zxycloud.common.utils.netWork.NetUtils;
import com.zxycloud.common.widget.BswRecyclerView.BswRecyclerView;
import com.zxycloud.common.widget.BswRecyclerView.ConvertViewCallBack;
import com.zxycloud.common.widget.BswRecyclerView.RecyclerViewHolder;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

/**
 * @author leiming
 * @date 2019/3/28.
 */
public class RiskProgressListFragment extends BaseBackFragment {
    private String id;
    private BswRecyclerView<RiskBean> riskProgressRv;
    private CustomPopupWindows riskProcessWindows;
    private List<ProcessStateBean> processStateList;
    private TextView btn;
    private String processState;
    private Toolbar toolbar;
    private int showType;
    private List<RiskBean> riskBeanList;

    public static RiskProgressListFragment newInstance(String id, int showType) {
        Bundle args = new Bundle();
        args.putString("id", id);
        args.putInt("showType", showType);
        RiskProgressListFragment fragment = new RiskProgressListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_risk_progress_list;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        setOnClickListener(onClickListener, R.id.back, R.id.btn);

        id = getArguments().getString("id");
        showType = getArguments().getInt("showType");

        toolbar = findViewById(R.id.toolbar);
        ImageView back = findViewById(R.id.back);
        back.setVisibility(View.VISIBLE);
        back.setColorFilter(getResources().getColor(R.color.common_color_text));

        btn = findViewById(R.id.btn);

        TextView title = findViewById(R.id.title);
        title.setText(R.string.string_risk_progress_title);
        title.setTextColor(getResources().getColor(R.color.common_color_text));

        riskProgressRv = findViewById(R.id.risk_progress_rv);
        riskProgressRv.initAdapter(R.layout.item_risk_progress_list_layout, riskCallBack)
                .setLayoutManager()
                .setDecoration();

        getRiskProgressList();
    }

    @org.greenrobot.eventbus.Subscribe(sticky = true, threadMode = org.greenrobot.eventbus.ThreadMode.MAIN)
    public void onPushRefresh(JPushEvent event) {
        getRiskProgressList();
    }

    /**
     * 隐患能否进行操作
     */
    private void getRiskCanProgress() {
        NetUtils.getNewInstance(_mActivity)
                .request(
                        new NetUtils.NetRequestCallBack<ResultCanRiskProcessBean>() {
                            @Override
                            public void success(String action, ResultCanRiskProcessBean resultCanRiskProcessBean, Object tag) {
                                if (resultCanRiskProcessBean.isSuccessful() && resultCanRiskProcessBean.getData() == 1) {
                                    btn.setText(R.string.common_string_operation);
                                    btn.setTextColor(getResources().getColor(R.color.common_color_text));
                                    btn.setVisibility(View.VISIBLE);
                                    getRiskCanProcessStateList();
                                } else {
                                    btn.setVisibility(View.GONE);
                                }
                            }

                            @Override
                            public void error(String action, Throwable e, Object tag) {
                                e.printStackTrace();
                            }
                        }
                        , false
                        , new ApiRequest<>(NetBean.actionGetRiskCanProcess, ResultCanRiskProcessBean.class)
                                .setRequestParams("hiddenId", id));
    }


    /**
     * 获取隐患可以操作的列表
     */
    private void getRiskCanProcessStateList() {
        ApiRequest<ResultProcessStateListBean> apiRequest = new ApiRequest<>(NetBean.actionGetCanProcessStateList, ResultProcessStateListBean.class);
        if (TextUtils.isEmpty(processState)) {
            if (CommonUtils.judgeListNull(riskBeanList) > 0) {
                processState = riskBeanList.get(0).getProcessResult();
                if (TextUtils.isEmpty(processState)) {
                    return;
                }
            } else return;
        }
        apiRequest.setRequestParams("code", processState);
        NetUtils.getNewInstance(_mActivity)
                .request(
                        new NetUtils.NetRequestCallBack<ResultProcessStateListBean>() {
                            @Override
                            public void success(String action, ResultProcessStateListBean resultProcessStateListBean, Object tag) {
                                if (resultProcessStateListBean.isSuccessful()) {
                                    processStateList = resultProcessStateListBean.getData();
                                    riskProcessWindows = CustomPopupWindows.getInstance(_mActivity
                                            , btn
                                            , R.layout.popup_list_wrap_content_layout
                                            , new PopupParam(PopupParam.RELATIVE_SCREEN, PopupParam.SHOW_POSITION_RIGHT, PopupParam.SHOW_POSITION_TOP)
                                                    .setWindowWidth(false)
                                                    .setMarginTop(toolbar.getHeight()));
                                }
                            }

                            @Override
                            public void error(String action, Throwable e, Object tag) {

                            }
                        }
                        , false
                        , apiRequest);
    }

    /**
     * 获取隐患整改列表
     */
    private void getRiskProgressList() {
        //noinspection unchecked
        netWork().setRefreshListener(R.id.refresh_layout, false, true, new NetRequestListener<ResultRiskListBean>() {
            @Override
            public void success(String action, ResultRiskListBean baseBean, Object tag) {
                if (baseBean.isSuccessful()) {
                    riskBeanList = baseBean.getData();
                    int dataSize = CommonUtils.judgeListNull(riskBeanList);
                    if (dataSize > 0) {
                        processState = riskBeanList.get(0).getProcessResult();
                        if (showType != RiskShowType.SHOW_TYPE_REPORT)
                            getRiskCanProgress();
                    }
                    riskProgressRv.setData(riskBeanList);
                }
            }
        }, netWork().apiRequest(NetBean.actionGetRiskProgressList, ResultRiskListBean.class, ApiRequest.REQUEST_TYPE_GET, R.id.load_layout)
                .setRequestParams("id", id));
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onRiskProcess(RiskEvent event) {
        getRiskProgressList();
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
        if (null != riskProcessWindows && riskProcessWindows.isShowing()) {
            riskProcessWindows.dismiss();
        }
    }

    private ConvertViewCallBack<RiskBean> riskCallBack = new ConvertViewCallBack<RiskBean>() {
        @Override
        public void convert(RecyclerViewHolder holder, final RiskBean riskBean, int position, int layoutTag) {
            holder.setText(R.id.risk_progress_process_user, riskBean.getProcessUserName())
                    .setText(R.id.risk_progress_process_time, riskBean.getCreateTime())
                    .setText(R.id.risk_progress_process_description, riskBean.getDescription())
                    .setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            start(RiskProcessDetailFragment.newInstance(riskBean.getId()));
                        }
                    });
            setStateImg(holder, riskBean.getProcessResult());
        }

        @Override
        public void loadingFinished() {

        }

        private void setStateImg(RecyclerViewHolder holder, String processState) {
            int processImgRes;
            switch (processState) {
                case "0":
                    processImgRes = R.mipmap.ic_risk_progress_state_no_rectification;
                    break;

                case "1":
                    processImgRes = R.mipmap.ic_risk_progress_state_rectification;
                    break;

                case "2":
                    processImgRes = R.mipmap.ic_risk_progress_state_pending_trial;
                    break;

                case "3":
                    processImgRes = R.mipmap.ic_risk_progress_state_qualified;
                    break;

                case "4":
                    processImgRes = R.mipmap.ic_risk_progress_state_unqualified;
                    break;

                default:
                    processImgRes = R.mipmap.ic_risk_progress_state_no_rectification;
            }
            holder.setImageRes(R.id.iv_risk_progress_state, processImgRes);
        }
    };

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.back:
                    finish();
                    break;

                case R.id.btn:
                    if (null == riskProcessWindows) {
                        return;
                    }
                    riskProcessWindows.showPopu(new CustomPopupWindows.InitCustomPopupListener() {
                        @Override
                        public void initPopup(CustomPopupWindows.PopupHolder holder) {
                            BswRecyclerView<ProcessStateBean> popupRv = holder.getView(R.id.popup_rv);
                            popupRv.initAdapter(R.layout.item_popup_text_layout, new ConvertViewCallBack<ProcessStateBean>() {
                                @Override
                                public void convert(RecyclerViewHolder holder, final ProcessStateBean processStateBean, final int position, int layoutTag) {
                                    holder.setText(R.id.tv_popup_text, processStateBean.getName())
                                            .setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    riskProcessWindows.dismiss();
                                                    start(RiskProcessReportFragment.newInstance(id, processStateBean.getCode(), processStateBean.getName()));
                                                }
                                            });
                                }

                                @Override
                                public void loadingFinished() {

                                }
                            }).setLayoutManager()
                                    .setDecoration()
                                    .setData(processStateList);
                        }
                    });
                    break;
            }
        }
    };
}
