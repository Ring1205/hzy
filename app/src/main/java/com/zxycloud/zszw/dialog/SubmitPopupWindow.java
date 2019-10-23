package com.zxycloud.zszw.dialog;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.zxycloud.zszw.R;
import com.zxycloud.zszw.adapter.AccessoryAdapter;
import com.zxycloud.zszw.fragment.common.ImagePagerFragment;
import com.zxycloud.zszw.listener.OnItemClickListener;
import com.zxycloud.zszw.listener.OnLongClickListener;
import com.zxycloud.zszw.model.PathTypeBean;
import com.zxycloud.zszw.model.ResultUploadFileBean;
import com.zxycloud.zszw.model.ResultUploadImgBean;
import com.zxycloud.common.base.BaseBean;
import com.zxycloud.common.base.fragment.SupportFragment;
import com.zxycloud.common.utils.CommonUtils;
import com.zxycloud.common.utils.Luban;
import com.zxycloud.common.utils.netWork.ApiRequest;
import com.zxycloud.common.utils.netWork.NetBean;
import com.zxycloud.common.utils.netWork.NetUtils;
import com.zxycloud.common.widget.RecordPopupWindow;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;

public class SubmitPopupWindow extends PopupWindow implements AccessoryAdapter.UploadFileOnListener, OnItemClickListener, OnLongClickListener, View.OnClickListener {
    private View mView;
    private Context mContext; // 上下文参数
    private RecyclerView recyclerView;
    private EditText information;
    private AccessoryAdapter pathAdapter;
    private SupportFragment mFragment;

    public SubmitPopupWindow(SupportFragment fragment) {
        this.mContext = fragment.getContext();
        this.mFragment = fragment;
        initView();
    }

    private void initView() {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mView = inflater.inflate(R.layout.submit_task_pop, null);

        recyclerView = mView.findViewById(R.id.recycler_add_accessory);
        recyclerView.setLayoutManager(new GridLayoutManager(mContext, 3));
        pathAdapter = new AccessoryAdapter(mContext);
        pathAdapter.setOnItemClickListener(this, this, this);
        recyclerView.setAdapter(pathAdapter);

        mView.findViewById(R.id.iv_dismiss).setOnClickListener(this);
        mView.findViewById(R.id.btn_submit).setOnClickListener(this);
        information = mView.findViewById(R.id.et_note);
        information.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (information.getText().toString().length() >= 50) {
                    mView.findViewById(R.id.tv_descriptor_limit).setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        // 导入布局
        this.setContentView(mView);
        // 设置动画效果
        this.setAnimationStyle(R.style.popwindow_anim_style);
        this.setWidth(WindowManager.LayoutParams.MATCH_PARENT);
        this.setHeight(WindowManager.LayoutParams.MATCH_PARENT);
        // 设置可触
        this.setFocusable(true);
        ColorDrawable dw = new ColorDrawable(0x0000000);
        this.setBackgroundDrawable(dw);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_dismiss:
                if (pathAdapter != null)
                    for (PathTypeBean pathDatum : pathAdapter.getPathData())
                        onAdditionalListener.removeImageUrl(pathDatum.getDataPath());
                dismiss();
                break;
            case R.id.btn_submit:
                if (TextUtils.isEmpty(information.getText().toString())) {
                    CommonUtils.toast(mContext, R.string.toast_error_information);
                } else {
                    onAdditionalListener.setInformation(information.getText().toString());
                    dismiss();
                }
                break;
        }
    }

