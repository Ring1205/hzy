package com.zxycloud.zszw.fragment.service.install.area;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.CursorLoader;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.zxycloud.zszw.R;
import com.zxycloud.zszw.adapter.ContactsAdapter;
import com.zxycloud.zszw.base.BaseBackFragment;
import com.zxycloud.zszw.dialog.LinkmanDialog;
import com.zxycloud.zszw.event.type.LinkmanShowType;
import com.zxycloud.zszw.listener.NetRequestListener;
import com.zxycloud.zszw.model.RequestAddAreaLinkmanBean;
import com.zxycloud.zszw.model.ResultLinkmanListBean;
import com.zxycloud.zszw.model.bean.LinkmanBean;
import com.zxycloud.common.base.BaseBean;
import com.zxycloud.common.utils.CommonUtils;
import com.zxycloud.common.utils.PermissionUtils;
import com.zxycloud.common.utils.netWork.ApiRequest;
import com.zxycloud.common.utils.netWork.NetBean;

import java.util.List;
import java.util.Map;

public class LinkmanListFragment extends BaseBackFragment {
    private RecyclerView recyclerView;
    private ContactsAdapter adapter;
    private String searchId;
    private int linkmanType;
    private LinkmanDialog linkmanDialog;

    public static LinkmanListFragment newInstance(@LinkmanShowType.showType int linkmanType, String searchId) {
        Bundle bundle = new Bundle();
        bundle.putInt("linkmanType", linkmanType);
        bundle.putString("searchId", searchId);
        LinkmanListFragment linkmanListFragment = new LinkmanListFragment();
        linkmanListFragment.setArguments(bundle);
        return linkmanListFragment;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.toolbar_list;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        setToolbarTitle(R.string.menu_contacts).initToolbarNav().setToolbarMenu(R.menu.add, onMenuItemClickListener);

        Bundle bundle = getArguments();
        linkmanType = bundle.getInt("linkmanType");
        searchId = bundle.getString("searchId");

        recyclerView = findViewById(R.id.recycler);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new ContactsAdapter(this, getContext());
        recyclerView.setAdapter(adapter);

        getLinkmanList();
    }

    private void getLinkmanList() {
        netWork().setRefreshListener(R.id.refreshLayout, true, false, new NetRequestListener() {
            @Override
            public void success(String action, BaseBean baseBean, Object tag) {
                if (baseBean.isSuccessful()) {
                    List<LinkmanBean> videoBeans = ((ResultLinkmanListBean) baseBean).getData();
                    adapter.setData(videoBeans);
                }
            }
        }, netWork().apiRequest(NetBean.actionGetPlaceLinkmanList, ResultLinkmanListBean.class, ApiRequest.REQUEST_TYPE_GET, R.id.loading)
                .setRequestParams(LinkmanShowType.LINKMAN_TYPE_PLACE == linkmanType ? "placeId" : "areaId", searchId));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            // ContentProvider展示数据类似一个单个数据库表
            // ContentResolver实例带的方法可实现找到指定的ContentProvider并获取到ContentProvider的数据
            ContentResolver reContentResolverol = _mActivity.getContentResolver();
            // URI,每个ContentProvider定义一个唯一的公开的URI,用于指定到它的数据集
            Uri contactData = data.getData();
            // 查询就是输入URI等参数,其中URI是必须的,其他是可选的,如果系统能找到URI对应的ContentProvider将返回一个Cursor对象.

            CursorLoader cursorLoader = new CursorLoader(_mActivity, contactData, null, null, null, null);
            Cursor cursor = cursorLoader.loadInBackground();
            if (cursor == null) {
                CommonUtils.toast(_mActivity, R.string.fail_to_get_contact);
                return;
            }
            cursor.moveToFirst();
            // 获得DATA表中的名字
            String username = cursor.getString(cursor
                    .getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
            // 条件为联系人ID
            String contactId = cursor.getString(cursor
                    .getColumnIndex(ContactsContract.Contacts._ID));
            // 获得DATA表中的电话号码，条件为联系人ID,因为手机号码可能会有多个
            @SuppressLint("Recycle") Cursor phone = reContentResolverol.query(
                    ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                    String.format("%s = %s", ContactsContract.CommonDataKinds.Phone.CONTACT_ID, contactId), null, null);
            if (phone != null) {
                while (phone.moveToNext()) {
                    String usernumber = phone
                            .getString(phone
                                    .getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                    if (null == linkmanDialog) {
                        return;
                    }
                    linkmanDialog.setContacts(username, usernumber.replace("-", "").replace("+86", "").replace(" ", ""));
                }
                phone.close();
            } else {
                CommonUtils.toast(_mActivity, R.string.fail_to_get_contact);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionUtils.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private Toolbar.OnMenuItemClickListener onMenuItemClickListener = new Toolbar.OnMenuItemClickListener() {
        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            linkmanDialog = new LinkmanDialog();
            linkmanDialog.setListener(_mActivity, onCustomContactListener);
            FragmentTransaction ft = _mActivity.getSupportFragmentManager().beginTransaction();
            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
            linkmanDialog.show(ft, "df");
            return true;
        }
    };

    private LinkmanDialog.OnCustomContactListener onCustomContactListener = new LinkmanDialog.OnCustomContactListener() {
        @Override
        public void getResult(Map<String, String> contact) {
            ApiRequest apiRequest = netWork().apiRequest(NetBean.actionAddPlaceLinkman, BaseBean.class, ApiRequest.REQUEST_TYPE_POST)
                    .setRequestBody(new RequestAddAreaLinkmanBean(searchId, contact.get(LinkmanDialog.linkmanName), contact.get(LinkmanDialog.linkmanPhone)));

            netWork().setRequestListener(new NetRequestListener() {
                @Override
                public void success(String action, BaseBean baseBean, Object tag) {
                    if (LinkmanShowType.LINKMAN_TYPE_PLACE == linkmanType) {
                        // 添加场所联系人
                        if (baseBean.isSuccessful()) {
                            // 添加场所联系人成功
                            getLinkmanList();
                        } else {
                            CommonUtils.toast(getContext(), baseBean.getMessage());
                        }
                    }else {
                        // 添加区域联系人
                    }
                }
            }, apiRequest);
        }

        @Override
        public void getContact() {
            PermissionUtils.setRequestPermissions(LinkmanListFragment.this, new PermissionUtils.PermissionGrant() {
                @Override
                public Integer[] onPermissionGranted() {
                    return new Integer[]{PermissionUtils.CODE_READ_CONTACTS};
                }

                @Override
                public void onRequestResult(List<String> deniedPermission) {
                    if (CommonUtils.judgeListNull(deniedPermission) == 0) {
                        startActivityForResult(new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI), 0);
                    }
                }
            });
        }
    };
}