package com.zxycloud.common.utils.netWork;

import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.HeaderMap;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.QueryMap;

public interface RetrofitService {
    @GET("{action}")
    Observable<ResponseBody> getResult(@Path("action") String action, @HeaderMap Map<String, Object> headerParams, @QueryMap Map<String, Object> params);

    @POST("{action}")
    Observable<ResponseBody> postResult(@Path("action") String action, @HeaderMap Map<String, Object> headerParams, @Body RequestBody requestBody);

    @Multipart
    @POST("{action}")
    // 上传文件的不传递参数的Service
    Observable<ResponseBody> fileResult(@Path("action") String action, @HeaderMap Map<String, Object> headerParams, @Part List<MultipartBody.Part> parts);
}
