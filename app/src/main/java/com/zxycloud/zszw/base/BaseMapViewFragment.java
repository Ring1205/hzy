package com.zxycloud.zszw.base;

import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.EditText;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.maps.model.animation.AlphaAnimation;
import com.amap.api.maps.model.animation.Animation;
import com.amap.api.maps.model.animation.AnimationSet;
import com.amap.api.maps.model.animation.ScaleAnimation;
import com.amap.api.maps.model.animation.TranslateAnimation;
import com.amap.api.services.core.AMapException;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.core.SuggestionCity;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.RegeocodeQuery;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;
import com.zxycloud.zszw.R;
import com.zxycloud.zszw.listener.OnMapLocationListener;
import com.zxycloud.common.utils.CommonUtils;
import com.zxycloud.common.utils.PermissionUtils;

import java.util.List;

import db.bsw.map.poi.MyPoiOverlay;

public abstract class BaseMapViewFragment extends BaseBackFragment implements LocationSource, PoiSearch.OnPoiSearchListener, View.OnClickListener  {
    private float Zoom = 16;//地图放大比例
    private AMap aMap;
    private MapView mapView;
    private AMapLocationClient mlocationClient;
    private LocationSource.OnLocationChangedListener mListener;
    private AMapLocationClientOption mLocationOption;

    private PoiSearch.Query query;// Poi查询条件类
    private PoiResult poiResult; // poi返回的结果
    private PoiSearch poiSearch;// POI搜索
    private MyPoiOverlay poiOverlay;// poi图层
    private List<PoiItem> poiItems;// poi数据
    private LatLonPoint latLng;// poi中心点
    private Marker mlastMarker, screenMarker;

    private boolean followMove;
    private CardView cvLayout;
    private EditText inputEdittext;
    private OnMapLocationListener listener;

    /**
     * 初始化地图
     *
     * @param savedInstanceState
     */
    public AMap initMapView(Bundle savedInstanceState) {
        mapView = findViewById(R.id.map_view);
        mapView.onCreate(savedInstanceState);
        PermissionUtils.setRequestPermissions(this, new PermissionUtils.PermissionGrant() {
            @Override
            public Integer[] onPermissionGranted() {
                return new Integer[]{PermissionUtils.CODE_ACCESS_COARSE_LOCATION, PermissionUtils.CODE_ACCESS_FINE_LOCATION};
            }

            @Override
            public void onRequestResult(List<String> deniedPermission) {
                if (CommonUtils.judgeListNull(deniedPermission) != 0) {
                    finish();
                }
            }
        });

        aMap = mapView.getMap();

        if (aMap != null){
            //初始化地图属性
            //地图模式可选类型：MAP_TYPE_NORMAL 矢量地图模式 ,MAP_TYPE_SATELLITE 卫星地图模式,MAP_TYPE_NIGHT 夜景地图模式,MAP_TYPE_NAVI 导航地图模式
            aMap.setMapType(AMap.MAP_TYPE_NORMAL);// 普通地图模式
            //设置地图可以执行的手势 ：
            // 设置地图是否可以手势滑动 setScrollGesturesEnabled
            // 设置地图是否可以手势缩放大小 setZoomGesturesEnabled
            // 设置地图是否可以倾斜 setTiltGesturesEnabled
            // 设置地图是否可以旋转 setRotateGesturesEnabled
            // 设置地图默认的比例尺是否显示 setScaleControlsEnabled
            // 设置地图默认的缩放按钮是否显示 setZoomControlsEnabled
            // 设置地图默认的指南针是否显示 setCompassEnabled
            final UiSettings mUiSettings = aMap.getUiSettings();
            mUiSettings.setZoomControlsEnabled(false);// 设置隐藏缩放按钮
            // 是否显示默认的定位按钮 setMyLocationButtonEnabled
//        mUiSettings.setMyLocationButtonEnabled(false);

            aMap.setLocationSource(this);// 设置定位监听 获取OnLocationChangedListener显示定位小图标
            aMap.setMyLocationEnabled(true);//开启定位
            aMap.moveCamera(CameraUpdateFactory.zoomTo(Zoom));//设置地图可视等级

            aMap.setOnMapTouchListener(new AMap.OnMapTouchListener() {
                @Override
                public void onTouch(MotionEvent motionEvent) {
                    //用户拖动地图后，不再跟随移动，直到用户点击定位按钮
                    followMove = false;
                }
            });

            // 设置定位样式
            setLocationStyle();

            // 设置Marker
            setMarker();
        }

        setOnClickListener(this, R.id.img_btn_location);
        return aMap;
    }

