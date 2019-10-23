package com.zxycloud.zszw.fragment.service.install.area;

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

import com.zxycloud.zszw.R;
import com.zxycloud.zszw.base.BaseBackFragment;
import com.zxycloud.zszw.base.MyBaseAdapter;
import com.zxycloud.zszw.event.type.SelectType;
import com.zxycloud.zszw.fragment.service.install.place.SelectAreaFragment;
import com.zxycloud.zszw.listener.NetRequestListener;
import com.zxycloud.zszw.listener.OnProjectObtainListener;
import com.zxycloud.zszw.model.ResultUserPhoneBean;
import com.zxycloud.zszw.model.bean.UserPhoneBean;
import com.zxycloud.common.base.BaseBean;
import com.zxycloud.common.utils.CommonUtils;
import com.zxycloud.common.utils.SPUtils;
import com.zxycloud.common.utils.netWork.ApiRequest;
import com.zxycloud.common.utils.netWork.NetBean;
import com.zxycloud.common.widget.BswRecyclerView.RecyclerViewHolder;

import java.util.List;

public class AddAreaFragment extends BaseBackFragment implements Toolbar.OnMenuItemClickListener, View.OnClickListener {
    private EditText etAreaName, etAreaUnit, etSuperArea, etAreaAnitType, etAreaPhone;
    private RecyclerView phoneRecycler;
    private MyBaseAdapter phoneAdapter;
    private String AdminId, areaId;
    private boolean isSearchPhone;

    public static AddAreaFragment newInstance(String areaId, String areaName) {
        Bundle args = new Bundle();
        args.putString("parentAreaId", areaId);
        args.putString("areaName", areaName);
        AddAreaFragment fragment = new AddAreaFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.add_area;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        setToolbarTitle(R.string.home_add_area).initToolbarNav().setToolbarMenu(R.menu.save, this);
        etAreaName = findViewById(R.id.et_area_name);// 区域名称
        etAreaUnit = findViewById(R.id.et_area_unit);// 所属单位
        etSuperArea = findViewById(R.id.et_place_area);// 管辖区域
        etAreaAnitType = findViewById(R.id.et_area_unit_type);// 范围备注
        etAreaPhone = findViewById(R.id.et_area_administrator_phone);// 电话
        areaId = getArguments().getString("parentAreaId");
        String areaName = getArguments().getString("areaName");

        if (!TextUtils.isEmpty(areaName)) {
            etSuperArea.setText(areaName);
            findViewById(R.id.tv_select_parent_area).setVisibility(View.GONE);
        } else {
            findViewById(R.id.til_parent).setVisibility(View.INVISIBLE);
        }

        initPhoneEdit();

        initData();

        setOnClickListener(this, R.id.tv_select_parent_area, R.id.iv_delete);
    }

    private void initData() {
        getProject(new OnProjectObtainListener() {
            @Override
            public void success(String projectId, String projectName) {
                etAreaUnit.setText(projectName);
                netWork().setRequestListener(new NetRequestListener() {
                                                 @Override
                                                 public void success(String action, BaseBean baseBean, Object tag) {
                                                     if (baseBean.isSuccessful())
                                                         switch (action) {
                                                             case NetBean.actionPostAddArea:
                                                                 setFragmentResult(RESULT_OK, new Bundle());
                                                                 finish();
                                                                 CommonUtils.toast(getContext(), baseBean.getMessage());
                                                                 break;
                                                             case NetBean.actionGetUserList:
                                                                 if (isSearchPhone)
                                                                     showPhone(((ResultUserPhoneBean) baseBean).getData());
                                                                 break;
                                                         }
                                                     else
                                                         CommonUtils.toast(getContext(), baseBean.getMessage());
                                                 }
                                             }
                        , netWork().apiRequest(NetBean.actionGetUserList, ResultUserPhoneBean.class, ApiRequest.REQUEST_TYPE_GET)
                                .setRequestParams("projectId", projectId)
                                .setRequestParams("searchInfo", "")// 模糊搜索电话号
                                .setRequestParams("pageSize", 5)
                                .setRequestParams("pageIndex", 1));
            }
        });
    }

    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {
        if (etAreaPhone.getText().toString().trim().isEmpty()) {
            CommonUtils.toast(getContext(), R.string.hint_null_phone);
        } else if (!CommonUtils.judge().isChinaMobilePhoneLegal(etAreaPhone.getText().toString().trim())) {
            CommonUtils.toast(getContext(), R.string.hint_error_phone);
        } else if (etAreaName.getText().toString().trim().isEmpty()) {
            CommonUtils.toast(getContext(), R.string.hint_null_unit_name);
        }  else if (etAreaAnitType.getText().toString().trim().isEmpty()) {
            CommonUtils.toast(getContext(), R.string.hint_null_unit_name);
        } else if (getResources().getString(R.string.select_units).equals(etAreaUnit.getText().toString())) {
            CommonUtils.toast(getContext(), R.string.hint_null_unit);
        } else {
            addAreaRequest();
        }
        return true;
    }

