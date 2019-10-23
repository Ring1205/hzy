package com.zxycloud.zszw.fragment.statistics.chart;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.View;
import android.widget.TextView;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.zxycloud.zszw.R;
import com.zxycloud.zszw.fragment.statistics.ChartUpDataFragment;
import com.zxycloud.zszw.base.MyBaseAdapter;
import com.zxycloud.zszw.listener.NetRequestListener;
import com.zxycloud.zszw.listener.OnProjectObtainListener;
import com.zxycloud.zszw.model.bean.FireReviewAnalyzeBean;
import com.zxycloud.zszw.model.ResultFireReviewAnalyzeListBean;
import com.zxycloud.zszw.model.ResultStatisticsFireInfoBean;
import com.zxycloud.zszw.model.StatisticsFireInfoBean;
import com.zxycloud.zszw.widget.StatisticsMarkerView;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.zxycloud.common.base.BaseBean;
import com.zxycloud.common.utils.CommonUtils;
import com.zxycloud.common.utils.netWork.ApiRequest;
import com.zxycloud.common.utils.netWork.NetBean;
import com.zxycloud.common.widget.BswRecyclerView.RecyclerViewHolder;
import com.zxycloud.common.widget.BswRecyclerView.SwipeItemLayout;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * @author leiming
 * @date 2019/4/20.
 */
public class FireStateFragment extends ChartUpDataFragment implements MyBaseAdapter.OnBindViewHolderListener {
    private PieChart fireStateChart;
    private String[] fireReview;
    private MyBaseAdapter myBaseAdapter;
    private StatisticsFireInfoBean fireInfoBean;
    private SparseArray<String> sparseArray;
    private RefreshLayout mRefreshLayout;
    private int pageIndex = 1;

    private List<FireReviewAnalyzeBean> analyzeBeans;

