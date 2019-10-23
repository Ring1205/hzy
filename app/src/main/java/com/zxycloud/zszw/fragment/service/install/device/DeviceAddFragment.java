package com.zxycloud.zszw.fragment.service.install.device;

import android.animation.Animator;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.animation.AccelerateInterpolator;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.zxycloud.common.utils.FileSubmitJudgeUtils;
import com.zxycloud.common.widget.CommonDialog;
import com.zxycloud.zszw.R;
import com.zxycloud.zszw.adapter.AccessoryAdapter;
import com.zxycloud.zszw.base.BaseBackFragment;
import com.zxycloud.zszw.event.type.VideoShowType;
import com.zxycloud.zszw.fragment.common.CameraFragment;
import com.zxycloud.zszw.fragment.common.ImagePagerFragment;
import com.zxycloud.zszw.fragment.common.PlanFragment;
import com.zxycloud.zszw.fragment.common.PlayVideoFragment;
import com.zxycloud.zszw.fragment.common.StringSelectFragment;
import com.zxycloud.zszw.listener.NetRequestListener;
import com.zxycloud.zszw.listener.OnItemClickListener;
import com.zxycloud.zszw.listener.OnLongClickListener;
import com.zxycloud.zszw.listener.OnProjectObtainListener;
import com.zxycloud.zszw.model.PathTypeBean;
import com.zxycloud.zszw.model.RequestAllocateBean;
import com.zxycloud.zszw.model.ResultDeviceBean;
import com.zxycloud.zszw.model.ResultSystemListBean;
import com.zxycloud.zszw.model.ResultUploadFileBean;
import com.zxycloud.zszw.model.ResultUploadImgBean;
import com.zxycloud.zszw.model.bean.AllocateBean;
import com.zxycloud.zszw.model.bean.DeviceBean;
import com.zxycloud.zszw.model.bean.SystemBean;
import com.zxycloud.common.base.BaseBean;
import com.zxycloud.common.utils.CommonUtils;
import com.zxycloud.common.utils.Luban;
import com.zxycloud.common.utils.PermissionUtils;
import com.zxycloud.common.utils.netWork.ApiRequest;
import com.zxycloud.common.utils.netWork.NetBean;
import com.zxycloud.common.utils.netWork.NetUtils;
import com.zxycloud.common.widget.AlertDialog;
import com.zxycloud.common.widget.RecordPopupWindow;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.zxycloud.zszw.fragment.common.PlanFragment.POINT_TYPE_ADD;

public class DeviceAddFragment extends BaseBackFragment implements Toolbar.OnMenuItemClickListener, OnLongClickListener, AccessoryAdapter.UploadFileOnListener, OnItemClickListener, NetRequestListener, View.OnClickListener {
    private final int PHOTO = 0xa1;//拍照
    private final int VIDEO = 0xa2;//视频
    private AccessoryAdapter pathAdapter;
    private RecyclerView recyclerAddAccessory;
    private String imgPath;// 照片地址
    private List<SystemBean> systemData;
    private String picUrl;
    private boolean isShowAccessory;
    private PathTypeBean loadBean;
    private DeviceBean deviceBean;

    private AllocateBean allocateBean = new AllocateBean();
    private AllocateBean.InstallationDetails installationDetailsBean = new AllocateBean.InstallationDetails();
    private AllocateBean.Position positionBean = new AllocateBean.Position();
    private EditText edDeviceSystem, etDeviceModel, etDeviceType;

    private CommonDialog submitDialog;
    private FileSubmitJudgeUtils submitJudgeBean;

