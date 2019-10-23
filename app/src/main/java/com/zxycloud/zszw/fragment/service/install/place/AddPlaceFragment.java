package com.zxycloud.zszw.fragment.service.install.place;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.Html;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.geocoder.RegeocodeAddress;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.zxycloud.zszw.R;
import com.zxycloud.zszw.base.BaseMapViewFragment;
import com.zxycloud.zszw.base.MyBaseAdapter;
import com.zxycloud.zszw.event.type.SelectType;
import com.zxycloud.zszw.listener.NetRequestListener;
import com.zxycloud.zszw.listener.OnMapLocationListener;
import com.zxycloud.zszw.model.ResultUserPhoneBean;
import com.zxycloud.zszw.model.bean.UserPhoneBean;
import com.zxycloud.common.base.BaseBean;
import com.zxycloud.common.utils.CommonUtils;
import com.zxycloud.common.utils.SPUtils;
import com.zxycloud.common.utils.netWork.ApiRequest;
import com.zxycloud.common.utils.netWork.NetBean;
import com.zxycloud.common.widget.BswRecyclerView.RecyclerViewHolder;

import java.util.List;

public class AddPlaceFragment extends BaseMapViewFragment implements Toolbar.OnMenuItemClickListener {
    private String areaId;
    private EditText etAdminPhone, etPlaceName, etPlaceLocation, etPlaceUnit;
    private LatLonPoint latlon;
    private boolean isSearchPhone;
    private RecyclerView phoneRecycler;
    private MyBaseAdapter phoneAdapter;
    private String AdminId;