    /**
     * 设置定位样式
     */
    private void setLocationStyle() {
        MyLocationStyle myLocationStyle = new MyLocationStyle();
        myLocationStyle.strokeColor(Color.TRANSPARENT);// 设置圆形的边框颜色
        myLocationStyle.radiusFillColor(Color.TRANSPARENT);// 设置圆形的填充颜色
        aMap.setMyLocationStyle(myLocationStyle);
    }

    /**
     * 初始化搜索功能
     */
    public void initSearch() {
        cvLayout = findViewById(R.id.cv_search_layout);
        inputEdittext = findViewById(R.id.input_edittext);

        cvLayout.setVisibility(View.VISIBLE);

        setOnClickListener(this, R.id.btn_search);
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
//        aMap.setOnInfoWindowClickListener(this);// 设置点击infoWindow事件监听器
//        aMap.setOnMarkerClickListener(this);// 设置点击marker事件监听器
//        aMap.setOnMarkerDragListener(this);// 设置marker可拖拽事件监听器
//        aMap.setOnMapLoadedListener(this);// 设置amap加载成功事件监听器

        // 设置可视范围变化时的回调的接口方法
        aMap.setOnCameraChangeListener(new AMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(CameraPosition position) {

            }

            @Override
            public void onCameraChangeFinish(CameraPosition postion) {
                //屏幕中心的Marker跳动
                startJumpAnimation();
            }
        });

