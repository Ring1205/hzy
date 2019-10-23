package com.zxycloud.startup.ui.base;

import android.support.annotation.IdRes;
import android.support.v7.app.AppCompatActivity;
import android.util.SparseArray;
import android.view.View;

import com.zxycloud.common.utils.CommonUtils;
import com.zxycloud.common.utils.TimeUpUtils;

/**
 * @author leiming
 * @date 2019/4/7.
 */
public abstract class BaseActivity extends AppCompatActivity {
    /**
     * 自定义事件间隔的点击监听Id缓存
     */
    private SparseArray<Long> clickIntervalTimes;

    /**
     * 批量设置点击事件
     *
     * @param viewIds 被点击控件的Id
     */
    protected void setOnClickView(@IdRes int... viewIds) {
        for (int layout : viewIds) {
            findViewById(layout).setOnClickListener(onClickListener);
        }
    }

    /**
     * 批量设置点击事件
     *
     * @param intervalTimes 点击防抖的时间间隔
     * @param viewIds       被点击控件的Id
     */
    protected void setOnClickView(long intervalTimes, @IdRes int... viewIds) {
        if (CommonUtils.isEmpty(clickIntervalTimes)) {
            clickIntervalTimes = new SparseArray<>();
        }
        for (int layout : viewIds) {
            clickIntervalTimes.put(layout, intervalTimes);
            findViewById(layout).setOnClickListener(onClickListener);
        }
    }

    /**
     * 防抖后的点击事件方法
     *
     * @param view 被点击控件
     */
    protected abstract void onViewClick(View view);

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int id = v.getId();
            if (CommonUtils.isEmpty(clickIntervalTimes)) {                                                      // 没有自定义点击间隔时间
                if (! CommonUtils.timeUpUtils().isTimeUp(TimeUpUtils.TIME_UP_CLICK, System.currentTimeMillis())) {        // 若两次点击在默认时间间隔内，则不执行后一次点击事件的操作
                    return;
                }
            } else {                                                                                            // 自定义点击事件间隔
                Long clickIntervalTime = clickIntervalTimes.get(id);
                if (CommonUtils.isEmpty(clickIntervalTime)                                                        // 被点击控件没有设置点击时间间隔的前提下，若两次点击在默认时间间隔内，则不执行后一次点击事件的操作
                        && ! CommonUtils.timeUpUtils().isTimeUp(TimeUpUtils.TIME_UP_CLICK, System.currentTimeMillis())) {
                    return;
                } else if (CommonUtils.notEmpty(clickIntervalTime)                                                // 被点击控件设置了点击事件间隔的前提下，若两次点击在设置的的时间间隔内，则不执行后一次点击事件的操作
                        && ! CommonUtils.timeUpUtils().isTimeUp(TimeUpUtils.TIME_UP_CLICK, System.currentTimeMillis(), clickIntervalTime)) {
                    return;
                }
            }
            onViewClick(v);
        }
    };
}
