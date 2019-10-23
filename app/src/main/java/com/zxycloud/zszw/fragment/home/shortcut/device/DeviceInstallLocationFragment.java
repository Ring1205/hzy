package com.zxycloud.zszw.fragment.home.shortcut.device;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.zxycloud.zszw.R;
import com.zxycloud.zszw.base.BaseBackFragment;
import com.zxycloud.zszw.event.type.VideoShowType;
import com.zxycloud.zszw.fragment.common.ImagePagerFragment;
import com.zxycloud.zszw.fragment.common.PlayVideoFragment;
import com.zxycloud.zszw.model.ResultDeviceInstallDetailBean;
import com.zxycloud.zszw.model.bean.DeviceInstallDetailBean;
import com.zxycloud.common.utils.CommonUtils;
import com.zxycloud.common.utils.GlideUtils;
import com.zxycloud.common.utils.netWork.ApiRequest;
import com.zxycloud.common.utils.netWork.NetBean;
import com.zxycloud.common.utils.netWork.NetUtils;
import com.zxycloud.common.widget.BswRecyclerView.BswRecyclerView;
import com.zxycloud.common.widget.BswRecyclerView.ConvertViewCallBack;
import com.zxycloud.common.widget.BswRecyclerView.LimitAnnotation;
import com.zxycloud.common.widget.BswRecyclerView.RecyclerViewHolder;
import com.zxycloud.common.widget.RecordPopupWindow;

import java.util.ArrayList;
import java.util.List;

/**
 * @author leiming
 * @date 2019/3/28.
 */
public class DeviceInstallLocationFragment extends BaseBackFragment implements View.OnClickListener {
    private TextView deviceDescription;
    private BswRecyclerView<String> deviceShowImgRv;
    private DeviceInstallDetailBean deviceBean;
    private View deviceDescriptionLl, deviceImgLl, deviceVideoLl, deviceVoiceLl;

    public static DeviceInstallLocationFragment newInstance(String deviceId, String adapterName) {
        Bundle args = new Bundle();
        args.putString("deviceId", deviceId);
        args.putString("adapterName", adapterName);
        DeviceInstallLocationFragment fragment = new DeviceInstallLocationFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_device_install_location;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        setToolbarTitle(R.string.device_install_details).initToolbarNav();

        deviceDescription = findViewById(R.id.device_description);
        deviceDescriptionLl = findViewById(R.id.device_description_ll);
        deviceImgLl = findViewById(R.id.device_img_ll);
        deviceVideoLl = findViewById(R.id.device_video_ll);
        deviceVoiceLl = findViewById(R.id.device_voice_ll);


        deviceShowImgRv = findViewById(R.id.device_show_img_rv);
        deviceShowImgRv.initAdapter(R.layout.item_img_layout, convertViewCallBack)
                .setLayoutManager(LimitAnnotation.VERTICAL, 3)
                .setMaxCount(3);

        getDeviceDetail();
        setOnClickListener(this, R.id.item_video_img, R.id.item_voice_img);
    }

    private void getDeviceDetail() {
        NetUtils.getNewInstance(_mActivity).request(new NetUtils.NetRequestCallBack<ResultDeviceInstallDetailBean>() {
            @Override
            public void success(String action, ResultDeviceInstallDetailBean bean, Object tag) {
                if (bean.isSuccessful()) {
                    deviceBean = bean.getData();
                    deviceDescription.setText(deviceBean.getDescription());
                    deviceShowImgRv.setData(deviceBean.getImgUrl());

//                    if (TextUtils.isEmpty(deviceBean.getDescription())) {
//                        deviceDescriptionLl.setVisibility(View.GONE);
//                    }
                    if (TextUtils.isEmpty(deviceBean.getVideoUrl())) {
                        deviceVideoLl.setVisibility(View.GONE);
                    }
                    if (TextUtils.isEmpty(deviceBean.getVoiceUrl())) {
                        deviceVoiceLl.setVisibility(View.GONE);
                    }
                    if (CommonUtils.judgeListNull(deviceBean.getImgUrl()) == 0) {
                        deviceImgLl.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void error(String action, Throwable e, Object tag) {

            }
        }, true, new ApiRequest<>(NetBean.actionGetDeviceInstallDetail, ResultDeviceInstallDetailBean.class)
                .setRequestParams("deviceId", getArguments().getString("deviceId"))
                .setRequestParams("adapterName", getArguments().getString("adapterName")));
    }

    private ConvertViewCallBack<String> convertViewCallBack = new ConvertViewCallBack<String>() {
        GlideUtils glideUtils = CommonUtils.glide();

        @Override
        public void convert(RecyclerViewHolder holder, final String s, final int position, int layoutTag) {
            glideUtils.loadImageView(_mActivity, s, holder.getImageView(R.id.item_img_show));
            holder.setVisibility(R.id.item_img_add, View.GONE)
                    .setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            List<String> paths = deviceBean.getImgUrl();
                            if (CommonUtils.judgeListNull(paths) > 0) {
                                start(ImagePagerFragment.newInstance(position, (ArrayList<String>) paths));
                            }
                        }
                    });
        }

        @Override
        public void loadingFinished() {

        }
    };

    @Override
    public void onClick(View view) {
        if (deviceBean != null)
            switch (view.getId()) {
                case R.id.item_video_img:
                    start(PlayVideoFragment.newInstance(VideoShowType.PLAY_TYPE_INSTALL_VIDEO, deviceBean.getVideoUrl()));
                    break;

                case R.id.item_voice_img:
                    RecordPopupWindow recordPopupWindow = new RecordPopupWindow(getContext(), deviceBean.getVoiceUrl());
                    recordPopupWindow.show(LayoutInflater.from(getContext()).inflate(R.layout.activity_base, null));
                    break;
            }
    }
}
