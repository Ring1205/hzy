package com.zxycloud.common.utils.netWork;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.sarlmoclen.router.SRouter;
import com.sarlmoclen.router.SRouterRequest;
import com.sarlmoclen.router.forMonitor.LogoutActionName;
import com.zxycloud.common.R;
import com.zxycloud.common.base.BaseBean;
import com.zxycloud.common.utils.CommonUtils;
import com.zxycloud.common.utils.MediaFileJudgeUtils;
import com.zxycloud.common.widget.BswProgressDialog;

import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.ConnectException;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Interceptor;
import okhttp3.JavaNetCookieJar;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.HttpException;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class NetUtils {
    private final NetUtils netUtils;

    /**
     * 上下文
     */
    private final Context mContext;
    /**
     * 网络请求结果回调
     */
    private NetRequestCallBack netRequestCallBack;
    /**
     * header
     */
    private Map<String, Object> headerParams;
    /**
     * 加载提示框
     */
    private BswProgressDialog bswProgressDialog;

    /**
     * header cookie
     */
    private static InDiskCookieStore cookieStore;

    public static NetUtils getNewInstance(Context mContext) {
        if (null == mContext)
            return null;
        else
            return new NetUtils(mContext);
    }

    public NetUtils(Context mContext) {
        this.mContext = mContext;
        this.netUtils = this;
        initHeader();
        if (null == cookieStore) {
            cookieStore = new InDiskCookieStore(mContext);
        }
        bswProgressDialog = new BswProgressDialog(mContext, R.style.progress_dialog_loading, R.string.is_loading);
    }

    /**
     * 初始化请求头，具体情况根据需求设置
     */
    private void initHeader() {
        if (headerParams == null) {
            headerParams = new HashMap<>();
            headerParams.put("Content-Type", "application/json");
            // 火知眼助手的APP编码是2
            headerParams.put("appCode", 2);
            headerParams.put("language", Locale.getDefault().getLanguage());
        }
    }

    /**
     * 更新header语言环境
     */
    private void updateHeaderLanguage() {
        if (null == headerParams) {
            initHeader();
        } else {
            headerParams.put("language", Locale.getDefault().getLanguage());
        }
    }
    private OkHttpClient.Builder okHttpClient;
    private Retrofit.Builder retrofit;

    /**
     * 初始化数据
     *
     * @param action    当前请求的尾址
     * @param inputType 网络请求类型
     */
    private Retrofit initBaseData(final String action, int inputType) {
        updateHeaderLanguage();
        // https信任管理
        TrustManager[] trustManager = new TrustManager[]{
                new X509TrustManager() {

                    @SuppressLint("TrustAllX509TrustManager")
                    @Override
                    public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) throws java.security.cert.CertificateException {

                    }

                    @SuppressLint("TrustAllX509TrustManager")
                    @Override
                    public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType){

                    }

                    @Override
                    public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                        return new java.security.cert.X509Certificate[0];
                    }
                }
        };

        if (okHttpClient == null)
            okHttpClient = new OkHttpClient.Builder();
        if (inputType != ApiRequest.API_TYPE_FILE_OPERATION) {
            // 请求超时
            okHttpClient.connectTimeout(20, TimeUnit.SECONDS);
            okHttpClient.readTimeout(20, TimeUnit.SECONDS);
        } else {
            okHttpClient.connectTimeout(60, TimeUnit.SECONDS);
            okHttpClient.readTimeout(60, TimeUnit.SECONDS);
        }
        okHttpClient.retryOnConnectionFailure(false);
        // 请求参数获取
        okHttpClient.addInterceptor(new Interceptor() {
            @Override
            public okhttp3.Response intercept(@android.support.annotation.NonNull Chain chain) throws IOException {
                Request request = chain.request();
                okhttp3.Response proceed = chain.proceed(request);
                CommonUtils.log().i("request", "request header : ".concat(request.headers().toString())
                        .concat("\nrequest content : ".concat(request.toString()))
                        .concat("\nproceed header : ".concat(proceed.headers().toString())));
                return proceed;
            }
        });
        CookieManager cookieManager = new CookieManager(cookieStore, CookiePolicy.ACCEPT_ORIGINAL_SERVER);
        okHttpClient.cookieJar(new JavaNetCookieJar(cookieManager));

        try {
            // https信任
            SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustManager, new SecureRandom());
            SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();
            //noinspection deprecation
            okHttpClient.sslSocketFactory(sslSocketFactory);
            okHttpClient.hostnameVerifier(new HostnameVerifier() {
                @SuppressLint("BadHostnameVerifier")
                @Override
                public boolean verify(String hostname, SSLSession session) {
                    // 全部信任
                    return true;
                }
            });
        } catch (NoSuchAlgorithmException | KeyManagementException e) {
            e.printStackTrace();
        }

        // 构建Builder，请求结果RxJava接收，使用GSON转化为Bean，
        if (retrofit == null)
            retrofit = new Retrofit.Builder()
                    .client(okHttpClient.build())
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create());


        if (0 == inputType)
            inputType = ApiRequest.API_TYPE_NORMAL;

        retrofit.baseUrl(NetBean.getUrl(mContext, inputType, action));
        return retrofit.build();
    }

    /**
     * 网络请求
     *
     * @param netRequestCallBack 网络请求回调
     * @param showDialog         是否显示弹窗
     * @param apiRequests        请求类
     */
    public void request(NetRequestCallBack netRequestCallBack, final boolean showDialog, final ApiRequest... apiRequests) {
        if (CommonUtils.judgeListNull(apiRequests) == 0) {
            throw new IllegalArgumentException("apiRequests can't be null");
        }
        if (showDialog) {
            showLoadDialog();
        }
        tokenJudge(netRequestCallBack, showDialog, apiRequests);
    }

    /**
     * token存在性判断，若有token则直接请求，若没有token则先获取token
     *
     * @param netRequestCallBack 网络请求回调
     * @param showDialog         是否显示弹窗
     * @param apiRequests        请求类
     */
    private void tokenJudge(final NetRequestCallBack netRequestCallBack, final boolean showDialog, final ApiRequest... apiRequests) {
        CommonUtils.threadPoolExecute(new Runnable() {
            @SuppressWarnings("unchecked")
            @Override
            public void run() {
                if (apiRequests[0].getAction().contains("http")) {
                    execute(netRequestCallBack, showDialog, apiRequests);
                    return;
                }

                if (!cookieStore.hasCookies(cookieStore.getHost(NetBean.getUrl(mContext, ApiRequest.API_TYPE_NORMAL, "")))) {
                    netUtils.netRequestCallBack = new NetRequestCallBack() {
                        @Override
                        public void success(String action, BaseBean baseBean, Object tag) {
                            if (action.equals(NetBean.actionGetToken) && baseBean.isSuccessful()) {
                                execute(netRequestCallBack, showDialog, apiRequests);
                            } else {
                                CommonUtils.toast(mContext, baseBean.getMessage());
                            }
                        }

                        @Override
                        public void error(String action, Throwable e, Object tag) {
                            CommonUtils.toast(mContext, R.string.error_toast_401);
                            netRequestCallBack.error(action, e, tag);
                        }
                    };

                    get(new ApiRequest(NetBean.actionGetToken, BaseBean.class), false);
                } else {
                    execute(netRequestCallBack, showDialog, apiRequests);
                }
            }
        });
    }

    /**
     * 执行网络请求
     *
     * @param netRequestCallBack 网络请求回调
     * @param showDialog         是否显示弹窗
     * @param apiRequests        请求类
     */
    private void execute(final NetRequestCallBack netRequestCallBack, final boolean showDialog, final ApiRequest... apiRequests) {
        this.netRequestCallBack = netRequestCallBack;

        for (ApiRequest apiRequest : apiRequests) {
            if (apiRequest != null)
                switch (apiRequest.getRequestType()) {
                    case ApiRequest.REQUEST_TYPE_GET:
                        if (apiRequest.getApiType() == ApiRequest.API_TYPE_LOGIN_SETTING)
                            getSystemSetting(apiRequest, showDialog);
                        else
                            get(apiRequest, showDialog);
                        break;

                    case ApiRequest.REQUEST_TYPE_POST:
                        if (apiRequest.getSpecialTreatment() == ApiRequest.SPECIAL_FILE_UPLOAD)
                            postFile(apiRequest, showDialog);
                        else
                            post(apiRequest, showDialog);
                        break;
                }
        }
    }

    /**
     * Get请求
     *
     * @param apiRequest {@link ApiRequest}
     */
    private <T extends BaseBean> void get(ApiRequest<T> apiRequest, boolean showDialog) {
        String action = apiRequest.getAction();
        RetrofitService getService = initBaseData(action.substring(0, action.lastIndexOf("/") + 1), apiRequest.getApiType()).create(RetrofitService.class);
        Map<String, Object> params = apiRequest.getRequestParams();
        if (params == null) {
            params = new HashMap<>();
        }

        CommonUtils.log().i("zzz", "request====" + new JSONObject(params));

        if (null != headerParams) {
            headerParams.put("language", Locale.getDefault().getLanguage());
        }

        getService.getResult(action.substring(action.lastIndexOf("/") + 1), headerParams, params)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new MyObserver<>(apiRequest, showDialog));
        ;
    }

    /**
     * Get请求
     */
    private <T extends BaseBean> void getSystemSetting(ApiRequest<T> apiRequest, boolean showDialog) {
        RetrofitService getService = initBaseData(apiRequest.getAction(), apiRequest.getApiType()).create(RetrofitService.class);
        Map<String, Object> params = apiRequest.getRequestParams();
        if (params == null) {
            params = new HashMap<>();
        }

        CommonUtils.log().i("zzz", "request====" + new JSONObject(params));

        if (null != headerParams) {
            headerParams.put("language", Locale.getDefault().getLanguage());
        }

        getService.getResult(NetBean.actionAppConfig, headerParams, params)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new MyObserver<>(apiRequest, showDialog));
    }

    /**
     * Post请求
     *
     * @param apiRequest 请求参数
     * @param showDialog 是否展示请求加载框
     */
    private <T extends BaseBean> void post(ApiRequest<T> apiRequest, boolean showDialog) {

        Map<String, Object> fieldMap = apiRequest.getRequestParams();
        boolean isFieldMapUseful = CommonUtils.judgeListNull(fieldMap) > 0;
        String requestString;
        String action = apiRequest.getAction();
        String useAction = action.substring(action.lastIndexOf("/") + 1);
        Object o = apiRequest.getRequestBody();
        if (CommonUtils.notEmpty(o)) {
            requestString = new Gson().toJson(o);
            if (isFieldMapUseful) {
                for (Map.Entry<String, Object> entry : fieldMap.entrySet()) {
                    useAction = useAction.concat(useAction.contains("?") ? "&" : "?").concat(entry.getKey()).concat("=").concat(entry.getValue().toString());
                }
            }
        } else {
            requestString = String.valueOf(new JSONObject(isFieldMapUseful ? fieldMap : new HashMap<String, Object>()));
        }

        CommonUtils.log().i("request", "request====" + requestString);


        if (TextUtils.isEmpty(requestString) && CommonUtils.notEmpty(netRequestCallBack)) {
            netRequestCallBack.error(action, new Exception(CommonUtils.string().getString(mContext, R.string.data_abnormal)), apiRequest.getTag());
        }

        RetrofitService jsonService = initBaseData(action.substring(0, action.lastIndexOf("/") + 1), apiRequest.getApiType()).create(RetrofitService.class);

        RequestBody requestBody =
                RequestBody.create(MediaType.parse("application/json; charset=utf-8"),
                        requestString);

        if (null != headerParams) {
            headerParams.put("language", Locale.getDefault().getLanguage());
        }

        jsonService.postResult(useAction, headerParams, requestBody)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new MyObserver<>(apiRequest, showDialog));
    }

    /**
     * 上传文件
     *
     * @param <T>        上传数据类泛型
     * @param apiRequest 请求参数
     */
