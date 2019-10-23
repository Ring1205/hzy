package com.zxycloud.zszw.fragment.statistics.chart;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
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
import com.zxycloud.zszw.model.ResultStatisticsPatrolListBean;
import com.zxycloud.zszw.model.bean.StatisticsPatrolInfoBean;
import com.zxycloud.zszw.widget.StatisticsMarkerView;
import com.zxycloud.common.utils.CommonUtils;
import com.zxycloud.common.utils.SPUtils;
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
public class PatrolAnalyzeFragment extends ChartUpDataFragment implements MyBaseAdapter.OnBindViewHolderListener {
    private BarChart patrolChart;
    private MyBaseAdapter myBaseAdapter;
    private SparseArray<String> sparseArray;

    private List<StatisticsPatrolInfoBean> patrolInfoBeans;

    public static PatrolAnalyzeFragment newInstance() {
        return new PatrolAnalyzeFragment();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.statistics_barchart;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        patrolChart = findViewById(R.id.fault_chart);
        RecyclerView recyclerView = findViewById(R.id.recycler_fire);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        myBaseAdapter = new MyBaseAdapter(getContext(), R.layout.base_item, this);
        recyclerView.setAdapter(myBaseAdapter);

        initData();
    }

    @Override
    protected void initData() {
        if (TextUtils.isEmpty(CommonUtils.getSPUtils(getContext()).getString(SPUtils.PROJECT_ID)))
            return;

        sparseArray = CommonUtils.string().formatStringLength(_mActivity
                , R.string.title_patrol_count
                , R.string.title_finish_patrol_count);

        netWork().setRefreshListener(R.id.refresh_layout, true, false, new NetRequestListener<ResultStatisticsPatrolListBean>() {
            @Override
            public void success(String action, ResultStatisticsPatrolListBean baseBean, Object tag) {
                if (baseBean.isSuccessful()) {
                    patrolInfoBeans = baseBean.getData();
                    initChart(patrolChart);
                    setReviewData(patrolInfoBeans);
                    myBaseAdapter.setData(patrolInfoBeans);
                } else {
                    CommonUtils.toast(getContext(), baseBean.getMessage());
                }
            }
        }, netWork().apiRequest(NetBean.actionPatrolInfo, ResultStatisticsPatrolListBean.class, ApiRequest.REQUEST_TYPE_GET, R.id.loading)
                .setApiType(ApiRequest.API_TYPE_STATISTICS)
                .setRequestParams("projectId", CommonUtils.getSPUtils(getContext()).getString(SPUtils.PROJECT_ID)));
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
                if (toInt >= patrolInfoBeans.size())
                    return "";

                if (toInt == value)
                    return patrolInfoBeans.get(toInt).getPlaceName();
                else
                    return "";
            }
        });

        Legend l = chart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
    }

    private void setReviewData(List<StatisticsPatrolInfoBean> fireInfoBean) {
        ArrayList<BarEntry> values = new ArrayList<>();

        for (int i = 0; i < fireInfoBean.size(); i++) {
            StatisticsPatrolInfoBean bean = fireInfoBean.get(i);
            int processCount = bean.getFinishCount();
            values.add(new BarEntry(
                    i,
                    new float[]{processCount, bean.getPatrolCount() - processCount}));
        }

        BarDataSet set1 = new BarDataSet(values, "");
        set1.setDrawIcons(false);
        set1.setColors(getResources().getColor(R.color.color_state_normal), getResources().getColor(R.color.color_state_fault));
        set1.setStackLabels(getResources().getStringArray(R.array.fire_review_count_array));
        set1.setDrawValues(false);

        ArrayList<IBarDataSet> dataSets = new ArrayList<>();
        dataSets.add(set1);

        BarData data = new BarData(dataSets);
        data.setValueFormatter(new StackedValueFormatter(false, "", 1));
        data.setValueTextColor(Color.BLACK);

        patrolChart.setData(data);

        patrolChart.invalidate();
    }

    @Override
    public void onBindViewHolder(int position, View view, RecyclerViewHolder holder) {
        StatisticsPatrolInfoBean bean = patrolInfoBeans.get(position);
        ((SwipeItemLayout)holder.getView(R.id.sil_drag)).setSwipeEnable(false);
        holder.setText(R.id.item_title, bean.getPlaceName());
        holder.setText(R.id.item_1, sparseArray.get(R.string.title_patrol_count).concat(CommonUtils.string().getString(bean.getPatrolCount())));
        holder.setText(R.id.item_2, sparseArray.get(R.string.title_finish_patrol_count).concat(CommonUtils.string().getString(bean.getFinishCount())));
    }

    private StatisticsMarkerView.MarkerFormatCallback callback = new StatisticsMarkerView.MarkerFormatCallback() {
        @Override
        public void markerFormat(int x, Entry entry, TextView tvContent, Object tag) {
            StatisticsPatrolInfoBean infoBean = patrolInfoBeans.get(x);
            tvContent.setText(sparseArray.get(R.string.title_patrol_count).concat(infoBean.getPatrolCount() + "\n")
                    .concat(sparseArray.get(R.string.title_finish_patrol_count).concat(infoBean.getFinishCount() + "")));
        }
    };
}
