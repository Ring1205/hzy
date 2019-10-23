package com.zxycloud.zszw;

import android.content.Intent;
import android.os.Bundle;
import android.support.test.rule.ActivityTestRule;

import com.zxycloud.zszw.test.TestActivity;
import com.zxycloud.common.utils.CommonUtils;

import org.json.JSONObject;
import org.junit.Rule;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;

public class ActivityTest {
    @Rule
    public final ActivityTestRule activityTestRule =
            new ActivityTestRule<>(TestActivity.class, false, false);

    @Test
    public void blockingTest() throws Exception {
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putString("alertBean", String.valueOf(new JSONObject("{\"adapterName\":\"TX3252_CESHI201904280007\",\"areaName\":\"默认区域\",\"deviceFlag\":4,\"deviceId\":\"684636d1-58ec-49a5-bbfd-403221950b75\",\"deviceInstallLocation\":\"2\",\"gcj02Latitude\":40.021032,\"gcj02Longitude\":116.464997,\"happenTime\":1562313157817,\"isHasCamera\":0,\"isHasInstallationDetail\":0,\"isHasLayerPoint\":0,\"placeName\":\"axjcs02\",\"processTime\":0,\"processTypeName\":\"未复核\",\"processUserName\":\"\",\"projectName\":\"安小建目\",\"receiveTime\":1562313157842,\"recordId\":\"cc12009cca1946aa86f732abcff21891\",\"stateGroupName\":\"预警\",\"userDeviceTypeCode\":46,\"userDeviceTypeName\":\"可燃气体探测器\",\"wgs84Latitude\":40.01970693,\"wgs84Longitude\":116.45884937}")));
        bundle.putInt("stateCode", CommonUtils.STATE_CODE_FIRE);
        intent.putExtra("bundle", bundle);
        activityTestRule.launchActivity(intent);
        CountDownLatch countdown = new CountDownLatch(1);
        countdown.await();
    }
}
