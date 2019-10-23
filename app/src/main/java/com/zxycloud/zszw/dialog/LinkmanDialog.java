package com.zxycloud.zszw.dialog;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.zxycloud.zszw.R;
import com.zxycloud.common.utils.CommonUtils;
import com.zxycloud.common.utils.StringFormatUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * @author leiming
 * @date 2019/3/26.
 */
public class LinkmanDialog extends DialogFragment {
    public static final String linkmanName = "linkmanName";
    public static final String linkmanPhone = "linkmanPhone";

    private OnCustomContactListener listener;

    private EditText etLinkmanName;
    private EditText etLinkmanPhone;
    private View v;
    private StringFormatUtils formatUtils;
    private Context mContext;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        formatUtils = CommonUtils.string();
        setStyle(DialogFragment.STYLE_NO_TITLE, android.R.style.Theme_Holo_Light_Dialog_NoActionBar_MinWidth);
    }

    @Override
    public void onStart() {
        super.onStart();
        getDialog().getWindow().setBackgroundDrawableResource(R.drawable.bg_white_corner_10);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_add_linkman, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        etLinkmanName = view.findViewById(R.id.et_linkman_name);
        etLinkmanPhone = view.findViewById(R.id.et_linkman_phone);
        view.findViewById(R.id.clear_linkman_name).setOnClickListener(onClickListener);
        view.findViewById(R.id.clear_linkman_phone).setOnClickListener(onClickListener);
        view.findViewById(R.id.add_linkman_cancel).setOnClickListener(onClickListener);
        view.findViewById(R.id.add_linkman_save).setOnClickListener(onClickListener);
        view.findViewById(R.id.use_contract).setOnClickListener(onClickListener);
    }

    public void setListener(Context mContext, OnCustomContactListener listener) {
        this.mContext = mContext;
        this.listener = listener;
    }

    public void setContacts(String username, String phone) {
        etLinkmanName.setText(username);
        etLinkmanPhone.setText(phone);
    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.clear_linkman_name:
                    etLinkmanName.setText("");
                    break;

                case R.id.clear_linkman_phone:
                    etLinkmanPhone.setText("");
                    break;

                case R.id.add_linkman_cancel:
                    dismiss();
                    break;

                case R.id.add_linkman_save:
                    String linkmanNameString = formatUtils.getString(etLinkmanName);
                    String linkmanPhoneString = formatUtils.getString(etLinkmanPhone);
                    if (TextUtils.isEmpty(linkmanNameString) || TextUtils.isEmpty(linkmanPhoneString) || !CommonUtils.judge().isChinaPhoneLegal(linkmanNameString)) {
                        CommonUtils.toast(mContext, R.string.string_contact_wrong_format);
                        return;
                    }
                    Map<String, String> map = new HashMap<>();
                    map.put(linkmanName, formatUtils.getString(etLinkmanName));
                    map.put(linkmanPhone, formatUtils.getString(etLinkmanPhone));
                    listener.getResult(map);
                    dismiss();
                    break;

                case R.id.use_contract:
                    listener.getContact();
                    break;
            }
        }
    };

    public interface OnCustomContactListener {
        void getResult(Map<String, String> contact);

        void getContact();
    }
}
