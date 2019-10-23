package com.zxycloud.zszw.fragment.statistics.chart;

import android.graphics.Color;
import android.os.Bundle;
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
import com.zxycloud.zszw.model.ResultStatisticsOnlineAdapterListBean;
import com.zxycloud.zszw.model.ResultStatisticsOnlineBean;
import com.zxycloud.zszw.model.bean.StatisticsOnlineAdapterBean;
import com.zxycloud.zszw.model.bean.StatisticsOnlineBean;
import com.zxycloud.zszw.widget.StatisticsMarkerView;
import com.zxycloud.common.base.BaseBean;
import com.zxycloud.common.utils.CommonUtils;
import com.zxycloud.common.utils.netWork.ApiRequest;
import com.zxycloud.common.utils.netWork.NetBean;
import com.zxycloud.common.widget.BswRecyclerView.RecyclerViewHolder;
import com.zxycloud.common.widget.BswRecyclerView.SwipeItemLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * @author leiming
 * @date 2019/4/20.
 */
public class OnlineAnalyzeFragment extends ChartUpDataFragment implements MyBaseAdapter.OnBindViewHolderListener {
    private PieChart onlineChart;
    private MyBaseAdapter myBaseAdapter;

    private StatisticsOnlineBean onlineBean;
    private List<StatisticsOnlineAdapterBean> onlineAdapterBeans;
    private String[] onlineState;
    private SparseArray<String> sparseArray;

    public static OnlineAnalyzeFragment newInstance() {
        return new OnlineAnalyzeFragment();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.statistics_piechart;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        onlineChart = findViewById(R.id.fire_state_chart);
        RecyclerView recyclerView = findViewById(R.id.recycler_fire);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        myBaseAdapter = new MyBaseAdapter(getContext(), R.layout.base_item, this);
        recyclerView.setAdapter(myBaseAdapter);

        onlineState = getResources().getStringArray(R.array.online_array);

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
        sparseArray = CommonUtils.string().formatStringLength(_mActivity
                , R.string.title_device_code
                , R.string.title_online_state
                , R.string.title_last_connect_time);
        getProject(new OnProjectObtainListener() {
            @Override
            public void success(String projectId, String projectName) {
                netWork().setRefreshListener(R.id.refresh_layout, true, true, new NetRequestListener() {
                            @Override
                            public void success(String action, BaseBean baseBean, Object tag) {
                                if (baseBean.isSuccessful()) {
                                    switch (action) {
                                        case NetBean.actionDeviceOnlineInfo:
                                            onlineBean = ((ResultStatisticsOnlineBean) baseBean).getData();
                                            initChart(onlineChart);
                                            setReviewData(onlineBean);
                                            break;

                                        case NetBean.actionOnlineAdapterList:
                                            if (tag == null || (int) tag == 1)
                                                onlineAdapterBeans = ((ResultStatisticsOnlineAdapterListBean) baseBean).getData();
                                            else
                                                onlineAdapterBeans.addAll(onlineAdapterBeans.size(), ((ResultStatisticsOnlineAdapterListBean) baseBean).getData());
                                            myBaseAdapter.setData(onlineAdapterBeans);
                                            break;
                                    }
                                } else {
                                    CommonUtils.toast(getContext(), baseBean.getMessage());
                                }
                            }
                        }, netWork().apiRequest(NetBean.actionDeviceOnlineInfo, ResultStatisticsOnlineBean.class, ApiRequest.REQUEST_TYPE_GET)
                                .setApiType(ApiRequest.API_TYPE_STATISTICS)
                                .setRequestParams("projectId", projectId)
                        , netWork().apiRequest(NetBean.actionOnlineAdapterList, ResultStatisticsOnlineAdapterListBean.class, ApiRequest.REQUEST_TYPE_POST, R.id.loading)
                                .setApiType(ApiRequest.API_TYPE_STATISTICS)
                                .setRequestParams("pageIndex", 1)
                                .setRequestParams("pageSize", 10)
                                .setRequestParams("projectId", projectId));
            }
        });
    }

    private void setReviewData(StatisticsOnlineBean onlineBean) {
        ArrayList<PieEntry> entries = new ArrayList<>();
        // NOTE: The order of the entries when being added to the entries array determines their position around the center of
        // the chart.
        for (int i = 0; i < 2; i++) {
            switch (i) {
                case 0:
                    entries.add((PieEntry) new PieEntry(onlineBean.getOnLineCount(),
                            onlineState[i]).setTag(i));
                    break;

                case 1:
                    entries.add((PieEntry) new PieEntry(onlineBean.getOffLineCount(),
                            onlineState[i]).setTag(i));
                    break;
            }
        }

        PieDataSet dataSet = new PieDataSet(entries, "");

        dataSet.setDrawIcons(false);

        dataSet.setSliceSpace(0f);
        dataSet.setSelectionShift(5f);

        // add a lot of colors

        ArrayList<Integer> colors = new ArrayList<>();

        colors.add(getResources().getColor(R.color.color_state_normal));
        colors.add(getResources().getColor(R.color.color_state_offline));

        dataSet.setColors(colors);

        PieData data = new PieData(dataSet);
        data.setValueFormatter(new PercentFormatter(onlineChart));
        data.setValueTextSize(11f);
        data.setValueTextColor(Color.WHITE);
        data.setDrawValues(false);
        onlineChart.setData(data);

        // undo all highlights
        onlineChart.highlightValues(null);

        onlineChart.invalidate();
    }

    @Override
    public void onBindViewHolder(int position, View view, RecyclerViewHolder holder) {
        StatisticsOnlineAdapterBean bean = onlineAdapterBeans.get(position);
        ((SwipeItemLayout) holder.getView(R.id.sil_drag)).setSwipeEnable(false);
        holder.setText(R.id.item_title, bean.getUserDeviceTypeName());
        holder.setText(R.id.item_1, sparseArray.get(R.string.title_device_code).concat(bean.getAdapterName()));
        holder.setText(R.id.item_2,sparseArray.get(R.string.title_online_state).concat(bean.getConnectStatus() == 1 ? OnlineAnalyzeFragment.this.onlineState[0] : OnlineAnalyzeFragment.this.onlineState[1]));
        holder.setText(R.id.item_3,(sparseArray.get(R.string.title_last_connect_time)).concat(CommonUtils.date().format(bean.getLastConnectTime())));
    }

    private StatisticsMarkerView.MarkerFormatCallback callback = new StatisticsMarkerView.MarkerFormatCallback() {
        @Override
        public void markerFormat(int x, Entry entry, TextView tvContent, Object tag) {
            int entryTag = (int) entry.getTag();
            tvContent.setText(onlineState[entryTag].concat(entryTag == 0 ? onlineBean.getOnLineCount() + "" : onlineBean.getOffLineCount() + ""));
        }
    };

}