    public void show() {
        showAtLocation(LayoutInflater.from(mContext).inflate(R.layout.activity_base, null), Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
    }

    public void setAddImgOnClickListener(View.OnClickListener listener) {
        mView.findViewById(R.id.tv_add_img).setOnClickListener(listener);
    }

    public void setPathData(PathTypeBean beanVideo) {
        pathAdapter.addData(beanVideo);
        if (pathAdapter.getItemCount() >= 9)
            mView.findViewById(R.id.tv_add_img).setVisibility(View.GONE);
    }

    @Override
    public void onItemClick(int position, View view, RecyclerView.ViewHolder vh) {
        switch (pathAdapter.getPathData().get(position).getType()) {
            case AccessoryAdapter.TYPE_RECORD:
                RecordPopupWindow recordPopupWindow = new RecordPopupWindow(mContext, pathAdapter.getPathData().get(position).getDataPath());
                recordPopupWindow.show(LayoutInflater.from(mContext).inflate(R.layout.activity_base, null));
                break;
            case AccessoryAdapter.TYPE_PHOTO:
                ArrayList<String> paths = new ArrayList<>();
                paths.add(pathAdapter.getPathData().get(position).getDataPath());
                mFragment.startForResult(ImagePagerFragment.newInstance(position, paths), 5110);
                break;
            case AccessoryAdapter.TYPE_VIDEO:
//                mFragment.start(PlayVideoFragment.newInstance(VideoShowType.PLAY_TYPE_INSTALL_VIDEO, pathAdapter.getPathData().get(position).getDataPath()));
                break;
        }
        dismiss();
    }

    private PathTypeBean upLoadBean;

    @Override
    public void upLoadFile(final PathTypeBean bean) {
        upLoadBean = bean;
        ApiRequest request;
        String saveAlbumPath;
        if (pathAdapter.TYPE_PHOTO.equals(bean.getType())) {
            request = new ApiRequest<>(NetBean.actionPostUploadImg, ResultUploadImgBean.class);
            saveAlbumPath = Luban.get(mContext).load(new File(bean.getDataPath())).launch().getAbsolutePath();
        } else {
            request = new ApiRequest<>(NetBean.actionPostUploadFlie, ResultUploadFileBean.class);
            saveAlbumPath = bean.getDataPath();
        }
        new NetUtils(mContext).request(new NetUtils.NetRequestCallBack() {
            @Override
            public void success(String action, BaseBean baseBean, Object tag) {
                if (baseBean.isSuccessful()) {
                    String loadUrl;
                    if (pathAdapter.TYPE_RECORD.equals(upLoadBean.getType())) {
                        loadUrl = ((ResultUploadFileBean) (baseBean)).getData().getUrl();
                        onAdditionalListener.setVoiceUrl(loadUrl);
                    } else if (pathAdapter.TYPE_VIDEO.equals(upLoadBean.getType())) {
                        loadUrl = ((ResultUploadFileBean) (baseBean)).getData().getUrl();
                        onAdditionalListener.setVideoUrl(loadUrl);
                    } else {
                        loadUrl = ((ResultUploadImgBean) (baseBean)).getData().getImgUrl();
                        onAdditionalListener.addImageUrl(loadUrl);
                    }
                    PathTypeBean typeBean = new PathTypeBean(upLoadBean.getType(), upLoadBean.getDataPath(), loadUrl);
                    Collections.replaceAll(pathAdapter.getPathData(), upLoadBean, typeBean);
                }
            }

            @Override
            public void error(String action, Throwable e, Object tag) {

            }
        }, false, request.setRequestType(ApiRequest.REQUEST_TYPE_POST)
                .setApiType(ApiRequest.API_TYPE_FILE_OPERATION)
                .setRequestBody(saveAlbumPath)
                .uploadFile());

    }


    @Override
    public void onItemLongClick(final int position, View view, RecyclerView.ViewHolder vh) {
        final com.zxycloud.common.widget.AlertDialog builder = new com.zxycloud.common.widget.AlertDialog(mContext).builder();
        builder.setTitle(R.string.long_click_delete).setMsg(R.string.hint_delete);
        builder.setNegativeButton(R.string.dialog_no, null).setPositiveButton(R.string.dialog_yes, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (pathAdapter.getPathData() != null && pathAdapter.getPathData().size() > position) {
                    String type = pathAdapter.getPathData().get(position).getType();
                    String url = pathAdapter.getPathData().get(position).getLoadUrl();
                    new NetUtils(mContext).request(null, false, new ApiRequest(NetBean.actionPostDeleteFlie, BaseBean.class)
                            .setRequestType(ApiRequest.REQUEST_TYPE_POST)
                            .setApiType(ApiRequest.API_TYPE_FILE_OPERATION)
                            .setRequestParams("url", url));
                    pathAdapter.deleteData(position);
                    if (pathAdapter.TYPE_RECORD.equals(type))
                        onAdditionalListener.setVoiceUrl(null);
                    else if (pathAdapter.TYPE_VIDEO.equals(type))
                        onAdditionalListener.setVideoUrl(null);
                    else
                        onAdditionalListener.removeImageUrl(url);
                    mView.findViewById(R.id.tv_add_img).setVisibility(pathAdapter.getItemCount() >= 9 ? View.GONE : View.VISIBLE);
                }
            }
        }).show();
    }

    public void setAdditionalListener(OnAdditionalListener onAdditionalListener) {
        this.onAdditionalListener = onAdditionalListener;
    }

    OnAdditionalListener onAdditionalListener;

    public interface OnAdditionalListener {
        void setInformation(String message);

        void setVoiceUrl(String voiceUrl);

        void setVideoUrl(String videoUrl);

        void addImageUrl(String imageUrl);

        void removeImageUrl(String imageUrl);
    }

}
