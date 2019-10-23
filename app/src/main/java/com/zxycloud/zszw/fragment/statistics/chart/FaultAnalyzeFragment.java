package com.zxycloud.zszw.fragment.statistics.chart;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
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
import com.zxycloud.zszw.model.ResultStatisticsFaultDeviceListBean;
import com.zxycloud.zszw.model.ResultStatisticsFaultListBean;
import com.zxycloud.zszw.model.bean.StatisticsFaultBean;
import com.zxycloud.zszw.model.bean.StatisticsFaultDeviceBean;
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
public class FaultAnalyzeFragment extends ChartUpDataFragment implements MyBaseAdapter.OnBindViewHolderListener {
    private BarChart faultChart;
    private MyBaseAdapter myBaseAdapter;
    private SparseArray<String> sparseArray;

    private List<StatisticsFaultBean> faultBeans;
    private List<StatisticsFaultDeviceBean> faultDeviceBean;

    public static FaultAnalyzeFragment newInstance() {
        return new FaultAnalyzeFragment();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.statistics_barchart;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        faultChart = findViewById(R.id.fault_chart);
        RecyclerView recyclerView = findViewById(R.id.recycler_fire);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        myBaseAdapter = new MyBaseAdapter(getContext(), R.layout.base_item, this);
        recyclerView.setAdapter(myBaseAdapter);

        initData();
    }

    @Override
    protected void initData() {
        sparseArray = CommonUtils.string().formatStringLength(getContext()
                , R.string.title_device_code
                , R.string.title_place_name
                , R.string.title_installation
                , R.string.title_alarm_time);
        getProject(new OnProjectObtainListener() {
            @Override
            public void success(String projectId, String projectName) {
                netWork().setRefreshListener(R.id.refresh_layout, true, true, new NetRequestListener() {
                            @Override
                            public void success(String action, BaseBean baseBean, Object tag) {
                                if (baseBean.isSuccessful())
                                    switch (action) {
                                        case NetBean.actionFaultInfo:
                                            faultBeans = ((ResultStatisticsFaultListBean) baseBean).getData();
                                            initChart(faultChart);
                                            setReviewData();
                                            break;
                                        case NetBean.actionFaultDeviceListInfo:
                                            if (tag == null || (int) tag == 1)
                                                faultDeviceBean = ((ResultStatisticsFaultDeviceListBean) baseBean).getData();
                                            else
                                                faultDeviceBean.addAll(faultDeviceBean.size(), ((ResultStatisticsFaultDeviceListBean) baseBean).getData());
                                            myBaseAdapter.setData(faultDeviceBean);
                                            break;
                                    }
                                 else
                                    CommonUtils.toast(_mActivity, baseBean.getMessage());
                            }
                        }, netWork().apiRequest(NetBean.actionFaultInfo, ResultStatisticsFaultListBean.class, ApiRequest.REQUEST_TYPE_GET)
                                .setApiType(ApiRequest.API_TYPE_STATISTICS)
                                .setRequestParams("statisticsCount", 6)
                                .setRequestParams("projectId", projectId)
                        , netWork().apiRequest(NetBean.actionFaultDeviceListInfo, ResultStatisticsFaultDeviceListBean.class, ApiRequest.REQUEST_TYPE_POST, R.id.loading)
                                .setApiType(ApiRequest.API_TYPE_STATISTICS)
                                .setRequestParams("pageIndex", 1)
                                .setRequestParams("pageSize", 10)
                                .setRequestParams("statisticsCount", 6)
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

        StatisticsMarkerView mv = new StatisticsMarkerView(getContext(), R.layout.dialog_chart_layout, null, callback);
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
                if (toInt >= faultBeans.size())
                    return "";
                if (toInt == value)
                    return faultBeans.get(toInt).getYearMonthTime();
                else
                    return "";
            }
        });

        Legend l = chart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
    }

    private void setReviewData() {
        ArrayList<BarEntry> values = new ArrayList<>();

        for (int i = 0; i < faultBeans.size(); i++) {
            StatisticsFaultBean bean = faultBeans.get(i);
            int faultNumber = bean.getFaultNumber();
            values.add(new BarEntry(
                    i,
                    faultNumber));
        }

        BarDataSet set1 = new BarDataSet(values, "");
        set1.setDrawIcons(false);
        set1.setColor(getResources().getColor(R.color.color_state_fault));
        set1.setLabel(getResources().getString(R.string.device_fault_count));
        set1.setDrawValues(false);

        ArrayList<IBarDataSet> dataSets = new ArrayList<>();
        dataSets.add(set1);

        BarData data = new BarData(dataSets);
        data.setValueFormatter(new StackedValueFormatter(false, "", 1));
        data.setValueTextColor(Color.BLACK);

        faultChart.setData(data);

        faultChart.setFitBars(true);
        faultChart.invalidate();
    }
    @Override
    public void onBindViewHolder(int position, View view, RecyclerViewHolder holder) {
        StatisticsFaultDeviceBean bean = faultDeviceBean.get(position);
        ((SwipeItemLayout)holder.getView(R.id.sil_drag)).setSwipeEnable(false);
        holder.setText(R.id.item_title, bean.getDeviceTypeName());
        holder.setText(R.id.item_1, sparseArray.get(R.string.title_device_code).concat(CommonUtils.string().getString(bean.getAdapterName())));
        holder.setText(R.id.item_2, sparseArray.get(R.string.title_place_name).concat(CommonUtils.string().getString(bean.getPlaceName())));
        holder.setText(R.id.item_3, sparseArray.get(R.string.title_alarm_time).concat(CommonUtils.date().format(bean.getCreateTime())));
    }

    private StatisticsMarkerView.MarkerFormatCallback callback = new StatisticsMarkerView.MarkerFormatCallback() {
        @Override
        public void markerFormat(int x, Entry entry, TextView tvContent, Object tag) {
            StatisticsFaultBean faultBean = faultBeans.get(x);
            tvContent.setText(faultBean.getYearMonthTime().concat("\n").concat(CommonUtils.string().getString(_mActivity, R.string.title_number_of_defect)).concat(String.valueOf(faultBean.getFaultNumber())));
        }
    };
}
