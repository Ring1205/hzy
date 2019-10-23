package com.zxycloud.common.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.AppCompatEditText;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.zxycloud.common.R;
import com.zxycloud.common.utils.CommonUtils;

import java.io.UnsupportedEncodingException;
import java.util.regex.Pattern;

/**
 * 多功能EditText，可以实现文本清除，密码设置，非法字符过滤，获取焦点时隐藏hint的功能
 *
 * @author leiming
 * @date 2017/10/11
 */
public class BswEditText extends AppCompatEditText {
    private final int CLEAR = 101;
    private final int PASSWORD = 102;
    private String hint;
    private int currentType;
    /**
     * 右侧功能键引用
     */
    private Drawable mDrawable;
    /**
     * password可见状态
     */
    private boolean isPasswordHide = true;
    private boolean showIcon = false;

    private OnTextDeletedListener listener;

    public BswEditText(Context context) {
        super(context);
        init();
    }

    public BswEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    @Override
    protected void onTextChanged(CharSequence text, int start, int lengthBefore, int lengthAfter) {
        super.onTextChanged(text, start, lengthBefore, lengthAfter);
        CharSequence noEmojiString = emojiJudge(text);

        // text是SpannableStringBuilder，不能直接判断
        if (! text.toString().equals(noEmojiString.toString())) {
            setText(noEmojiString);
            // 如果
            setSelection(noEmojiString.length());
        }
        if (showIcon) {
            switch (currentType) {
                case CLEAR:
                    setRightIconVisible(getText().length() > 0);
                    break;

                case PASSWORD:
                    setRightIconVisible(getText().length() > 0);
                    break;
            }
        }
    }

    /**
     * UTF-16双音节过滤
     *
     * @param text 输入的文本
     * @return 滤除特殊符号后的文本
     */
    private CharSequence emojiJudge(CharSequence text) {
        try {
            return Pattern.compile("[\ud800\udc00-\udbff\udfff]|[\u2600-\u27ff]|\r|\n|\\s",
                    Pattern.UNICODE_CASE | Pattern.CASE_INSENSITIVE).matcher(new String(text.toString().getBytes("UTF-8"), "UTF-8")).replaceAll("");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return text;
        }

    }

    /**
     * 获得hint值
     *
     * @author admin 2016-9-5 下午4:32:19
     */
    private void init() {
        hint = getHint().toString();
    }

    /**
     * 设置删除按钮
     */
    public void setClear() {
        showIcon = true;
        currentType = CLEAR;
        setRightDrawable();
    }

    /**
     * 设置密码展示
     */
    public void setPassword() {
        showIcon = true;
        currentType = PASSWORD;
        setRightDrawable();
    }

    /**
     * 设置右侧图片
     */
    private void setRightDrawable() {
        mDrawable = getCompoundDrawables()[2];
        if (mDrawable == null) {
            switch (currentType) {
                case CLEAR:
                    mDrawable = getResources().getDrawable(R.mipmap.icon_delete_text);
                    break;

                case PASSWORD:
                    mDrawable = getResources().getDrawable(R.mipmap.icon_password_hide);
                    break;
            }
        }
        if (mDrawable == null) {
            return;
        }
        switch (currentType) {
            case CLEAR:
                mDrawable.setBounds(0, 0, ((int) (mDrawable.getIntrinsicWidth() * 0.8)), ((int) (mDrawable.getIntrinsicHeight() * 0.8)));
                setRightIconVisible(getText().length() > 0);
                break;

            case PASSWORD:
                mDrawable.setBounds(0, 0, ((int) (mDrawable.getIntrinsicWidth() * 0.8)), ((int) (mDrawable.getIntrinsicHeight() * 0.8)));
                setRightIconVisible(getText().length() > 0);
                break;
        }
    }

    /**
     * 设置清除图标的显示与隐藏，调用setCompoundDrawables为EditText绘制上去
     *
     * @param visible 是否可见
     */
    protected void setRightIconVisible(boolean visible) {
        Drawable right = visible ? mDrawable : null;
        setCompoundDrawables(getCompoundDrawables()[0], getCompoundDrawables()[1], right, getCompoundDrawables()[3]);
    }

    @Override
    protected void onFocusChanged(boolean focused, int direction,
                                  Rect previouslyFocusedRect) {
        if (focused) {
            setHint("");
        } else {
            setHint(hint);
        }
        if (showIcon) {
            switch (currentType) {
                case CLEAR:
                    setRightIconVisible(focused && getText().length() > 0);
                    break;

                case PASSWORD:
                    setRightIconVisible(focused && getText().length() > 0);
                    break;
            }
        }
        super.onFocusChanged(focused, direction, previouslyFocusedRect);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (showIcon) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_UP:
                    CommonUtils.log().i("mDrawable", getId() + " ________________event.getRawX() = " + event.getRawX() + " getRight() = " + getRight() + " mDrawable.getBounds().width() = " + mDrawable.getBounds().width());
                    if (mDrawable != null && event.getRawX() >= getRight() - mDrawable.getBounds().width() * 2) {
                        switch (currentType) {
                            case CLEAR:
                                setText("");
                                if (null != listener) {
                                    listener.onDeleted();
                                }
                                break;

                            case PASSWORD:
                                if (isPasswordHide) {
                                    //设置EditText的密码为可见的
                                    setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                                    mDrawable = getResources().getDrawable(R.mipmap.icon_password_show);
                                } else {
                                    //设置密码为隐藏的
                                    setTransformationMethod(PasswordTransformationMethod.getInstance());
                                    mDrawable = getResources().getDrawable(R.mipmap.icon_password_hide);
                                }
                                setSelection(getText().toString().trim().length());
                                isPasswordHide = ! isPasswordHide;
                                mDrawable.setBounds(0, 0, ((int) (mDrawable.getIntrinsicWidth() * 0.8)), ((int) (mDrawable.getIntrinsicHeight() * 0.8)));
                                setRightIconVisible(getText().length() > 0);
                                break;
                        }
                    }
                    break;
            }
        }
        return super.onTouchEvent(event);
    }

    public void setOnTextDeletedListener(OnTextDeletedListener listener) {
        this.listener = listener;
    }

    public interface OnTextDeletedListener {
        void onDeleted();
    }
}