    public static DeviceAddFragment newInstance(int flag, String adapterName, String deviceId, String placeId, String placeName, String picUrl) {
        DeviceAddFragment fragment = new DeviceAddFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("flag", flag);
        bundle.putString("adapterName", adapterName);
        bundle.putString("deviceId", deviceId);
        bundle.putString("placeId", placeId);
        bundle.putString("placeName", placeName);
        bundle.putString("picUrl", picUrl);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.device_add;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        setToolbarTitle(R.string.install).initToolbarNav().setToolbarMenu(R.menu.save, this);
        findViewById(R.id.ll_accessory).setVisibility(View.GONE);
        findViewById(R.id.ll_device_plan).setVisibility(View.GONE);
        edDeviceSystem = findViewById(R.id.et_device_system);
        etDeviceModel = findViewById(R.id.et_device_model);
        etDeviceType = findViewById(R.id.et_device_type);

        recyclerAddAccessory = findViewById(R.id.recycler_add_accessory);
        recyclerAddAccessory.setLayoutManager(new GridLayoutManager(getContext(), 3));

        pathAdapter = new AccessoryAdapter(getContext());
        pathAdapter.setOnItemClickListener(this, this, this);
        recyclerAddAccessory.setAdapter(pathAdapter);

        submitJudgeBean = new FileSubmitJudgeUtils(_mActivity, new FileSubmitJudgeUtils.OnSubmitStateChangeListener() {
            @Override
            public void onSubmitStateChanged(String stateString) {
                if (null != submitDialog && submitDialog.isShowing()) {
                    submitDialog.update(CommonDialog.ID_VICE_CONTENT, stateString);
                }
            }
        });

        picUrl = getArguments().getString("picUrl");
        if (TextUtils.isEmpty(picUrl)) {
            findViewById(R.id.ll_device_plan).setVisibility(View.GONE);
        } else {
            findViewById(R.id.ll_device_plan).setVisibility(View.VISIBLE);
            CommonUtils.glide().loadImageView(getContext(), picUrl, (ImageView) findViewById(R.id.iv_point_layout));
        }
        allocateBean.setPlaceId(getArguments().getString("placeId"));
        allocateBean.setDeviceId(getArguments().getString("deviceId"));
        allocateBean.setAdapterName(getArguments().getString("adapterName"));
        ((TextView) findViewById(R.id.tv_place)).setText(getArguments().getString("placeName"));

        // 初始化数据
        initData();

        setOnClickListener(this, R.id.iv_point_layout, R.id.fab_add_Accessory, R.id.rl_add_accessory, R.id.ll_voice, R.id.ll_video, R.id.ll_photo, R.id.et_device_model, R.id.et_device_system, R.id.et_device_type, R.id.iv_restore);
    }

    private void initData() {
        getProject(new OnProjectObtainListener() {
            @Override
            public void success(String projectId, String projectName) {
                netWork().setRequestListener(DeviceAddFragment.this,
                        netWork().apiRequest(NetBean.actionGetDeviceDetail, ResultDeviceBean.class, ApiRequest.REQUEST_TYPE_GET)//获取设备详情
                                .setRequestParams("deviceId", getArguments().getString("deviceId"))
                                .setRequestParams("adapterName", getArguments().getString("adapterName")));
            }
        });
        getDeviceSystemList();
    }

