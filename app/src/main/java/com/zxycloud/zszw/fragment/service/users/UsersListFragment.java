package com.zxycloud.zszw.fragment.service.users;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.zxycloud.zszw.R;
import com.zxycloud.zszw.adapter.UserAdapter;
import com.zxycloud.zszw.base.BaseBackFragment;
import com.zxycloud.zszw.event.type.ShowLoadType;
import com.zxycloud.zszw.listener.OnItemClickListener;

public class UsersListFragment extends BaseBackFragment {
    private RecyclerView recyclerView;

    public static UsersListFragment newInstance() {
        Bundle args = new Bundle();
        UsersListFragment fragment = new UsersListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.toolbar_list;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        setToolbarTitle(R.string.users_list).initToolbarNav();
        recyclerView = findViewById(R.id.recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        UserAdapter pointAdapter = new UserAdapter(getContext());
        recyclerView.setAdapter(pointAdapter);
        pointAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(int position, View view, RecyclerView.ViewHolder vh) {
                start(UserinfoFragment.newInstance());
            }
        });
        netWork().showLoading(R.id.loading, ShowLoadType.SHOW_CONTENT);
    }

}
