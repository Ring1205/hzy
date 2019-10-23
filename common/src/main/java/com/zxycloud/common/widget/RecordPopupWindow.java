package com.zxycloud.common.widget;

import android.content.Context;
import android.graphics.ColorFilter;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.zxycloud.common.R;
import com.zxycloud.common.utils.CommonUtils;
import com.zxycloud.common.utils.FileUtils;
import com.zxycloud.common.utils.TimerUtils;
import com.zxycloud.common.widget.ASRDigitalDialog.ColorFilterGenerator;
import com.zxycloud.common.widget.ASRDigitalDialog.MRecorder;
import com.zxycloud.common.widget.ASRDigitalDialog.SDKAnimationView;
import com.zxycloud.common.widget.ASRDigitalDialog.SDKProgressBar;
import com.zxycloud.common.widget.ASRDigitalDialog.UPlayer;

public class RecordPopupWindow extends PopupWindow {
    private View mView; // PopupWindow 菜单布局
    private Context mContext; // 上下文参数
    private SDKAnimationView voicewaveView;
    private SDKProgressBar progress;
    private LinearLayout btnLayout;
    private MRecorder recorder;
    private UPlayer player;
    String path;
    TextView tipsText, speakComplete, cancelText, retryText;

    public RecordPopupWindow(Context context, String path) {
        super(context);
        this.mContext = context;

        Init();

        if (path != null) {
            this.path = path;
            speakComplete.setVisibility(View.GONE);
            progress.setVisibility(View.GONE);
            btnLayout.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 设置布局以及点击事件
     */
    private void Init() {
        // 音频录制, 开始录音, 请说话, 说完了, 保存中, 重试/取消
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mView = inflater.inflate(R.layout.record_pop_item, null);
        tipsText = mView.findViewById(R.id.tips_text);//音频录制, 请说话,保存中
        speakComplete = mView.findViewById(R.id.speak_complete);//说完了, 开始录音
        cancelText = mView.findViewById(R.id.cancel_text_btn);//取消
        retryText = mView.findViewById(R.id.retry_text_btn);//重试
        progress = mView.findViewById(R.id.progress);
        btnLayout = mView.findViewById(R.id.ll_btn);
        voicewaveView = mView.findViewById(R.id.voicewave_view);

        ColorMatrix cm = new ColorMatrix();
        ColorFilterGenerator.adjustColor(cm, 0, 0, 0, 0);
        ColorFilter filter = new ColorMatrixColorFilter(cm);
        progress.setTheme(0);
        progress.setHsvFilter(filter);
        voicewaveView.setHsvFilter(filter);
        voicewaveView.startInitializingAnimation();
        voicewaveView.startPreparingAnimation();

        retryText.setOnClickListener(playOnClickListener);
        speakComplete.setOnClickListener(startOnClickListener);
        cancelText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
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
        // 单击弹出窗以外处 关闭弹出窗
        mView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int height = mView.findViewById(R.id.ll_pop).getTop();
                int witch = mView.findViewById(R.id.ll_pop).getHeight();
                int y = (int) event.getY();
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (y < height || (height + witch) < y) {
                        dismiss();
                    }
                }
                return true;
            }
        });

        path = FileUtils.getNewFile(FileUtils.RAW_RECORD).getAbsolutePath();
        recorder = new MRecorder(path);
    }

    @Override
    public void dismiss() {
        if (player != null)
            player.stop();
        if (recorder != null)
            recorder.stop();
        super.dismiss();
    }

    private View.OnClickListener startOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            onRecognitionStart();
        }
    };

    private View.OnClickListener finishOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            stopRecordFinish();
        }
    };

    private View.OnClickListener playOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (player == null) {
                player = new UPlayer(path, new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        voicewaveView.setVisibility(View.GONE);
                        voicewaveView.startRecognizingAnimation();
                        player.stop();
                        player = null;
                        retryText.setText(R.string.replay);
                    }
                });
                player.start();
                voicewaveView.setVisibility(View.VISIBLE);
                voicewaveView.startRecordingAnimation();
            }
        }
    };

    /**
     * 启动录音
     */
    public void onRecognitionStart() {
        recorder.start();
        voicewaveView.setVisibility(View.VISIBLE);
        voicewaveView.startRecordingAnimation();

        speakComplete.setText(R.string.done);
        speakComplete.setOnClickListener(finishOnClickListener);

        speakComplete.setVisibility(View.VISIBLE);
        btnLayout.setVisibility(View.GONE);

    }

    /**
     * 结束录音
     */
    public void stopRecordFinish() {
        if (recorder.stop()) {
            retryText.setText(R.string.keep);
            retryText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CommonUtils.toast(mContext, R.string.transcoding);
                }
            });
            setProgressTimer();
        } else {
            retryText.setText(R.string.remake);
            retryText.setOnClickListener(startOnClickListener);
            CommonUtils.toast(mContext, R.string.short_recording);
        }
        voicewaveView.setVisibility(View.GONE);
        voicewaveView.startRecognizingAnimation();
        voicewaveView.resetAnimation();

        btnLayout.setVisibility(View.VISIBLE);
        speakComplete.setVisibility(View.GONE);
    }

    public void show(View view) {
        showAtLocation(view, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
    }

    private void setProgressTimer() {
        new TimerUtils(1000, 100, new TimerUtils.OnBaseTimerCallBack() {
            @Override
            public void onTick(Object tag, long millisUntilFinished) {
                progress.setProgress((int) (1000 / millisUntilFinished));
            }

            @Override
            public void onFinish(Object tag) {
                progress.setVisibility(View.GONE);
                retryText.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        filePathListener.getFilePath(path.replace(".raw", ".mp3"));
                        dismiss();
                    }
                });
            }
        }).start();
    }

    private FilePathListener filePathListener;

    public void getFilePath(FilePathListener filePathListener) {
        this.filePathListener = filePathListener;
    }

    public interface FilePathListener {
        void getFilePath(String path);
    }
}
