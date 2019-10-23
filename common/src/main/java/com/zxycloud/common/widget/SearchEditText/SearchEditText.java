package com.zxycloud.common.widget.SearchEditText;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;

import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.zxycloud.common.R;
import com.zxycloud.common.utils.PopWinDownUtil;

/**
 * Created by cuiMarker on 2016/12/13.
 */
public class SearchEditText extends AppCompatEditText implements PopWinDownUtil.OnDismissLisener, TextWatcher {
    /**
     * 菜单按钮底部的提示条
     */
    private boolean isCheced;
    private PopWinDownUtil popWinDownUtil;
    private Context mContext;
    private OnClickRightListener listener;
    private SmartRefreshLayout refreshLayout;

    public SearchEditText(Context context) {
        super(context);
        init(context);
    }

    public SearchEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public SearchEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        mContext = context;
        addTextChangedListener(this);
        setRightDrawable(getResources().getDrawable(R.drawable.ic_arrow_drop_down));
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_UP:
                Drawable drawable = getCompoundDrawables()[2];
                if (drawable != null && event.getX() >= (getWidth() - drawable.getIntrinsicWidth() - getPaddingRight())) {
                    if (isFocusable() && getText().toString().length() > 0) {
                        listener.onClick(this, getText().toString());
                        popWinDownUtil.show();
                    } else {
                        isCheced = !isCheced;
                        isCheckedRightDrawable(isCheced);
                    }
                    return false;
                } else {
                    setFocusableInTouchMode(true);
                    setCursorVisible(true);
                    setFocusable(true);
                    setSelection(getText().toString().length());
                    requestFocus();
                    if (getText().toString().length()>0)
                        setRightDrawable(getResources().getDrawable(R.drawable.ic_search_black_26dp));
                }
                break;
        }
        return super.onTouchEvent(event);
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
    public void isCheckedRightDrawable(boolean checked) {
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
        setRightDrawable(icon);
    }

    public void setEditText(CharSequence text) {
        setFocusableInTouchMode(false);
        setCursorVisible(false);
        setFocusable(false);
        requestFocus();
        setText(text);
        onDismiss();
    }

    public void addRightOnClickListener(OnClickRightListener listener) {
        this.listener = listener;
    }

    public SmartRefreshLayout getRefreshLayout() {
        return refreshLayout;
    }

    public void setRightDrawable(Drawable drawable) {
        Drawable[] drawables = getCompoundDrawables();
        setCompoundDrawablesWithIntrinsicBounds(drawables[0], drawables[1], drawable, drawables[3]);
    }

    @Override
    public void onDismiss() {
        isCheckedRightDrawable(false);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (isFocusable())
            if (s.length() > 0)
                setRightDrawable(getResources().getDrawable(R.drawable.ic_search_black_26dp));
            else
                setDownDrawable();
        else
            setDownDrawable();
    }

    private void setDownDrawable() {
        if (listener != null)
            listener.onDownDrawable(this);
        setRightDrawable(getResources().getDrawable(R.drawable.ic_arrow_drop_down));
    }

    @Override
    public void afterTextChanged(Editable s) {
    }
}
