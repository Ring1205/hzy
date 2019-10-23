package com.zxycloud.zszw.fragment.service.risk;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.SparseArray;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.zxycloud.zszw.R;
import com.zxycloud.zszw.base.BaseBackFragment;
import com.zxycloud.zszw.event.RiskEvent;
import com.zxycloud.zszw.event.type.VideoShowType;
import com.zxycloud.zszw.fragment.common.CameraFragment;
import com.zxycloud.zszw.fragment.common.ImagePagerFragment;
import com.zxycloud.zszw.fragment.common.PlayVideoFragment;
import com.zxycloud.common.utils.FileSubmitJudgeUtils;
import com.zxycloud.zszw.model.ResultUploadFileBean;
import com.zxycloud.zszw.model.ResultUploadImgBean;
import com.zxycloud.zszw.model.bean.UploadImgBean;
import com.zxycloud.zszw.model.request.ImageAddBean;
import com.zxycloud.zszw.model.request.RequestRiskProcessReportBean;
import com.zxycloud.common.base.BaseBean;
import com.zxycloud.common.utils.CommonUtils;
import com.zxycloud.common.utils.GlideUtils;
import com.zxycloud.common.utils.Luban;
import com.zxycloud.common.utils.PermissionUtils;
import com.zxycloud.common.utils.PopupWindows.CustomPopupWindows;
import com.zxycloud.common.utils.PopupWindows.PopupParam;
import com.zxycloud.common.utils.netWork.ApiRequest;
import com.zxycloud.common.utils.netWork.NetBean;
import com.zxycloud.common.utils.netWork.NetUtils;
import com.zxycloud.common.widget.BswRecyclerView.BswRecyclerView;
import com.zxycloud.common.widget.BswRecyclerView.ConvertViewCallBack;
import com.zxycloud.common.widget.BswRecyclerView.LimitAnnotation;
import com.zxycloud.common.widget.BswRecyclerView.RecyclerViewHolder;
import com.zxycloud.common.widget.CommonDialog;
import com.zxycloud.common.widget.RecordPopupWindow;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.iwf.photopicker.PhotoPicker;
import me.iwf.photopicker.PhotoPickerActivity;

/**
 * @author leiming
 * @date 2019/3/28.
 */
public class RiskProcessReportFragment extends BaseBackFragment {
    private final String DIVIDE_STRING = "!~@#$%^&*";

    private CommonDialog submitDialog;

    private final int MAX_COUNT = 9;

    private final int PHOTO = 0xa1;//拍照
    private final int ALBUM = 0xa2;//拍照
    private final int VIDEO = 0xa3;//视频

    /**
     * 拍照的照片地址
     */
    private String imgPath;
    private String videoPath;
    private String recordPath;
    private BswRecyclerView<ImageAddBean> riskShowImgRv;
    private ImageView itemVideoAdd, itemVideoShow;
    private View itemVideoDelete;
    private NetUtils netUtils;
    private ImageView itemVoiceAdd;
    private View itemVoiceDelete;
    private TextView riskDescription;
    private View itemAdd;

    private RequestRiskProcessReportBean riskReportBean;

    /**
     * 文件上传状态判断类，防止图片、视频、语音未上传完成时，用户点击提交，造成的数据损失
     */
    private FileSubmitJudgeUtils submitJudgeBean;

    private CustomPopupWindows riskLevelWindows;
    private String riskId;
    private String processState;
    private String processStateName;
    private TextView riskProcessOperation;