    private void getDeviceSystemList() {
        getProject(new OnProjectObtainListener() {
            @Override
            public void success(String projectId, String projectName) { // 获取系统列表
                netWork().addRequestListener(netWork().apiRequest(NetBean.actionGetDeviceSystem, ResultSystemListBean.class, ApiRequest.REQUEST_TYPE_GET));
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_point_layout:// 添加点位
                if (!TextUtils.isEmpty(picUrl))
                    if (positionBean.getLayerImageX() != 0 || positionBean.getLayerImageY() != 0)
                        startForResult(PlanFragment.newInstance(POINT_TYPE_ADD, picUrl, positionBean.getLayerImageX(), positionBean.getLayerImageY()), 1102);
                    else
                        startForResult(PlanFragment.newInstance(POINT_TYPE_ADD, picUrl, null, null), 1102);
                break;
            case R.id.rl_add_accessory:// 隐藏附件添加窗口
            case R.id.fab_add_Accessory:// 展开附件添加窗口
                addAccessory();
                break;
            case R.id.ll_voice:// 录音
                addAccessory();
                PermissionUtils.setRequestPermissions(DeviceAddFragment.this, new PermissionUtils.PermissionGrant() {
                    @Override
                    public Integer[] onPermissionGranted() {
                        return new Integer[]{PermissionUtils.CODE_RECORD_AUDIO, PermissionUtils.CODE_WRITE_EXTERNAL_STORAGE, PermissionUtils.CODE_READ_EXTERNAL_STORAGE};
                    }

                    @Override
                    public void onRequestResult(List<String> deniedPermission) {
                        if (CommonUtils.judgeListNull(deniedPermission) == 0) {
                            RecordPopupWindow recordPopupWindow = new RecordPopupWindow(getContext(), null);
                            recordPopupWindow.show(LayoutInflater.from(getContext()).inflate(R.layout.activity_base, null));
                            recordPopupWindow.getFilePath(new RecordPopupWindow.FilePathListener() {
                                @Override
                                public void getFilePath(String path) {
                                    PathTypeBean beanVideo = new PathTypeBean();
                                    beanVideo.setDataPath(path);
                                    beanVideo.setType(pathAdapter.TYPE_RECORD);
                                    pathAdapter.addData(beanVideo);
                                }
                            });
                        }
                    }
                });
                break;
            case R.id.ll_video:// 录视频
                addAccessory();
                PermissionUtils.setRequestPermissions(DeviceAddFragment.this, new PermissionUtils.PermissionGrant() {
                    @Override
                    public Integer[] onPermissionGranted() {
                        return new Integer[]{PermissionUtils.CODE_CAMERA, PermissionUtils.CODE_RECORD_AUDIO, PermissionUtils.CODE_READ_EXTERNAL_STORAGE, PermissionUtils.CODE_WRITE_EXTERNAL_STORAGE};
                    }

                    @Override
                    public void onRequestResult(List<String> deniedPermission) {
                        if (CommonUtils.judgeListNull(deniedPermission) == 0) {
                            startForResult(CameraFragment.newInstance(), VIDEO);
                        }
                    }
                });
                break;
            case R.id.ll_photo:// 拍照
                addAccessory();
                PermissionUtils.setRequestPermissions(DeviceAddFragment.this, new PermissionUtils.PermissionGrant() {
                    @Override
                    public Integer[] onPermissionGranted() {
                        return new Integer[]{PermissionUtils.CODE_CAMERA, PermissionUtils.CODE_READ_EXTERNAL_STORAGE, PermissionUtils.CODE_WRITE_EXTERNAL_STORAGE};
                    }

                    @Override
                    public void onRequestResult(List<String> deniedPermission) {
                        if (CommonUtils.judgeListNull(deniedPermission) == 0) {
                            imgPath = CommonUtils.openCamera(DeviceAddFragment.this, PHOTO);
                        }
                    }
                });
                break;
            case R.id.iv_restore:// 还原
                etDeviceType.setTextColor(getResources().getColor(R.color.colorTite));
                ((EditText) findViewById(R.id.ed_device_name)).setText(deviceBean.getDeviceTypeName());
                etDeviceType.setText(deviceBean.getDeviceTypeName());
                allocateBean.setUserDeviceTypeCode(deviceBean.getDeviceTypeCode());
                break;
            case R.id.et_device_type:// 设备类型
                AlertDialog builder = new AlertDialog(getContext()).builder();
                builder.setTitle(R.string.hint_device_type_choose)
                        .setMsg(R.string.hint_device_type_msg)
                        .setNegativeButton(R.string.dialog_no, null)
                        .setPositiveButton(R.string.dialog_yes, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                startForResult(SelectDeviceTypeFragment.newInstance(), 2110);
                            }
                        }).show();
                break;
            case R.id.et_device_model:// 设备型号
                startForResult(SelectDeviceModelFragment.newInstance(), 1231);
                break;
            case R.id.et_device_system:// 所属系统
                if (null == systemData) {
                    CommonUtils.toast(_mActivity, R.string.data_abnormal);
                    getDeviceSystemList();
                    return;
                }
                ArrayList<String> datass = new ArrayList<>();
                for (SystemBean systemDatum : systemData)
                    datass.add(systemDatum.getDeviceSystemName());
                StringSelectFragment selectFragmentss = StringSelectFragment.newInstance(R.string.string_select_title_device_system, datass);
                selectFragmentss.setOnItemClickListener(new OnItemClickListener() {
                    @Override
                    public void onItemClick(int position, View view, RecyclerView.ViewHolder vh) {
                        edDeviceSystem.setText(systemData.get(position).getDeviceSystemName());
                        allocateBean.setDeviceSystemCode(systemData.get(position).getDeviceSystemCode());
                        allocateBean.setDeviceSystemName(systemData.get(position).getDeviceSystemName());
                    }
                });
                start(selectFragmentss);
                break;
        }
    }

