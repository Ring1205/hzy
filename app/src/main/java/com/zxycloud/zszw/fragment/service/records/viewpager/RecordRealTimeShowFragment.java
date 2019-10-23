package com.zxycloud.zszw.fragment.service.records.viewpager;

import android.os.Bundle;
import android.support.annotation.IntDef;
import android.view.View;

import com.zxycloud.zszw.R;
import com.zxycloud.zszw.base.BaseBackFragment;
import com.zxycloud.zszw.event.type.AlertShowType;
import com.zxycloud.zszw.fragment.home.message.AlertDetailFragment;
import com.zxycloud.zszw.fragment.service.patrol.task.TaskListFragment;
import com.zxycloud.zszw.fragment.service.records.RecordStatisticsFragment;
import com.zxycloud.zszw.listener.NetRequestListener;
import com.zxycloud.zszw.model.ResultRecordListBean;
import com.zxycloud.zszw.model.bean.RecordBean;
import com.zxycloud.zszw.utils.StateTools;
import com.zxycloud.common.base.BaseBean;
import com.zxycloud.common.base.fragment.SupportFragment;
import com.zxycloud.common.utils.CommonUtils;
import com.zxycloud.common.utils.netWork.ApiRequest;
import com.zxycloud.common.utils.netWork.NetBean;
import com.zxycloud.common.widget.BswRecyclerView.BswRecyclerView;
import com.zxycloud.common.widget.BswRecyclerView.ConvertViewCallBack;
import com.zxycloud.common.widget.BswRecyclerView.RecyclerViewHolder;
import com.zxycloud.common.widget.BswRecyclerView.SwipeItemLayout;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.List;

public class RecordRealTimeShowFragment extends BaseBackFragment {
    public static final int FROM_TYPE_STATISTICS = 11;
    public static final int FROM_TYPE_SERVICE = 12;

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({FROM_TYPE_STATISTICS, FROM_TYPE_SERVICE})
    @interface FromType {
    }

    public static final int STATE_CODE_FIRE = 1;
    public static final int STATE_CODE_PREFIRE = 2;
    public static final int STATE_CODE_FAULT = 6;
    public static final int STATE_CODE_EVENT = 95;
    public static final int STATE_CODE_OFFLINE = 96;
    public static final int STATE_CODE_RISK = -2;
    public static final int STATE_CODE_ALERT = -1;

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({STATE_CODE_FIRE
            , STATE_CODE_PREFIRE
            , STATE_CODE_FAULT
            , STATE_CODE_EVENT
            , STATE_CODE_RISK
            , STATE_CODE_OFFLINE
            , STATE_CODE_ALERT})
    @interface StateCode {}

    private BswRecyclerView<RecordBean> recordRv;

    private int stateCode = STATE_CODE_FIRE;

    private int pageSize = 20;
    private int pageIndex = 1;

    public static RecordRealTimeShowFragment newInstance(@FromType int fromType, @StateCode int stateCode) {
        Bundle args = new Bundle();
        args.putInt("stateCode", stateCode);
        args.putInt("fromType", fromType);
        RecordRealTimeShowFragment fragment = new RecordRealTimeShowFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.record_recycler;
    }

    ApiRequest<ResultRecordListBean> apiRequest;

    /**
     * 获取场所列表
     */
    private void getRecordList() {
        apiRequest = netWork().apiRequest(NetBean.actionGetRealTimeRecordList, ResultRecordListBean.class, ApiRequest.REQUEST_TYPE_POST, R.id.load_layout)
                .setRequestParams("pageSize", pageSize)
                .setRequestParams("pageIndex", pageIndex);
        switch (stateCode) {
            case STATE_CODE_ALERT:
                apiRequest.setRequestParams("stateGroupCodeList", new Integer[]{STATE_CODE_FIRE, STATE_CODE_PREFIRE});
                break;
            default:
                apiRequest.setRequestParams("stateGroupCodeList", new Integer[]{stateCode});
                break;
        }

        netWork().setRefreshListener(R.id.refresh_layout, true, true, new NetRequestListener() {
            @Override
            public void success(String action, BaseBean baseBean, Object tag) {
                if (baseBean.isSuccessful()) {
                    List<RecordBean> recordBeans = ((ResultRecordListBean) baseBean).getData();
                    int currentIndex = (int) apiRequest.getRequestParams().get("pageIndex");
                    recordRv.setData(recordBeans, currentIndex, pageSize);
                }
            }
        }, apiRequest);
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        stateCode = getArguments().getInt("stateCode", STATE_CODE_FIRE);

        recordRv = findViewById(R.id.record_rv);
        recordRv.initAdapter(R.layout.base_item, recordCallBack)
                .setLayoutManager();

        getRecordList();
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

    public Integer[] getStateArray() {
        if (stateCode == STATE_CODE_ALERT)
            return new Integer[]{STATE_CODE_FIRE, STATE_CODE_PREFIRE};
         else
            return new Integer[]{stateCode};
    }

    private ConvertViewCallBack<RecordBean> recordCallBack = new ConvertViewCallBack<RecordBean>() {
        @Override
        public void convert(RecyclerViewHolder holder, final RecordBean recordBean, int position, int layoutTag) {
            ((SwipeItemLayout) holder.getView(R.id.sil_drag)).setSwipeEnable(false);
            StateTools.setStateTint(recordBean.getStateGroupCode(), holder.getImageView(R.id.item_state));
            holder.setText(R.id.item_title, recordBean.getPlaceName());
            holder.setTextWithLeftDrawables(R.id.item_1, R.mipmap.ic_item_address, recordBean.getDeviceInstallLocation());
            holder.setTextWithLeftDrawables(R.id.item_2, R.mipmap.ic_item_device_type, recordBean.getDeviceName());
            holder.setTextWithLeftDrawables(R.id.item_3, R.mipmap.ic_point_time, CommonUtils.date().format(recordBean.getReceiveTime()));
            holder.setTextWithLeftDrawables(R.id.item_4, R.mipmap.ic_item_linkage, recordBean.getPlaceAdminName().concat("(").concat(recordBean.getPlaceAdminPhoneNumber()).concat(")"));

            holder.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            SupportFragment fragment = null;
                            switch (getArguments().getInt("fromType")) {
                                case FROM_TYPE_SERVICE:
                                    fragment = (TaskListFragment) getParentFragment();
                                    break;
                                case FROM_TYPE_STATISTICS:
                                    fragment = (RecordStatisticsFragment) getParentFragment();
                                    break;
                            }
                            if (fragment != null)
                                fragment.start(AlertDetailFragment.newInstance(AlertShowType.ALERT_HISTORY,
                                        recordBean.getStateGroupCode() == RecordBean.RECORD_STATE_FIRE ? CommonUtils.STATE_CODE_FIRE : CommonUtils.STATE_CODE_PREFIRE,
                                        recordBean.getRecordId()));
                        }
                    });
        }

        @Override
        public void loadingFinished() {
        }
    };

}