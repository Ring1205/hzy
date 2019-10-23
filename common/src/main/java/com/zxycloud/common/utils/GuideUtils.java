package com.zxycloud.common.utils;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * @author 半寿翁
 * @date 2017/11/9
 */
public class GuideUtils {
    public static final byte BAIDU_MAP = 0x00;
    public static final byte GAODE_MAP = 0x01;

    private static final String BAIDU_MAP_PACKAGE = "com.baidu.BaiduMap";
    private static final String GAODE_MAP_PACKAGE = "com.autonavi.minimap";

    /**
     * 检查手机上是否安装了指定的软件
     *
     * @param context     上下文
     * @param packageName 应用包名
     * @return 当前包是否存在
     */
    private static boolean isAvilible(Context context, String packageName) {
        //获取packagemanager
        final PackageManager packageManager = context.getPackageManager();
        //获取所有已安装程序的包信息
        List<PackageInfo> packageInfos = packageManager.getInstalledPackages(0);
        //用于存储所有已安装程序的包名
        List<String> packageNames = new ArrayList<String>();
        //从pinfo中将包名字逐一取出，压入pName list中
        if (packageInfos != null) {
            for (int i = 0; i < packageInfos.size(); i++) {
                String packName = packageInfos.get(i).packageName;
                packageNames.add(packName);
            }
        }
        //判断packageNames中是否有目标程序的包名，有TRUE，没有FALSE
        return packageNames.contains(packageName);
    }

    public static boolean jumpTo(Context context, byte which, double lng, double lat) {
        return GuideUtils.jumpTo(context, which, lng + "", lat + "");
    }

    /**
     * 跳转到其他APP
     *
     * @param context 上下文
     * @param which   跳转到哪个APP
     * @return 是否跳转成功
     */
    public static boolean jumpTo(Context context, byte which, String lng, String lat) {
        switch (which) {
            case BAIDU_MAP:
                if (isAvilible(context, BAIDU_MAP_PACKAGE)) {
                    openBaiduNavi(context, lng, lat);
                } else {
                    installAPP(context, which);
                }
                break;

            case GAODE_MAP:
                if (isAvilible(context, GAODE_MAP_PACKAGE)) {
                    openGaoDeNavi(context, lng, lat);
                } else {
                    installAPP(context, which);
                }
                break;
        }
        return true;
    }

    /**
     * 启动高德App进行导航
     * sourceApplication 必填 第三方调用应用名称。如 amap
     * poiname           非必填 POI 名称
     * dev               必填 是否偏移(0:lat 和 lon 是已经加密后的,不需要国测加密; 1:需要国测加密)
     * style             必填 导航方式(0 速度快; 1 费用少; 2 路程短; 3 不走高速；4 躲避拥堵；5 不走高速且避免收费；6 不走高速且躲避拥堵；7 躲避收费和拥堵；8 不走高速躲避收费和拥堵))
     */
    private static void openGaoDeNavi(Context context, String lng, String lat) {
        StringBuffer stringBuffer = new StringBuffer("androidamap://navi?sourceApplication=")
                .append("yitu8_driver").append("&lat=").append(lat)
                .append("&lon=").append(lng)
                .append("&dev=").append(0)
                .append("&style=").append(0);
        //        if (!TextUtils.isEmpty(poiname)) {
//            stringBuffer.append("&poiname=").append(poiname);
//        }
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(stringBuffer.toString()));
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.setPackage("com.autonavi.minimap");
        context.startActivity(intent);
    }

    /**
     * 打开百度地图导航客户端
     * intent = Intent.getIntent("baidumap://map/navi?location=34.264642646862,108.95108518068&type=BLK&src=thirdapp.navi.you
     * location 坐标点 location与query二者必须有一个，当有location时，忽略query
     * query    搜索key   同上
     * type 路线规划类型  BLK:躲避拥堵(自驾);TIME:最短时间(自驾);DIS:最短路程(自驾);FEE:少走高速(自驾);默认DIS
     */
    private static void openBaiduNavi(Context context, String lng, String lat) {
        StringBuffer stringBuffer = new StringBuffer("baidumap://map/navi?location=")
                .append(lat).append(",").append(lng).append("&type=TIME");
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(stringBuffer.toString()));
        intent.setPackage("com.baidu.BaiduMap");
        context.startActivity(intent);
    }

    /**
     * 若APP不存在安装
     *
     * @param which 哪个APP
     */
    private static void installAPP(Context context, byte which) {
        Intent intent = null;
        Uri uri = null;
        switch (which) {
            case BAIDU_MAP:
                Toast.makeText(context, "您尚未安装百度地图", Toast.LENGTH_LONG).show();
                uri = Uri.parse("market://details?id=com.baidu.BaiduMap");
                intent = new Intent(Intent.ACTION_VIEW, uri);
                break;

            case GAODE_MAP:
                Toast.makeText(context, "您尚未安装高德地图", Toast.LENGTH_LONG).show();
                uri = Uri.parse("market://details?id=com.autonavi.minimap");
                intent = new Intent(Intent.ACTION_VIEW, uri);
                break;
        }
        context.startActivity(intent);
    }
}
