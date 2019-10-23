package com.zxycloud.common.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;

import com.zxycloud.common.R;
import com.zxycloud.common.utils.db.DbUtils;
import com.zxycloud.common.utils.rxbus2.RxBus;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author leiming
 * @date 2018/12/19.
 */
public class CommonUtils {
    /**
     * 火警
     */
    public static final int STATE_CODE_FIRE = 1;
    /**
     * 预警
     */
    public static final int STATE_CODE_PREFIRE = 2;
    /**
     * 启动
     */
    public static final int STATE_CODE_STARTUP = 3;
    /**
     * 监管
     */
    public static final int STATE_CODE_SUPERVISION = 4;
    /**
     * 反馈
     */
    public static final int STATE_CODE_FEEDBACK = 5;
    /**
     * 故障
     */
    public static final int STATE_CODE_FAULT = 6;
    /**
     * 屏蔽
     */
    public static final int STATE_CODE_SHIELDING = 7;
    /**
     * 隐患
     */
    public static final int STATE_CODE_RISK = 8;
    /**
     * 事件
     */
    public static final int STATE_CODE_EVENT = 95;
    /**
     * 离线
     */
    public static final int STATE_CODE_OFFLINE = 96;
    /**
     * 正常
     */
    public static final int STATE_CODE_NORMAL = 99;

    /**
     * 极光相关参数
     */
    public static final String MESSAGE_RECEIVED_ACTION = "com.example.jpushdemo.MESSAGE_RECEIVED_ACTION";
    public static final String KEY_MESSAGE = "message";
    public static final String KEY_EXTRAS = "extras";

    /*---------------------------------------Activity存在性验证----------------------------------------*/
    private static List<String> openActivityNameList = new ArrayList<>();
    private static List<Activity> openActivityList = new ArrayList<>();

    /**
     * 是否存在Activity
     *
     * @param activityClass 被判断的Activity
     * @return 是否存在
     */
    public static boolean hasActivity(Class activityClass) {
        return openActivityNameList.contains(activityClass.getSimpleName());
    }

    /**
     * 是否存在Activity
     *
     * @param activitySimpleName 被判断的Activity名称
     * @return 是否存在
     */
    public static boolean hasActivity(String activitySimpleName) {
        return openActivityNameList.contains(activitySimpleName);
    }

    /**
     * 以开启新的Activity
     *
     * @param activity 被开启Activity
     */
    public static void addActivity(Activity activity) {
        openActivityNameList.add(activity.getClass().getSimpleName());
        openActivityList.add(activity);
    }

    /**
     * 关闭Activity
     *
     * @param activity 被关闭Activity
     */
    public static void removeActivity(Activity activity) {
        openActivityNameList.remove(activity.getClass().getSimpleName());
        openActivityList.add(activity);
    }

    /**
     * 关闭Activity
     */
    public static void closeActivity() {
        for (Activity activity : openActivityList) {
            activity.finish();
        }
    }

    /**
     * 关闭Activity
     */
    public static void closeActivity(Class activityClass) {
        if (! openActivityNameList.contains(activityClass.getSimpleName())) {
            return;
        }
        for (Activity activity : openActivityList) {
            if (activity.getClass().equals(activityClass)) {
                activity.finish();
            }
        }
    }

    /*---------------------------------------Fragment存在性验证----------------------------------------*/
    private static List<String> openFragmentNameList = new ArrayList<>();
    private static List<Fragment> openFragmentList = new ArrayList<>();

    /**
     * 是否存在Fragment
     *
     * @param fragmentClass 被判断的Fragment
     * @return 是否存在
     */
    public static boolean hasFragment(Class fragmentClass) {
        return openFragmentNameList.contains(fragmentClass.getSimpleName());
    }

    /**
     * 是否存在Fragment
     *
     * @param fragmentSimpleName 被判断的Fragment名称
     * @return 是否存在
     */
    public static boolean hasFragment(String fragmentSimpleName) {
        return openFragmentNameList.contains(fragmentSimpleName);
    }

    /**
     * 以开启新的Fragment
     *
     * @param fragment 被开启Fragment
     */
    public static void addFragment(Fragment fragment) {
        openFragmentNameList.add(fragment.getClass().getSimpleName());
        openFragmentList.add(fragment);
    }

    /**
     * 关闭Fragment
     *
     * @param fragment 被关闭Fragment
     */
    public static void removeFragment(Fragment fragment) {
        openFragmentNameList.remove(fragment.getClass().getSimpleName());
        openFragmentList.remove(fragment);
    }

//    /**
//     * 关闭Fragment
//     */
//    public static void closeFragment() {
//        for (Fragment fragment : openFragmentList) {
//            ((MyBaseFragment) fragment).finish();
//        }
//    }

