package com.zxycloud.zszw.listener;

import com.amap.api.services.geocoder.RegeocodeResult;

public interface OnMapLocationListener {
    void onRegeocodeSearched(RegeocodeResult regeocodeResult, int i);
}
