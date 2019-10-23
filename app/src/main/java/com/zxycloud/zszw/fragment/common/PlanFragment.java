package com.zxycloud.zszw.fragment.common;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.zxycloud.zszw.R;
import com.zxycloud.zszw.base.BaseBackFragment;
import com.zxycloud.zszw.listener.NetRequestListener;
import com.zxycloud.zszw.model.ResultPointBean;
import com.zxycloud.common.utils.CommonUtils;
import com.zxycloud.common.utils.netWork.ApiRequest;
import com.zxycloud.common.utils.netWork.NetBean;
import com.zxycloud.common.widget.FloorPointView.BswFloorPointView;
import com.zxycloud.common.widget.FloorPointView.PointBean;

import java.util.ArrayList;
import java.util.List;

public class PlanFragment extends BaseBackFragment implements Toolbar.OnMenuItemClickListener {
    public static String POINT_TYPE_ADD = "point_type_add";// 添加点位
    public static String POINT_TYPE_LIST = "point_type_list";// 展现点位
    private BswFloorPointView pointLayout;
    private List<PointBean> pointBeans;
    private com.zxycloud.zszw.model.bean.PointBean pointBean;

    public static PlanFragment newInstance(String type, String imgUrl, String deviceId, String adapterName) {
        PlanFragment fragment = new PlanFragment();
        Bundle bundle = new Bundle();
        bundle.putString("point_type", type);
        bundle.putString("imgUrl", imgUrl);
        bundle.putString("deviceId", deviceId);
        bundle.putString("adapterName", adapterName);
        fragment.setArguments(bundle);
        return fragment;
    }

    public static PlanFragment newInstance(String type, String imgUrl, double layX, double layY) {
        PlanFragment fragment = new PlanFragment();
        Bundle bundle = new Bundle();
        bundle.putString("point_type", type);
        bundle.putString("imgUrl", imgUrl);
        bundle.putDouble("layX", layX);
        bundle.putDouble("layY", layY);
        fragment.setArguments(bundle);
        return fragment;
    }

    private void loadData() {
        if (POINT_TYPE_LIST.equals(getArguments().getString("point_type"))) {
            netWork().setRequestListener(new NetRequestListener<ResultPointBean>() {
                @Override
                public void success(String action, ResultPointBean baseBean, Object tag) {
                    if (baseBean.isSuccessful()) {
                        pointBean = baseBean.getData();
                        pointBeans = new ArrayList<>();
                        pointBeans.add(new PointBean(baseBean.getData().getLayerImageX(), baseBean.getData().getLayerImageY(), R.drawable.mark_touch, PointBean.POSITION_ABOVE, PointBean.COORDINATES_ABSOLUTE));
                        pointLayout.setPointList(pointBeans)
                                .setFloorBackground(baseBean.getData().getPlaceLayerImage())
                                .setSize(BswFloorPointView.CHANGE_SIZE)
                                .setPointCoordinates(PointBean.COORDINATES_ABSOLUTE)
                                .paint();
                    }
                }
            }, netWork().apiRequest(NetBean.actionGetLayerPoint, ResultPointBean.class, ApiRequest.REQUEST_TYPE_GET)
                    .setRequestParams("deviceId", getArguments().getString("deviceId"))
                    .setRequestParams("adapterName", getArguments().getString("adapterName")));
        } else if (POINT_TYPE_ADD.equals(getArguments().getString("point_type"))) {
            double lx = getArguments().getDouble("layX");
            double ly = getArguments().getDouble("layY");
            if (lx != 0 || ly != 0) {
                pointBeans = new ArrayList<>();
                pointBeans.add(new PointBean(lx, ly, R.drawable.mark_touch, PointBean.POSITION_ABOVE, PointBean.COORDINATES_ABSOLUTE));
                pointLayout.setPointList(pointBeans)
                        .setFloorBackground(getArguments().getString("imgUrl"))
                        .setPointCoordinates(PointBean.COORDINATES_ABSOLUTE)
                        .setCanMarker(true)
                        .paint();
            } else {
                pointLayout.setFloorBackground(getArguments().getString("imgUrl"))
                        .setCanMarker(true)
                        .setImgResId(R.drawable.mark_touch)
                        .setPointCoordinates(PointBean.COORDINATES_ABSOLUTE)
                        .setPositionLimit(PointBean.POSITION_ABOVE)
                        .paint();
            }
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.add_point;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        if (POINT_TYPE_LIST.equals(getArguments().getString("point_type")))
            setToolbarTitle(R.string.plan).initToolbarNav();
        else
            setToolbarTitle(R.string.plan).initToolbarNav().setToolbarMenu(R.menu.add, this);

        pointLayout = findViewById(R.id.point_layout);

        loadData();
    }

    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.action_add:
                if (pointLayout.isMarked()) {
                    Bundle bundle = new Bundle();
                    bundle.putDouble("width", pointLayout.getSize().getPx());
                    bundle.putDouble("height", pointLayout.getSize().getPy());
                    setFragmentResult(RESULT_OK, bundle);
                    finish();
                } else {
                    CommonUtils.toast(getContext(), R.string.toast_add_poin_point);
                }
                break;
        }
        return true;
    }

    @Override
    public void onFragmentResult(int requestCode, int resultCode, Bundle data) {
        super.onFragmentResult(requestCode, resultCode, data);
        if (requestCode == 1101 && resultCode == RESULT_OK && data != null) {
            pointBeans.add(new PointBean(data.getDouble("width"), data.getDouble("height"), R.drawable.mark_touch, PointBean.POSITION_ABOVE, PointBean.COORDINATES_ABSOLUTE));
            pointLayout.setPointList(pointBeans)
                    .setFloorBackground("http://photocdn.sohu.com/20120418/Img340874501.jpg")
                    .setSize(BswFloorPointView.CHANGE_SIZE)
                    .paint();
        }
    }
}