    /**
     * Toast
     */
    @SuppressLint("StaticFieldLeak")
    private static ToastUtils toastUtils;

    /**
     * SharedPreference工具类
     */
    private static SPUtils spUtils;

    /**
     * 正则判断工具
     */
    private static RegularJudgeUtils regularJudgeUtils;

    /**
     * 文本格式化类
     */
    private static StringFormatUtils stringFormatUtils;

    /**
     * 日期格式化类
     */
    private static DateUtils dateFormatUtils;

    /**
     * RxBus
     */
    private static RxBus rxBus;
    /**
     * 日志打印
     */
    private static Logger logger;
    /**
     * glide图片工具
     */
    private static GlideUtils glideUtils;
    /**
     * 控件测量工具
     */
    private static MeasureUtil measureUtil;
    /**
     * 屏幕测量工具
     */
    private static ScreenUtil screenUtil;
    private static TimeUpUtils timeUpUtils;

    private static ExecutorService threadPoolExecutor;
    /*-------------------------------------------线程池--------------------------------------------*/

    /**
     * 启动线程池
     *
     * @param runnable 需要在子线程执行的程序
     */
    public static void threadPoolExecute(Runnable runnable) {
        if (isEmpty(threadPoolExecutor)) {
            threadPoolExecutor = Executors.newFixedThreadPool(3);
        }
        threadPoolExecutor.execute(runnable);
    }

    /*-------------------------------------------长度判断-------------------------------------------*/

    /**
     * 判断集合的长度
     *
     * @param list 所要判断的集合
     * @return 集合的大小，若为空也则返回0
     */
    public static int judgeListNull(List list) {
        if (list == null || list.size() == 0) {
            return 0;
        } else {
            return list.size();
        }
    }

    /**
     * 判断集合的长度
     *
     * @param map 所要判断的集合
     * @return 集合的大小，若为空也则返回0
     */
    public static int judgeListNull(Map map) {
        if (map == null || map.size() == 0) {
            return 0;
        } else {
            return map.size();
        }
    }

    /**
     * 判断集合的长度
     *
     * @param list 索要获取长度的集合
     * @return 该集合的长度
     */
    public static <T> int judgeListNull(T[] list) {
        if (list == null || list.length == 0) {
            return 0;
        } else {
            return list.length;
        }
    }

    /*-------------------------------------------空判断-------------------------------------------*/

    /**
     * 当前对象为空判断
     *
     * @param o 被判断对象
     * @return 是否为空
     */
    public static boolean isEmpty(Object o) {
        return null == o;
    }

    /**
     * 当前对象存在判断
     *
     * @param o 被判断对象
     * @return 是否存在
     */
    public static boolean notEmpty(Object o) {
        return null != o;
    }

    /*-------------------------------------------Activity判断-------------------------------------------*/

    /**
     * 跳转页面
     *
     * @param context        当前上下文
     * @param targetActivity 所跳转的目的Activity类
     */
    public static void jumpTo(Context context, Class<?> targetActivity) {
        jumpTo(context, targetActivity, null);
    }

    /**
     * 跳转页面
     *
     * @param context        当前上下文
     * @param targetActivity 所跳转的目的Activity类
     * @param bundle         跳转所携带的信息
     */
    public static void jumpTo(Context context, Class<?> targetActivity, Bundle bundle) {
        if (! timeUpUtils().isTimeUp(TimeUpUtils.TIME_UP_JUMP, System.currentTimeMillis())) {
            return;
        }
        Intent intent = new Intent(context, targetActivity);
        if (bundle != null) {
            intent.putExtra("bundle", bundle);
        }
        context.startActivity(intent);
    }

    /**
     * 跳转页面
     *
     * @param context        当前上下文
     * @param targetActivity 所跳转的目的Activity类
     */
    public static void receiverJumpTo(Context context, Class<?> targetActivity) {
        receiverJumpTo(context, targetActivity, null);
    }

