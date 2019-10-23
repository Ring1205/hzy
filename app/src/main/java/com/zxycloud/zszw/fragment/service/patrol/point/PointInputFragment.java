package com.zxycloud.zszw.fragment.service.patrol.point;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.GravityCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;

import com.zxycloud.zszw.MainActivity;
import com.zxycloud.zszw.R;
import com.zxycloud.zszw.adapter.PatrolProAdapter;
import com.zxycloud.zszw.base.BaseBackFragment;
import com.zxycloud.zszw.event.type.SelectType;
import com.zxycloud.zszw.fragment.common.CaptureFragment;
import com.zxycloud.zszw.fragment.common.StringSelectFragment;
import com.zxycloud.zszw.listener.NetRequestListener;
import com.zxycloud.zszw.listener.OnItemClickListener;
import com.zxycloud.zszw.listener.OnNewIntentListener;
import com.zxycloud.zszw.model.ResultCardTypeBean;
import com.zxycloud.zszw.model.ResultEquTypeBean;
import com.zxycloud.zszw.model.ResultPatrolPointBean;
import com.zxycloud.zszw.model.ResultPointStateBean;
import com.zxycloud.zszw.model.ResultTagNumVerifyBean;
import com.zxycloud.zszw.model.ResultTypeMenuBean;
import com.zxycloud.common.base.BaseBean;
import com.zxycloud.common.utils.CommonUtils;
import com.zxycloud.common.utils.SPUtils;
import com.zxycloud.common.utils.netWork.ApiRequest;
import com.zxycloud.common.utils.netWork.NetBean;
import com.zxycloud.common.widget.ScanPopupWindow;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class PointInputFragment extends BaseBackFragment implements Toolbar.OnMenuItemClickListener, View.OnClickListener {
    private List<ResultCardTypeBean.DataBean> cardTypeBeans;
    private List<ResultTypeMenuBean.DataBean> dataBeans;
    private List<ResultEquTypeBean.DataBean> equTypeBeans;
    private PatrolProAdapter patrolProAdapter;
    private int patrolItemType, cardType, equType;
    private boolean isEdit;
    private String areaId;
    private String openDate;
    private String useYears = "3";// 使用年限
    // nfc相关参数
    private ScanPopupWindow scanPopupWindow;
    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            if (scanPopupWindow != null)
                scanPopupWindow.dismiss();
            return false;
        }
    });

    public static PointInputFragment newInstance(String id) {
        PointInputFragment fragment = new PointInputFragment();
        Bundle arg = new Bundle();
        arg.putString("edit_id", id);
        fragment.setArguments(arg);
        return fragment;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.point_add;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        if (getArguments().getString("edit_id") != null)
            setToolbarTitle(R.string.change_point).initToolbarNav().setToolbarMenu(R.menu.point_finish, this);
        else
            setToolbarTitle(R.string.add_point).initToolbarNav().setToolbarMenu(R.menu.point_finish, this);

        RecyclerView recyclerView = findViewById(R.id.recycler_patrol_project);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        patrolProAdapter = new PatrolProAdapter(getContext());
        recyclerView.setAdapter(patrolProAdapter);

        initData();
        // 初始化生产日期
        Calendar ca = Calendar.getInstance();
        mYear = ca.get(Calendar.YEAR);
        mMonth = ca.get(Calendar.MONTH);
        mDay = ca.get(Calendar.DAY_OF_MONTH);
        ((EditText) findViewById(R.id.et_date_of_manufacture)).setText(CommonUtils.date().getTime());// 默认日期为当日

        setOnClickListener(this, R.id.et_scan_tag_id, R.id.et_clock_area, R.id.et_date_of_manufacture, R.id.et_clock_in_type, R.id.et_select_type, R.id.et_facility_type);
    }

    private void initData() {
        netWork().setRequestListener(netRequestListener,
                netWork().apiRequest(NetBean.actionGetCardTypeMenuList, ResultCardTypeBean.class, ApiRequest.REQUEST_TYPE_GET).setApiType(ApiRequest.API_TYPE_PATROL),// 打卡类型
                // 下面两个请求属于懒加载状态，参数不全
                netWork().apiRequest(NetBean.actionGetEquTypeMenu, ResultEquTypeBean.class, ApiRequest.REQUEST_TYPE_GET, R.id.loading).setApiType(ApiRequest.API_TYPE_PATROL),//设施类型列表
                netWork().apiRequest(NetBean.actionPostPatrolByType, ResultPointStateBean.class, ApiRequest.REQUEST_TYPE_POST)
                        .setApiType(ApiRequest.API_TYPE_PATROL)//获取巡查项集合
                        .setRequestParams("companyId", CommonUtils.getSPUtils(getContext()).getString(SPUtils.PROJECT_ID)),
                getArguments().getString("edit_id") != null ? // 该页面是否为编辑页面
                        netWork().apiRequest(NetBean.actionGetPatrolPointDetails, ResultPatrolPointBean.class, ApiRequest.REQUEST_TYPE_GET, R.id.loading)// 编辑数据
                                .setApiType(ApiRequest.API_TYPE_PATROL).setRequestParams("id", getArguments().getString("edit_id")) :
                        netWork().apiRequest(NetBean.actionGetPatrolTypeMenu, ResultTypeMenuBean.class, ApiRequest.REQUEST_TYPE_GET).setApiType(ApiRequest.API_TYPE_PATROL));//巡查类型列表
    }

    private void recallPatrolPoint() {
        netWork().addRequestListener(getArguments().getString("edit_id") != null ? // 该页面是否为编辑页面
                netWork().apiRequest(NetBean.actionGetPatrolPointDetails, ResultPatrolPointBean.class, ApiRequest.REQUEST_TYPE_GET, R.id.loading)// 编辑数据
                        .setApiType(ApiRequest.API_TYPE_PATROL).setRequestParams("id", getArguments().getString("edit_id")) :
                netWork().apiRequest(NetBean.actionGetPatrolTypeMenu, ResultTypeMenuBean.class, ApiRequest.REQUEST_TYPE_GET).setApiType(ApiRequest.API_TYPE_PATROL));//巡查类型列表
    }

    /**
     * 选择巡查项类型
     *
     * @param i
     */
    private void setPatrolItemType(int i) {
        if (!((EditText) findViewById(R.id.et_select_type)).getText().toString().equals(dataBeans.get(i).getPatrolItemTypeName())) {
            ((EditText) findViewById(R.id.et_select_type)).setText(dataBeans.get(i).getPatrolItemTypeName());
            if (!isEdit) {//非再编译第一次修改类型排除
                ((EditText) findViewById(R.id.et_facility_type)).setText("");
                setFacilityType(0);
            }
        }
        patrolItemType = dataBeans.get(i).getId();
        netWork().setRequestListener(NetBean.actionGetEquTypeMenu).setRequestParams("patrolType", patrolItemType);//设施类型列表
    }

    /**
     * 选择设施类型
     */
    private void setFacilityType(int equ) {
        equType = equ;
        if (!TextUtils.isEmpty(((EditText) findViewById(R.id.et_select_type)).getText().toString()) &&
                !TextUtils.isEmpty(((EditText) findViewById(R.id.et_facility_type)).getText().toString()) && equType != 0 && patrolItemType != 0)
            netWork().setRequestListener(NetBean.actionPostPatrolByType, 1000L).setRequestParams("equTypeList", new int[]{equType}).setRequestParams("patrolItemType", patrolItemType);
        else
            patrolProAdapter.setDataBeans(null);
    }

    /**
     * 选择打卡类型
     *
     * @param i
     */
    private void setCardType(int i) {
        ((EditText) findViewById(R.id.et_clock_in_type)).setText(cardTypeBeans.get(i).getCardTypeName());
        cardType = cardTypeBeans.get(i).getId();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.et_scan_tag_id:
                final PopupMenu popupMenu = new PopupMenu(getContext(), view, GravityCompat.END);
                popupMenu.inflate(R.menu.task_pop);
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.scan_nfc:
                                scanPopupWindow = new ScanPopupWindow(getContext());
                                scanPopupWindow.show(LayoutInflater.from(getContext()).inflate(R.layout.activity_base, null));
                                ((MainActivity) getContext()).setmOnScanListener(new OnNewIntentListener() {
                                    @Override
                                    public void onIntentData(String nfcID, String labelID) {
                                        mHandler.sendMessage(new Message());
                                        isUniquenessCheck(labelID);
                                    }
                                });
                                break;
                            case R.id.scan_qr_code:
                                startForResult(CaptureFragment.newInstance(), 1010);
                                break;
                        }
                        popupMenu.dismiss();
                        return true;
                    }
                });
                popupMenu.show();
                break;
            case R.id.et_clock_area:
                // 选择区域
                startForResult(SelectPointAreaFragment.newInstance(SelectType.AREA_TYPE), 1011);
                break;
            case R.id.et_date_of_manufacture:
                // 弹出日历选择框
                new DatePickerDialog(getContext(), onDateSetListener, mYear, mMonth, mDay).show();
                break;
            case R.id.et_clock_in_type:// 打卡类型
                if (null == cardTypeBeans) {
                    CommonUtils.toast(_mActivity, R.string.data_abnormal);
                    netWork().addRequestListener(netWork().apiRequest(NetBean.actionGetCardTypeMenuList, ResultCardTypeBean.class, ApiRequest.REQUEST_TYPE_GET).setApiType(ApiRequest.API_TYPE_PATROL));//设施类型列表
                    return;
                }
                ArrayList<String> data = new ArrayList<>();
                for (ResultCardTypeBean.DataBean cardTypeBean : cardTypeBeans)
                    data.add(cardTypeBean.getCardTypeName());
                StringSelectFragment selectFragment = StringSelectFragment.newInstance(R.string.sign_in_type, data);
                selectFragment.setOnItemClickListener(new OnItemClickListener() {
                    @Override
                    public void onItemClick(int position, View view, RecyclerView.ViewHolder vh) {
                        setCardType(position);
                    }
                });
                start(selectFragment);
                break;
            case R.id.et_select_type:// 巡查项类型
                if (null == dataBeans) {
                    CommonUtils.toast(_mActivity, R.string.data_abnormal);
                    recallPatrolPoint();
                    return;
                }
                ArrayList<String> datas = new ArrayList<>();
                for (ResultTypeMenuBean.DataBean dataBean : dataBeans)
                    datas.add(dataBean.getPatrolItemTypeName());
                StringSelectFragment selectFragments = StringSelectFragment.newInstance(R.string.string_select_title_patrol_item_type, datas);
                selectFragments.setOnItemClickListener(new OnItemClickListener() {
                    @Override
                    public void onItemClick(int position, View view, RecyclerView.ViewHolder vh) {
                        isEdit = false;
                        setPatrolItemType(position);
                    }
                });
                start(selectFragments);
                break;
            case R.id.et_facility_type:// 设施类型
                if (null == equTypeBeans) {
                    CommonUtils.toast(_mActivity, R.string.data_abnormal);
                    netWork().setRequestListener(NetBean.actionGetEquTypeMenu).setRequestParams("patrolType", patrolItemType);//设施类型列表
                    return;
                }
                ArrayList<String> datass = new ArrayList<>();
                for (ResultEquTypeBean.DataBean equTypeBean : equTypeBeans)
                    datass.add(equTypeBean.getEquTypeName());
                StringSelectFragment selectFragmentss = StringSelectFragment.newInstance(R.string.string_select_title_facility_type, datass);
                selectFragmentss.setOnItemClickListener(new OnItemClickListener() {
                    @Override
                    public void onItemClick(int position, View view, RecyclerView.ViewHolder vh) {
                        ((EditText) findViewById(R.id.et_facility_type)).setText(equTypeBeans.get(position).getEquTypeName());
                        setFacilityType(equTypeBeans.get(position).getId());
                    }
                });
                start(selectFragmentss);
                break;
        }
    }

    private MenuItem mMenuItem;

    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {
        mMenuItem = menuItem;
        switch (menuItem.getItemId()) {
            case R.id.action_finish:
                if (TextUtils.isEmpty(((EditText) findViewById(R.id.et_point_name)).getText().toString().trim())) {
                    CommonUtils.toast(getContext(), R.string.hint_enter_names);
                    return false;
                } else if (TextUtils.isEmpty(((EditText) findViewById(R.id.et_select_type)).getText().toString().trim())) {
                    CommonUtils.toast(getContext(), R.string.hint_select_type);
                    return false;
                } else if (TextUtils.isEmpty(((EditText) findViewById(R.id.et_facility_type)).getText().toString().trim())) {
                    CommonUtils.toast(getContext(), R.string.hint_facility_type);
                    return false;
                } else if (TextUtils.isEmpty(((EditText) findViewById(R.id.et_scan_tag_id)).getText().toString().trim())) {
                    CommonUtils.toast(getContext(), R.string.hint_scan_tag_id);
                    return false;
                } else if (patrolProAdapter.getIntener() == null || patrolProAdapter.getIntener().length == 0) {
                    CommonUtils.toast(getContext(), R.string.hint_patrol_project);
                    return false;
                } else if (areaId == null || TextUtils.isEmpty(((EditText) findViewById(R.id.et_clock_area)).getText().toString())) {
                    CommonUtils.toast(getContext(), R.string.hint_clock_area);
                    return false;
                } else if (TextUtils.isEmpty(((EditText) findViewById(R.id.et_devices_count)).getText().toString()) || Integer.valueOf(((EditText) findViewById(R.id.et_devices_count)).getText().toString()) < 1) {
                    CommonUtils.toast(getContext(), R.string.hint_durable_years);
                    return false;
                } else if (cardType == 0 || TextUtils.isEmpty(((EditText) findViewById(R.id.et_clock_in_type)).getText().toString())) {
                    CommonUtils.toast(getContext(), R.string.hint_clock_in_type);
                    return false;
                }
                mMenuItem.setCheckable(false);
                if (getArguments().getString("edit_id") != null)//编辑巡查点
                    netWork().addRequestListener(netWork().apiRequest(NetBean.actionPostEditPatrolPointById, BaseBean.class, ApiRequest.REQUEST_TYPE_POST).setApiType(ApiRequest.API_TYPE_PATROL)
                            .setRequestParams("id", getArguments().getString("edit_id"))
                            .setRequestParams("areaId", areaId)
                            .setRequestParams("areaName", ((EditText) findViewById(R.id.et_clock_area)).getText().toString())
                            .setRequestParams("cardType", cardType)// 打卡类型
                            .setRequestParams("durableYear", useYears)// 使用年限
                            .setRequestParams("producedDate", ((EditText) findViewById(R.id.et_date_of_manufacture)).getText().toString())// 生产日期
                            .setRequestParams("deviceCount", ((EditText) findViewById(R.id.et_devices_count)).getText().toString())// 设备数量
                            .setRequestParams("patrolPointName", ((EditText) findViewById(R.id.et_point_name)).getText().toString())// 巡查点名称
                            .setRequestParams("patrolItemType", patrolItemType)// 巡查项类型
                            .setRequestParams("equType", equType)// 巡查设施分类
                            .setRequestParams("equTypeName", ((EditText) findViewById(R.id.et_facility_type)).getText().toString())
                            .setRequestParams("tagNumber", ((EditText) findViewById(R.id.et_scan_tag_id)).getText().toString())
                            .setRequestParams("address", ((EditText) findViewById(R.id.et_point_location)).getText().toString())
                            .setRequestParams("openDate", openDate)// 获取当前日期，为投产日期
                            .setRequestParams("companyName", CommonUtils.getSPUtils(getContext()).getString(SPUtils.PROJECT_NAME))
                            .setRequestParams("companyId", CommonUtils.getSPUtils(getContext()).getString(SPUtils.PROJECT_ID))// 单位信息
                            .setRequestParams("ids", patrolProAdapter.getIntener()));
                else // 添加巡查点
                    netWork().addRequestListener(netWork().apiRequest(NetBean.actionPostMobilePatrolPoint, BaseBean.class, ApiRequest.REQUEST_TYPE_POST).setApiType(ApiRequest.API_TYPE_PATROL)
                            .setRequestParams("areaId", areaId)
                            .setRequestParams("areaName", ((EditText) findViewById(R.id.et_clock_area)).getText().toString())
                            .setRequestParams("cardType", cardType)// 打卡类型
                            .setRequestParams("durableYear", useYears)// 使用年限
                            .setRequestParams("producedDate", ((EditText) findViewById(R.id.et_date_of_manufacture)).getText().toString())// 生产日期
                            .setRequestParams("deviceCount", ((EditText) findViewById(R.id.et_devices_count)).getText().toString())// 设备数量
                            .setRequestParams("patrolPointName", ((EditText) findViewById(R.id.et_point_name)).getText().toString())// 巡查点名称
                            .setRequestParams("patrolItemType", patrolItemType)// 巡查项类型
                            .setRequestParams("equType", equType)// 巡查设施分类
                            .setRequestParams("equTypeName", ((EditText) findViewById(R.id.et_facility_type)).getText().toString())
                            .setRequestParams("tagNumber", ((EditText) findViewById(R.id.et_scan_tag_id)).getText().toString())
                            .setRequestParams("address", ((EditText) findViewById(R.id.et_point_location)).getText().toString())
                            .setRequestParams("openDate", CommonUtils.date().getTime())// 获取当前日期，为投产日期
                            .setRequestParams("companyName", CommonUtils.getSPUtils(getContext()).getString(SPUtils.PROJECT_NAME))
                            .setRequestParams("companyId", CommonUtils.getSPUtils(getContext()).getString(SPUtils.PROJECT_ID))// 单位信息
                            .setRequestParams("ids", patrolProAdapter.getIntener()));
                break;
        }
        return true;
    }

    @Override
    public void onFragmentResult(int requestCode, int resultCode, Bundle data) {
        super.onFragmentResult(requestCode, resultCode, data);
        if (requestCode == 1010 && resultCode == RESULT_OK && null != data) {
            isUniquenessCheck(data.getString(CaptureFragment.SCAN_RESULT, ""));
        } else if (requestCode == 1011 && resultCode == RESULT_OK && data != null) {
            ((EditText) findViewById(R.id.et_clock_area)).setText(data.getString("areaName", ""));
            areaId = data.getString("areaId");
        }
    }

    /**
     * 验证巡查编码的唯一性
     *
     * @param id
     */
    private void isUniquenessCheck(String id) {
        netWork().addRequestListener(netWork().apiRequest(NetBean.actionGetTagNumberVerify, ResultTagNumVerifyBean.class, ApiRequest.REQUEST_TYPE_GET).setApiType(ApiRequest.API_TYPE_PATROL).setRequestParams("tagNumber", id).setTag(id));
    }

    private int mYear, mMonth, mDay;
    /**
     * 日期选择器对话框监听
     */
    private DatePickerDialog.OnDateSetListener onDateSetListener = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            mYear = year;
            mMonth = monthOfYear;
            mDay = dayOfMonth;
            String days;
            if (mMonth + 1 < 10)
                if (mDay < 10)
                    days = new StringBuffer().append(mYear).append("-").append("0").
                            append(mMonth + 1).append("-").append("0").append(mDay).toString();
                else
                    days = new StringBuffer().append(mYear).append("-").append("0").
                            append(mMonth + 1).append("-").append(mDay).toString();
            else if (mDay < 10)
                days = new StringBuffer().append(mYear).append("-").
                        append(mMonth + 1).append("-").append("0").append(mDay).toString();
            else
                days = new StringBuffer().append(mYear).append("-").
                        append(mMonth + 1).append("-").append(mDay).toString();
            ((EditText) findViewById(R.id.et_date_of_manufacture)).setText(days);
        }
    };

    private NetRequestListener netRequestListener = new NetRequestListener() {
        @Override
        public void success(String action, BaseBean baseBean, Object tag) {
            if (baseBean.isSuccessful())
                switch (action) {
                    case NetBean.actionGetPatrolTypeMenu:// 获取巡查项类型列表
                        dataBeans = ((ResultTypeMenuBean) baseBean).getData();
                        if (dataBeans != null && !dataBeans.isEmpty() && dataBeans.size() > patrolItemType)
                            setPatrolItemType(patrolItemType - 1);
                        break;
                    case NetBean.actionGetCardTypeMenuList:// 获取打卡类型列表
                        cardTypeBeans = ((ResultCardTypeBean) baseBean).getData();
                        break;
                    case NetBean.actionGetEquTypeMenu:// 获取设施类型列表
                        equTypeBeans = ((ResultEquTypeBean) baseBean).getData();
                        break;
                    case NetBean.actionPostPatrolByType:// 通过类型集合获取巡查项集合
                        patrolProAdapter.setDataBeans(((ResultPointStateBean) baseBean).getData());
                        break;
                    case NetBean.actionGetPatrolPointDetails:// 编辑时获取的数据
                        isEdit = true;
                        ResultPatrolPointBean.DataBean dataBean = ((ResultPatrolPointBean) baseBean).getData();
                        ((EditText) findViewById(R.id.et_point_name)).setText(dataBean.getPatrolPointName());
                        ((EditText) findViewById(R.id.et_point_location)).setText(dataBean.getAddress());
                        findViewById(R.id.et_point_location).requestFocus();
                        ((EditText) findViewById(R.id.et_select_type)).setText(dataBean.getPatrolItemTypeName());
                        ((EditText) findViewById(R.id.et_facility_type)).setText(dataBean.getEquTypeName());
                        ((EditText) findViewById(R.id.et_scan_tag_id)).setText(dataBean.getTagNumber());
                        ((EditText) findViewById(R.id.et_clock_area)).setText(dataBean.getAreaName());
                        ((EditText) findViewById(R.id.et_date_of_manufacture)).setText(dataBean.getProducedDate());
                        ((EditText) findViewById(R.id.et_devices_count)).setText(String.valueOf(dataBean.getDeviceCount()));
                        ((EditText) findViewById(R.id.et_clock_in_type)).setText(dataBean.getCardTypeName());
                        String[] days = null;
                        if (!TextUtils.isEmpty(dataBean.getProducedDate()))
                            days = dataBean.getProducedDate().split("-");
                        if (days != null && days.length > 2) {
                            mYear = Integer.valueOf(days[0]);
                            mMonth = Integer.valueOf(days[1]);
                            mDay = Integer.valueOf(days[2]);
                        }
                        areaId = dataBean.getAreaId();
                        cardType = dataBean.getCardType();
                        useYears = dataBean.getDurableYear();
                        openDate = dataBean.getOpenDate();
                        patrolItemType = dataBean.getPatrolItemType();
                        equType = dataBean.getEquType();
                        patrolProAdapter.setDataBeans(dataBean.getPatrolItemVOList());
                        // 编辑时：标签编码和巡检点名称不能修改
                        if (getArguments().getString("edit_id") != null) {
                            findViewById(R.id.et_point_name).setFocusable(false);
                            findViewById(R.id.et_point_name).setFocusableInTouchMode(false);
                            findViewById(R.id.et_scan_tag_id).setOnClickListener(null);
                        }
                        netWork().addRequestListener(netWork().apiRequest(NetBean.actionGetPatrolTypeMenu, ResultTypeMenuBean.class, ApiRequest.REQUEST_TYPE_GET).setApiType(ApiRequest.API_TYPE_PATROL));
                        break;
                    case NetBean.actionPostMobilePatrolPoint:
                    case NetBean.actionPostEditPatrolPointById:
                        CommonUtils.toast(getContext(), baseBean.getMessage());
                        setFragmentResult(RESULT_OK, new Bundle());
                        finish();
                        break;
                    case NetBean.actionGetTagNumberVerify:
                        if (tag != null && ((ResultTagNumVerifyBean) baseBean).getData() != 1)
                            ((EditText) findViewById(R.id.et_scan_tag_id)).setText((CharSequence) tag);
                        else
                            CommonUtils.toast(getContext(), R.string.toast_tag_number_verify);
                        break;
                }
            if (mMenuItem != null)
                mMenuItem.setCheckable(true);
        }
    };
}
