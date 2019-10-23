package com.zxycloud.zszw.fragment.common;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.WindowManager;

import com.zxycloud.zszw.R;
import com.zxycloud.zszw.base.BasePlayerFragment;
import com.zxycloud.zszw.event.type.VideoShowType;
import com.zxycloud.zszw.listener.NetRequestListener;
import com.zxycloud.zszw.model.ResultVideoBean;
import com.zxycloud.common.base.BaseBean;
import com.zxycloud.common.utils.CommonUtils;
import com.zxycloud.common.utils.netWork.ApiRequest;
import com.zxycloud.common.utils.netWork.NetBean;

import static com.zxycloud.zszw.event.type.VideoShowType.PLAY_TYPE_INSTALL_VIDEO;
import static com.zxycloud.zszw.event.type.VideoShowType.PLAY_TYPE_LINKAGE_CAMERA;
import static com.zxycloud.zszw.event.type.VideoShowType.PLAY_TYPE_LINKAGE_DEVICE;

public class PlayVideoFragment extends BasePlayerFragment implements Toolbar.OnMenuItemClickListener {
    private String video;
    private String deviceId;
    private String adapterName;

    private int playType = PLAY_TYPE_INSTALL_VIDEO;

    public static PlayVideoFragment newInstance(@VideoShowType.PlayType int playType, String video) {
        Bundle bundle = new Bundle();
        bundle.putString("video", video);
        bundle.putInt("playType", playType);
        PlayVideoFragment fragment = new PlayVideoFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    public static PlayVideoFragment newInstance(@VideoShowType.PlayType int playType, String deviceId, String adapterName) {
        Bundle bundle = new Bundle();
        bundle.putString("deviceId", deviceId);
        bundle.putString("adapterName", adapterName);
        bundle.putInt("playType", playType);
        PlayVideoFragment fragment = new PlayVideoFragment();
        fragment.setArguments(bundle);
        return fragment;
    }
    @Override
    protected int getLayoutId() {
        return R.layout.play_video;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        setToolbarTitle(R.string.play_video).initToolbarNav().setToolbarMenu(R.menu.play_video, this);
        _mActivity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        Bundle bundle = getArguments();
        video = bundle.getString("video");
        deviceId = bundle.getString("deviceId");
        adapterName = bundle.getString("adapterName");
        playType = bundle.getInt("playType");

        mViewHolder = findViewById(R.id.rl_view_holder);
        mediaController = findViewById(R.id.media_controller_bar);

        if (playType == PLAY_TYPE_INSTALL_VIDEO) {
            startPlayer(video);
        } else {
            getVideoPath();
        }
    }

    private void getVideoPath() {
        ApiRequest apiRequest = null;
        if (playType == PLAY_TYPE_LINKAGE_CAMERA) {
            apiRequest = new ApiRequest<>(NetBean.actionGetVideoPathByCamera, ResultVideoBean.class)
                    .setRequestParams("id", video);
        } else if (playType == PLAY_TYPE_LINKAGE_DEVICE) {
            apiRequest = new ApiRequest<>(NetBean.actionGetVideoPathByDevice, ResultVideoBean.class)
                    .setRequestParams("id", deviceId)
                    .setRequestParams("adapterName", adapterName);
        }
        netWork().setRequestListener(new NetRequestListener() {
            @Override
            public void success(String action, BaseBean baseBean, Object tag) {
                if (baseBean.isSuccessful())
                    startPlayer(((ResultVideoBean) baseBean).getData().getSourceAddr());
                 else
                    CommonUtils.toast(_mActivity, baseBean.getMessage());
            }
        },apiRequest);
    }

    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {
        mVV.start();
        return true;
    }
}
