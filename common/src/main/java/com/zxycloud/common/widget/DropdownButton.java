package com.zxycloud.common.widget;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Checkable;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.zxycloud.common.R;
import com.zxycloud.common.utils.PopWinDownUtil;

/**
 * Created by cuiMarker on 2016/12/13.
 */
public class DropdownButton extends RelativeLayout implements Checkable, View.OnClickListener, PopWinDownUtil.OnDismissLisener {
    /**
     * 菜单按钮文字内容
     */
    private ImageView text;
    /**
     * 菜单按钮底部的提示条
     */
    private boolean isCheced;
    private PopWinDownUtil popWinDownUtil;
    private Context mContext;
    private SmartRefreshLayout refreshLayout;

    public DropdownButton(Context context) {
        this(context, null);
    }

    public DropdownButton(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DropdownButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        mContext = context;
        //菜单按钮的布局
        View view = LayoutInflater.from(getContext()).inflate(R.layout.drop_down_button, this, true);
        text = view.findViewById(R.id.iv_mneu_down);

        //点击事件，点击外部区域隐藏popupWindow
        setOnClickListener(this);
    }

    public void setAdapter(@NonNull RecyclerView.Adapter adapter) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.drop_down_window, null);
        RelativeLayout relativeLayout = view.findViewById(R.id.content);
        refreshLayout = view.findViewById(R.id.refreshLayout);
        refreshLayout.setEnableLoadMore(false);
        refreshLayout.setEnableRefresh(false);
        relativeLayout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                popWinDownUtil.hide();
            }
        });

        RecyclerView recyclerView = view.findViewById(R.id.list);
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        recyclerView.setAdapter(adapter);
        popWinDownUtil = new PopWinDownUtil(view, this);
        popWinDownUtil.setOnDismissListener(this);
    }

    /**
     * 根据传过来的参数改变状态
     *
     * @param checked
     */
    @Override
    public void setChecked(boolean checked) {
        if (popWinDownUtil != null) {
            isCheced = checked;
            Drawable icon;
            popWinDownUtil.setWidth(getWidth());
            if (checked) {
                icon = getResources().getDrawable(R.drawable.ic_arrow_drop_up);
                popWinDownUtil.show();
            } else {
                icon = getResources().getDrawable(R.drawable.ic_arrow_drop_down);
                popWinDownUtil.hide();
            }
            text.setImageDrawable(icon);
        }
    }

    public SmartRefreshLayout getRefreshLayout(){
        return refreshLayout;
    }

    @Override
    public boolean isChecked() {
        return isCheced;
    }

    @Override
    public void toggle() {
        setChecked(!isCheced);
    }

    @Override
    public void onClick(View v) {
        setChecked(!isCheced);
    }

    @Override
    public void onDismiss() {
        setChecked(false);
    }
}
