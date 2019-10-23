package com.zxycloud.zszw.fragment.statistics;

import com.zxycloud.zszw.base.BaseBackFragment;

public abstract class ChartUpDataFragment extends BaseBackFragment {
    protected abstract void initData();

    public void upData(){
        initData();
    }
}