    private void addAreaRequest() {
        netWork().addRequestListener(netWork().apiRequest(NetBean.actionPostAddArea, BaseBean.class, ApiRequest.REQUEST_TYPE_POST)
                .setRequestParams("areaName", etAreaName.getText().toString())// 区域名称
                .setRequestParams("parentAreaId", areaId)// 父级区域Id（若为一级区域，则不传该参数） projectId和parentAreaId必须传递一个参数
                .setRequestParams("areaAddress", etAreaAnitType.getText().toString())// 区域地址
                .setRequestParams("areaAdminId", AdminId)// 区域管理员id
                .setRequestParams("areaAdminPhone", etAreaPhone.getText().toString())// 区域管理员电话
                .setRequestParams("projectId", CommonUtils.getSPUtils(_mActivity).getString(SPUtils.PROJECT_ID)));
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
                        etAreaPhone.setText(bean.getPhoneNumber());
                        etAreaPhone.setSelection(bean.getPhoneNumber().length());
                    }
                });
            }
        });
        phoneRecycler.setAdapter(phoneAdapter);
        etAreaPhone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // 搜索电话号码
                if (isSearchPhone) {
                    String oldStr = s.toString().substring(0, etAreaPhone.getSelectionStart());
                    if (oldStr.isEmpty()) {
                        if (start == 0 && count == 0) {
                            isSearchPhone = false;
                            AdminId = null;
                            etAreaPhone.setText(oldStr);
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
        etAreaPhone.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    isSearchPhone = true;
                } else {
                    isSearchPhone = false;
                    etAreaPhone.setText(etAreaPhone.getText().toString());
                    phoneRecycler.setVisibility(View.GONE);
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_select_parent_area:
                // 选择父级区域
                startForResult(SelectAreaFragment.newInstance(SelectType.AREA_TYPE), 1001);
                break;
            case R.id.iv_delete:
                v.setVisibility(View.GONE);
                findViewById(R.id.til_parent).setVisibility(View.INVISIBLE);
                findViewById(R.id.tv_select_parent_area).setVisibility(View.VISIBLE);
                areaId = null;
                break;
        }
    }

    public void showPhone(List<UserPhoneBean> bean) {
        if (etAreaPhone.hasFocus()) {
            isSearchPhone = false;
            int len = etAreaPhone.getSelectionStart();
            String oldStr = etAreaPhone.getText().toString().substring(0, len);
            if (bean != null && !bean.isEmpty() && bean.size() > 0) {
                AdminId = bean.get(0).getId();
                String html1 = oldStr + "<html><body><a><font color=\"#bbbbbb\">" + bean.get(0).getPhoneNumber().replace(oldStr, "") + "</a></body></html>";
                etAreaPhone.setText(Html.fromHtml(html1));
                etAreaPhone.setSelection(len);
                bean.remove(0);
                phoneAdapter.setData(bean);
                if (bean.size() > 0)
                    phoneRecycler.setVisibility(View.VISIBLE);
            } else {
                AdminId = null;
                etAreaPhone.setText(oldStr);
                etAreaPhone.setSelection(oldStr.length());
                phoneRecycler.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void onFragmentResult(int requestCode, int resultCode, Bundle data) {
        super.onFragmentResult(requestCode, resultCode, data);
        if (requestCode == 1001 && resultCode == RESULT_OK && data != null) {
            findViewById(R.id.iv_delete).setVisibility(View.VISIBLE);
            findViewById(R.id.tv_select_parent_area).setVisibility(View.GONE);
            findViewById(R.id.til_parent).setVisibility(View.VISIBLE);
            etSuperArea.setText(data.getString("areaName", ""));
            areaId = data.getString("areaId");
        }
    }

}
