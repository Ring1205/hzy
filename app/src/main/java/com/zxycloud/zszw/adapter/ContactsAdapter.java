package com.zxycloud.zszw.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.zxycloud.zszw.R;
import com.zxycloud.zszw.fragment.service.install.area.LinkmanListFragment;
import com.zxycloud.zszw.model.bean.LinkmanBean;
import com.zxycloud.common.base.BaseBean;
import com.zxycloud.common.utils.CommonUtils;
import com.zxycloud.common.utils.netWork.ApiRequest;
import com.zxycloud.common.utils.netWork.NetBean;
import com.zxycloud.common.utils.netWork.NetUtils;
import com.zxycloud.common.widget.BswRecyclerView.RecyclerViewHolder;

import java.util.List;

public class ContactsAdapter extends RecyclerView.Adapter<ContactsAdapter.ContactHolder> {
    private Context context;
    private LinkmanListFragment linkmanListFragment;
    private List<LinkmanBean> contactsBean;
    private SparseArray<String> sparseArray;

    public ContactsAdapter(LinkmanListFragment linkmanListFragment, Context context) {
        this.context = context;
        this.linkmanListFragment = linkmanListFragment;
        sparseArray = CommonUtils.string().formatStringLength(context
                , R.string.title_contacts
                , R.string.title_mobile_phone);
    }

    public void setData(List<LinkmanBean> contactsBean) {
        this.contactsBean = contactsBean;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ContactHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new ContactHolder(LayoutInflater.from(context).inflate(R.layout.item_linkman_layout, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final ContactHolder placeHolder, final int i) {
        final LinkmanBean bean = contactsBean.get(i);
        placeHolder.setText(R.id.item_linkman_name, sparseArray.get(R.string.title_contacts)
                .concat(bean.getLinkmanName()))
                .setText(R.id.item_linkman_phone
                        , Html.fromHtml(String.format(CommonUtils.string().getString(context, R.string.string_linkman_detail)
                                , sparseArray.get(R.string.title_mobile_phone)
                                , bean.getLinkmanPhoneNumber())))
                .setOnClickListener(R.id.linkman_call, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + bean.getLinkmanPhoneNumber()));
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        linkmanListFragment.startActivity(intent);
                    }
                })
                .setOnClickListener(R.id.linkman_delete, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new NetUtils(context).request(new NetUtils.NetRequestCallBack() {
                            @Override
                            public void success(String action, BaseBean baseBean, Object tag) {
                                if (baseBean.isSuccessful()) {
                                    //删除联系人成功
                                    contactsBean.remove(i);
                                    notifyDataSetChanged();
                                } else {
                                    CommonUtils.toast(context, baseBean.getMessage());
                                }
                            }

                            @Override
                            public void error(String action, Throwable e, Object tag) {

                            }
                        }, false, new ApiRequest(NetBean.actionGetDeleteContacts, BaseBean.class)
                                .setRequestParams("linkmanId", bean.getLinkmanId()));
                    }
                });
    }

    @Override
    public int getItemCount() {
        return contactsBean != null ? contactsBean.size() : 0;
    }

    class ContactHolder extends RecyclerViewHolder {

        public ContactHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

}