    public static RiskProcessReportFragment newInstance(String riskId, String processState, String processStateName) {
        Bundle args = new Bundle();
        args.putString("riskId", riskId);
        args.putString("processState", processState);
        args.putString("processStateName", processStateName);
        RiskProcessReportFragment fragment = new RiskProcessReportFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_risk_process_report;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        setToolbarTitle(R.string.string_risk_progress_report_title);
        setToolbarMenu(R.menu.menu_save, menuItemClickListener);
        initToolbarNav();

        riskId = getArguments().getString("riskId");
        processState = getArguments().getString("processState");
        processStateName = getArguments().getString("processStateName");

        setOnClickListener(onClickListener, R.id.item_add
                , R.id.item_img_delete
                , R.id.item_voice_add
                , R.id.item_voice_delete);

        netUtils = NetUtils.getNewInstance(_mActivity);

        submitJudgeBean = new FileSubmitJudgeUtils(_mActivity, new FileSubmitJudgeUtils.OnSubmitStateChangeListener() {
            @Override
            public void onSubmitStateChanged(String stateString) {
                CommonUtils.log().i("FileSubmitJudgeUtils#stateString = " + stateString);
                if (null != submitDialog && submitDialog.isShowing()) {
                    submitDialog.update(CommonDialog.ID_VICE_CONTENT, stateString);
                }
            }
        });

        riskReportBean = new RequestRiskProcessReportBean(riskId, processState);

        itemVoiceAdd = findViewById(R.id.item_voice_add);
        itemVoiceDelete = findViewById(R.id.item_voice_delete);
        riskDescription = findViewById(R.id.risk_description);
        riskProcessOperation = findViewById(R.id.risk_process_operation);
        riskProcessOperation.setText(processStateName);
        itemAdd = findViewById(R.id.item_add);

        riskShowImgRv = findViewById(R.id.risk_show_img_rv);
        riskShowImgRv.initAdapter(R.layout.item_img_layout, convertViewCallBack)
                .setLayoutManager(LimitAnnotation.VERTICAL, 3)
                .setMaxCount(9);

        List<ImageAddBean> addBeans = new ArrayList<>();
        addBeans.add(new ImageAddBean(R.mipmap.ic_can_add_file));
        riskShowImgRv.setData(addBeans);

        itemVideoAdd = findViewById(R.id.item_img_add);
        itemVideoShow = findViewById(R.id.item_img_show);
        itemVideoDelete = findViewById(R.id.item_img_delete);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_CANCELED) {
            switch (requestCode) {
                case ALBUM:
                    List<String> selectedPhotos = data.getStringArrayListExtra(PhotoPicker.KEY_SELECTED_PHOTOS);
                    final Map<String, String> markedPhotos = new HashMap<>();
                    for (String s : selectedPhotos) {
                        String tempPhotoPath = s.concat(DIVIDE_STRING).concat(System.currentTimeMillis() + "");
                        markedPhotos.put(s, tempPhotoPath);
                        riskShowImgRv.addData(riskShowImgRv.getItemCount() - 1, new ImageAddBean(tempPhotoPath, false));
                        submitJudgeBean.uploadFile(FileSubmitJudgeUtils.UPLOAD_TYPE_IMG, tempPhotoPath, "");
                    }
                    for (final String photo : selectedPhotos) {
                        CommonUtils.threadPoolExecute(new Runnable() {
                            @Override
                            public void run() {
                                final String tempPhotoPath = markedPhotos.get(photo);
                                final String saveAlbumPath = Luban.get(_mActivity).load(new File(photo)).launch().getAbsolutePath();
                                netUtils.request(new NetUtils.NetRequestCallBack<ResultUploadImgBean>() {
                                    @Override
                                    public void success(String action, ResultUploadImgBean resultImageUploadBean, Object tag) {
                                        if (resultImageUploadBean.isSuccessful()) {
                                            UploadImgBean imageUploadBean = resultImageUploadBean.getData();
                                            String imgUrl = imageUploadBean.getImgUrl();
                                            submitJudgeBean.uploadFile(FileSubmitJudgeUtils.UPLOAD_TYPE_IMG, (String) tag, imgUrl);
                                        } else {
                                            CommonUtils.toast(_mActivity, resultImageUploadBean.getMessage());
                                        }
                                    }

                                    @Override
                                    public void error(String action, Throwable e, Object tag) {

                                    }
                                }, false, new ApiRequest<>(NetBean.actionPostUploadImg, ResultUploadImgBean.class)
                                        .setRequestType(ApiRequest.REQUEST_TYPE_POST)
                                        .setRequestBody(saveAlbumPath)
                                        .setTag(tempPhotoPath)
                                        .uploadFile());
                            }
                        });
                    }
                    break;

                case PHOTO:
                    String savePhotoPath = Luban.get(_mActivity).load(new File(imgPath)).launch().getAbsolutePath();
                    netUtils.request(new NetUtils.NetRequestCallBack<ResultUploadImgBean>() {
                        @Override
                        public void success(String action, ResultUploadImgBean resultImageUploadBean, Object tag) {
                            if (resultImageUploadBean.isSuccessful()) {
                                UploadImgBean imageUploadBean = resultImageUploadBean.getData();
                                String imgUrl = imageUploadBean.getImgUrl();
                                submitJudgeBean.uploadFile(FileSubmitJudgeUtils.UPLOAD_TYPE_IMG, (String) tag, imgUrl);
                            } else {
                                CommonUtils.toast(_mActivity, resultImageUploadBean.getMessage());
                            }
                        }

                        @Override
                        public void error(String action, Throwable e, Object tag) {

                        }
                    }, false, new ApiRequest<>(NetBean.actionPostUploadImg, ResultUploadImgBean.class)
                            .setRequestType(ApiRequest.REQUEST_TYPE_POST)
                            .setRequestBody(savePhotoPath)
                            .setTag(savePhotoPath)
                            .uploadFile());
                    riskShowImgRv.addData(riskShowImgRv.getItemCount() - 1, new ImageAddBean(savePhotoPath, true));
                    submitJudgeBean.uploadFile(FileSubmitJudgeUtils.UPLOAD_TYPE_IMG, savePhotoPath, null);
                    break;
            }
        }
    }

    @Override
    public void onFragmentResult(int requestCode, int resultCode, Bundle data) {
        super.onFragmentResult(requestCode, resultCode, data);
        if (requestCode == VIDEO && resultCode == RESULT_OK && data != null) {
            videoPath = data.getString(CameraFragment.PATH_VIDEO);
            netUtils.request(new NetUtils.NetRequestCallBack<ResultUploadFileBean>() {
                @Override
                public void success(String action, ResultUploadFileBean resultFileUploadBean, Object tag) {
                    if (resultFileUploadBean.isSuccessful()) {
                        String videoUrl = resultFileUploadBean.getData().getUrl();
                        submitJudgeBean.uploadFile(FileSubmitJudgeUtils.UPLOAD_TYPE_VIDEO, (String) tag, videoUrl);
                    } else {
                        CommonUtils.toast(_mActivity, resultFileUploadBean.getMessage());
                    }
                }

                @Override
                public void error(String action, Throwable e, Object tag) {

                }
            }, false, new ApiRequest<>(NetBean.actionPostUploadFlie, ResultUploadFileBean.class)
                    .setApiType(ApiRequest.API_TYPE_FILE_OPERATION)
                    .setRequestType(ApiRequest.REQUEST_TYPE_POST)
                    .setRequestBody(videoPath)
                    .setTag(videoPath)
                    .uploadFile());
            submitJudgeBean.uploadFile(FileSubmitJudgeUtils.UPLOAD_TYPE_VIDEO, videoPath, null);
            itemVideoAdd.setImageResource(R.drawable.ic_play_arrow_play_48dp);
            itemAdd.setBackgroundResource(R.color.common_color_text);
            CommonUtils.glide().loadImageViewThumbnail(_mActivity, data.getString(CameraFragment.VIDEO_CAPTURE), itemVideoShow);
            itemVideoShow.setVisibility(View.VISIBLE);
            itemVideoDelete.setVisibility(View.VISIBLE);
        }
    }

    private void deleteUrl(String url) {
        netUtils.request(new NetUtils.NetRequestCallBack() {
            @Override
            public void success(String action, BaseBean baseBean, Object tag) {

            }

            @Override
            public void error(String action, Throwable e, Object tag) {

            }
        }, false, new ApiRequest<>(NetBean.actionPostDeleteFlie, BaseBean.class)
                .setRequestType(ApiRequest.REQUEST_TYPE_POST)
                .setApiType(ApiRequest.API_TYPE_FILE_OPERATION)
                .setRequestParams("url", TextUtils.isEmpty(url) ? "" : url));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionUtils.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private Toolbar.OnMenuItemClickListener menuItemClickListener = new Toolbar.OnMenuItemClickListener() {
        @Override
        public boolean onMenuItemClick(MenuItem item) {
            if (submitJudgeBean.isCanSubmit()) {
                submitRisk();
            } else {
                submitDialog = new CommonDialog.Builder()
                        .setTitleRes(R.string.dialog_file_upload_title)
                        .setContentRes(R.string.dialog_file_upload_content)
                        .setViceContent(submitJudgeBean.fileUploadState(), false)
                        .setRightRes(R.string.cancel)
                        .setLeftRes(R.string.common_string_submit)
                        .build(_mActivity, onCommonClickListener);
                submitDialog.show();
            }
            return true;
        }
    };

    private void submitRisk() {
        riskReportBean.setDescription(CommonUtils.string().getString(riskDescription));
        SparseArray<List<String>> fileResult = submitJudgeBean.getUploadUrls();
        riskReportBean.setVoiceUrl(fileResult.get(FileSubmitJudgeUtils.UPLOAD_TYPE_VOICE));
        riskReportBean.setVideoUrl(fileResult.get(FileSubmitJudgeUtils.UPLOAD_TYPE_VIDEO));
        riskReportBean.setImgUrl(fileResult.get(FileSubmitJudgeUtils.UPLOAD_TYPE_IMG));
        if (riskReportBean.canReport(processState)) {
            netUtils.request(new NetUtils.NetRequestCallBack() {
                @Override
                public void success(String action, BaseBean baseBean, Object tag) {
                    if (baseBean.isSuccessful()) {
                        setFragmentResult(RESULT_OK, null);
                        EventBus.getDefault().postSticky(new RiskEvent());
                        finish();
                    }
                    CommonUtils.toast(_mActivity, baseBean.getMessage());
                }

                @Override
                public void error(String action, Throwable e, Object tag) {

                }
            }, true, new ApiRequest<>(NetBean.actionPostRiskProcessReport, BaseBean.class)
                    .setRequestType(ApiRequest.REQUEST_TYPE_POST)
                    .setRequestBody(riskReportBean));
        } else {
            CommonUtils.toast(_mActivity, R.string.toast_risk_report_data_incomplete);
        }
    }

    private ConvertViewCallBack<ImageAddBean> convertViewCallBack = new ConvertViewCallBack<ImageAddBean>() {
        GlideUtils glideUtils = CommonUtils.glide();

        @Override
        public void convert(final RecyclerViewHolder holder, final ImageAddBean imageAddBean, final int position, int layoutTag) {
            if (imageAddBean.getImgResId() == 0) {
                glideUtils.loadImageView(_mActivity, imageAddBean.getImgPath(DIVIDE_STRING), holder.getImageView(R.id.item_img_show));
                holder.setVisibility(R.id.item_img_delete, View.VISIBLE)
                        .setVisibility(R.id.item_img_add, View.GONE)
                        .setVisibility(R.id.item_img_show, View.VISIBLE)
                        .setOnClickListener(R.id.item_img_delete, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                riskShowImgRv.removeItem(position);
                                deleteUrl(submitJudgeBean.getItem(FileSubmitJudgeUtils.UPLOAD_TYPE_IMG, imageAddBean.getImgPath()));
                                submitJudgeBean.removeItem(FileSubmitJudgeUtils.UPLOAD_TYPE_IMG, imageAddBean.getImgPath());
                            }
                        })
                        .setClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                List<ImageAddBean> addBeans = riskShowImgRv.getData();
                                ArrayList<String> paths = new ArrayList<>();
                                for (ImageAddBean addBean : addBeans) {
                                    String path = addBean.getImgPath();
                                    if (!TextUtils.isEmpty(path)) {
                                        paths.add(path);
                                    }
                                }
                                start(ImagePagerFragment.newInstance(position, paths));
                            }
                        });
            } else {
                holder.setVisibility(R.id.item_img_show, View.GONE)
                        .setVisibility(R.id.item_img_delete, View.GONE)
                        .setVisibility(R.id.item_img_add, View.VISIBLE)
                        .setClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (isInputMethodShowing()) {
                                    hideSoftInput();
                                }
                                riskLevelWindows = CustomPopupWindows.getInstance(_mActivity
                                        , holder.getView(R.id.item_img_add)
                                        , R.layout.popup_submit_img_layout
                                        , new PopupParam(PopupParam.RELATIVE_SCREEN, PopupParam.SHOW_POSITION_BOTTOM));
                                riskLevelWindows.showPopu(new CustomPopupWindows.InitCustomPopupListener() {
                                    @Override
                                    public void initPopup(CustomPopupWindows.PopupHolder holder) {
                                        holder.setClickListener(R.id.popup_capture, new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                // 拍照password/randomCode/get
                                                PermissionUtils.setRequestPermissions(RiskProcessReportFragment.this, new PermissionUtils.PermissionGrant() {
                                                    @Override
                                                    public Integer[] onPermissionGranted() {
                                                        return new Integer[]{PermissionUtils.CODE_CAMERA, PermissionUtils.CODE_READ_EXTERNAL_STORAGE, PermissionUtils.CODE_WRITE_EXTERNAL_STORAGE};
                                                    }

                                                    @Override
                                                    public void onRequestResult(List<String> deniedPermission) {
                                                        if (CommonUtils.judgeListNull(deniedPermission) == 0) {
                                                            imgPath = CommonUtils.openCamera(RiskProcessReportFragment.this, PHOTO);
                                                            riskLevelWindows.dismiss();
                                                        } else {
                                                            CommonUtils.toast(_mActivity, R.string.common_permission_camera);
                                                        }
                                                    }
                                                });
                                            }
                                        }).setClickListener(R.id.popup_album, new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                PermissionUtils.setRequestPermissions(RiskProcessReportFragment.this, new PermissionUtils.PermissionGrant() {
                                                    @Override
                                                    public Integer[] onPermissionGranted() {
                                                        return new Integer[]{PermissionUtils.CODE_READ_EXTERNAL_STORAGE, PermissionUtils.CODE_WRITE_EXTERNAL_STORAGE};
                                                    }

                                                    @Override
                                                    public void onRequestResult(List<String> deniedPermission) {
                                                        if (CommonUtils.judgeListNull(deniedPermission) == 0) {
                                                            Intent inImg = new Intent(_mActivity, PhotoPickerActivity.class);
                                                            inImg.putExtra(PhotoPicker.EXTRA_SHOW_CAMERA, false);
                                                            inImg.putExtra(PhotoPicker.EXTRA_MAX_COUNT, MAX_COUNT - riskShowImgRv.getItemCount() + 1);  // 添加图片的加号占一项
                                                            startActivityForResult(inImg, ALBUM);
                                                            riskLevelWindows.dismiss();
                                                        } else {
                                                            CommonUtils.toast(_mActivity, R.string.common_permission_album);
                                                        }
                                                    }
                                                });
                                            }
                                        }).setClickListener(R.id.popup_cancel, new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                riskLevelWindows.dismiss();
                                            }
                                        });
                                    }
                                });
                            }
                        });
            }
        }

        @Override
        public void loadingFinished() {

        }
    };

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            int i = view.getId();
            if (i == R.id.item_add) {
                if (TextUtils.isEmpty(videoPath)) {
                    // 录视频
                    PermissionUtils.setRequestPermissions(RiskProcessReportFragment.this, new PermissionUtils.PermissionGrant() {
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
                } else {
                    start(PlayVideoFragment.newInstance(VideoShowType.PLAY_TYPE_INSTALL_VIDEO, videoPath));
                }

                // 由于界面复用，因此这里是删除视频的调用
            } else if (i == R.id.item_img_delete) {
                itemVideoAdd.setImageResource(R.mipmap.ic_can_add_file);
                itemAdd.setBackgroundResource(android.R.color.white);
                itemVideoDelete.setVisibility(View.GONE);
                itemVideoShow.setVisibility(View.GONE);
                itemVideoShow.setImageResource(0);
                deleteUrl(submitJudgeBean.getItem(FileSubmitJudgeUtils.UPLOAD_TYPE_VIDEO, videoPath));
                submitJudgeBean.removeItem(FileSubmitJudgeUtils.UPLOAD_TYPE_VIDEO, videoPath);
                videoPath = null;
            } else if (i == R.id.item_voice_delete) {
                itemVoiceAdd.setImageResource(R.mipmap.ic_can_add_file);
                itemVoiceDelete.setVisibility(View.GONE);
                deleteUrl(submitJudgeBean.getItem(FileSubmitJudgeUtils.UPLOAD_TYPE_VOICE, recordPath));
                submitJudgeBean.removeItem(FileSubmitJudgeUtils.UPLOAD_TYPE_VOICE, recordPath);
                recordPath = null;
            } else if (i == R.id.item_voice_add) {
                if (TextUtils.isEmpty(recordPath)) {
                    PermissionUtils.setRequestPermissions(RiskProcessReportFragment.this, new PermissionUtils.PermissionGrant() {
                        @Override
                        public Integer[] onPermissionGranted() {
                            return new Integer[]{PermissionUtils.CODE_RECORD_AUDIO, PermissionUtils.CODE_WRITE_EXTERNAL_STORAGE, PermissionUtils.CODE_READ_EXTERNAL_STORAGE};
                        }

                        @Override
                        public void onRequestResult(List<String> deniedPermission) {
                            if (CommonUtils.judgeListNull(deniedPermission) == 0) {
                                final RecordPopupWindow recordPopupWindow = new RecordPopupWindow(getContext(), null);
                                recordPopupWindow.show(LayoutInflater.from(getContext()).inflate(R.layout.activity_base, null));
                                recordPopupWindow.getFilePath(new RecordPopupWindow.FilePathListener() {
                                    @Override
                                    public void getFilePath(String path) {
                                        recordPopupWindow.dismiss();
                                        recordPath = path;
                                        if (!TextUtils.isEmpty(recordPath)) {
                                            CommonUtils.threadPoolExecute(new Runnable() {
                                                @Override
                                                public void run() {
                                                    long time = System.currentTimeMillis();
                                                    try {
                                                        CommonUtils.log().i("语音上传测试  开始等待 time = " + time);
                                                        Thread.sleep(10000);
                                                    } catch (InterruptedException e) {
                                                        e.printStackTrace();
                                                        long errorTime = System.currentTimeMillis();
                                                        CommonUtils.log().i("语音上传测试  等待异常 errorTime = " + errorTime + "  wait = " + (errorTime - time));
                                                    } finally {
                                                        long finallyTime = System.currentTimeMillis();
                                                        CommonUtils.log().i("语音上传测试  等待结束 finallyTime = " + finallyTime + "  wait = " + (finallyTime - time));
                                                        netUtils.request(new NetUtils.NetRequestCallBack<ResultUploadFileBean>() {
                                                            @Override
                                                            public void success(String action, ResultUploadFileBean resultFileUploadBean, Object tag) {
                                                                if (resultFileUploadBean.isSuccessful()) {
                                                                    String voiceUrl = resultFileUploadBean.getData().getUrl();
                                                                    submitJudgeBean.uploadFile(FileSubmitJudgeUtils.UPLOAD_TYPE_VOICE, (String) tag, voiceUrl);
                                                                } else {
                                                                    CommonUtils.toast(_mActivity, resultFileUploadBean.getMessage());
                                                                }
                                                            }

                                                            @Override
                                                            public void error(String action, Throwable e, Object tag) {

                                                            }
                                                        }, false, new ApiRequest<>(NetBean.actionPostUploadFlie, ResultUploadFileBean.class)
                                                                .setRequestType(ApiRequest.REQUEST_TYPE_POST)
                                                                .setApiType(ApiRequest.API_TYPE_FILE_OPERATION)
                                                                .setRequestBody(recordPath)
                                                                .setTag(recordPath)
                                                                .uploadFile());
                                                    }
                                                }
                                            });
                                            submitJudgeBean.uploadFile(FileSubmitJudgeUtils.UPLOAD_TYPE_VOICE, recordPath, null);
                                            itemVoiceAdd.setImageResource(R.drawable.ic_keyboard_voice_main_24dp);
                                            itemVoiceDelete.setVisibility(View.VISIBLE);
                                        }
                                    }
                                });
                            }
                        }
                    });
                } else {
                    RecordPopupWindow recordPopupWindow = new RecordPopupWindow(getContext(), recordPath);
                    recordPopupWindow.show(LayoutInflater.from(getContext()).inflate(R.layout.activity_base, null));
                }
            }
        }
    };

    private CommonDialog.OnCommonClickListener onCommonClickListener = new CommonDialog.OnCommonClickListener() {

        @Override
        public void onClick(View view, Object tag) {
            if (view.getId() == CommonDialog.ID_LEFT) {
                submitRisk();
            }
        }
    };
}
