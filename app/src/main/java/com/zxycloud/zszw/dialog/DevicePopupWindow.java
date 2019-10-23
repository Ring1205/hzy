package com.zxycloud.zszw.dialog;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.PopupWindow;

import com.zxycloud.zszw.R;

public class DevicePopupWindow extends PopupWindow {
    private View mView; // PopupWindow 菜单布局
    private Context mContext; // 上下文参数
    private View.OnClickListener allocateListener;
    private View.OnClickListener unassignListener;
    private View.OnClickListener deleteListener;

    public DevicePopupWindow(Context context, View.OnClickListener allocateListener, View.OnClickListener unassignListener, View.OnClickListener deleteListener) {
        super(context);
        this.mContext = context;
        this.allocateListener = allocateListener;
        this.unassignListener = unassignListener;
        this.deleteListener = deleteListener;
        Init();
    }

    /**
     * 设置布局以及点击事件
     */
    private void Init() {
        LayoutInflater inflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mView = inflater.inflate(R.layout.device_pop_item, null);
        Button btn_allocate = mView.findViewById(R.id.btn_allocate);
        Button btn_unassign = mView.findViewById(R.id.btn_unassign);
        Button btn_delete = mView.findViewById(R.id.btn_delete);
        Button btn_cancel = mView.findViewById(R.id.btn_cancel);

        btn_allocate.setOnClickListener(allocateListener);
        btn_unassign.setOnClickListener(unassignListener);
        btn_delete.setOnClickListener(deleteListener);
        btn_cancel.setOnClickListener(new View.OnClickListener() {
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
        this.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        // 设置可触
        this.setFocusable(true);
        ColorDrawable dw = new ColorDrawable(0x0000000);
        this.setBackgroundDrawable(dw);
        // 单击弹出窗以外处 关闭弹出窗
        mView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int height = mView.findViewById(R.id.ll_pop).getTop();
                int y = (int) event.getY();
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (y < height) {
                        dismiss();
                    }
                }
                return true;
            }
        });
    }
}
