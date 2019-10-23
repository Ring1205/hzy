package com.zxycloud.common.widget;

import android.content.Context;
import android.support.v7.widget.AppCompatTextView;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.ViewDebug.ExportedProperty;

/**
 * @author leiming
 * @date 2017/10/11
 */
public class MargueeTextView extends AppCompatTextView {

    public MargueeTextView(Context context) {
        this(context, null);
    }

    public MargueeTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setSingleLine();
        setEllipsize(TextUtils.TruncateAt.MARQUEE);
        setMarqueeRepeatLimit(- 1);
        setHorizontallyScrolling(true);
    }

    public MargueeTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    @Override
    @ExportedProperty(category = "focus")
    public boolean isFocused() {
        return true;
    }

}
