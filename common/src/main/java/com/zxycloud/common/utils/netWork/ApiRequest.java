package com.zxycloud.common.utils.netWork;

import android.support.annotation.IntDef;

import com.zxycloud.common.base.BaseBean;
import com.zxycloud.common.utils.CommonUtils;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.HashMap;
import java.util.Map;

/**
 * @author leiming
 * @date 2018/12/20.
 */
public class ApiRequest<T extends BaseBean> {
    /**
     * 通用业务
     */
    public static final int API_TYPE_NORMAL = 0x096;
    /**
     * 文件操作
     */
    public static final int API_TYPE_FILE_OPERATION = 0x095;
    /**
     * 统计
     */
    public static final int API_TYPE_STATISTICS = 0x094;
    /**
     * 登录设置
     */
    public static final int API_TYPE_LOGIN_SETTING = 0x093;
    /**
     * 登录设置
     */
    public static final int API_TYPE_PATROL = 0x092;

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({API_TYPE_NORMAL, API_TYPE_FILE_OPERATION, API_TYPE_STATISTICS, API_TYPE_LOGIN_SETTING, API_TYPE_PATROL})
    public @interface NetApiType {
    }

    public static final int REQUEST_TYPE_GET = 0x079;
    public static final int REQUEST_TYPE_POST = 0x078;

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({REQUEST_TYPE_GET, REQUEST_TYPE_POST})
    public @interface NetRequestType {
    }

    public static final int SPECIAL_GET_BITMAP = 0x069;
    public static final int SPECIAL_FILE_UPLOAD = 0x068;

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({SPECIAL_GET_BITMAP})
    public @interface NetSpecialRequestType {
    }

    /**
     * 网络请求尾址
     */
    private String action;

    /**
     * 网络请求API类型
     * API_TYPE_NORMAL              业务请求
     * API_TYPE_FILE_OPERATION      文件操作
     * API_TYPE_STATISTICS          统计
     * API_TYPE_LOGIN_SETTING       登录设置
     */
    private int apiType = API_TYPE_NORMAL;

    /**
     * 网络请求类型
     * GET
     * POST
     */
    private int requestType = 0;

    /**
     * 需要特殊处理的类型
     * SPECIAL_GET_BITMAP       返回结果获取bitmap
     * SPECIAL_FILE_UPLOAD      需要上传文件
     */
    private int specialTreatment = 0;

    /**
     * 标签，用于同一个接口多环境调用时区分，或特殊值需在返回时使用时传参
     */
    private Object tag;

    /**
     * 键值对请求参数
     */
    private Map<String, Object> requestParams;

    /**
     * post请求体
     */
    private Object requestBody;

    /**
     * 返回结果的类，用于Gson装换
     */
    private Class<T> resultClazz;

    /**
     * 构造方法
     *
     * @param action      {@link ApiRequest#action}
     * @param resultClazz {@link ApiRequest#resultClazz}
     */
    public ApiRequest(String action, Class<T> resultClazz) {
        this.action = action;
        this.resultClazz = resultClazz;
    }

    /**
     * 构造方法
     *
     * @param action     {@link ApiRequest#action}
     * @param resultType {@link ApiRequest#specialTreatment}
     */
    public ApiRequest(String action, @NetSpecialRequestType int resultType) {
        this.action = action;
        specialTreatment = resultType;
    }

    /**
     * 设置请求的API类型，将影响调用的路径
     *
     * @param apiType {@link ApiRequest#apiType}
     * @return 当前类
     */
    public ApiRequest<T> setApiType(@NetApiType int apiType) {
        this.apiType = apiType;
        return this;
    }

    /**
     * @param requestType {@link ApiRequest#requestType}
     * @return 当前类
     */
    public ApiRequest<T> setRequestType(@NetRequestType int requestType) {
        if (this.requestType != 0) {
            throw new IllegalStateException("Request type can't repeat settings");
        }
        this.requestType = requestType;
        return this;
    }


    /**
     * 设置请求参数
     *
     * @param key   请求键
     * @param value 请求值
     * @return 当前类
     */
    public ApiRequest<T> setRequestParams(String key, Object value) {
        if (CommonUtils.isEmpty(requestParams))
            requestParams = new HashMap<>();
        if (!CommonUtils.isEmpty(value))
            requestParams.put(key, value);
        return this;
    }

    /**
     * 防止多次添加body导致异常
     *
     * @param body post信息体
     */
    public ApiRequest<T> setRequestBody(Object body) {
        if (requestType == REQUEST_TYPE_GET) {
            throw new IllegalStateException("GET can't support body, must be POST");
        }
        requestBody = body;
        return this;
    }

    /**
     * 特殊请求类型：文件上传
     * {@link ApiRequest#specialTreatment}
     *
     * @return 当前类
     */
    public ApiRequest<T> uploadFile() {
        specialTreatment = SPECIAL_FILE_UPLOAD;
        return this;
    }

    /**
     * 上传网络请求标签
     *
     * @param tag {@link ApiRequest#tag}
     * @return
     */
    public ApiRequest<T> setTag(Object tag) {
        this.tag = tag;
        return this;
    }

    /**
     * 获取尾址
     *
     * @return {@link ApiRequest#action}
     */
    public String getAction() {
        return action;
    }

    /**
     * 获取尾址
     *
     * @return {@link ApiRequest#action}
     */
    public int getApiType() {
        return apiType == 0 ? API_TYPE_NORMAL : apiType;
    }

    /**
     * 获取尾址
     *
     * @return {@link ApiRequest#action}
     */
    public int getRequestType() {
        return requestType == 0 ? REQUEST_TYPE_GET : requestType;
    }

    /**
     * 获取标签
     *
     * @return {@link ApiRequest#tag}
     */
    public Object getTag() {
        return tag;
    }

    /**
     * 获取请求键值对
     *
     * @return {@link ApiRequest#requestParams}
     */
    public Map<String, Object> getRequestParams() {
        return requestParams;
    }

    /**
     * 获取根据网络请求键获取值
     *
     * @return {@link ApiRequest#requestParams}
     */
    public Object getRequestParams(String key) {
        return requestParams.get(key);
    }

    /**
     * 获取返回结果的class
     *
     * @return {@link ApiRequest#resultClazz}
     */
    public Class<T> getResultClazz() {
        return resultClazz;
    }

    /**
     * 获取POST请求体
     *
     * @return {@link ApiRequest#requestBody}
     */
    public Object getRequestBody() {
        return requestBody;
    }

    /**
     * 获取特殊请求体
     *
     * @return {@link ApiRequest#specialTreatment}
     */
    public int getSpecialTreatment() {
        return specialTreatment;
    }

    @Override
    public String toString() {
        return "ApiRequest{" +
                "action='" + action + '\'' +
                ", apiType=" + apiType +
                ", requestType=" + requestType +
                ", specialTreatment=" + specialTreatment +
                ", tag=" + tag +
                ", requestParams=" + requestParams +
                ", requestBody=" + requestBody +
                ", resultClazz=" + resultClazz +
                '}';
    }
}