//    private <T extends BaseBean> void postFile(String action, Object files, final Class<T> clazz, Object tag) {
    private <T extends BaseBean> void postFile(ApiRequest<T> apiRequest, boolean showDialog) {
        try {
            Object files = apiRequest.getRequestBody();
            List<File> fileList = null;
            if (files instanceof List) {
                fileList = (List<File>) files;
            } else if (files instanceof File) {
                fileList = new ArrayList<>();
                fileList.add((File) files);
            } else if (files instanceof String) {
                fileList = new ArrayList<>();
                fileList.add(new File((String) files));
            }
            if (CommonUtils.judgeListNull(fileList) == 0) {
//                throw new IllegalArgumentException("You haven’t chosen file");
                return;
            }
            initHeader();
            //防止重置私有云后，不重新创建，导致异常
            String action = apiRequest.getAction();
            RetrofitService fileService = initBaseData(action.substring(0, action.lastIndexOf("/") + 1), ApiRequest.API_TYPE_FILE_OPERATION).create(RetrofitService.class);

            Map<String, Object> fileHeader = headerParams;
            if (fileHeader.containsKey("Content-Type")) {
                fileHeader.remove("Content-Type");
                fileHeader.put("language", Locale.getDefault().getLanguage());
            }
            MediaFileJudgeUtils.MediaFileType mediaFileType = MediaFileJudgeUtils.getFileType(fileList.get(0).getAbsolutePath());
            if (CommonUtils.isEmpty(mediaFileType)) {
                throw new IllegalArgumentException("File is wrong type");
            }
            List<MultipartBody.Part> partList = filesToMultipartBodyParts(fileList, mediaFileType.mimeType);

            fileService.fileResult(action.substring(action.lastIndexOf("/") + 1), fileHeader, partList)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new MyObserver<>(apiRequest, showDialog));
        } catch (ClassCastException e) {
            CommonUtils.log().e(getClass().getSimpleName(), e);
        }
    }

    private List<MultipartBody.Part> filesToMultipartBodyParts(List<File> files, String fileType) {
        CommonUtils.log().i(getClass().getSimpleName(), fileType);
        List<MultipartBody.Part> parts = new ArrayList<>(files.size());
        for (File file : files) {
            RequestBody requestBody = RequestBody.create(MediaType.parse(fileType), file);
            MultipartBody.Part part = MultipartBody.Part.createFormData("file", file.getName(), requestBody);
            parts.add(part);
        }
        return parts;
    }

    public void removeCookies() {
        cookieStore.removeAll();
    }

    private class MyObserver<T extends BaseBean> implements Observer<ResponseBody> {

        private ApiRequest<T> apiRequest;
        private Class<T> clazz;
        private String action;
        boolean showDialog;
        /**
         * 返回结果状态：0、正常Bean；1、Bitmap
         */
        private int resultStatus = 0;
        private Object tag;

        MyObserver(ApiRequest<T> apiRequest, boolean showDialog) {
            this.apiRequest = apiRequest;
            this.action = apiRequest.getAction();
            this.tag = apiRequest.getTag();
            if (apiRequest.getSpecialTreatment() == ApiRequest.SPECIAL_GET_BITMAP) {
                this.resultStatus = 1;
            } else {
                this.resultStatus = 0;
                clazz = apiRequest.getResultClazz();
            }
            this.showDialog = showDialog;
        }

        @Override
        public void onSubscribe(@NonNull Disposable d) {

        }

        @SuppressWarnings("unchecked")
        @Override
        public void onNext(@NonNull ResponseBody responseBody) {
            if (showDialog) {
                hideLoadDialog();
            }
            try {
                switch (resultStatus) {
                    case 0:
                        String responseString = responseBody.string();
                        CommonUtils.log().i("responseString ：", action + "********** responseString get  " + responseString);
                        if (CommonUtils.notEmpty(netRequestCallBack)) {
                            netRequestCallBack.success(action, (T) new Gson().fromJson(responseString, clazz), tag);
                        }
                        break;

                    case 1:
                        if (CommonUtils.notEmpty(netRequestCallBack)) {
                            netRequestCallBack.success(action, BitmapFactory.decodeStream(responseBody.byteStream()), tag);
                        }
                        CommonUtils.log().i("responseString ：", action + "********** 图片获取成功 ");
                        break;

                    default:
                        break;
                }
            } catch (Exception e) {
                CommonUtils.log().e(getClass().getSimpleName(), e);
                netRequestCallBack.error(action, e, tag);
            }
        }

        @Override
        public void onError(@NonNull Throwable e) {
            if (showDialog) {
                hideLoadDialog();
            }
            try {
                if (e instanceof HttpException) {
                    ResponseBody errorbody = ((HttpException) e).response().errorBody();
                    if (CommonUtils.notEmpty(errorbody)) {
                        CommonUtils.log().i("responseString ：", String.format("%s********** responseString get error %s content %s", action, e.toString(), TextUtils.isEmpty(errorbody.string()) ? "" : errorbody));
                    }
                } else {
                    CommonUtils.log().i("responseString ：", String.format("%s********** responseString get error %s", action, e.toString()));
                }
                if (e.toString().contains("401")) {
                    removeCookies();
                    if (!action.equals(NetBean.actionSignInByTokenId)) {
                        CommonUtils.toast(mContext, R.string.error_toast_401);
                    }
                    if (!CommonUtils.hasActivity("MainActivity")) {
                        return;
                    }
                    CommonUtils.closeActivity();
                    SRouter.getInstance().sendMessage(mContext, SRouterRequest.creat().action(LogoutActionName.name));
                } else {
                    if (action.equals(NetBean.actionSetPushInfo)) {
                        return;
                    }
                    if (!(e instanceof ConnectException)) {
//                        CommonUtils.toast(mContext, CommonUtils.string().getString(mContext, R.string.error_toast_other).concat(e.getMessage()));
                        CommonUtils.log().i(CommonUtils.string().getString(mContext, R.string.error_toast_other).concat(e.getMessage()).concat(apiRequest.toString()));
                    }
                }
            } catch (IOException | NullPointerException e1) {
                e1.printStackTrace();
            }
            if (CommonUtils.notEmpty(netRequestCallBack)) {
                netRequestCallBack.error(action, e, tag);
            }
        }

        @Override
        public void onComplete() {
            if (showDialog) {
                hideLoadDialog();
            }
        }
    }

    /**
     * 隐藏加载提示框
     */
    private void hideLoadDialog() {
        try {
            ((Activity) mContext).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (bswProgressDialog != null && bswProgressDialog.isShowing()) {
                        bswProgressDialog.dismiss();
                    }
                }
            });
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }

    /**
     * 显示加载提示框
     */
    private void showLoadDialog() {
        try {
            ((Activity) mContext).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        if (bswProgressDialog != null && !bswProgressDialog.isShowing()) {
                            bswProgressDialog.show();
                        }
                    } catch (Exception ex) {
                        StringWriter stackTrace = new StringWriter();
                        ex.printStackTrace(new PrintWriter(stackTrace));
                        CommonUtils.log().i("Exception", stackTrace.toString());
                    }
                }
            });
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }

    public boolean hasToken(String baseUrl) {
        return cookieStore.hasCookies(baseUrl);
    }

    public boolean hasToken() {
        return cookieStore.hasCookies(cookieStore.getHost(NetBean.getUrl(mContext, ApiRequest.API_TYPE_NORMAL, "")));
    }

    /**
     * 网络请求文本结果回调接口
     */
    public abstract static class NetRequestCallBack<TT extends BaseBean> {

        public void success(String action, Bitmap bitmap, Object tag) {

        }

        public void success(String action, File file, Object tag) {

        }

        public abstract void success(String action, TT baseBean, Object tag);

        /**
         * 访问失败回调抽象方法
         *
         * @param action 网络访问尾址
         * @param e      所返回的异常
         * @param tag    当接口复用时，用于区分请求的表识
         */
        public abstract void error(String action, Throwable e, Object tag);
    }
}