    /**
     * 添加附件控件动画
     */
    @SuppressLint("NewApi")
    private void addAccessory() {
        isShowAccessory = !isShowAccessory;
        findViewById(R.id.rl_add_accessory).post(new Runnable() {
            @Override
            public void run() {
                // 启动点位置
                FloatingActionButton fab = findViewById(R.id.fab_add_Accessory);
                fab.setImageResource(isShowAccessory ? R.drawable.ic_close_black_24dp : R.drawable.ic_add_black_24dp);
                int sx = (fab.getLeft() + fab.getRight()) / 2;
                int sy = (fab.getTop() + fab.getBottom()) / 2;
                // 开始动画大小半径
                float startX = isShowAccessory ? 0f : (float) Math.sqrt(Math.pow(getResources().getDisplayMetrics().widthPixels, 2) + Math.pow(getResources().getDisplayMetrics().heightPixels, 2));
                // 结束动画大小半径
                float endX = isShowAccessory ? (float) Math.sqrt(Math.pow(getResources().getDisplayMetrics().widthPixels, 2) + Math.pow(getResources().getDisplayMetrics().heightPixels, 2)) : 0f;
                Animator animator = ViewAnimationUtils.createCircularReveal(findViewById(R.id.rl_add_accessory), sx, sy, startX, endX);
                //在动画开始的地方速率改变比较慢,然后开始加速
                animator.setInterpolator(new AccelerateInterpolator());
                animator.setDuration(600);
                animator.start();

                if (isShowAccessory)
                    findViewById(R.id.rl_add_accessory).setVisibility(View.VISIBLE);
                animator.addListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        if (!isShowAccessory)
                            findViewById(R.id.rl_add_accessory).setVisibility(View.GONE);
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {
                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {
                    }
                });
            }
        });
    }

    /**
     * 相机返回
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_CANCELED && requestCode == PHOTO) {
            String saveAlbumPath = Luban.get(_mActivity).load(new File(imgPath)).launch().getAbsolutePath();
            PathTypeBean bean = new PathTypeBean();
            bean.setDataPath(saveAlbumPath);
            bean.setType(pathAdapter.TYPE_PHOTO);
            pathAdapter.addData(bean);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 视频返回，添加点位返回
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    public void onFragmentResult(int requestCode, int resultCode, Bundle data) {
        super.onFragmentResult(requestCode, resultCode, data);
        if (requestCode == VIDEO && resultCode == RESULT_OK && data != null) {
            String videoPath = data.getString(CameraFragment.PATH_VIDEO);
            String imgPath = data.getString(CameraFragment.PATH_VIDEO);
            if (!TextUtils.isEmpty(videoPath)) {
                PathTypeBean beanVideo = new PathTypeBean();
                beanVideo.setDataPath(videoPath.concat(PathTypeBean.SPLIT).concat(imgPath));
                beanVideo.setType(pathAdapter.TYPE_VIDEO);
                pathAdapter.addData(beanVideo);
            }
        } else if (requestCode == 1102 && resultCode == RESULT_OK && data != null) {
            positionBean.setLayerImageX(data.getDouble("width"));
            positionBean.setLayerImageY(data.getDouble("height"));
        } else if (requestCode == 2110 && resultCode == RESULT_OK && data != null) {
            etDeviceType.setTextColor(getResources().getColor(R.color.colorTite));
            String deviceType = data.getString("deviceTypeName");
            ((EditText) findViewById(R.id.ed_device_name)).setText(deviceType.substring(deviceType.indexOf("-") + 1));
            etDeviceType.setText(deviceType.substring(deviceType.indexOf("-") + 1));
            allocateBean.setUserDeviceTypeCode(data.getInt("deviceTypeCode"));
        } else if (requestCode == 1231 && resultCode == RESULT_OK && data != null) {
            String unitName = data.getString("unitName");
            etDeviceModel.setText(unitName);
            allocateBean.setDeviceUnitTypeName(unitName);
        }
    }

    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {
        if (allocateBean.getUserDeviceTypeCode() == 0) {
            etDeviceType.setTextColor(getResources().getColor(R.color.fire));
            CommonUtils.toast(getContext(), R.string.hint_device_type_toast);
            return true;
        } else if (edDeviceSystem.getText().toString().trim().isEmpty()) {
            CommonUtils.toast(getContext(), R.string.hint_device_system);
            return true;
        }
        if (submitJudgeBean.isCanSubmit()) {
            submitData();
        } else {
            submitDialog = new CommonDialog.Builder()
                    .setTitleRes(R.string.dialog_file_upload_title)
                    .setContentRes(R.string.dialog_file_upload_content)
                    .setViceContent(submitJudgeBean.fileUploadState(), false)
                    .setRightRes(R.string.cancel)
                    .setLeftRes(R.string.common_string_submit)
                    .build(_mActivity, new CommonDialog.OnCommonClickListener() {
                        @Override
                        public void onClick(View view, Object tag) {
                            if (view.getId() == CommonDialog.ID_LEFT) {
                                submitData();
                            }
                        }
                    });
            submitDialog.show();
        }
        return true;
    }

    private void submitData() {
        SparseArray<List<String>> fileResult = submitJudgeBean.getUploadUrls();
        if (!fileResult.get(FileSubmitJudgeUtils.UPLOAD_TYPE_VOICE).isEmpty())
            installationDetailsBean.setVoiceUrl(fileResult.get(FileSubmitJudgeUtils.UPLOAD_TYPE_VOICE).get(0));
        if (!fileResult.get(FileSubmitJudgeUtils.UPLOAD_TYPE_VIDEO).isEmpty())
            installationDetailsBean.setVideoUrl(fileResult.get(FileSubmitJudgeUtils.UPLOAD_TYPE_VIDEO).get(0));
        installationDetailsBean.setImgUrls(fileResult.get(FileSubmitJudgeUtils.UPLOAD_TYPE_IMG));
        allocateBean.setDeviceInstallLocation(((EditText) (findViewById(R.id.et_location_details))).getText().toString());
        allocateBean.setUserDeviceTypeName(((EditText) findViewById(R.id.ed_device_name)).getText().toString());
        allocateBean.setDeviceTypeName(etDeviceType.getText().toString());
        installationDetailsBean.setDescription(((EditText) (findViewById(R.id.et_postscript))).getText().toString());
        allocateBean.setInstallationDetails(installationDetailsBean);
        allocateBean.setPosition(positionBean);
        if (getArguments().getInt("flag") != 0) {
            // 独立式上传
            allocateDevice();
        } else {
            // 组合式回调
            Bundle bundle = new Bundle();
            bundle.putString("allocateBean", new Gson().toJson(allocateBean));
            setFragmentResult(RESULT_OK, bundle);
            finish();
        }
    }

    /**
     * 开启请求，分配独立式设备
     */
    private void allocateDevice() {
        List<AllocateBean> allocateBeans = new ArrayList<>();
        allocateBeans.add(allocateBean);
        RequestAllocateBean requestAllocateBean = new RequestAllocateBean();
        requestAllocateBean.setFlag(getArguments().getInt("flag"));
        requestAllocateBean.setDeviceDistributions(allocateBeans);
        netWork().addRequestListener(netWork().apiRequest(NetBean.actionPostAllocateDevice, BaseBean.class, ApiRequest.REQUEST_TYPE_POST).setRequestBody(requestAllocateBean));
    }

    /**
     * 附件条目点击事件，查看附件
     *
     * @param position
     * @param view
     * @param vh
     */
    @Override
    public void onItemClick(int position, View view, RecyclerView.ViewHolder vh) {
        switch (pathAdapter.getPathData().get(position).getType()) {
            case AccessoryAdapter.TYPE_RECORD:
                RecordPopupWindow recordPopupWindow = new RecordPopupWindow(getContext(), pathAdapter.getPathData().get(position).getDataPath());
                recordPopupWindow.show(LayoutInflater.from(getContext()).inflate(R.layout.activity_base, null));
                break;
            case AccessoryAdapter.TYPE_PHOTO:
                ArrayList<String> paths = new ArrayList<>();
                paths.add(pathAdapter.getPathData().get(position).getDataPath());
                start(ImagePagerFragment.newInstance(position, paths));
//                CommonUtils.openSystemPictureViewer(DeviceAddFragment.this, pathAdapter.getPathData().get(position).getDataPath());
                break;
            case AccessoryAdapter.TYPE_VIDEO:
                start(PlayVideoFragment.newInstance(VideoShowType.PLAY_TYPE_INSTALL_VIDEO, pathAdapter.getPathData().get(position).getDataPath()));
                break;
        }
    }

    /**
     * 附件条目长按事件.删除附件
     *
     * @param position
     * @param view
     * @param vh
     */
    @Override
    public void onItemLongClick(final int position, View view, RecyclerView.ViewHolder vh) {
        final com.zxycloud.common.widget.AlertDialog builder = new com.zxycloud.common.widget.AlertDialog(getContext()).builder();
        builder.setTitle(R.string.long_click_delete).setMsg(R.string.hint_delete);
        builder.setNegativeButton(R.string.dialog_no, null).setPositiveButton(R.string.dialog_yes, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (pathAdapter.getPathData() != null && pathAdapter.getPathData().size() > position) {
                    String type = pathAdapter.getPathData().get(position).getType();
                    new NetUtils(getContext()).request(null, false, new ApiRequest(NetBean.actionPostDeleteFlie, BaseBean.class)
                            .setRequestType(ApiRequest.REQUEST_TYPE_POST)
                            .setApiType(ApiRequest.API_TYPE_FILE_OPERATION)
                            .setRequestParams("url", pathAdapter.getPathData().get(position).getLoadUrl()));
                    pathAdapter.deleteData(position);
                    if (pathAdapter.TYPE_RECORD.equals(type)) {
                        submitJudgeBean.removeItem(FileSubmitJudgeUtils.UPLOAD_TYPE_VOICE, pathAdapter.getPathData().get(position).getDataPath());
                    } else if (pathAdapter.TYPE_VIDEO.equals(type)) {
                        submitJudgeBean.removeItem(FileSubmitJudgeUtils.UPLOAD_TYPE_VIDEO, pathAdapter.getPathData().get(position).getDataPath());
                    } else {
                        submitJudgeBean.removeItem(FileSubmitJudgeUtils.UPLOAD_TYPE_IMG, pathAdapter.getPathData().get(position).getDataPath());
                    }
                    setAccessory(false, type);
                }
            }
        }).show();
    }

    /**
     * 上传附件
     *
     * @param bean
     */
    @Override
    public void upLoadFile(PathTypeBean bean) {
        loadBean = bean;
        setAccessory(true, bean.getType());

        ApiRequest request;
        String saveAlbumPath;
        if (pathAdapter.TYPE_PHOTO.equals(bean.getType())) {
            saveAlbumPath = Luban.get(getContext()).load(new File(bean.getDataPath())).launch().getAbsolutePath();
            request = new ApiRequest<>(NetBean.actionPostUploadImg, ResultUploadImgBean.class);
            submitJudgeBean.uploadFile(FileSubmitJudgeUtils.UPLOAD_TYPE_IMG, saveAlbumPath, "");
        } else {
            saveAlbumPath = bean.getDataPath();
            request = new ApiRequest<>(NetBean.actionPostUploadFlie, ResultUploadFileBean.class);
            submitJudgeBean.uploadFile(pathAdapter.TYPE_VIDEO.equals(bean.getType()) ? FileSubmitJudgeUtils.UPLOAD_TYPE_VIDEO : FileSubmitJudgeUtils.UPLOAD_TYPE_VOICE, saveAlbumPath, null);
        }
        netWork().addRequestListener(request.setRequestType(ApiRequest.REQUEST_TYPE_POST)
                .setApiType(ApiRequest.API_TYPE_FILE_OPERATION)
                .setRequestBody(saveAlbumPath)
                .setTag(saveAlbumPath)
                .uploadFile());

    }

    /**
     * 根据上传文件隐藏或者显示附件按钮
     *
     * @param isEmpty
     * @param type
     */
    private void setAccessory(boolean isEmpty, String type) {
        if (isEmpty)
            findViewById(R.id.ll_accessory).setVisibility(View.VISIBLE);
        else if (pathAdapter.getPathData().size() <= 0)
            findViewById(R.id.ll_accessory).setVisibility(View.GONE);


        if (pathAdapter.TYPE_RECORD.equals(type)) {
            findViewById(R.id.ll_voice).setVisibility(isEmpty ? View.GONE : View.VISIBLE);
        } else if (pathAdapter.TYPE_VIDEO.equals(type)) {
            findViewById(R.id.ll_video).setVisibility(isEmpty ? View.GONE : View.VISIBLE);
        } else {
            int i = 0;
            for (PathTypeBean bean : pathAdapter.getPathData())
                if (bean.getType() == type)
                    ++i;

            if (i >= 9)
                findViewById(R.id.ll_photo).setVisibility(View.GONE);
            else
                findViewById(R.id.ll_photo).setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void success(String action, BaseBean baseBean, Object tag) {
        if (baseBean.isSuccessful())
            switch (action) {
                case NetBean.actionGetDeviceDetail:
                    if (((ResultDeviceBean) baseBean).getData() != null) {
                        deviceBean = ((ResultDeviceBean) baseBean).getData();
                        ((TextView) findViewById(R.id.tv_device_number)).setText(CommonUtils.string().getString(getContext(), R.string.title_device_code).concat(deviceBean.getDeviceNumber()));// 设备编码
                        ((EditText) findViewById(R.id.et_location_details)).setText(deviceBean.getDeviceInstallLocation());// 安装位置
                        ((EditText) findViewById(R.id.ed_device_name)).setText(deviceBean.getUserDeviceTypeName());// 设备名称
                        findViewById(R.id.ed_device_name).requestFocus();

                        edDeviceSystem.setText(deviceBean.getDeviceSystemName());
                        etDeviceType.setText(deviceBean.getDeviceTypeName());
                        allocateBean.setDeviceSystemCode(deviceBean.getDeviceSystemCode());
                        allocateBean.setDeviceSystemName(deviceBean.getDeviceSystemName());
                        allocateBean.setUserDeviceTypeCode(deviceBean.getDeviceTypeCode());
                        allocateBean.setDeviceUnitTypeName(deviceBean.getDeviceUnitTypeName());
                        if (getPreFragment() instanceof DeviceInstallListFragment) {// 安装列表回显功能
                            DeviceInstallListFragment fragment = (DeviceInstallListFragment) getPreFragment();
                            AllocateBean mAbean = fragment.installAdapter.getDeviceDistributions().get(fragment.itemPosition);
                            String deviceInstallLocation = mAbean.getDeviceInstallLocation();// 安装位置
                            String deviceSystemName = mAbean.getDeviceSystemName();// 所属系统
                            int deviceSystemCode = mAbean.getDeviceSystemCode();// 所属系统code
                            String deviceUnitTypeName = mAbean.getDeviceUnitTypeName();// 设备型号
                            String userDeviceTypeName = mAbean.getUserDeviceTypeName();// 设备名称
                            String deviceTypeName = mAbean.getDeviceTypeName();// 设备类型
                            int userDeviceTypeCode = mAbean.getUserDeviceTypeCode();// 设备类型code
                            if (!TextUtils.isEmpty(deviceInstallLocation))
                                ((EditText) findViewById(R.id.et_location_details)).setText(deviceInstallLocation);
                            if (!TextUtils.isEmpty(deviceSystemName)) {
                                edDeviceSystem.setText(deviceSystemName);
                                allocateBean.setDeviceSystemName(deviceSystemName);
                            }
                            if (deviceSystemCode != 0)
                                allocateBean.setDeviceSystemCode(deviceSystemCode);
                            if (!TextUtils.isEmpty(deviceUnitTypeName)) {
                                etDeviceModel.setText(deviceUnitTypeName);
                                allocateBean.setDeviceUnitTypeName(deviceUnitTypeName);
                            }
                            if (userDeviceTypeCode != 0)
                                allocateBean.setUserDeviceTypeCode(userDeviceTypeCode);
                            if (!TextUtils.isEmpty(userDeviceTypeName)) {
                                ((EditText) findViewById(R.id.ed_device_name)).setText(userDeviceTypeName);// 设备名称
                                findViewById(R.id.ed_device_name).requestFocus();
                            }
                            if (!TextUtils.isEmpty(deviceTypeName))
                                etDeviceType.setText(deviceTypeName);
                        }
                    }
                    break;
                case NetBean.actionGetDeviceSystem:
                    systemData = ((ResultSystemListBean) (baseBean)).getData();
                    break;
                case NetBean.actionPostAllocateDevice:
                    finish();
                    break;
                case NetBean.actionPostUploadImg:
                case NetBean.actionPostUploadFlie:
                    if (loadBean != null) {
                        String loadUrl;
                        if (pathAdapter.TYPE_RECORD.equals(loadBean.getType())) {
                            loadUrl = ((ResultUploadFileBean) (baseBean)).getData().getUrl();
                            submitJudgeBean.uploadFile(FileSubmitJudgeUtils.UPLOAD_TYPE_VOICE, (String) tag, loadUrl);
                        } else if (pathAdapter.TYPE_VIDEO.equals(loadBean.getType())) {
                            loadUrl = ((ResultUploadFileBean) (baseBean)).getData().getUrl();
                            submitJudgeBean.uploadFile(FileSubmitJudgeUtils.UPLOAD_TYPE_VIDEO, (String) tag, loadUrl);
                        } else {
                            loadUrl = ((ResultUploadImgBean) (baseBean)).getData().getImgUrl();
                            submitJudgeBean.uploadFile(FileSubmitJudgeUtils.UPLOAD_TYPE_IMG, (String) tag, loadUrl);
                        }
                        PathTypeBean typeBean = new PathTypeBean(loadBean.getType(), loadBean.getDataPath(), loadUrl);
                        Collections.replaceAll(pathAdapter.getPathData(), loadBean, typeBean);
                    }
                    break;
            }
        else
            CommonUtils.toast(getContext(), baseBean.getMessage());
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionUtils.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}