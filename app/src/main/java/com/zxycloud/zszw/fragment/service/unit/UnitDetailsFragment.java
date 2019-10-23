package com.zxycloud.zszw.fragment.service.unit;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.SparseArray;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;

import com.zxycloud.zszw.R;
import com.zxycloud.zszw.base.BaseBackFragment;
import com.zxycloud.zszw.listener.NetRequestListener;
import com.zxycloud.zszw.model.ResultUnitBean;
import com.zxycloud.zszw.model.bean.UnitBean;
import com.zxycloud.common.base.BaseBean;
import com.zxycloud.common.utils.CommonUtils;
import com.zxycloud.common.utils.netWork.ApiRequest;
import com.zxycloud.common.utils.netWork.NetBean;
import com.zxycloud.common.utils.netWork.NetUtils;

public class UnitDetailsFragment extends BaseBackFragment implements View.OnClickListener {
    private String projectId;
    private NetUtils netUtils;

    private SparseArray<EditText> editTexts;
    private View llProjectDetail;
    private View llProjectBaseTitle;
    private CheckBox cbProjectNoticeMessage;
    private CheckBox cbProjectNoticeVoice;
    private ImageView imgProjectLogo;

    public static UnitDetailsFragment newInstance(String projectId) {
        Bundle args = new Bundle();
        args.putString("projectId", projectId);
        UnitDetailsFragment fragment = new UnitDetailsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.service_unit_details;
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.project_detail_title) {
            llProjectDetail.setVisibility(llProjectDetail.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
        } else if (id == R.id.project_detail_base_title) {
            llProjectBaseTitle.setVisibility(llProjectBaseTitle.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
        }
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        setToolbarTitle(R.string.project_message).initToolbarNav();

        setOnClickListener(this, R.id.project_detail_title, R.id.project_detail_base_title);
        Bundle bundle = getArguments();
        if (null == bundle) {
            return;
        }

        projectId = bundle.getString("projectId");
        editTexts = new SparseArray<>();
        editTexts.put(R.id.et_project_type_project_name, (EditText) findViewById(R.id.et_project_type_project_name));
        editTexts.put(R.id.et_project_type, (EditText) findViewById(R.id.et_project_type));
        editTexts.put(R.id.et_project_region, (EditText) findViewById(R.id.et_project_region));
        editTexts.put(R.id.et_project_address, (EditText) findViewById(R.id.et_project_address));
        editTexts.put(R.id.et_project_monitoring_range, (EditText) findViewById(R.id.et_project_monitoring_range));
        editTexts.put(R.id.et_project_charger, (EditText) findViewById(R.id.et_project_charger));
        editTexts.put(R.id.et_project_charger_phone, (EditText) findViewById(R.id.et_project_charger_phone));
        editTexts.put(R.id.et_project_manager, (EditText) findViewById(R.id.et_project_manager));
        editTexts.put(R.id.et_project_manager_phone, (EditText) findViewById(R.id.et_project_manager_phone));
        editTexts.put(R.id.et_project_show_name, (EditText) findViewById(R.id.et_project_show_name));

        llProjectDetail = findViewById(R.id.ll_project_detail);
        llProjectBaseTitle = findViewById(R.id.ll_project_base_title);

        cbProjectNoticeVoice = findViewById(R.id.cb_project_notice_voice);
        cbProjectNoticeMessage = findViewById(R.id.cb_project_notice_message);

        imgProjectLogo = findViewById(R.id.img_project_logo);

        setEnable(false);

        netUtils = new NetUtils(_mActivity);

        getProjectDetail();
    }

    private void setEnable(boolean enable) {
        for (int i = 0, nsize = editTexts.size(); i < nsize; i++) {
            editTexts.valueAt(i).setEnabled(enable);
        }
    }

    private void getProjectDetail() {
        netWork().setRequestListener(new NetRequestListener() {
            @Override
            public void success(String action, BaseBean baseBean, Object tag) {
                if (baseBean.isSuccessful()) {
                    UnitBean projectBean = ((ResultUnitBean) baseBean).getData();
                    editTexts.get(R.id.et_project_type_project_name).setText(projectBean.getProjectName());
                    editTexts.get(R.id.et_project_type).setText(projectBean.getTypeName());
                    editTexts.get(R.id.et_project_region).setText(projectBean.getCountryName());
                    editTexts.get(R.id.et_project_address).setText(projectBean.getAddress());
                    editTexts.get(R.id.et_project_monitoring_range).setText(projectBean.getProjectMonitorRegion());
                    editTexts.get(R.id.et_project_charger).setText(projectBean.getPrincipalName());
                    editTexts.get(R.id.et_project_charger_phone).setText(projectBean.getPrincipalPhoneNumber());
                    editTexts.get(R.id.et_project_manager).setText(securityStr(false, projectBean.getAdminName()));
                    editTexts.get(R.id.et_project_manager_phone).setText(securityStr(true, projectBean.getAdminPhoneNumber()));
                    editTexts.get(R.id.et_project_show_name).setText(projectBean.getTitle());

                    cbProjectNoticeVoice.setChecked(projectBean.getPhoneNotified() == 1);
                    cbProjectNoticeMessage.setChecked(projectBean.getMessageNotified() == 1);

                    CommonUtils.glide().loadImageView(_mActivity, projectBean.getLogoUrl(), imgProjectLogo);
                }
            }
        }, netWork().apiRequest(NetBean.actionGetProjectDetail, ResultUnitBean.class, ApiRequest.REQUEST_TYPE_GET)
                .setRequestParams("projectId", projectId));
    }

    /**
     * 保密措施
     *
     * @param isPhone 是否是手机号
     * @param str     保密内容
     */
    private String securityStr(boolean isPhone, String str) {
        if (!TextUtils.isEmpty(str)) {
            if (isPhone) {
                if (str.length() > 7) {
                    String a = str.substring(0,3);
                    String c = str.substring(7);
                    str = a.concat("****").concat(c);
                }
            } else {
                String b = str.substring(str.length() - 1);
                for (int i = 0; i < str.length() - 1; i++) {
                    b = "*".concat(b);
                }
                str = b;
            }
        }
        return str;
    }
}