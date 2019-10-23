package com.zxycloud.common.widget.BswRecyclerView;

/**
 * @author leiming
 * @date 2019/3/12.
 */
public interface BswFilterLayoutFilter<T> {
    void performFilter(T t, BswLayoutItem layoutItem);
}
