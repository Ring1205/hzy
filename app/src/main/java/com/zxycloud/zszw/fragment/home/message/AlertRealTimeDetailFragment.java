package com.zxycloud.zszw.fragment.home.message;

import android.annotation.SuppressLint;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.AMapUtils;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.zxycloud.zszw.R;
import com.zxycloud.zszw.base.BaseBackFragment;
import com.zxycloud.zszw.event.type.AlertShowType;
import com.zxycloud.zszw.event.type.VideoShowType;
import com.zxycloud.zszw.fragment.common.PlanFragment;
import com.zxycloud.zszw.fragment.common.PlayVideoFragment;
import com.zxycloud.zszw.fragment.home.shortcut.device.DeviceDetailsFragment;
import com.zxycloud.zszw.listener.NetRequestListener;
import com.zxycloud.zszw.model.ResultAlertBean;
import com.zxycloud.zszw.model.bean.AlarmBean;
import com.zxycloud.zszw.model.bean.PublicFireDetachmentsBean;
import com.zxycloud.zszw.model.bean.PublicFireFightingAndRescuesBean;
import com.zxycloud.zszw.model.bean.PublicMedicalStationsBean;
import com.zxycloud.zszw.model.bean.PublicWatersBean;
import com.zxycloud.common.utils.CommonUtils;
import com.zxycloud.common.utils.GuideUtils;
import com.zxycloud.common.utils.PermissionUtils;
import com.zxycloud.common.utils.TimerUtils;
import com.zxycloud.common.utils.netWork.ApiRequest;
import com.zxycloud.common.utils.netWork.NetBean;
import com.zxycloud.common.utils.rxbus2.RxBusCode;
import com.zxycloud.common.utils.rxbus2.Subscribe;
import com.zxycloud.common.utils.rxbus2.ThreadMode;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * @author leiming
 * @date 2019/3/27.
 */
public class AlertRealTimeDetailFragment extends BaseBackFragment implements View.OnClickListener {
    private AlarmBean alarmBean;
    public static boolean isPush;

    private SparseArray<String> sparseLessArray;
    private SparseArray<String> sparseMoreArray;

    private AMapLocationClient mLocationClient;
    private AMap aMap;

    private int showType = AlertShowType.ALERT_HISTORY;
    private int stateCode = CommonUtils.STATE_CODE_FIRE;
    private String alertString;

    private int detailLessHeight;
    private int detailMoreHeight;
    private int distance = 0;

    private TextView alertDetailLess, alertDetailMore, alertDetailGuide;
    private MapView mapView;

    private VoiceControlListener voiceControlListener;

    private DistanceCalculate distanceCalculate;
    private boolean isFire;