    public static FireStateFragment newInstance() {
        return new FireStateFragment();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.statistics_piechart;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        fireStateChart = findViewById(R.id.fire_state_chart);
        RecyclerView recyclerView = findViewById(R.id.recycler_fire);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        myBaseAdapter = new MyBaseAdapter(getContext(), R.layout.base_item, this);
        recyclerView.setAdapter(myBaseAdapter);

        sparseArray = CommonUtils.string().formatStringLength(getContext()
                , R.string.title_gateway_name
                , R.string.title_affiliated_place
                , R.string.title_alarm_state
                , R.string.string_alert_review_result
                , R.string.string_alert_happen_time);

        initChart(fireStateChart);

        mRefreshLayout = findViewById(R.id.refresh_layout);
        mRefreshLayout.setEnableLoadMore(true);
        mRefreshLayout.setEnableRefresh(true);
        mRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                mRefreshLayout.finishRefresh(1200, true);
                pageIndex = 1;
                getData();
            }
        });
        mRefreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                mRefreshLayout.finishLoadMore(800);
                pageIndex++;
                getData();
            }
        });

        initData();
    }

    private void initChart(PieChart chart) {
        chart.setUsePercentValues(true);
        chart.getDescription().setEnabled(false);

        chart.setDragDecelerationFrictionCoef(0.95f);

        chart.setDrawHoleEnabled(false);
        chart.setUsePercentValues(false);

        chart.setRotationAngle(0);
        // enable rotation of the chart by touch
        chart.setRotationEnabled(false);
        chart.setHighlightPerTapEnabled(true);
        chart.setDrawEntryLabels(false);

        chart.animateY(1400, Easing.EaseInOutQuad);

        StatisticsMarkerView mv = new StatisticsMarkerView(_mActivity, R.layout.dialog_chart_layout, null, callback);

        // Set the marker to the chart
        mv.setChartView(chart);
        chart.setMarker(mv);

        Legend l = chart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);

        // entry label styling
        chart.setEntryLabelColor(Color.WHITE);
        chart.setEntryLabelTextSize(12f);
    }

    @Override
    protected void initData() {
        pageIndex = 1;
        getData();
    }

    private void getData() {
        getProject(new OnProjectObtainListener() {
            @Override
            public void success(String projectId, String projectName) {
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(System.currentTimeMillis());
                calendar.add(Calendar.MONTH, -1);

                netWork().setRequestListener(
                        new NetRequestListener() {
                            @Override
                            public void success(String action, BaseBean baseBean, Object tag) {
                                if (baseBean.isSuccessful())
                                    switch (action) {
                                        case NetBean.actionFireInfo:
                                            fireInfoBean = ((ResultStatisticsFireInfoBean) baseBean).getData();
                                            setReviewData(fireInfoBean);
                                            break;

                                        case NetBean.actionFireList:
                                            if (pageIndex == 1)
                                                analyzeBeans = ((ResultFireReviewAnalyzeListBean) baseBean).getData();
                                            else
                                                analyzeBeans.addAll(analyzeBeans.size(), ((ResultFireReviewAnalyzeListBean) baseBean).getData());

                                            myBaseAdapter.setData(analyzeBeans);
                                            if (baseBean.getTotalPages() <= pageIndex)
                                                mRefreshLayout.finishLoadMoreWithNoMoreData();
                                            else
                                                mRefreshLayout.resetNoMoreData();// 开启上拉手势
                                            break;
                                    }
                                else
                                    CommonUtils.toast(getContext(), baseBean.getMessage());
                            }
                        }, netWork().apiRequest(NetBean.actionFireInfo, ResultStatisticsFireInfoBean.class, ApiRequest.REQUEST_TYPE_GET)
                                .setApiType(ApiRequest.API_TYPE_STATISTICS)
                                .setRequestParams("startTime", calendar.getTime().getTime())
                                .setRequestParams("endTime", System.currentTimeMillis())
                                .setRequestParams("projectId", projectId)
                        , netWork().apiRequest(NetBean.actionFireList, ResultFireReviewAnalyzeListBean.class, ApiRequest.REQUEST_TYPE_POST, R.id.loading)
                                .setApiType(ApiRequest.API_TYPE_STATISTICS)
                                .setRequestParams("pageIndex", pageIndex)
                                .setRequestParams("pageSize", 10)
                                .setRequestParams("startTime", calendar.getTime().getTime())
                                .setRequestParams("endTime", System.currentTimeMillis())
                                .setRequestParams("projectId", projectId));
            }
        });
    }

    private void setReviewData(StatisticsFireInfoBean fireInfoBean) {
        ArrayList<PieEntry> entries = new ArrayList<>();
        fireReview = getResources().getStringArray(R.array.fire_review_array);
        // NOTE: The order of the entries when being added to the entries array determines their position around the center of
        // the chart.
        for (int i = 0; i < fireReview.length; i++) {
            switch (i) {
                case 0:
                    entries.add((PieEntry) new PieEntry(fireInfoBean.getTrueAlarmCount(), fireReview[i]).setTag(i));
                    break;
                case 1:
                    entries.add((PieEntry) new PieEntry(fireInfoBean.getFalseAlarmCount(), fireReview[i]).setTag(i));
                    break;
                case 2:
                    entries.add((PieEntry) new PieEntry(fireInfoBean.getTestAlarmCount(), fireReview[i]).setTag(i));
                    break;
                case 3:
                    entries.add((PieEntry) new PieEntry(fireInfoBean.getNoProcessAlarmCount(), fireReview[i]).setTag(i));
                    break;
            }
        }

        PieDataSet dataSet = new PieDataSet(entries, "");
        dataSet.setDrawIcons(false);
        dataSet.setSliceSpace(0f);
        dataSet.setSelectionShift(5f);

        // add a lot of colors

        ArrayList<Integer> colors = new ArrayList<>();

        colors.add(getResources().getColor(R.color.color_state_fire));
        colors.add(getResources().getColor(R.color.color_state_fault));
        colors.add(getResources().getColor(R.color.color_state_event));
        colors.add(getResources().getColor(R.color.color_state_offline));

        dataSet.setColors(colors);

        PieData data = new PieData(dataSet);
        data.setValueFormatter(new PercentFormatter(fireStateChart));
        data.setValueTextSize(11f);
        data.setValueTextColor(Color.WHITE);
        data.setDrawValues(false);

        fireStateChart.setData(data);

        // undo all highlights
        fireStateChart.highlightValues(null);

        fireStateChart.invalidate();
    }

    @Override
    public void onBindViewHolder(int position, View view, RecyclerViewHolder holder) {
        FireReviewAnalyzeBean bean = analyzeBeans.get(position);
        ((SwipeItemLayout) holder.getView(R.id.sil_drag)).setSwipeEnable(false);
        holder.setText(R.id.item_title, bean.getDeviceName());
        holder.setText(R.id.item_1, sparseArray.get(R.string.title_gateway_name).concat(CommonUtils.string().getString(bean.getAdapterName())));
        holder.setText(R.id.item_2, sparseArray.get(R.string.title_affiliated_place).concat(CommonUtils.string().getString(bean.getPlaceName())));
        holder.setText(R.id.item_3, sparseArray.get(R.string.title_alarm_state).concat(CommonUtils.string().getString(bean.getDeviceStateGroupName())));
        holder.setText(R.id.item_4, sparseArray.get(R.string.string_alert_review_result).concat(CommonUtils.string().getString(bean.getProcessName())));
        holder.setText(R.id.item_5, sparseArray.get(R.string.string_alert_happen_time).concat(CommonUtils.date().format(bean.getReceiveTime())));
    }

    private StatisticsMarkerView.MarkerFormatCallback callback = new StatisticsMarkerView.MarkerFormatCallback() {
        @Override
        public void markerFormat(int x, Entry entry, TextView tvContent, Object tag) {
            int reviewCount = 0;
            Integer entryTag = (Integer) entry.getTag();
            switch (entryTag) {
                case 0:
                    reviewCount = fireInfoBean.getTrueAlarmCount();
                    break;
                case 1:
                    reviewCount = fireInfoBean.getFalseAlarmCount();
                    break;
                case 2:
                    reviewCount = fireInfoBean.getTestAlarmCount();
                    break;
                case 3:
                    reviewCount = fireInfoBean.getNoProcessAlarmCount();
                    break;
            }
            tvContent.setText(fireReview[entryTag].concat(": ".concat(reviewCount + "")));
        }
    };

}