package com.zxycloud.zszw.fragment.statistics.chart;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.StackedValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.zxycloud.zszw.R;
import com.zxycloud.zszw.fragment.statistics.ChartUpDataFragment;
import com.zxycloud.zszw.base.MyBaseAdapter;
import com.zxycloud.zszw.listener.NetRequestListener;
import com.zxycloud.zszw.listener.OnProjectObtainListener;
import com.zxycloud.zszw.model.ResultStatisticsAlarmFalseListBean;
import com.zxycloud.zszw.model.bean.StatisticsAlarmFalseBean;
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
public class AlarmFalseAnalyzeFragment extends ChartUpDataFragment implements MyBaseAdapter.OnBindViewHolderListener {
    private BarChart falseChart;
    private MyBaseAdapter myBaseAdapter;

    private List<StatisticsAlarmFalseBean> alarmFalseBean;

    public static AlarmFalseAnalyzeFragment newInstance() {
        return new AlarmFalseAnalyzeFragment();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.statistics_barchart;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        falseChart = findViewById(R.id.fault_chart);
        RecyclerView recyclerView = findViewById(R.id.recycler_fire);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        myBaseAdapter = new MyBaseAdapter(getContext(), R.layout.base_item, this);
        recyclerView.setAdapter(myBaseAdapter);

        initData();
    }

    @Override
    protected void initData() {
        getProject(new OnProjectObtainListener() {
            @Override
            public void success(String projectId, String projectName) {
                netWork().setRefreshListener(R.id.refresh_layout, true, false, new NetRequestListener<ResultStatisticsAlarmFalseListBean>() {
                    @Override
                    public void success(String action, ResultStatisticsAlarmFalseListBean baseBean, Object tag) {
                        if (baseBean.isSuccessful()) {
                            alarmFalseBean = baseBean.getData();
                            initChart(falseChart);
                            setFalseData();
                            myBaseAdapter.setData(alarmFalseBean);
                        } else {
                            CommonUtils.toast(getContext(), baseBean.getMessage());
                        }
                    }
                }, netWork().apiRequest(NetBean.actionAlarmFalseInfo, ResultStatisticsAlarmFalseListBean.class, ApiRequest.REQUEST_TYPE_GET, R.id.loading)
                        .setApiType(ApiRequest.API_TYPE_STATISTICS)
                        .setRequestParams("days", 30)
                        .setRequestParams("projectId", projectId));
            }
        });
    }

    private void initChart(BarChart chart) {
        chart.getDescription().setEnabled(false);

        chart.setDragDecelerationFrictionCoef(0.95f);

        chart.animateY(1400, Easing.EaseInOutQuad);

        chart.setPinchZoom(false);
        chart.setScaleEnabled(false);

        chart.setDrawGridBackground(false);
        chart.setDrawBarShadow(false);

        chart.setDrawValueAboveBar(false);
        chart.setHighlightFullBarEnabled(false);

        StatisticsMarkerView mv = new StatisticsMarkerView(_mActivity, R.layout.dialog_chart_layout, null, callback);
        // Set the marker to the chart
        mv.setChartView(chart);
        chart.setMarker(mv);

        YAxis leftAxis = chart.getAxisLeft();
        leftAxis.setAxisMinimum(0f); // this replaces setStartAtZero(true)
        chart.getAxisRight().setEnabled(false);

        XAxis xLabels = chart.getXAxis();
        xLabels.setPosition(XAxis.XAxisPosition.BOTTOM);
        xLabels.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                /*
                 * 设置横坐标显示样式
                 * 将获取到的value四舍五入，如果直接强转会导致日期重复，其他情况需要具体问题具体分析
                 */
                int toInt = (int) Math.rint(value);
                if (toInt >= alarmFalseBean.size()) {
                    return "";
                }
                if (toInt == value) {
                    return alarmFalseBean.get(toInt).getTime();
                } else {
                    return "";
                }
            }
        });

        Legend l = chart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
    }

    private void setFalseData() {
        ArrayList<BarEntry> values = new ArrayList<>();

        for (int i = 0; i < alarmFalseBean.size(); i++) {
            StatisticsAlarmFalseBean bean = alarmFalseBean.get(i);
            int falseNumber = bean.getNumber();
            values.add(new BarEntry(i, falseNumber));
        }

        BarDataSet set1 = new BarDataSet(values, "");
        set1.setDrawIcons(false);
        set1.setColor(getResources().getColor(R.color.color_state_fault));
        set1.setLabel(getResources().getString(R.string.device_misinformation_count));
        set1.setDrawValues(false);

        ArrayList<IBarDataSet> dataSets = new ArrayList<>();
        dataSets.add(set1);

        BarData data = new BarData(dataSets);
        data.setValueFormatter(new StackedValueFormatter(false, "", 1));
        data.setValueTextColor(Color.BLACK);

        falseChart.setData(data);

        falseChart.setFitBars(true);
        falseChart.invalidate();
    }

    @Override
    public void onBindViewHolder(int position, View view, RecyclerViewHolder holder) {
        StatisticsAlarmFalseBean bean = alarmFalseBean.get(position);
        ((SwipeItemLayout) holder.getView(R.id.sil_drag)).setSwipeEnable(false);
        holder.setText(R.id.item_title, bean.getTime());
        holder.setText(R.id.item_1, CommonUtils.string().getString(getContext(), R.string.title_alarm_false_count).concat(CommonUtils.string().getString(bean.getNumber())));
    }

    private StatisticsMarkerView.MarkerFormatCallback callback = new StatisticsMarkerView.MarkerFormatCallback() {
        @Override
        public void markerFormat(int x, Entry entry, TextView tvContent, Object tag) {
            StatisticsAlarmFalseBean falseBean = alarmFalseBean.get(x);
            tvContent.setText(falseBean.getTime().concat("\n").
                    concat(CommonUtils.string().getString(getContext(), R.string.string_alarm_false_count)).concat(falseBean.getNumber() + ""));
        }
    };
}