    public static AlertRealTimeDetailFragment newInstance(@AlertShowType.ShowType int showType, int stateCode, String alertString) {
        Bundle args = new Bundle();
        args.putInt("showType", showType);
        args.putInt("stateCode", stateCode);
        args.putString("alertString", alertString);
        AlertRealTimeDetailFragment fragment = new AlertRealTimeDetailFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_record_detail;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void initView(Bundle savedInstanceState) {
        setOnClickListener(this
                , R.id.btn
                , R.id.alert_detail_floor_place
                , R.id.alert_detail_video
                , R.id.alert_detail_device_detail
                , R.id.alert_detail_guide);

        distanceCalculate = new DistanceCalculate();

        showType = getArguments().getInt("showType");
        alertString = getArguments().getString("alertString");
        stateCode = getArguments().getInt("stateCode");

        isFire = stateCode == CommonUtils.STATE_CODE_FIRE;

        ((TextView) findViewById(R.id.title)).setText(isFire ? R.string.string_alert_fire_title : R.string.string_alert_prefire_title);
        ((TextView) findViewById(R.id.btn)).setText(R.string.quit);
        findViewById(R.id.btn).setVisibility(View.VISIBLE);
        findViewById(R.id.toolbar).setBackgroundResource(isFire ? R.color.color_state_fire : R.color.color_state_prefire);

        mapView = findViewById(R.id.alert_detail_map);
        mapView.onCreate(savedInstanceState);
        aMap = mapView.getMap();


        alertDetailLess = findViewById(R.id.alert_detail_less);
        alertDetailLess.setTag(true);
        alertDetailMore = findViewById(R.id.alert_detail_more);
        alertDetailMore.setTag(false);
        alertDetailGuide = findViewById(R.id.alert_detail_guide);

        alertDetailLess.setOnTouchListener(new View.OnTouchListener() {
            float startY;
            float currentDistance;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (!((Boolean) v.getTag())) {
                    return false;
                }
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        Drawable drawableBottomDown = alertDetailLess.getCompoundDrawables()[3];
                        CommonUtils.log().i("event.getY() = " + event.getY() + "event.getRawY() = " + event.getRawY() + "   alertDetailLess.getBottom() = " + alertDetailLess.getBottom() + "   drawableBottomDown.getBounds().height() = " + drawableBottomDown.getBounds().height());
                        if (event.getY() >= alertDetailLess.getBottom() - drawableBottomDown.getBounds().height() * 2) {
                            startY = event.getY();
                            detailLessHeight = CommonUtils.measureView().getHeight(alertDetailLess);
                            detailMoreHeight = CommonUtils.measureView().getHeight(alertDetailMore);
                            distance = detailMoreHeight - detailLessHeight;
                            CommonUtils.log().i("detailLessHeight = " + detailLessHeight + "   detailMoreHeight = " + detailMoreHeight + "   distance = " + distance);
                            return true;
                        } else {
                            startY = -1;
                            return false;
                        }

                    case MotionEvent.ACTION_MOVE:
                        if (startY == -1) {
                            return false;
                        }
                        int currentY = (int) event.getY();
                        currentDistance = currentY - startY;
                        if (currentDistance >= distance) {
                            currentDistance = distance;
                        } else if (currentDistance < 0) {
                            currentDistance = 0;
                        }
                        CommonUtils.log().i("currentDistance = " + currentDistance + " distance = " + distance);
                        alertDetailLess.setAlpha(1 - currentDistance / 1.8f / distance);
                        alertDetailMore.setAlpha(currentDistance / 1.8f / distance);
                        alertDetailLess.setHeight((int) (currentDistance + detailLessHeight));
                        break;

                    case MotionEvent.ACTION_UP:
                        if (startY == -1) {
                            return false;
                        }
                        animDetailChange(false, currentDistance);
                        break;
                }
                return false;
            }
        });

