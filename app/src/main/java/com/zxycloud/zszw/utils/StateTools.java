package com.zxycloud.zszw.utils;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorInt;
import android.support.annotation.DrawableRes;
import android.support.annotation.StringRes;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.view.View;
import android.widget.ImageView;

import com.zxycloud.zszw.R;
import com.zxycloud.zszw.model.IconBean;

import java.util.ArrayList;

public class StateTools {
    public static void setStateTint(int state, ImageView view) {
        int img;
        switch (state) {
            case 1://火警
                img = R.mipmap.ic_state_fire;
                break;
            case 2://预警
                img = R.mipmap.ic_state_prefire;
                break;
            case 3://启动
                img = R.mipmap.ic_state_launch;
                break;
            case 4://监管
                img = R.mipmap.ic_state_supervise;
                break;
            case 5://反馈
                img = R.mipmap.ic_state_tickling;
                break;
            case 6://故障
                img = R.mipmap.ic_state_fault;
                break;
            case 7://屏蔽
                img = R.mipmap.ic_state_shield;
                break;
//            case 8://隐患
//                break;
//            case 11://火警消除
//                break;
//            case 13://停止
//                break;
//            case 14://监管撤销
//                break;
//            case 15://反馈撤销
//                break;
//            case 16://故障恢复
//                break;
//            case 17://屏蔽撤销
//                break;
            case 95:// 事件
                img = R.mipmap.ic_state_incident;
                break;
            case 96:// 离线
                img = R.mipmap.ic_state_offline;
                break;
//            case 97:// 上线
//                break;
//            case 98:// 操作
//                break;
            case 99:// 正常
                img = R.mipmap.ic_state_normal;
                break;
            // 其他
            default:
                img = R.mipmap.ic_state_normal;
                break;
        }
        view.setVisibility(View.VISIBLE);
        view.setImageResource(img);
    }

    public static void setViewColor(Context context, ImageView view, @DrawableRes int id, @ColorInt int color) {
        Drawable drawable = ContextCompat.getDrawable(context, id);
        Drawable.ConstantState states = drawable.getConstantState();
        drawable = DrawableCompat.wrap(states == null ? drawable : states.newDrawable()).mutate();
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        DrawableCompat.setTint(drawable, color);
        view.setImageDrawable(drawable);
    }

    public static ArrayList initListBean(@StringRes int[] ints) {
        ArrayList<IconBean> beans = new ArrayList<>();
        for (@StringRes int anInt : ints) {
            switch (anInt) {
                // 统计列表
                case R.string.home_stat_area:
                    beans.add(new IconBean(anInt, R.mipmap.ic_statistices_area));
                    break;
                case R.string.home_stat_device:
                    beans.add(new IconBean(anInt, R.mipmap.ic_statistices_device));
                    break;
                case R.string.home_stat_account:
                    beans.add(new IconBean(anInt, R.mipmap.ic_statistices_account));
                    break;
                case R.string.home_stat_point:
                    beans.add(new IconBean(anInt, R.mipmap.ic_statistices_point));
                    break;
                case R.string.home_stat_inspection:
                    beans.add(new IconBean(anInt, R.mipmap.ic_statistices_inspection));
                    break;
                case R.string.home_stat_history:
                    beans.add(new IconBean(anInt, R.mipmap.ic_alarm_history));
                    break;
                case R.string.home_stat_place:
                    beans.add(new IconBean(anInt, R.mipmap.ic_statistices_place));
                    break;
                // 快捷方式
                case R.string.home_device:
                    beans.add(new IconBean(anInt, R.mipmap.ic_device));
                    break;
                case R.string.home_site_entry:
                    beans.add(new IconBean(anInt, R.mipmap.ic_site_entry));
                    break;
                case R.string.home_add_area:
                    beans.add(new IconBean(anInt, R.mipmap.ic_add_area));
                    break;
                case R.string.home_infrastructure:
                    beans.add(new IconBean(anInt, R.mipmap.ic_infrastructure));
                    break;
                case R.string.home_hidden_danger:
                    beans.add(new IconBean(anInt, R.mipmap.ic_hidden_trouble_report));
                    break;
                case R.string.home_task:
                    beans.add(new IconBean(anInt, R.mipmap.ic_task));
                    break;
            }
        }
        return beans;
    }
// 网关状态色
    public static int stateColor(int stateCode) {
        switch (stateCode) {
            // 火警、启动、监管、反馈
            case 1:
            case 3:
            case 4:
            case 5:
                return R.color.color_state_fire;

            // 预警
            case 2:
                return R.color.color_state_prefire;

            // 故障、屏蔽
            case 6:
            case 7:
                return R.color.color_state_fault;

            // 离线
            case 96:
                return R.color.color_state_offline;

            // 正常
            default:
                return R.color.color_state_normal;
        }
    }
}
