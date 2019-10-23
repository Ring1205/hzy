package com.zxycloud.zszw.fragment.statistics.chart;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import com.zxycloud.zszw.base.MyBaseAdapter;
import com.zxycloud.zszw.event.type.ShowLoadType;
import com.zxycloud.zszw.fragment.statistics.ChartUpDataFragment;
import com.zxycloud.zszw.listener.NetRequestListener;
import com.zxycloud.zszw.listener.OnProjectObtainListener;
import com.zxycloud.zszw.model.ResultStatisticsDeviceTypeAlarmListBean;
import com.zxycloud.zszw.model.bean.StatisticsDeviceTypeAlarmBean;
import com.zxycloud.zszw.widget.StatisticsMarkerView;
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
public class DeviceTypeAlarmAnalyzeFragment extends ChartUpDataFragment implements MyBaseAdapter.OnBindViewHolderListener {
    private PieChart deviceTypeChart;
    private MyBaseAdapter myBaseAdapter;

    private List<StatisticsDeviceTypeAlarmBean> deviceTypeAlarmBeans;

    public static DeviceTypeAlarmAnalyzeFragment newInstance() {
        return new DeviceTypeAlarmAnalyzeFragment();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.statistics_piechart;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        deviceTypeChart = findViewById(R.id.fire_state_chart);
        RecyclerView recyclerView = findViewById(R.id.recycler_fire);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        myBaseAdapter = new MyBaseAdapter(getContext(), R.layout.base_item, this);
        recyclerView.setAdapter(myBaseAdapter);

        initChart(deviceTypeChart);

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
        getProject(new OnProjectObtainListener() {
            @Override
            public void success(String projectId, String projectName) {
                netWork().setRefreshListener(R.id.refresh_layout, true, false, new NetRequestListener<ResultStatisticsDeviceTypeAlarmListBean>() {
                    @Override
                    public void success(String action, ResultStatisticsDeviceTypeAlarmListBean baseBean, Object tag) {
                        if (baseBean.isSuccessful()) {
                            if (baseBean.getData().isEmpty()) {
                                netWork().showLoading(ShowLoadType.SHOW_EMPTY);
                            } else {
                                deviceTypeAlarmBeans = baseBean.getData();
                                setReviewData();
                                myBaseAdapter.setData(deviceTypeAlarmBeans);
                            }
                        } else {
                            CommonUtils.toast(getContext(), baseBean.getMessage());
                        }
                    }
                }, netWork().apiRequest(NetBean.actionAlarmByDeviceType, ResultStatisticsDeviceTypeAlarmListBean.class, ApiRequest.REQUEST_TYPE_GET, R.id.loading)
                        .setApiType(ApiRequest.API_TYPE_STATISTICS)
                        .setRequestParams("projectId", projectId));
            }
        });

    }

    private void setReviewData() {
        ArrayList<PieEntry> entries = new ArrayList<>();
        // NOTE: The order of the entries when being added to the entries array determines their position around the center of
        // the chart.
        for (int i = 0; i < deviceTypeAlarmBeans.size(); i++) {
            StatisticsDeviceTypeAlarmBean alarmBean = deviceTypeAlarmBeans.get(i);
            entries.add(new PieEntry(alarmBean.getNumber(), alarmBean.getDeviceTypeName(), i));
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
        data.setValueFormatter(new PercentFormatter(deviceTypeChart));
        data.setValueTextSize(11f);
        data.setValueTextColor(Color.WHITE);
        data.setDrawValues(false);

        deviceTypeChart.setData(data);

        // undo all highlights
        deviceTypeChart.highlightValues(null);

        deviceTypeChart.invalidate();
    }

    @Override
    public void onBindViewHolder(int position, View view, RecyclerViewHolder holder) {
        StatisticsDeviceTypeAlarmBean bean = deviceTypeAlarmBeans.get(position);
        ((SwipeItemLayout) holder.getView(R.id.sil_drag)).setSwipeEnable(false);
        holder.setText(R.id.item_title, bean.getDeviceTypeName());
        holder.setText(R.id.item_1, CommonUtils.string().getString(getContext(), R.string.title_fire_count).concat(String.valueOf(bean.getNumber())));
    }

    private StatisticsMarkerView.MarkerFormatCallback callback = new StatisticsMarkerView.MarkerFormatCallback() {
        @Override
        public void markerFormat(int x, Entry entry, TextView tvContent, Object tag) {
            StatisticsDeviceTypeAlarmBean alarmBean = deviceTypeAlarmBeans.get((Integer) entry.getTag());
            tvContent.setText(alarmBean.getDeviceTypeName().concat(": ").concat(alarmBean.getNumber() + ""));
        }
    };

}
