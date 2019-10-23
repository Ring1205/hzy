package com.zxycloud.zszw.test;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.google.gson.Gson;
import com.zxycloud.zszw.AlertActivity;
import com.zxycloud.zszw.R;
import com.zxycloud.zszw.model.bean.AlarmBean;
import com.zxycloud.common.utils.CommonUtils;
import com.zxycloud.common.utils.JumpToUtils;

public class TestActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

//        String s = getIntent().getBundleExtra("bundle").getString("alertString");
//        if (!TextUtils.isEmpty(s)) {
//            ((TextView) findViewById(R.id.text)).setText(s);
//        }
    }

    public void OnClick(View view) {
//        Intent intent = new Intent(TestActivity.this, UpdateService.class);
//        intent.putExtra(UpdateService.APP_PATH, "http://www.jzxfyun.net/apk/hzy.apk");
//        startService(intent);
//        {"adapterName":"TX3252_xian180511000066001","areaName":"城盈中心1","deviceFlag":4,"deviceId":"6442f3bf-5ad7-48fc-9323-c58302f1063c","deviceInstallLocation":"测试","gcj02Latitude":40.021588,"gcj02Longitude":116.465605,"happenTime":1564130693097,"isHasCamera":1,"isHasInstallationDetail":0,"isHasLayerPoint":0,"placeName":"1406办公室","processTime":0,"processTypeName":"未复核","processUserName":"","projectName":"诚盈中心研发办公室","receiveTime":1564130693110,"recordId":"842c40a38fb249c49769b47a0e11eef8","stateGroupName":"火警","userDeviceTypeCode":59,"userDeviceTypeName":"手动火灾报警按钮","wgs84Latitude":40.02026443,"wgs84Longitude":116.45945927}
        Bundle bundle = new Bundle();
        bundle.putString("alertBean", new Gson().toJson(new Gson().fromJson("{\"adapterName\":\"TX3252_xian180511000066001\",\"areaName\":\"城盈中心1\",\"deviceFlag\":4,\"deviceId\":\"6442f3bf-5ad7-48fc-9323-c58302f1063c\",\"deviceInstallLocation\":\"测试\",\"gcj02Latitude\":40.021588,\"gcj02Longitude\":116.465605,\"happenTime\":1564130693097,\"isHasCamera\":1,\"isHasInstallationDetail\":0,\"isHasLayerPoint\":0,\"placeName\":\"1406办公室\",\"processTime\":0,\"processTypeName\":\"未复核\",\"processUserName\":\"\",\"projectName\":\"诚盈中心研发办公室\",\"receiveTime\":1564130693110,\"recordId\":\"842c40a38fb249c49769b47a0e11eef8\",\"stateGroupName\":\"火警\",\"userDeviceTypeCode\":59,\"userDeviceTypeName\":\"手动火灾报警按钮\",\"wgs84Latitude\":40.02026443,\"wgs84Longitude\":116.45945927}", AlarmBean.class)));
        bundle.putInt("stateCode", CommonUtils.STATE_CODE_FIRE);
        JumpToUtils.receiverJumpTo(this, AlertActivity.class, bundle);
    }
}
