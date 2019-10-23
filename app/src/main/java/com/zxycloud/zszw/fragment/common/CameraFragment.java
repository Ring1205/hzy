package com.zxycloud.zszw.fragment.common;

import android.graphics.Bitmap;
import android.os.Bundle;

import com.zxycloud.zszw.R;
import com.zxycloud.zszw.base.BaseBackFragment;
import com.zxycloud.common.utils.FileUtils;
import com.zxycloud.common.widget.cameralibrary.JCameraView;
import com.zxycloud.common.widget.cameralibrary.listener.ClickListener;
import com.zxycloud.common.widget.cameralibrary.listener.ErrorListener;
import com.zxycloud.common.widget.cameralibrary.listener.JCameraListener;
import com.zxycloud.common.widget.cameralibrary.util.FileUtil;

public class CameraFragment extends BaseBackFragment {
    public static final String PATH_VIDEO = "video_path";//视频路径
    public static final String VIDEO_CAPTURE = "video_capture";//视频路径
    private JCameraView jCameraView;

    public static CameraFragment newInstance() {
        return new CameraFragment();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.camera_record_video;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        setToolbarTitle(R.string.record_video).initToolbarNav();
        jCameraView = findViewById(R.id.jcameraview);
        initCamera();
    }

    private void initCamera() {
        //设置视频保存路径
        jCameraView.setVideoFileName(FileUtils.getNewFileDir(FileUtils.VIDEO_RECORD));
        jCameraView.setFeatures(JCameraView.BUTTON_STATE_ONLY_RECORDER);
        jCameraView.setTip(R.string.long_press_camera);
        jCameraView.setMediaQuality(JCameraView.MEDIA_QUALITY_POOR);
        jCameraView.setErrorLisenter(new ErrorListener() {
            @Override
            public void onError() {
                //错误监听
                setFragmentResult(RESULT_CANCELED, new Bundle());
            }

            @Override
            public void AudioPermissionError() {
            }
        });
        //JCameraView监听
        jCameraView.setJCameraLisenter(new JCameraListener() {
            @Override
            public void captureSuccess(Bitmap bitmap) {
                //获取图片bitmap
            }

            @Override
            public void recordSuccess(String url, Bitmap firstFrame) {
                //获取视频路径
                Bundle intent = new Bundle();
                String path = FileUtil.saveBitmap("JCamera", firstFrame);
                intent.putString(PATH_VIDEO, url);
                intent.putString(VIDEO_CAPTURE, path);
                setFragmentResult(RESULT_OK, intent);
                finish();
            }
        });

        jCameraView.setLeftClickListener(new ClickListener() {
            @Override
            public void onClick() {
                finish();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        jCameraView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        jCameraView.onPause();
    }
}
