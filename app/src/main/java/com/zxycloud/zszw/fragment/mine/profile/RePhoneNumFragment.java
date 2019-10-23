package com.zxycloud.zszw.fragment.mine.profile;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.EditText;

import com.zxycloud.zszw.R;
import com.zxycloud.zszw.base.BaseBackFragment;

public class RePhoneNumFragment extends BaseBackFragment implements Toolbar.OnMenuItemClickListener {
    private static String PHONE_TITLE = "phone_title";
    private Bundle args;
    private EditText editPhone;

    public static RePhoneNumFragment newInstance(String title) {
        Bundle args = new Bundle();
        RePhoneNumFragment fragment = new RePhoneNumFragment();
        args.putString(PHONE_TITLE, title);
        fragment.setArguments(args);
        return fragment;
    }
    @Override
    protected int getLayoutId() {
        return R.layout.mine_phone;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        if (args != null && !args.getString(PHONE_TITLE).isEmpty()) {
            setToolbarTitle(R.string.change_phone_number).initToolbarNav().setToolbarMenu(R.menu.save, this);
        }else {
            setToolbarTitle(R.string.add_phone_num).initToolbarNav().setToolbarMenu(R.menu.save, this);
        }

        editPhone = findViewById(R.id.edit_phone);

    }

    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.action_save:
                Bundle bundle = new Bundle();
                bundle.putString(ProfileFragment.PHONE_NUMBER, editPhone.getText().toString());
                setFragmentResult(RESULT_OK, bundle);
                break;
        }
        return true;
    }
}