    /**
     * 跳转页面
     *
     * @param context        当前上下文
     * @param targetActivity 所跳转的目的Activity类
     * @param bundle         跳转所携带的信息
     */
    public static void receiverJumpTo(Context context, Class<?> targetActivity, Bundle bundle) {
        if (! timeUpUtils().isTimeUp(TimeUpUtils.TIME_UP_JUMP, System.currentTimeMillis())) {
            return;
        }
        Intent intent = new Intent(context, targetActivity);
        if (bundle != null) {
            intent.putExtra("bundle", bundle);
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    /**
     * 跳转页面
     *
     * @param context        当前上下文
     * @param targetActivity 所跳转的Activity类
     * @param requestCode    请求码
     */
    public static void jumpTo(Context context, Class<?> targetActivity, int requestCode) {
        jumpTo(context, targetActivity, requestCode, null);
    }

    /**
     * 跳转页面
     *
     * @param context        当前上下文
     * @param targetActivity 所跳转的Activity类
     * @param bundle         跳转所携带的信息
     * @param requestCode    请求码
     */
    public static void jumpTo(Context context, Class<?> targetActivity, int requestCode, Bundle bundle) {
        if (! timeUpUtils().isTimeUp(TimeUpUtils.TIME_UP_JUMP, System.currentTimeMillis())) {
            return;
        }
        Intent intent = new Intent(context, targetActivity);
        if (bundle != null) {
            intent.putExtra("bundle", bundle);
        }
        ((Activity) context).startActivityForResult(intent, requestCode);
    }

    /*-------------------------------------------类名获取-------------------------------------------*/

    /**
     * 获取类名
     *
     * @param clz 需要获取名称的类
     * @return 类名字符串
     */
    public static String getName(Class<?> clz) {
        return clz.getClass().getSimpleName();
    }

    /*-------------------------------------------判断APP是否在前台运行-------------------------------------------*/

    public static boolean isRunningForeground(Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        ComponentName cn = am.getRunningTasks(1).get(0).topActivity;
        String currentPackageName = cn.getPackageName();
        return ! TextUtils.isEmpty(currentPackageName) && currentPackageName.equals(context.getPackageName());
    }
    /*-------------------------------------------状态获取-------------------------------------------*/

    private static List<CommonStateBean> commonList;

    public static List<CommonStateBean> getCommonList(Context context) {
        if (null == commonList || commonList.size() == 0) {
            commonList = new ArrayList<>();
            String[] stateResArray = context.getResources().getStringArray(R.array.common_state_array);
            int[] stateCodeArray = new int[]{
                    -1
                    , STATE_CODE_NORMAL
                    , STATE_CODE_FIRE
                    , STATE_CODE_PREFIRE
                    , STATE_CODE_OFFLINE
                    , STATE_CODE_FAULT
                    , STATE_CODE_STARTUP
                    , STATE_CODE_SUPERVISION
                    , STATE_CODE_FEEDBACK
                    , STATE_CODE_SHIELDING};
            for (int i = 0; i < stateResArray.length; i++) {
                commonList.add(new CommonStateBean(stateResArray[i], stateCodeArray[i]));
            }
        }
        return commonList;
    }
    /*-------------------------------------------工具类封装-------------------------------------------*/

    /**
     * toast打印
     *
     * @param mContext  上下文
     * @param messageId 打印文本的Id
     */
    public static void toast(Context mContext, @StringRes int messageId) {
        if (isEmpty(toastUtils)) {
            toastUtils = new ToastUtils(mContext);
        }
        toastUtils.toast(messageId);
    }

    /**
     * toast打印
     *
     * @param mContext 上下文
     * @param message  打印的文本
     */
    public static void toast(Context mContext, String message) {
        if (isEmpty(toastUtils)) {
            toastUtils = new ToastUtils(mContext);
        }
        toastUtils.toast(message);
    }

    /**
     * 获取MD5加密字符串
     *
     * @param string 待加密字符串
     * @return 加密后的字符串
     */
    public static String getMD5Str(String string) {
        return MD5.getMD5Str(string);
    }

    /**
     * 获取SharedPreference工具
     *
     * @param mContext 上下文
     * @return 工具类
     */
    public static SPUtils getSPUtils(Context mContext) {
        if (isEmpty(spUtils)) {
            spUtils = SPUtils.getInstance(mContext);
        }
        return spUtils;
    }

    /**
     * 正则判断工具类
     *
     * @return 正则判断工具类
     */
    public static RegularJudgeUtils judge() {
        if (isEmpty(regularJudgeUtils)) {
            regularJudgeUtils = new RegularJudgeUtils();
        }
        return regularJudgeUtils;
    }

    /**
     * 文本整理工具类
     *
     * @return 文本整理工具类
     */
    public static StringFormatUtils string() {
        if (isEmpty(stringFormatUtils)) {
            stringFormatUtils = new StringFormatUtils();
        }
        return stringFormatUtils;
    }

    /**
     * 时间工具类
     *
     * @return 时间工具类
     */
    public static DateUtils date() {
        if (isEmpty(dateFormatUtils)) {
            dateFormatUtils = new DateUtils();
        }
        return dateFormatUtils;
    }

    /**
     * 注册RxBus
     *
     * @param subscriber RxBus订阅者，一般为当前Activity或Fragment
     */
    public static void registerRxBus(Object subscriber) {
        if (isEmpty(rxBus)) {
            rxBus = RxBus.get();
        }
        rxBus.register(subscriber);
    }

    /**
     * 退出RxBus注册
     *
     * @param subscriber RxBus订阅者，一般为当前Activity或Fragment
     */
    public static void unRegisterRxBus(Object subscriber) {
        if (isEmpty(rxBus)) {
            rxBus = RxBus.get();
        }
        rxBus.unRegister(subscriber);
    }

    /**
     * 获取RxBus工具
     *
     * @return RxBus工具
     */
    public static RxBus getRxBus() {
        if (isEmpty(rxBus)) {
            rxBus = RxBus.get();
        }
        return rxBus;
    }

    /**
     * 获取日志打印工具
     *
     * @return 日志打印工具
     */
    public static Logger log() {
        if (isEmpty(logger)) {
            logger = new Logger();
        }
        return logger;
    }

    /**
     * 获取图片加载工具
     *
     * @return 图片加载工具
     */
    public static GlideUtils glide() {
        if (isEmpty(glideUtils)) {
            glideUtils = new GlideUtils();
        }
        return glideUtils;
    }

    /**
     * 获取控件测量工具
     *
     * @return 控件测量工具
     */
    public static MeasureUtil measureView() {
        if (isEmpty(measureUtil)) {
            measureUtil = new MeasureUtil();
        }
        return measureUtil;
    }

    /**
     * 获取屏幕测量工具
     *
     * @return 屏幕测量工具
     */
    public static ScreenUtil measureScreen() {
        if (isEmpty(screenUtil)) {
            screenUtil = new ScreenUtil();
        }
        return screenUtil;
    }

    /**
     * 数据库类获取
     * 避免有新表无法刷新出现，所以每次重新创建数据库工具
     *
     * @param mContext 上下文
     * @return 数据库
     */
    public static DbUtils getDbUtils(Context mContext) {
        return new DbUtils(mContext);
    }

    /**
     * 数据库类获取
     * 避免有新表无法刷新出现，所以每次重新创建数据库工具
     *
     * @return 数据库
     */
    public static TimeUpUtils timeUpUtils() {
        if (CommonUtils.isEmpty(timeUpUtils)) {
            timeUpUtils = new TimeUpUtils();
        }
        return timeUpUtils;
    }

    /**
     * 打开相机并返回照片路径
     * @param fragment
     * @param photo
     * @return
     */
    public static String openCamera(Fragment fragment, int photo) {
        String imgPath = FileUtils.getNewFileDir(FileUtils.IMAGE_CAPTURE);
        if (! TextUtils.isEmpty(imgPath) && Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            File path = new File(imgPath);
            if (! path.exists()) {
                path.mkdirs();
            }
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            Uri uri;
            if (Build.VERSION.SDK_INT >= 24) {
                uri = FileProvider.getUriForFile(fragment.getContext(), "com.zxycloud.zszw.fileprovider", path);
            } else {
                uri = Uri.fromFile(path);
            }
            intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
            fragment.startActivityForResult(intent, photo);
        }
        return imgPath;
    }

    /**
     * 打开系统图片查看器
     * @param fragment
     * @param pathId
     */
    public static void openSystemPictureViewer(Fragment fragment, String pathId) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        Uri uri;
        File file = new File(pathId);
        if (Build.VERSION.SDK_INT >= 24) {
            uri = FileProvider.getUriForFile(fragment.getContext(), "com.zxycloud.zszw.fileprovider", file);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        } else {
            uri = Uri.fromFile(file);
        }
        intent.setDataAndType(uri, "image/*");
        fragment.startActivity(intent);
    }

    /**
     * gcj02转wgs84
     * 获取维度
     * @param lat
     * @param lon
     * @return
     */
    public static double gcToWgsLat(double lat, double lon) {
        return CoordinateUtil.gcj_To_Gps84(lat, lon).getWgLat();
    }

    /**
     * gcj02转wgs84
     * 获取经度
     * @param lat
     * @param lon
     * @return
     */
    public static double gcToWgsLon(double lat, double lon) {
        return CoordinateUtil.gcj_To_Gps84(lat, lon).getWgLon();
    }

    /*---------------------------------------极光推送注册Id----------------------------------------*/
    private static String registrationId;

    public static void setRegistrationId(String regId) {
        CommonUtils.registrationId = regId;
    }

    /**
     * 拼接数组
     * @param first 第一个数组
     * @param rest 其他数组
     * @param <T> 数组类型
     * @return
     */
    public static <T> T[] concatArray(T[] first, T[]... rest) {
        int totalLength = first.length;
        for (T[] array : rest) {
            totalLength += array.length;
        }
        T[] result = Arrays.copyOf(first, totalLength);
        int offset = first.length;
        for (T[] array : rest) {
            System.arraycopy(array, 0, result, offset, array.length);
            offset += array.length;
        }
        return result;
    }
}
