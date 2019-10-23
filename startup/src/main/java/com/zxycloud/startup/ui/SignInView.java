package com.zxycloud.startup.ui;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import com.zxycloud.startup.R;

public class SignInView extends FrameLayout {
    private Context mContext;

    public SignInView(@NonNull Context context) {
        this(context, null);
    }

    public SignInView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SignInView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        init();
    }

    private void init() {
        View view = LayoutInflater.from(mContext).inflate(R.layout.start_up_sign_in_layout, this);
//        view
    }
}
