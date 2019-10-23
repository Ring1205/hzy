package com.zxycloud.zszw.fragment.home.shortcut.area;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.NestedScrollView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zxycloud.zszw.R;
import com.zxycloud.zszw.base.BaseBackFragment;
import com.zxycloud.zszw.event.JPushEvent;
import com.zxycloud.zszw.event.type.SearchListShowType;
import com.zxycloud.zszw.fragment.service.install.area.AddAreaFragment;
import com.zxycloud.zszw.fragment.service.install.place.AddPlaceFragment;
import com.zxycloud.zszw.listener.NetRequestListener;
import com.zxycloud.zszw.model.ResultAreaBean;
import com.zxycloud.zszw.model.bean.AreaBean;
import com.zxycloud.common.base.BaseBean;
import com.zxycloud.common.utils.CommonUtils;
import com.zxycloud.common.utils.netWork.ApiRequest;
import com.zxycloud.common.utils.netWork.NetBean;

import org.greenrobot.eventbus.EventBus;

public class AreaDetailFragment extends BaseBackFragment implements View.OnClickListener {
    private String areaId;
    private AreaBean areaBean;
    private LinearLayout llPlaceView;
    private FrameLayout flList;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (flList != null) {
                ViewGroup.LayoutParams params = flList.getLayoutParams();
                params.height = findViewById(R.id.refresh_layout).getHeight();
                flList.setLayoutParams(params);
            }
        }
    };

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            NestedScrollView scrollView = findViewById(R.id.place_scroll);
            scrollView.fling(0);
            scrollView.smoothScrollTo(0,0);
        }};

    public static AreaDetailFragment newInstance(String areaId) {
        Bundle args = new Bundle();
        args.putString("areaId", areaId);
        AreaDetailFragment fragment = new AreaDetailFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.area_detail;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        setToolbarTitle(R.string.area_details).initToolbarNav();
        areaId = getArguments().getString("areaId");
        flList = findViewById(R.id.fl_list);
        llPlaceView = findViewById(R.id.ll_place_view);

        mHandler.sendMessage(new Message());

        getAreaDetail();

        setOnClickListener(this, R.id.rl_add_place, R.id.rl_add_area);
    }

    @Override
    public void onClick(View view) {
        if (areaBean != null)
            switch (view.getId()) {
                case R.id.rl_add_place:// 添加场所
                    startForResult(AddPlaceFragment.newInstance(areaBean.getAreaId(), areaBean.getAreaName()),123);
                    break;
                case R.id.rl_add_area:// 添加单位
                    startForResult(AddAreaFragment.newInstance(areaBean.getAreaId(), areaBean.getAreaName()),123);
                    break;
            }
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @org.greenrobot.eventbus.Subscribe(sticky = true, threadMode = org.greenrobot.eventbus.ThreadMode.MAIN)
    public void onPushRefresh(JPushEvent event) {
        getAreaDetail();
    }

    /**
     * 获取片求详情
     */
    private void getAreaDetail() {
        netWork().setRefreshListener(R.id.refresh_layout, true, false, new NetRequestListener() {
                       @Override
                       public void success(String action, BaseBean baseBean, Object tag) {
                           if (baseBean.isSuccessful()) {
                               areaBean = ((ResultAreaBean) baseBean).getData();
                               ((TextView) findViewById(R.id.tv_place_title)).setText(CommonUtils.string().getString(areaBean.getAreaName()));
                               ((TextView)findViewById(R.id.tv_area_project)).setText(CommonUtils.string().getString(areaBean.getProjectName()));// 所属单位
                               ((TextView)findViewById(R.id.tv_area_address)).setText(CommonUtils.string().getString(areaBean.getAreaAddress()));// 区域地址
                               ((TextView)findViewById(R.id.tv_area_admin)).setText(CommonUtils.string().getString(areaBean.getAreaAdminName()));// 管理人员
                               ((TextView)findViewById(R.id.tv_area_phone)).setText(CommonUtils.string().getString(areaBean.getAreaAdminPhone()));// 手机号码

                               if (areaBean.getSubAreaType() != 0)
                                   if (findChildFragment(SearchListFragment.class) != null) {
                                       findChildFragment(SearchListFragment.class).replaceFragment(SearchListFragment.newInstance(
                                               areaBean.getSubAreaType() != 1 ? SearchListShowType.SHOW_TYPE_PLACE_OF_AREA : SearchListShowType.SHOW_TYPE_AREA_OF_AREA ,
                                               areaBean.getAreaName(), areaId), false);
                                   } else {
                                       loadRootFragment(R.id.fl_list, SearchListFragment.newInstance(
                                               areaBean.getSubAreaType() != 1 ? SearchListShowType.SHOW_TYPE_PLACE_OF_AREA : SearchListShowType.SHOW_TYPE_AREA_OF_AREA ,
                                               areaBean.getAreaName(), areaId));
                                   }

                               new Thread(new Runnable() {
                                   @Override
                                   public void run() {
                                       try {
                                           Thread.sleep(800);
                                       } catch (InterruptedException e) {
                                           e.printStackTrace();
                                       }
                                       handler.sendMessage(new Message());
                                   }
                               }).start();

                               flList.setVisibility(View.VISIBLE);
                               findViewById(R.id.rl_add_area).setVisibility(areaBean.getIsAdd() != 1 ? View.GONE : View.VISIBLE);
                               // 子级区域类型：0未分配/1区域/2场所
                               switch (areaBean.getSubAreaType()) {
                                   case 0:
                                       flList.setVisibility(View.GONE);
                                       findViewById(R.id.rl_add_place).setVisibility(View.VISIBLE);
                                       findViewById(R.id.view_line_1px).setVisibility(View.VISIBLE);
                                       break;
                                   case 1:
                                       // 是否有添加下一级区域权限 1:有权限添加 0:没有权限添加
                                       if (areaBean.getIsAdd() == 1)
                                           llPlaceView.setVisibility(View.VISIBLE);
                                       else
                                           llPlaceView.setVisibility(View.GONE);

                                       findViewById(R.id.rl_add_place).setVisibility(View.GONE);
                                       findViewById(R.id.view_line_1px).setVisibility(View.GONE);
                                       break;
                                   case 2:
                                       findViewById(R.id.rl_add_place).setVisibility(View.VISIBLE);
                                       findViewById(R.id.rl_add_area).setVisibility(View.GONE);
                                       findViewById(R.id.view_line_1px).setVisibility(View.GONE);
                                       break;
                               }
                           } else {
                               CommonUtils.toast(getContext(), baseBean.getMessage());
                           }
                       }
                   }, netWork().apiRequest(NetBean.actionGetAreaDetail, ResultAreaBean.class, ApiRequest.REQUEST_TYPE_GET, R.id.loading).setRequestParams("areaId", areaId));
    }

    @Override
    public void onFragmentResult(int requestCode, int resultCode, Bundle data) {
        super.onFragmentResult(requestCode, resultCode, data);
        if (requestCode == 123 && resultCode == RESULT_OK)
            netWork().loading();
    }

    public int getToolbarBottom() {
        return mToolbar != null ? mToolbar.getBottom() : 0;
    }
}