        aMap.setOnMapClickListener(new AMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                if (mlastMarker != null) {
                    resetlastmarker();
                }
            }
        });

        aMap.setOnMarkerClickListener(new AMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                mOnMarkerClick(marker);
                return true;
            }
        });
    }

    /**
     * 在屏幕中心添加一个Marker
     */
    public void addMarkerInScreenCenter() {
        LatLng latLng = aMap.getCameraPosition().target;
        Point screenPosition = aMap.getProjection().toScreenLocation(latLng);
        screenMarker = aMap.addMarker(new MarkerOptions()
                .anchor(0.5f, 0.5f)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_location_on_black_24dp)));
        //设置Marker在屏幕上,不跟随地图移动
        screenMarker.setPositionByPixels(screenPosition.x, screenPosition.y);
    }

    public void setOnMarkerClickListener(final OnMapLocationListener listener){
        this.listener = listener;
        aMap.setOnMapClickListener(new AMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                aMap.clear();
                aMap.addMarker(new MarkerOptions()
                        .anchor(0.5f, 0.5f)
                        .icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.ic_location_on_black_24dp)))
                        .position(latLng));

                getAddressByLatlng(latLng);
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.img_btn_location:
                mlocationClient.startLocation();
                break;
            case R.id.btn_search:
                doSearchQuery(inputEdittext.getText().toString());
                break;
        }
    }

    /**
     * 搜索周边
     */
    private void doSearchQuery(String keyWord) {
        query = new PoiSearch.Query(keyWord, "", "");// 第一个参数表示搜索字符串，第二个参数表示poi搜索类型，第三个参数表示poi搜索区域（空字符串代表全国）
        query.setPageSize(100);// 设置每页最多返回多少条poiitem
        query.setPageNum(1);// 设置查第一页

        poiSearch = new PoiSearch(getContext(), query);
        poiSearch.setOnPoiSearchListener(this);
        // 周边搜索
        poiSearch.setBound(new PoiSearch.SearchBound(latLng, 5000, true));// 设置搜索区域为以latLng点为圆心，其周围5000米范围
        poiSearch.searchPOIAsyn();// 异步搜索
    }
    private void getAddressByLatlng(LatLng latLng) {
        //地理搜索类
        GeocodeSearch geocodeSearch = new GeocodeSearch(getContext());
        geocodeSearch.setOnGeocodeSearchListener(new GeocodeSearch.OnGeocodeSearchListener() {
            @Override
            public void onRegeocodeSearched(RegeocodeResult regeocodeResult, int i) {
                listener.onRegeocodeSearched(regeocodeResult, i);
            }

            @Override
            public void onGeocodeSearched(GeocodeResult geocodeResult, int i) {

            }
        });

        //逆地理编码查询条件：逆地理编码查询的地理坐标点、查询范围、坐标类型。
        LatLonPoint latLonPoint = new LatLonPoint(latLng.latitude, latLng.longitude);
        RegeocodeQuery query = new RegeocodeQuery(latLonPoint, 500f, GeocodeSearch.AMAP);
        //异步查询
        geocodeSearch.getFromLocationAsyn(query);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionUtils.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
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

    /**
     * 定位成功接口回调
     */
    private AMapLocationListener locationListener = new AMapLocationListener() {
        @Override
        public void onLocationChanged(AMapLocation amapLocation) {
            if (mListener != null && amapLocation != null && amapLocation.getErrorCode() == 0) {
                //定位成功回调信息，设置相关消息
                amapLocation.getLocationType();//获取当前定位结果来源，如网络定位结果，详见定位类型表
                double lat = amapLocation.getLatitude();//获取纬度
                double lng = amapLocation.getLongitude();//获取经度
                latLng = new LatLonPoint(lat, lng);
                /*Marker marker = */
//                aMap.addMarker(new MarkerOptions().position(new LatLng(lat, lng)));
                amapLocation.getAccuracy();//获取精度信息
                mListener.onLocationChanged(amapLocation);// 显示系统小蓝点
                if (!followMove) {
                    mlocationClient.stopLocation();
                }

                getAddressByLatlng(new LatLng(lat, lng));
            } else {
                //显示错误信息ErrCode是错误码，errInfo是错误信息，详见错误码表。
                Log.e("AmapError", "location Error, ErrCode:"
                        + amapLocation.getErrorCode() + ", errInfo:"
                        + amapLocation.getErrorInfo());
            }
        }
    };

    @Override
    public void activate(OnLocationChangedListener onLocationChangedListener) {
        mListener = onLocationChangedListener;
        if (mlocationClient == null) {
            mlocationClient = new AMapLocationClient(getContext());
            //初始化定位参数
            mLocationOption = new AMapLocationClientOption();
            //设置定位监听
            mlocationClient.setLocationListener(locationListener);
            //设置定位模式为高精度模式，Battery_Saving为低功耗模式，Device_Sensors是仅设备模式
            mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
            //设置定位间隔,单位毫秒,默认为2000ms
            mLocationOption.setInterval(1000);
            //设置定位参数
            mlocationClient.setLocationOption(mLocationOption);
            // 此方法为每隔固定时间会发起一次定位请求，为了减少电量消耗或网络流量消耗，
            // 注意设置合适的定位时间的间隔（最小间隔支持为1000ms），并且在合适时间调用stopLocation()方法来取消定位请求
            // 在定位结束后，在合适的生命周期调用onDestroy()方法
            // 在单次定位情况下，定位无论成功与否，都无需调用stopLocation()方法移除请求，定位sdk内部会移除
            //启动定位
            mlocationClient.startLocation();
        }
    }

    @Override
    public void deactivate() {
        mListener = null;
        if (mlocationClient != null) {
            mlocationClient.stopLocation();
            mlocationClient.onDestroy();
        }
        mlocationClient = null;
    }

    /**
     * Marker动画：会生长的Marker ture; 会呼吸的Marker faste
     */
    private void startGrowAnimation(Marker growMarker, Boolean bl) {
        if (growMarker != null) {
            if (bl) {
                Animation animation = new ScaleAnimation(0, 1, 0, 1);
                animation.setInterpolator(new LinearInterpolator());
                //整个移动所需要的时间
                animation.setDuration(1000);
                //设置动画
                growMarker.setAnimation(animation);
                //开始动画
                growMarker.startAnimation();
            } else {
                // 动画执行完成后，默认会保持到最后一帧的状态
                AnimationSet animationSet = new AnimationSet(true);

                AlphaAnimation alphaAnimation = new AlphaAnimation(0.5f, 0f);
                alphaAnimation.setDuration(2000);
                // 设置不断重复
                alphaAnimation.setRepeatCount(Animation.INFINITE);

                ScaleAnimation scaleAnimation = new ScaleAnimation(1, 3.5f, 1, 3.5f);
                scaleAnimation.setDuration(2000);
                // 设置不断重复
                scaleAnimation.setRepeatCount(Animation.INFINITE);


                animationSet.addAnimation(alphaAnimation);
                animationSet.addAnimation(scaleAnimation);
                animationSet.setInterpolator(new LinearInterpolator());
                growMarker.setAnimation(animationSet);
                growMarker.startAnimation();
            }
        }
    }

    /**
     * 屏幕中心marker 跳动
     */
    public void startJumpAnimation() {
        if (screenMarker != null) {
            //根据屏幕距离计算需要移动的目标点
            final LatLng latLng = screenMarker.getPosition();
            Point point = aMap.getProjection().toScreenLocation(latLng);
            point.y -= 15 * getContext().getResources().getDisplayMetrics().density + 0.5f;
            LatLng target = aMap.getProjection()
                    .fromScreenLocation(point);
            //使用TranslateAnimation,填写一个需要移动的目标点
            Animation animation = new TranslateAnimation(target);
            animation.setInterpolator(new Interpolator() {
                @Override
                public float getInterpolation(float input) {
                    // 模拟重加速度的interpolator
                    if (input <= 0.5) {
                        return (float) (0.5f - 2 * (0.5 - input) * (0.5 - input));
                    } else {
                        return (float) (0.5f - Math.sqrt((input - 0.5f) * (1.5f - input)));
                    }
                }
            });
            //整个移动所需要的时间
            animation.setDuration(500);
            //设置动画
            screenMarker.setAnimation(animation);
            //开始动画
            screenMarker.startAnimation();
        }
    }

    @Override
    public void onPoiSearched(PoiResult result, int rcode) {
        if (rcode == AMapException.CODE_AMAP_SUCCESS) {
            if (result != null && result.getQuery() != null) {// 搜索poi的结果
                if (result.getQuery().equals(query)) {// 是否是同一条
                    poiResult = result;
                    poiItems = poiResult.getPois();// 取得第一页的poiitem数据，页数从数字0开始
                    List<SuggestionCity> suggestionCities = poiResult
                            .getSearchSuggestionCitys();// 当搜索不到poiitem数据时，会返回含有搜索关键字的城市信息
                    if (poiItems != null && poiItems.size() > 0) {
                        //并还原点击marker样式
                        if (mlastMarker != null) {
                            resetlastmarker();
                        }
                        //清理之前搜索结果的marker
                        if (poiOverlay != null) {
                            poiOverlay.removeFromMap();
                        }
                        aMap.clear();
                        poiOverlay = new MyPoiOverlay(getContext(), aMap, poiItems);
                        poiOverlay.addToMap();
                        poiOverlay.zoomToSpan();

                        aMap.addMarker(new MarkerOptions()
                                .anchor(0.5f, 0.5f)
                                .icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.point4)))
                                .position(new LatLng(latLng.getLatitude(), latLng.getLongitude())));

                        // 呼吸动画
                        Marker marker = aMap.addMarker(new MarkerOptions()
                                .anchor(0.5f, 0.5f)
                                .icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.point4)))
                                .position(new LatLng(latLng.getLatitude(), latLng.getLongitude())));
                        startGrowAnimation(marker, false);

                    } else if (suggestionCities != null && suggestionCities.size() > 0) {
                        showSuggestCity(suggestionCities);
                    } else {
                        CommonUtils.toast(getContext(), R.string.safety_fire_knowledge);
                    }
                }
            } else {
                CommonUtils.toast(getContext(), R.string.safety_fire_knowledge);
            }
        } else {
            CommonUtils.toast(getContext(), R.string.safety_fire_knowledge);
        }
    }

    @Override
    public void onPoiItemSearched(PoiItem poiItem, int i) {

    }

    /**
     * poi没有搜索到数据，返回一些推荐城市的信息
     */
    private void showSuggestCity(List<SuggestionCity> cities) {
        String infomation = "推荐城市\n";
        for (int i = 0; i < cities.size(); i++) {
            infomation += "城市名称:" + cities.get(i).getCityName() + "城市区号:"
                    + cities.get(i).getCityCode() + "城市编码:"
                    + cities.get(i).getAdCode() + "\n";
        }
    }

    /**
     * 将之前被点击的marker置为原来的状态
     */
    private void resetlastmarker() {
        aMap.setInfoWindowAdapter(null);
        int index = poiOverlay.getPoiIndex(mlastMarker);
        if (index < 10) {
            mlastMarker.setIcon(BitmapDescriptorFactory
                    .fromBitmap(BitmapFactory.decodeResource(
                            getResources(),
                            poiOverlay.markers[index])));
        } else {
            mlastMarker.setIcon(BitmapDescriptorFactory.fromBitmap(
                    BitmapFactory.decodeResource(getResources(), R.drawable.marker_other_highlight)));
        }
        mlastMarker = null;

    }

    /**
     * maker的点击事件
     *
     * @param marker
     */
    private void mOnMarkerClick(Marker marker) {
        if (marker.getObject() != null) {
            try {
                PoiItem mCurrentPoi = (PoiItem) marker.getObject();
                if (mlastMarker == null) {
                    mlastMarker = marker;
                } else {
                    // 将之前被点击的marker置为原来的状态
                    resetlastmarker();
                    mlastMarker = marker;
                }
                marker.setIcon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.poi_marker_pressed)));
                marker.setTitle(mCurrentPoi.getTitle());
                marker.setSnippet(mCurrentPoi.getSnippet().concat("\n") + mCurrentPoi.getDistance());
                marker.setDraggable(true);
                marker.showInfoWindow();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            resetlastmarker();
        }
    }
}