    public static AddPlaceFragment newInstance(String areaId, String areaName) {
        AddPlaceFragment fragment = new AddPlaceFragment();
        Bundle bundle = new Bundle();
        bundle.putString("areaId", areaId);
        bundle.putString("areaName", areaName);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.add_place;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        setToolbarTitle(R.string.add_place).initToolbarNav().setToolbarMenu(R.menu.save, this);
        etAdminPhone = findViewById(R.id.et_admin_phone);
        etPlaceName = findViewById(R.id.et_place_name);
        etPlaceUnit = findViewById(R.id.et_place_unit);
        etPlaceLocation = findViewById(R.id.et_place_location);

        areaId = getArguments().getString("areaId");
        if (getArguments().getString("areaName") != null) {
            etPlaceUnit.setText(getArguments().getString("areaName"));
        } else {
            etPlaceUnit.setText(CommonUtils.string().getString(getContext(), R.string.select_area));
            setOnClickListener(this, R.id.et_place_unit);
        }

        initMapView(savedInstanceState).setPointToCenter(
                CommonUtils.measureScreen().getScreenWidth(_mActivity) / 2,
                ((CommonUtils.measureScreen().getScreenHeight(_mActivity)) * 3 / 5));

        setOnMarkerClickListener(new OnMapLocationListener() {
            @Override
            public void onRegeocodeSearched(RegeocodeResult regeocodeResult, int i) {
                latlon = regeocodeResult.getRegeocodeQuery().getPoint();
                RegeocodeAddress regeocodeAddress = regeocodeResult.getRegeocodeAddress();
                etPlaceLocation.setText(regeocodeAddress.getFormatAddress());
            }
        });


        initPhoneEdit();
        etAdminPhone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // 搜索电话号码
                if (isSearchPhone) {
                    String oldStr = s.toString().substring(0, etAdminPhone.getSelectionStart());
                    if (oldStr.isEmpty()) {
                        if (start == 0 && count == 0) {
                            isSearchPhone = false;
                            AdminId = null;
                            etAdminPhone.setText(oldStr);
                        }
                        phoneRecycler.setVisibility(View.GONE);
                    } else {
                        netWork().setRequestListener(NetBean.actionGetUserList, 800).setRequestParams("searchInfo", oldStr);
                    }
                } else {
                    isSearchPhone = true;
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        etAdminPhone.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    isSearchPhone = true;
                } else {
                    isSearchPhone = false;
                    etAdminPhone.setText(etAdminPhone.getText().toString());
                    phoneRecycler.setVisibility(View.GONE);
                }
            }
        });
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        switch (view.getId()) {
            case R.id.et_place_unit:
                // 选择父级区域
                startForResult(SelectAreaFragment.newInstance(SelectType.PLACE_TYPE), 1002);
                break;
        }
    }

    private void initPhoneEdit() {
        phoneRecycler = findViewById(R.id.recycler);
        phoneRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        phoneAdapter = new MyBaseAdapter(getContext(), R.layout.base_textview, new MyBaseAdapter.OnBindViewHolderListener() {
            @Override
            public void onBindViewHolder(int position, View view, RecyclerViewHolder holder) {
                final UserPhoneBean bean = (UserPhoneBean) phoneAdapter.getData().get(position);
                TextView textView = holder.getView(R.id.tv_index);
                textView.setSingleLine(true);
                textView.setEllipsize(TextUtils.TruncateAt.END);
                textView.setText(CommonUtils.string().getString(bean.getPhoneNumber())
                        .concat("(").concat(CommonUtils.string().getString(bean.getUserAccount())).concat(")")
                        .concat("(").concat(CommonUtils.string().getString(bean.getUserName())).concat(")"));
                textView.setPadding(0, 0, 0, 8);
                textView.setTextColor(Color.GRAY);
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AdminId = bean.getId();
                        etAdminPhone.setText(bean.getPhoneNumber());
                        etAdminPhone.setSelection(bean.getPhoneNumber().length());
                    }
                });
            }
        });
        phoneRecycler.setAdapter(phoneAdapter);

        initPhoneData();
    }

    private void initPhoneData() {
        if (!TextUtils.isEmpty(CommonUtils.getSPUtils(getContext()).getString(SPUtils.PROJECT_ID)))
            netWork().setRequestListener(new NetRequestListener() {
                @Override
                public void success(String action, BaseBean baseBean, Object tag) {
                    if (baseBean.isSuccessful())
                        switch (action) {
                            case NetBean.actionGetUserList:
                                if (isSearchPhone)
                                    showPhone(((ResultUserPhoneBean) baseBean).getData());
                                break;
                            case NetBean.actionPostPlaceAdd:
                                setFragmentResult(RESULT_OK, new Bundle());
                                finish();
                                CommonUtils.toast(getContext(), baseBean.getMessage());
                                break;
                        }
                    else
                        CommonUtils.toast(getContext(), baseBean.getMessage());
                }
            }, netWork().apiRequest(NetBean.actionGetUserList, ResultUserPhoneBean.class, ApiRequest.REQUEST_TYPE_GET)
                    .setRequestParams("projectId", CommonUtils.getSPUtils(getContext()).getString(SPUtils.PROJECT_ID))
                    .setRequestParams("searchInfo", "")// 模糊搜索电话号
                    .setRequestParams("pageSize", 5)
                    .setRequestParams("pageIndex", 1));
    }

    public void showPhone(List<UserPhoneBean> bean) {
        if (etAdminPhone.hasFocus()) {
            isSearchPhone = false;
            int len = etAdminPhone.getSelectionStart();
            String oldStr = etAdminPhone.getText().toString().substring(0, len);
            if (bean != null && !bean.isEmpty() && bean.size() > 0) {
                AdminId = bean.get(0).getId();
                String html1 = oldStr + "<html><body><a><font color=\"#bbbbbb\">" + bean.get(0).getPhoneNumber().replace(oldStr, "") + "</a></body></html>";
                etAdminPhone.setText(Html.fromHtml(html1));
                etAdminPhone.setSelection(len);
                bean.remove(0);
                phoneAdapter.setData(bean);
                if (bean.size() > 0)
                    phoneRecycler.setVisibility(View.VISIBLE);
            } else {
                AdminId = null;
                etAdminPhone.setText(oldStr);
                etAdminPhone.setSelection(oldStr.length());
                phoneRecycler.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {
        if (etAdminPhone.getText().toString().trim().isEmpty()) {
            CommonUtils.toast(getContext(), R.string.hint_null_phone);
        } else if (etPlaceName.getText().toString().trim().isEmpty()) {
            CommonUtils.toast(getContext(), R.string.hint_null_place_name);
        } else if (etPlaceUnit.getText().toString().trim().isEmpty()) {
            CommonUtils.toast(getContext(), R.string.hint_null_unit);
        } else if (etPlaceLocation.getText().toString().trim().isEmpty()) {
            CommonUtils.toast(getContext(), R.string.hint_null_lcation);
        } else {
            addPlaceRequest();
        }
        return true;
    }

    private void addPlaceRequest() {
        netWork().addRequestListener(netWork().apiRequest(NetBean.actionPostPlaceAdd, BaseBean.class, ApiRequest.REQUEST_TYPE_POST)
                .setRequestParams("placeName", etPlaceName.getText().toString())// 场所名称
                .setRequestParams("projectId", CommonUtils.getSPUtils(getContext()).getString(SPUtils.PROJECT_ID))// 单位ID areaId和projectId至少需要一个
                .setRequestParams("areaId", areaId)// 区域ID areaId和projectId至少需要一个
                .setRequestParams("placePrincipalName", null)// 场所负责人名称
                .setRequestParams("placePrincipalPhoneNumber", null)// 场所负责人电话
                .setRequestParams("placeAdminPhone", etAdminPhone.getText().toString())// 场所管理员手机号
                .setRequestParams("placeAdminId", AdminId)// 场所管理员ID
                .setRequestParams("placeAddress", etPlaceLocation.getText().toString())// 场所地址
                .setRequestParams("gcj02Latitude", latlon != null ? latlon.getLatitude() : "")// 国测局纬度
                .setRequestParams("gcj02Longitude", latlon != null ? latlon.getLongitude() : "")// 国测局经度
                .setRequestParams("wgs84Latitude", latlon != null ? CommonUtils.gcToWgsLat(latlon.getLatitude(), latlon.getLongitude()) : "")// 世界坐标纬度
                .setRequestParams("wgs84Longitude", latlon != null ? CommonUtils.gcToWgsLon(latlon.getLatitude(), latlon.getLongitude()) : ""));// 世界坐标经度
    }

    @Override
    public void onFragmentResult(int requestCode, int resultCode, Bundle data) {
        super.onFragmentResult(requestCode, resultCode, data);
        if (requestCode == 1002 && resultCode == RESULT_OK && data != null) {
            etPlaceUnit.setText(data.getString("areaName", ""));
            areaId = data.getString("areaId");
        }
    }

}
