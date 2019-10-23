package com.zxycloud.zszw.fragment.common;

import android.os.Bundle;
import android.support.annotation.StringRes;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.zxycloud.zszw.R;
import com.zxycloud.zszw.base.BaseBackFragment;
import com.zxycloud.zszw.base.MyBaseAdapter;
import com.zxycloud.zszw.event.type.ShowLoadType;
import com.zxycloud.zszw.listener.OnItemClickListener;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.zxycloud.common.widget.BswRecyclerView.RecyclerViewHolder;

import java.util.ArrayList;
import java.util.List;

import ezy.ui.layout.LoadingLayout;

public class StringSelectFragment extends BaseBackFragment implements MyBaseAdapter.OnBindViewHolderListener {
    private List<String> data;
    private LoadingLayout loadingLayout;

    public static StringSelectFragment newInstance(@StringRes int title, ArrayList<String> strings) {
        StringSelectFragment fragment = new StringSelectFragment();
        Bundle args = new Bundle();
        args.putInt("title", title);
        args.putStringArrayList("selectList", strings);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        setToolbarTitle(getArguments().getInt("title")).initToolbarNav();

        SmartRefreshLayout refreshLayout = findViewById(R.id.refreshLayout);
        refreshLayout.setEnableLoadMore(false);
        refreshLayout.setEnableRefresh(false);

        RecyclerView recyclerView = findViewById(R.id.recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        MyBaseAdapter baseAdapter = new MyBaseAdapter(getContext(), R.layout.item_textview, this);
        recyclerView.setAdapter(baseAdapter);
        data = getArguments().getStringArrayList("selectList");
        baseAdapter.setData(data);
        netWork().showLoading(R.id.loading, ShowLoadType.SHOW_CONTENT);
        loadingLayout = findViewById(R.id.loading);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(800);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                loadingLayout.post(new Runnable() {
                    @Override
                    public void run() {
                        loadingLayout.showContent();
                    }
                });
            }
        }).start();
    }

    @Override
    public void onBindViewHolder(final int position, View view, final RecyclerViewHolder holder) {
        holder.setText(R.id.tv_point_entry, data.get(position));
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mClickListener.onItemClick(position, v, holder);
                finish();
            }
        });
    }

    @Override
    protected int getLayoutId() {
        return R.layout.toolbar_list;
    }

    private OnItemClickListener mClickListener;
    public void setOnItemClickListener(OnItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }
}