        alertDetailMore.setOnTouchListener(new View.OnTouchListener() {
            float startY;
            float currentDistance;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (!((Boolean) v.getTag())) {
                    return false;
                }
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        Drawable drawableBottomDown = alertDetailMore.getCompoundDrawables()[3];

                        if (null != drawableBottomDown && event.getY() >= alertDetailMore.getBottom() - drawableBottomDown.getBounds().height() * 2) {
                            startY = event.getY();
                            detailLessHeight = CommonUtils.measureView().getHeight(alertDetailLess);
                            detailMoreHeight = CommonUtils.measureView().getHeight(alertDetailMore);
                            distance = detailMoreHeight - detailLessHeight;
                            CommonUtils.log().i("detailLessHeight = " + detailLessHeight + "   detailMoreHeight = " + detailMoreHeight + "   distance = " + distance);
                            return true;
                        } else {
                            startY = -1;
                            return false;
                        }

                    case MotionEvent.ACTION_MOVE:
                        if (startY == -1) {
                            return false;
                        }
                        int currentY = (int) event.getY();
                        currentDistance = currentY - startY;
                        if (currentDistance > 0) {
                            currentDistance = 0;
                        } else if (currentDistance + distance < 0) {
                            currentDistance = -distance;
                        }
                        CommonUtils.log().i("currentDistance = " + currentDistance + " distance = " + distance);
                        alertDetailMore.setAlpha(1 - Math.abs(currentDistance) / 1.8f / distance);
                        alertDetailLess.setAlpha(Math.abs(currentDistance) / 1.8f / distance);
                        alertDetailMore.setHeight((int) (currentDistance + detailMoreHeight));
                        break;

                    case MotionEvent.ACTION_UP:
                        if (startY == -1) {
                            return false;
                        }
                        animDetailChange(true, currentDistance);
                        break;
                }
                return false;
            }
        });

        PermissionUtils.setRequestPermissions(this, new PermissionUtils.PermissionGrant() {
            @Override
            public Integer[] onPermissionGranted() {
                return new Integer[]{PermissionUtils.CODE_ACCESS_COARSE_LOCATION, PermissionUtils.CODE_ACCESS_FINE_LOCATION};
            }

            @Override
            public void onRequestResult(List<String> deniedPermission) {
                if (CommonUtils.judgeListNull(deniedPermission) == 0) {
                    location();
                } else {
                    CommonUtils.toast(_mActivity, R.string.common_permission_location);
                }
            }
        });

        sparseLessArray = CommonUtils.string().formatStringLength(_mActivity
                , R.string.string_alert_project_name
                , R.string.string_alert_area_name
                , R.string.string_alert_place_name
                , R.string.string_alert_time);

        sparseMoreArray = CommonUtils.string().formatStringLength(_mActivity
                , R.string.title_place_name
                , R.string.string_alert_device_type
                , R.string.string_alert_device_install
                , R.string.string_alert_alert_type
                , R.string.string_alert_happen_time
                , R.string.string_alert_reviewer
                , R.string.string_alert_review_time
                , R.string.string_alert_review_result);

        if (showType == AlertShowType.ALERT_HAPPENING) {
            isPush = true;
            alarmBean = new Gson().fromJson(alertString, AlarmBean.class);
            alertDetailLess.setText(sparseLessArray.get(R.string.string_alert_project_name).concat(alarmBean.getProjectName()).concat("\n")
                    .concat(sparseLessArray.get(R.string.string_alert_area_name)).concat(alarmBean.getAreaName()).concat("\n")
                    .concat(sparseLessArray.get(R.string.string_alert_place_name)).concat(alarmBean.getPlaceName()).concat("\n")
                    .concat(sparseLessArray.get(R.string.string_alert_time)).concat(CommonUtils.date().format(alarmBean.getReceiveTime()).concat("\n"))
            );
            alertDetailMore.setText(sparseMoreArray.get(R.string.title_place_name).concat(alarmBean.getPlaceName()).concat("\n")
                    .concat(sparseMoreArray.get(R.string.string_alert_device_type)).concat(alarmBean.getUserDeviceTypeName()).concat("\n")
                    .concat(sparseMoreArray.get(R.string.string_alert_device_install)).concat(alarmBean.getDeviceInstallLocation()).concat("\n")
                    .concat(sparseMoreArray.get(R.string.string_alert_alert_type)).concat(alarmBean.getStateGroupName()).concat("\n")
                    .concat(sparseMoreArray.get(R.string.string_alert_happen_time)).concat(CommonUtils.date().format(alarmBean.getReceiveTime()).concat("\n"))
                    .concat(sparseMoreArray.get(R.string.string_alert_reviewer)).concat(alarmBean.getProcessUserName()).concat("\n")
                    .concat(sparseMoreArray.get(R.string.string_alert_review_time)).concat(CommonUtils.date().format(alarmBean.getProcessTime()).concat("\n"))
                    .concat(sparseMoreArray.get(R.string.string_alert_review_result)).concat(alarmBean.getProcessTypeName())
            );
            setAlarmInfo();
        } else {
            getDetail();
        }
    }

    /**
     * 抬起手指时的滑动动画
     *
     * @param isLessShow 将要显示的是否是简介
     * @param currentY   当前纵向移动距离
     */
    private void animDetailChange(final boolean isLessShow, final float currentY) {
        if (isLessShow) {
            if (currentY + distance < 0) {              // 已经移动完整距离，不需要缩放动画
                onInfoChangeFinished(true);
                return;
            } else if (currentY > 0) {                  // 未移动，维持原本的情况
                return;
            }
            final float part = (distance + currentY) / 10.0f;
            new TimerUtils(500, 50, new TimerUtils.OnBaseTimerCallBack() {
                int times = 0;
                float targetHeight = detailMoreHeight + currentY;

                float lessAlpha = alertDetailLess.getAlpha();
                float moreAlpha = alertDetailMore.getAlpha();

                @Override
                public void onTick(Object tag, long millisUntilFinished) {
                    times++;
                    targetHeight -= part;
                    alertDetailMore.setHeight((int) targetHeight);
                    alertDetailMore.setAlpha(lessAlpha - lessAlpha * times / 10f);
                    alertDetailLess.setAlpha(moreAlpha + (1 - moreAlpha) / 10 * times);
                }

                @Override
                public void onFinish(Object tag) {
                    onInfoChangeFinished(true);
                }
            }).start();
        } else {
            if (currentY > distance) {                  // 已经移动完整距离，不需要缩放动画
                onInfoChangeFinished(false);
                return;
            } else if (currentY < 0) {                  // 未移动，维持原本的情况
                return;
            }
            final float part = (distance - currentY) / 10.0f;
            new TimerUtils(500, 50, new TimerUtils.OnBaseTimerCallBack() {
                int times = 0;
                float targetHeight = detailLessHeight + currentY;

                float lessAlpha = alertDetailLess.getAlpha();
                float moreAlpha = alertDetailMore.getAlpha();

                @Override
                public void onTick(Object tag, long millisUntilFinished) {
                    times++;
                    targetHeight += part;
                    alertDetailLess.setHeight((int) targetHeight);
                    alertDetailLess.setAlpha(lessAlpha - lessAlpha * times / 10f);
                    alertDetailMore.setAlpha(moreAlpha + (1 - moreAlpha) / 10 * times);
                }

                @Override
                public void onFinish(Object tag) {
                    onInfoChangeFinished(false);
                }
            }).start();
        }
    }

    private void onInfoChangeFinished(boolean isLessShow) {
        if (isLessShow) {
            alertDetailMore.setAlpha(0);
            alertDetailMore.setHeight(detailMoreHeight);
            alertDetailMore.setTag(false);
            alertDetailLess.setAlpha(1);
            alertDetailLess.bringToFront();
            alertDetailLess.setTag(true);
        } else {
            alertDetailLess.setAlpha(0);
            alertDetailLess.setHeight(detailLessHeight);
            alertDetailLess.setTag(false);
            alertDetailMore.setAlpha(1);
            alertDetailMore.bringToFront();
            alertDetailMore.setTag(true);
        }
        alertDetailGuide.bringToFront();
    }

    @Override
    public void onClick(View view) {
        if (null != voiceControlListener) {
            voiceControlListener.stopVoice();
        }
        switch (view.getId()) {
            case R.id.btn:
                finish();
                break;

            case R.id.alert_detail_floor_place:
                if (null == alarmBean || alarmBean.getIsHasLayerPoint() == 0) {
                    CommonUtils.toast(_mActivity, R.string.toast_permissions_error);
                    return;
                }
                start(PlanFragment.newInstance(PlanFragment.POINT_TYPE_LIST, String.valueOf(alarmBean.getStateGroupCode()), alarmBean.getDeviceId(), alarmBean.getAdapterName()));
                break;

            case R.id.alert_detail_video:
                if (null == alarmBean || alarmBean.getIsHasCamera() == 0) {
                    CommonUtils.toast(_mActivity, R.string.toast_permissions_error);
                    return;
                }
                start(PlayVideoFragment.newInstance(VideoShowType.PLAY_TYPE_LINKAGE_DEVICE, alarmBean.getDeviceId(), alarmBean.getAdapterName()));
                break;

            case R.id.alert_detail_device_detail:
                if (null == alarmBean) {
                    CommonUtils.toast(_mActivity, R.string.toast_permissions_error);
                }
                start(DeviceDetailsFragment.newInstance(alarmBean.getDeviceId(), alarmBean.getAdapterName()));
                break;

            case R.id.alert_detail_guide:
                GuideUtils.jumpTo(_mActivity, GuideUtils.GAODE_MAP, alarmBean.getGcj02Longitude(), alarmBean.getGcj02Latitude());
                break;
        }
    }

    public void setVoiceControlListener(VoiceControlListener voiceControlListener) {
        this.voiceControlListener = voiceControlListener;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        isPush = false;
    }

    /**
     * 初始化地图
     */
    private void initMapView() {
//        aMap.setTrafficEnabled(false);// 显示实时交通状况
        //地图模式可选类型：MAP_TYPE_NORMAL 矢量地图模式 ,MAP_TYPE_SATELLITE 卫星地图模式,MAP_TYPE_NIGHT 夜景地图模式,MAP_TYPE_NAVI 导航地图模式
        aMap.setMapType(AMap.MAP_TYPE_NORMAL);// 普通地图模式
        //设置地图可以执行的手势 ：
        // 设置地图是否可以手势滑动 setScrollGesturesEnabled
        // 设置地图是否可以手势缩放大小 setZoomGesturesEnabled
        // 设置地图是否可以倾斜 setTiltGesturesEnabled
        // 设置地图是否可以旋转 setRotateGesturesEnabled
        UiSettings mUiSettings = aMap.getUiSettings();
        mUiSettings.setTiltGesturesEnabled(false);//设置不可倾斜
        mUiSettings.setRotateGesturesEnabled(false);//设置不可旋转
    }

    /**
     * 设置Marker参数
     */
    private void setMarker() {
        // MarkerOptions markerOptions = new MarkerOptions()
        // .icon(BitmapDescriptorFactory.fromBitmap(R.drawable.location_marker)//设置marker 图片样式
        // .position(latlng)//设置坐标
        // .title("title")//设置InfoWindow的标题
        // .snippet("details")//设置InfoWindow的主体内容
        // .draggable(true);//设置InfoWindow可跟随Maker移动
//        aMap.addMarker(markerOptions)// addMarker添加单个Marker ; addMarkers添加多个Marker
        // showInfoWindow();// showInfoWindow显示InfoWindow ; hideInfoWindow隐藏InfoWindow
        // 设置marker监听
//        aMap.setOnMarkerDragListener(this);// 设置marker可拖拽事件监听器
//        aMap.setOnMapLoadedListener(this);// 设置amap加载成功事件监听器
//        aMap.setOnMarkerClickListener(this);// 设置点击marker事件监听器
//        aMap.setOnInfoWindowClickListener(this);// 设置点击infoWindow事件监听器
    }

    /**
     * 定位功能
     */
    private void location() {
        mLocationClient = new AMapLocationClient(getContext());
        //初始化定位参数
        AMapLocationClientOption mLocationOption = new AMapLocationClientOption();
        //设置定位监听
        mLocationClient.setLocationListener(locationListener);
        //设置定位模式为高精度模式，Battery_Saving为低功耗模式，Device_Sensors是仅设备模式
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        //设置定位间隔,单位毫秒,默认为2000ms
        mLocationOption.setInterval(2000);
        //设置定位参数
        mLocationClient.setLocationOption(mLocationOption);
        // 此方法为每隔固定时间会发起一次定位请求，为了减少电量消耗或网络流量消耗，
        // 注意设置合适的定位时间的间隔（最小间隔支持为1000ms），并且在合适时间调用stopLocation()方法来取消定位请求
        // 在定位结束后，在合适的生命周期调用onDestroy()方法
        // 在单次定位情况下，定位无论成功与否，都无需调用stopLocation()方法移除请求，定位sdk内部会移除
        //启动定位
        mLocationClient.startLocation();
    }

    /**
     * 定位成功接口回调
     */
    private AMapLocationListener locationListener = new AMapLocationListener() {
        @Override
        public void onLocationChanged(AMapLocation amapLocation) {
            mLocationClient.stopLocation();
            if (amapLocation != null) {
                if (amapLocation.getErrorCode() == 0) {
                    //定位成功回调信息，设置相关消息
                    amapLocation.getLocationType();//获取当前定位结果来源，如网络定位结果，详见定位类型表
                    double lat = amapLocation.getLatitude();//获取纬度
                    double lng = amapLocation.getLongitude();//获取经度
                    /*Marker marker = */

                    LatLng location = new LatLng(lat, lng);

                    distanceCalculate.setLocation(location);

                    amapLocation.getAccuracy();//获取精度信息
                    @SuppressLint("SimpleDateFormat") SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    Date date = new Date(amapLocation.getTime());
                    df.format(date);//定位时间
                } else {
                    //显示错误信息ErrCode是错误码，errInfo是错误信息，详见错误码表。
                    Log.e("AmapError", "location Error, ErrCode:"
                            + amapLocation.getErrorCode() + ", errInfo:"
                            + amapLocation.getErrorInfo());
                }
            }
        }
    };

    private void getDetail() {
        netWork().setRequestListener(new NetRequestListener<ResultAlertBean>() {
            @Override
            public void success(String action, ResultAlertBean resultAlertBean, Object tag) {
                if (resultAlertBean.isSuccessful()) {
                    alarmBean = resultAlertBean.getData();
                    setAlarmInfo();
                } else {
                    CommonUtils.toast(_mActivity, resultAlertBean.getMessage());
                }
            }
        }, netWork().apiRequest(NetBean.actionGetRealTimeAlertDetail, ResultAlertBean.class, ApiRequest.REQUEST_TYPE_GET).setRequestParams("recordId", isPush ? alarmBean.getRecordId() : alertString));
    }

    private void setAlarmInfo() {
        alertDetailLess.setText(sparseLessArray.get(R.string.string_alert_project_name).concat(alarmBean.getProjectName()).concat("\n")
                .concat(sparseLessArray.get(R.string.string_alert_area_name)).concat(alarmBean.getAreaName()).concat("\n")
                .concat(sparseLessArray.get(R.string.string_alert_place_name)).concat(alarmBean.getPlaceName()).concat("\n")
                .concat(sparseLessArray.get(R.string.string_alert_time)).concat(CommonUtils.date().format(alarmBean.getReceiveTime()).concat("\n"))
        );
        alertDetailMore.setText(sparseMoreArray.get(R.string.title_place_name).concat(alarmBean.getPlaceName()).concat("\n")
                .concat(sparseMoreArray.get(R.string.string_alert_device_type)).concat(alarmBean.getUserDeviceTypeName()).concat("\n")
                .concat(sparseMoreArray.get(R.string.string_alert_device_install)).concat(alarmBean.getDeviceInstallLocation()).concat("\n")
                .concat(sparseMoreArray.get(R.string.string_alert_alert_type)).concat(alarmBean.getStateGroupName()).concat("\n")
                .concat(sparseMoreArray.get(R.string.string_alert_happen_time)).concat(CommonUtils.date().format(alarmBean.getReceiveTime()).concat("\n"))
                .concat(sparseMoreArray.get(R.string.string_alert_reviewer)).concat(alarmBean.getProcessUserName()).concat("\n")
                .concat(sparseMoreArray.get(R.string.string_alert_review_time)).concat(CommonUtils.date().format(alarmBean.getProcessTime()).concat("\n"))
                .concat(sparseMoreArray.get(R.string.string_alert_review_result)).concat(alarmBean.getProcessTypeName())
        );
        LatLng alarm = new LatLng(alarmBean.getGcj02Latitude(), alarmBean.getGcj02Longitude());
        distanceCalculate.setAlarm(alarm);
        aMap.addMarker(new MarkerOptions()
                .position(alarm)
                .icon(BitmapDescriptorFactory
                        .fromBitmap(BitmapFactory.decodeResource(getResources(), isFire ? R.mipmap.ic_alert_fire : R.mipmap.ic_alert_prefire))));
        aMap.moveCamera(CameraUpdateFactory.zoomTo(15));
        aMap.moveCamera(CameraUpdateFactory.changeLatLng(alarm));
        try {
            List<PublicFireDetachmentsBean> publicFireDetachments = alarmBean.getPublicFireDetachments();
            if (null != publicFireDetachments)
                for (PublicFireDetachmentsBean detachmentsBean : publicFireDetachments) {
                    aMap.addMarker(new MarkerOptions()
                            .position(new LatLng(detachmentsBean.getGcj02Latitude(), detachmentsBean.getGcj02Longitude()))
                            .icon(BitmapDescriptorFactory
                                    .fromBitmap(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_alert_fire_detachment))));
                }
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            List<PublicFireFightingAndRescuesBean> fireFightingAndRescues = alarmBean.getPublicFireFightingAndRescues();
            if (null != fireFightingAndRescues)
                for (PublicFireFightingAndRescuesBean rescuesBean : fireFightingAndRescues) {
                    aMap.addMarker(new MarkerOptions()
                            .position(new LatLng(rescuesBean.getGcj02Latitude(), rescuesBean.getGcj02Longitude()))
                            .icon(BitmapDescriptorFactory
                                    .fromBitmap(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_alert_fire_station))));
                }
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            List<PublicMedicalStationsBean> publicMedicalStations = alarmBean.getPublicMedicalStations();
            if (null != publicMedicalStations)
                for (PublicMedicalStationsBean medicalStationsBean : publicMedicalStations) {
                    aMap.addMarker(new MarkerOptions()
                            .position(new LatLng(medicalStationsBean.getGcj02Latitude(), medicalStationsBean.getGcj02Longitude()))
                            .icon(BitmapDescriptorFactory
                                    .fromBitmap(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_alert_hospital))));
                }
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            List<PublicWatersBean> publicWaters = alarmBean.getPublicWaters();
            if (null != publicWaters)
                for (PublicWatersBean publicWatersBean : publicWaters) {
                    aMap.addMarker(new MarkerOptions()
                            .position(new LatLng(publicWatersBean.getGcj02Latitude(), publicWatersBean.getGcj02Longitude()))
                            .icon(BitmapDescriptorFactory
                                    .fromBitmap(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_alert_fire_hydrants))));
                }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Subscribe(code = RxBusCode.RX_REVIEW, threadMode = ThreadMode.MAIN)
    public void refreshReview() {
        getDetail();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (hidden) {
            mapView.setVisibility(View.GONE);
            mapView.onPause();
        } else {
            mapView.onResume();
            mapView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.setVisibility(View.VISIBLE);
        mapView.onResume();

        CommonUtils.getRxBus().register(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.setVisibility(View.GONE);
        mapView.onPause();
        CommonUtils.getRxBus().unRegister(this);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    public interface VoiceControlListener {
        void stopVoice();
    }

    class DistanceCalculate {
        private LatLng location;
        private LatLng alarm;

        public void setLocation(LatLng location) {
            this.location = location;
            if (null != alarm) {
                distanceCalculate(location, alarm);
            }
        }

        public void setAlarm(LatLng alarm) {
            this.alarm = alarm;
            if (null != location) {
                distanceCalculate(location, alarm);
            }
        }

        /**
         * 计算两点之间的距离
         *
         * @param latLng1 点1
         * @param latLng2 点2
         */
        private void distanceCalculate(LatLng latLng1, LatLng latLng2) {
            float distance = AMapUtils.calculateLineDistance(latLng1, latLng2);
            if (distance > 1000) {
                alertDetailGuide.setText(String.format(Locale.getDefault(), "%.2f", distance / 1000).concat(" km"));
            } else {
                alertDetailGuide.setText(String.format(Locale.getDefault(), "%.2f", distance).concat(" m"));
            }
        }
    }
}