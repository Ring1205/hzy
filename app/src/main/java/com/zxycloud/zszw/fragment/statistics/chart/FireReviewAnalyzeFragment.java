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
import com.zxycloud.zszw.model.ResultStatisticsFireReviewInfoListBean;
import com.zxycloud.zszw.model.bean.StatisticsFireReviewInfoBean;
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
public class FireReviewAnalyzeFragment extends ChartUpDataFragment implements MyBaseAdapter.OnBindViewHolderListener {
    private BarChart fireReviewChart;
    private MyBaseAdapter myBaseAdapter;
    private SparseArray<String> sparseArray;

    private List<StatisticsFireReviewInfoBean> fireInfoViewBean;

    public static FireReviewAnalyzeFragment newInstance() {
        return new FireReviewAnalyzeFragment();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.statistics_barchart;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        fireReviewChart = findViewById(R.id.fault_chart);
        RecyclerView recyclerView = findViewById(R.id.recycler_fire);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        myBaseAdapter = new MyBaseAdapter(getContext(), R.layout.base_item, this);
        recyclerView.setAdapter(myBaseAdapter);

        initData();
    }

    @Override
    protected void initData() {
        sparseArray = CommonUtils.string().formatStringLength(getContext()
                , R.string.title_fire_count
                , R.string.title_fire_review_count);
        getProject(new OnProjectObtainListener() {
            @Override
            public void success(String projectId, String projectName) {
                netWork().setRefreshListener(R.id.refresh_layout, true, false, new NetRequestListener<ResultStatisticsFireReviewInfoListBean>() {
                    @Override
                    public void success(String action, ResultStatisticsFireReviewInfoListBean baseBean, Object tag) {
                        if (baseBean.isSuccessful()) {
                            fireInfoViewBean = baseBean.getData();
                            initChart(fireReviewChart);
                            setReviewData(fireInfoViewBean);
                            myBaseAdapter.setData(fireInfoViewBean);
                        } else {
                            CommonUtils.toast(getContext(), baseBean.getMessage());
                        }
                    }
                }, netWork().apiRequest(NetBean.actionFireReviewInfo, ResultStatisticsFireReviewInfoListBean.class, ApiRequest.REQUEST_TYPE_GET, R.id.loading)
                        .setApiType(ApiRequest.API_TYPE_STATISTICS)
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
                if (toInt >= fireInfoViewBean.size())
                    return "";
                if (toInt == value)
                    return fireInfoViewBean.get(toInt).getYearMonthTime();
                else
                    return "";
            }
        });

        Legend l = chart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
    }

    private void setReviewData(List<StatisticsFireReviewInfoBean> fireInfoBean) {
        ArrayList<BarEntry> values = new ArrayList<>();

        for (int i = 0; i < fireInfoBean.size(); i++) {
            StatisticsFireReviewInfoBean bean = fireInfoBean.get(i);
            int processCount = bean.getFireProcessCount();
            values.add(new BarEntry(i, new float[]{processCount, bean.getFireNumber() - processCount}));
        }

        BarDataSet set1 = new BarDataSet(values, "");
        set1.setDrawIcons(false);
        set1.setColors(getResources().getColor(R.color.color_state_normal), getResources().getColor(R.color.color_state_fire));
        set1.setStackLabels(getResources().getStringArray(R.array.fire_review_count_array));
        set1.setDrawValues(false);

        ArrayList<IBarDataSet> dataSets = new ArrayList<>();
        dataSets.add(set1);

        BarData data = new BarData(dataSets);
        data.setValueFormatter(new StackedValueFormatter(false, "", 1));
        data.setValueTextColor(Color.BLACK);

        fireReviewChart.setData(data);

        fireReviewChart.invalidate();
    }

    @Override
    public void onBindViewHolder(int position, View view, RecyclerViewHolder holder) {
        StatisticsFireReviewInfoBean bean = fireInfoViewBean.get(position);
        ((SwipeItemLayout) holder.getView(R.id.sil_drag)).setSwipeEnable(false);
        holder.setText(R.id.item_title, bean.getYearMonthTime());
        holder.setText(R.id.item_1, sparseArray.get(R.string.title_fire_count).concat(CommonUtils.string().getString(bean.getFireNumber())));
        holder.setText(R.id.item_2, sparseArray.get(R.string.title_fire_review_count).concat(CommonUtils.string().getString(bean.getFireProcessCount())));
    }

    private StatisticsMarkerView.MarkerFormatCallback callback = new StatisticsMarkerView.MarkerFormatCallback() {
        private SparseArray<String> sparseArray;

        @Override
        public void markerFormat(int x, Entry entry, TextView tvContent, Object tag) {
            if (null == sparseArray)
                sparseArray = CommonUtils.string().formatStringLength(_mActivity
                        , R.string.title_fire_count
                        , R.string.title_fire_review_count);
            StatisticsFireReviewInfoBean infoBean = fireInfoViewBean.get(x);
            tvContent.setText(sparseArray.get(R.string.title_fire_count).concat(infoBean.getFireNumber() + "\n")
                    .concat(sparseArray.get(R.string.title_fire_review_count).concat(infoBean.getFireProcessCount() + "")));
        }
    };
}
