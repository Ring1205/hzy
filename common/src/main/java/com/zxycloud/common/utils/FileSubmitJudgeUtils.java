package com.zxycloud.common.utils;

import android.content.Context;
import android.support.annotation.IntDef;
import android.text.TextUtils;
import android.util.SparseArray;

import com.zxycloud.common.R;
import com.zxycloud.common.utils.CommonUtils;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FileSubmitJudgeUtils {
    public static final int UPLOAD_TYPE_IMG = 0x10;
    public static final int UPLOAD_TYPE_VOICE = 0x11;
    public static final int UPLOAD_TYPE_VIDEO = 0x12;

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({UPLOAD_TYPE_IMG
            , UPLOAD_TYPE_VOICE
            , UPLOAD_TYPE_VIDEO})
    @interface UploadType {
    }

    /**
     * 处于上传中的文件数
     * imgNum   图片
     * voiceNum 语音
     * videoNum 视频
     */
    private int imgNum = 0;
    private int voiceNum = 0;
    private int videoNum = 0;

    private Map<String, String> imgUrlMap = new HashMap<>();
    /**
     * Map无需，所以这里使用List存储
     */
    private List<String> imgUrlList = new ArrayList<>();
    private Map<String, String> videoUrlMap = new HashMap<>();
    private Map<String, String> voiceUrlMap = new HashMap<>();

    private OnSubmitStateChangeListener listener;
    private Context mContext;

    public FileSubmitJudgeUtils(Context mContext, OnSubmitStateChangeListener listener) {
        this.mContext = mContext;
        this.listener = listener;
    }

    public synchronized boolean isCanSubmit() {
        return getImgNum() + voiceNum + videoNum == 0;
    }

    public synchronized void uploadFile(@UploadType final int uploadType, final String originalUrl, final String uploadUrl) {
        long uploadTime = System.currentTimeMillis();
        CommonUtils.log().i(uploadTime + "FileSubmitJudgeUtils#uploadFile ******  originalUrl = " + originalUrl + " uploadUrl = " + uploadUrl);
        boolean isFileUploaded = !TextUtils.isEmpty(uploadUrl);
        switch (uploadType) {
            case UPLOAD_TYPE_IMG:
                if (isFileUploaded && !imgUrlMap.containsKey(originalUrl)) {
                    break;
                }
                if (isFileUploaded)
                    imgUrlList.add(uploadUrl);
                else
                    imgUrlList.remove(uploadUrl);
                imgUrlMap.put(originalUrl, uploadUrl);

                imgNum = getImgNum();
                break;

            case UPLOAD_TYPE_VOICE:
                voiceNum = isFileUploaded ? 0 : 1;
                if (voiceUrlMap.size() > 0) {
                    voiceUrlMap.clear();
                }
                voiceUrlMap.put(originalUrl, uploadUrl);
                break;

            case UPLOAD_TYPE_VIDEO:
                videoNum = isFileUploaded ? 0 : 1;
                if (videoUrlMap.size() > 0) {
                    videoUrlMap.clear();
                }
                videoUrlMap.put(originalUrl, uploadUrl);
                break;
        }
        CommonUtils.log().i(uploadTime + "FileSubmitJudgeUtils#uploadFile ********   imgNum = " + getImgNum() + " voiceNum = " + voiceNum + " videoNum = " + videoNum);
        CommonUtils.log().i(uploadTime + "FileSubmitJudgeUtils#uploadFile ********   imgUrlMap = " + getMapString(imgUrlMap));
        CommonUtils.log().i(uploadTime + "FileSubmitJudgeUtils#uploadFile ********   voiceUrlMap = " + getMapString(voiceUrlMap));
        CommonUtils.log().i(uploadTime + "FileSubmitJudgeUtils#uploadFile ********   videoUrlMap = " + getMapString(videoUrlMap));
        if (null != listener) {
            listener.onSubmitStateChanged(fileUploadState());
        }
    }

    private synchronized String getMapString(Map map) {
        try {
            return map.toString();
        } catch (ConcurrentModificationException e) {
            e.printStackTrace();
            return "";
        }
    }

    private synchronized int getImgNum() {
        return imgUrlMap.size() - imgUrlList.size();
    }

    public synchronized String getItem(@UploadType final int uploadType, final String originalUrl) {
        switch (uploadType) {
            case UPLOAD_TYPE_IMG:
                return imgUrlMap.get(originalUrl);

            case UPLOAD_TYPE_VOICE:
                return voiceUrlMap.get(originalUrl);

            case UPLOAD_TYPE_VIDEO:
                return videoUrlMap.get(originalUrl);
        }
        return null;
    }

    public synchronized void removeItem(@UploadType final int uploadType, final String originalUrl) {
        long removeTime = System.currentTimeMillis();
        CommonUtils.log().i(removeTime + "FileSubmitJudgeUtils#removeItem  ********   originalUrl = " + originalUrl);
        switch (uploadType) {
            case UPLOAD_TYPE_IMG:
                imgUrlList.remove(imgUrlMap.get(originalUrl));
                imgUrlMap.remove(originalUrl);
                imgNum = getImgNum();
                break;

            case UPLOAD_TYPE_VOICE:
                voiceUrlMap.clear();
                voiceNum = 0;
                break;

            case UPLOAD_TYPE_VIDEO:
                videoUrlMap.clear();
                videoNum = 0;
                break;
        }
        if (null != listener) {
            listener.onSubmitStateChanged(fileUploadState());
        }

        CommonUtils.log().i(removeTime + "FileSubmitJudgeUtils#removeItem  ********   imgNum = " + imgNum + " voiceNum = " + voiceNum + " videoNum = " + videoNum);
        CommonUtils.log().i(removeTime + "FileSubmitJudgeUtils#removeItem  ********   imgUrlMap = " + imgUrlMap.toString());
        CommonUtils.log().i(removeTime + "FileSubmitJudgeUtils#removeItem  ********   voiceUrlMap = " + voiceUrlMap.toString());
        CommonUtils.log().i(removeTime + "FileSubmitJudgeUtils#removeItem  ********   videoUrlMap = " + videoUrlMap.toString());
    }

    public synchronized SparseArray<List<String>> getUploadUrls() {
        SparseArray<List<String>> uploadUrls = new SparseArray<>();
        uploadUrls.put(UPLOAD_TYPE_IMG, imgUrlList);

        List<String> voiceUrls = new ArrayList<>();
        for (String voiceUrl : voiceUrlMap.values()) {
            if (!TextUtils.isEmpty(voiceUrl)) {
                voiceUrls.add(voiceUrl);
            }
        }
        uploadUrls.put(UPLOAD_TYPE_VOICE, voiceUrls);

        List<String> videoUrls = new ArrayList<>();
        for (String videoUrl : videoUrlMap.values()) {
            if (!TextUtils.isEmpty(videoUrl)) {
                videoUrls.add(videoUrl);
            }
        }
        uploadUrls.put(UPLOAD_TYPE_VIDEO, videoUrls);

        return uploadUrls;
    }

    public synchronized String fileUploadState() {
        return CommonUtils.string().getFormatString(mContext, R.string.string_file_submit_judge_string_show, imgNum, videoNum, voiceNum);
    }

    public interface OnSubmitStateChangeListener {
        void onSubmitStateChanged(String stateString);
    }
}
