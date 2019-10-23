package com.zxycloud.zszw.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.zxycloud.zszw.R;
import com.zxycloud.zszw.listener.OnItemClickListener;
import com.zxycloud.zszw.model.IconBean;
import com.zxycloud.zszw.model.bean.StatistcsBean;
import com.zxycloud.common.utils.CommonUtils;

import java.util.ArrayList;

public class StatisticsAdapter extends RecyclerView.Adapter<StatisticsAdapter.StatisticsHolder> {
    private Context context;
    private ArrayList<IconBean> statisticsList;
    private OnItemClickListener mClickListener;
    private StatistcsBean bean;

    public StatisticsAdapter(Context context) {
        this.context = context;
    }

    public void setListData(ArrayList<IconBean> statisticsList) {
        this.statisticsList = statisticsList;
        notifyDataSetChanged();
    }

    public void setBean(StatistcsBean bean) {
        this.bean = bean;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public StatisticsHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new StatisticsHolder(LayoutInflater.from(context).inflate(R.layout.item_statistics_home, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final StatisticsHolder statisticsHolder, int i) {
        setStatistsc(statisticsHolder.tvNum, statisticsList.get(i).getNameID());
        IconBean bean = statisticsList.get(i);
        statisticsHolder.tvStatistics.setText(bean.getNameID());
        statisticsHolder.tvStatistics.setCompoundDrawablesWithIntrinsicBounds(null, context.getResources().getDrawable(bean.getIconID()), null, null);
        statisticsHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mClickListener.onItemClick(statisticsHolder.getAdapterPosition(), v, statisticsHolder);
            }
        });
    }

    private void setStatistsc(TextView tvNum, @StringRes int nameID) {
        if (bean != null)
            switch (nameID) {
                case R.string.home_stat_area:
                    tvNum.setText(CommonUtils.string().getString(bean.getFirstAreaCount()));
                    break;
                case R.string.home_stat_device:
                    tvNum.setText(CommonUtils.string().getString(bean.getDevicesCount()));
                    break;
                case R.string.home_stat_account:
                    tvNum.setText(CommonUtils.string().getString(bean.getUserCount()));
                    break;
                case R.string.home_stat_point:
                    tvNum.setText(CommonUtils.string().getString(bean.getPatrolPointCount()));
                    break;
                case R.string.home_stat_inspection:
                    tvNum.setText(CommonUtils.string().getString(bean.getPatrolTaskCount()));
                    break;
                case R.string.home_stat_history:
                    tvNum.setText(CommonUtils.string().getString(bean.getMalfunctionCount()));
                    break;
                case R.string.home_stat_place:
                    tvNum.setText(CommonUtils.string().getString(bean.getPlaceCount()));
                    break;
            }
    }

    @Override
    public int getItemCount() {
        return statisticsList != null ? statisticsList.size() : 0;
    }

    class StatisticsHolder extends RecyclerView.ViewHolder {
        private TextView tvStatistics, tvNum;

        public StatisticsHolder(@NonNull View itemView) {
            super(itemView);
            tvStatistics = itemView.findViewById(R.id.tv_statistics);
            tvNum = itemView.findViewById(R.id.tv_num);
        }
    }

    public void setOnItemClickListener(OnItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }
}
