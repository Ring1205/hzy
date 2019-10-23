package com.zxycloud.zszw.fragment.home.chart;

import android.os.Bundle;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.zxycloud.zszw.R;
import com.zxycloud.zszw.base.BaseMainFragment;
import com.zxycloud.zszw.listener.OnProjectObtainListener;
import com.zxycloud.zszw.model.ResultRecentStateListBean;
import com.zxycloud.zszw.model.bean.RecentStateBean;
import com.zxycloud.zszw.widget.StatisticsMarkerView;
import com.zxycloud.common.utils.CommonUtils;
import com.zxycloud.common.utils.StringFormatUtils;
import com.zxycloud.common.utils.netWork.ApiRequest;
import com.zxycloud.common.utils.netWork.NetBean;
import com.zxycloud.common.utils.netWork.NetUtils;

import java.util.ArrayList;
import java.util.List;

public class LineFragment extends BaseMainFragment {

    private LineChart lineChart;
    /**
     * 火警与故障的色值
     */
    private int[] colors;

    /**
     * 展示的总天数
     */
    private int days;
    private NetUtils netUtils;
    private List<RecentStateBean> recentStateBeans;

    private boolean canOutRefresh = false;

    @Override
    protected int getLayoutId() {
        return R.layout.chart_line;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        Bundle args = getArguments();
        days = args.getInt("days", 15);

        netUtils = NetUtils.getNewInstance(_mActivity);

        lineChart = findViewById(R.id.line_chart);
        lineChart.setDrawGridBackground(false);
        lineChart.getDescription().setEnabled(false);
        lineChart.setDrawBorders(false);

        lineChart.getAxisLeft().setEnabled(false);
        lineChart.getAxisRight().setDrawAxisLine(false);
        lineChart.getAxisRight().setDrawGridLines(false);
        lineChart.getAxisLeft().setDrawGridLines(false);

        // enable touch gestures
        lineChart.setTouchEnabled(true);
        lineChart.setClickable(true);

        // enable scaling and dragging
        lineChart.setDragEnabled(false);
        lineChart.setScaleEnabled(false);

        lineChart.setDrawGridBackground(false);

        // if disabled, scaling can be done on x- and y-axis separately
        lineChart.setPinchZoom(false);

        StatisticsMarkerView mv = new StatisticsMarkerView(_mActivity, R.layout.dialog_chart_layout, null, callback);

        // Set the marker to the chart
        mv.setChartView(lineChart);
        lineChart.setMarker(mv);

        Legend l = lineChart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);

        XAxis xAxis = lineChart.getXAxis();
        xAxis.setEnabled(true);//显示X轴
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);//X轴位置
        xAxis.setDrawGridLines(false);
        xAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                /*
                 * 设置横坐标显示样式
                 * 将获取到的value四舍五入，如果直接强转会导致日期重复，其他情况需要具体问题具体分析
                 */
                int toInt = (int) Math.rint(value);
                if (toInt >= days) {
                    return "";
                }
                if (toInt == value) {
                    return recentStateBeans.get(toInt).getDateString();
                } else {
                    return "";
                }
            }
        });

        YAxis leftAxis = lineChart.getAxisLeft();
        leftAxis.setAxisMinimum(0f);
        leftAxis.setSpaceMin(1f);
        leftAxis.setValueFormatter(new ValueFormatter() {
            int n = 0;

            @Override
            public String getFormattedValue(float value) {
                /*
                 * 设置纵坐标显示样式
                 */
                int currentValue = (int) value;
                if (n == currentValue)
                    return "";
                else
                    return String.valueOf(n = currentValue);
            }
        });
        lineChart.getAxisRight().setEnabled(false);
        lineChart.getAxisLeft().setEnabled(true);
        canOutRefresh = true;
        getStatistics();
    }

    public void outRefresh() {
        if (canOutRefresh)
            getStatistics();
    }

    private void getStatistics() {
        getProject(new OnProjectObtainListener() {
            @Override
            public void success(String projectId, String projectName) {
                ApiRequest apiRequest = new ApiRequest<>(NetBean.actionRecentAlarm, ResultRecentStateListBean.class)
                        .setApiType(ApiRequest.API_TYPE_STATISTICS)
                        .setRequestParams("days", days)
                        .setRequestParams("projectId", projectId);
                netUtils.request(new NetUtils.NetRequestCallBack<ResultRecentStateListBean>() {
                    @Override
                    public void success(String action, ResultRecentStateListBean resultRecentStateListBean, Object tag) {
                        if (resultRecentStateListBean.isSuccessful()) {
                            recentStateBeans = resultRecentStateListBean.getData();
                            initData();
                        }
                    }

                    @Override
                    public void error(String action, Throwable e, Object tag) {

                    }
                }, false, apiRequest);
            }
        });
    }

    private void initData() {
        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        for (int z = 0; z < 2; z++) {

            ArrayList<Entry> values = new ArrayList<>();

            for (int i = 0; i < days; i++) {
                int val = (int) ((Math.random() * days) + 3);
                values.add((Entry) new Entry(i, z % 2 == 0 ? recentStateBeans.get(i).getFireNumber() : recentStateBeans.get(i).getDeviceFaultNumber()));
//                values.add((Entry) new Entry(i, val).setTag(z == 0 ? MyMarkerView.MARKER_TYPE_FIRE : MyMarkerView.MARKER_TYPE_FAULT));
            }

            StringFormatUtils formatUtils = CommonUtils.string();

            LineDataSet d = new LineDataSet(values, formatUtils.getString(_mActivity, z == 0 ? R.string.string_line_fire : R.string.string_line_fault));
            d.setDrawValues(false);
            d.setLineWidth(2.5f);
            d.setCircleRadius(4f);

            if (null == colors) {
                colors = new int[]{
                        getResources().getColor(R.color.color_state_fire)
                        , getResources().getColor(R.color.color_state_fault)};
            }

            int color = colors[z];
            d.setColor(color);
            d.setCircleColor(color);
            dataSets.add(d);
        }

//        ((LineDataSet) dataSets.get(0)).enableDashedLine(10, 10, 0);
//        ((LineDataSet) dataSets.get(0)).setColors(ColorTemplate.VORDIPLOM_COLORS);
//        ((LineDataSet) dataSets.get(0)).setCircleColors(ColorTemplate.VORDIPLOM_COLORS);

        LineData data = new LineData(dataSets);
        lineChart.setData(data);
        lineChart.invalidate();
    }

    public static LineFragment newInstance(int days) {
        Bundle args = new Bundle();
        args.putInt("days", days);
        LineFragment fragment = new LineFragment();
        fragment.setArguments(args);
        return fragment;
    }

    private StatisticsMarkerView.MarkerFormatCallback callback = new StatisticsMarkerView.MarkerFormatCallback() {
        @Override
        public void markerFormat(int x, Entry entry, TextView tvContent, Object tag) {
            RecentStateBean recentStateBean = recentStateBeans.get(x);
            tvContent.setText(recentStateBean.getDateString().concat("\n")
                    .concat(String.format(CommonUtils.string().getString(_mActivity, R.string.string_line_fire_count), recentStateBean.getFireNumber())).concat("\n")
                    .concat(String.format(CommonUtils.string().getString(_mActivity, R.string.string_line_fault_count), recentStateBean.getDeviceFaultNumber())));
        }
    };
}
