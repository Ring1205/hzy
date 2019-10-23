package com.zxycloud.common.widget;

import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.zxycloud.common.R;
import com.zxycloud.common.utils.CommonUtils;
import com.zxycloud.common.utils.StringFormatUtils;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @author leiming
 * @date 2019/3/26.
 */
public class CommonDialog extends DialogFragment {
    public static final int ID_TITLE = R.id.dialog_title;
    public static final int ID_CONTENT = R.id.dialog_content;
    public static final int ID_VICE_CONTENT = R.id.dialog_content_2;
    public static final int ID_LEFT = R.id.dialog_left;
    public static final int ID_RIGHT = R.id.dialog_right;

    private static final String TITLE = "title";
    private static final String CONTENT = "content";
    private static final String VICE_CONTENT_RES = "viceContentRes";
    private static final String VICE_CONTENT = "viceContent";
    private static final String CB_RES = "cbRes";
    private static final String IS_SHOW_CB = "isShowCb";
    private static final String IS_CB_CHECKED = "isCbChecked";
    private static final String LEFT = "left";
    private static final String RIGHT = "right";
    private static final String AUTO_DISMISS = "autoDismiss";
    private static final String CAN_TOUCH_OUTSIDE = "canTouchOutside";

    private SparseArray<TextView> mViews;//集合类，layout里包含的View,以view的id作为key，value是view对象

    private int titleRes;
    private int contentRes;
    private int viceContentRes;
    private String viceContent;
    private boolean isShowViceContentUnderline;
    private int leftRes;
    private int rightRes;
    private boolean autoDismiss;
    private boolean canTouchOutside;
    private int cbRes;
    private boolean isShowCb;
    private boolean isCbChecked;
    private Object tag;
    private FragmentActivity activity;
    private OnCommonClickListener listener;

    private StringFormatUtils formatUtils;
    private TextView dialogTitle;
    private TextView dialogContent;
    private TextView dialogContent2;
    private TextView dialogLeft;
    private TextView dialogRight;
    private View dialogCbLl;
    private CheckBox dialogCb;
    private TextView dialogCbTv;

    private boolean isShowing;

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({View.VISIBLE, View.GONE, View.INVISIBLE})
    @interface Visibility {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        formatUtils = CommonUtils.string();
        setStyle(DialogFragment.STYLE_NO_TITLE, android.R.style.Theme_Holo_Light_Dialog_NoActionBar_MinWidth);
    }

    @Override
    public void onStart() {
        super.onStart();
        getDialog().getWindow().setBackgroundDrawableResource(R.drawable.common_bg_dialog);
    }

    public CommonDialog show() {
        FragmentTransaction ft = activity.getSupportFragmentManager().beginTransaction();
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        show(ft, "df");
        isShowing = true;
        return this;
    }

    public boolean isShowing() {
        return isShowing;
    }

    @Override
    public void dismiss() {
        try {
            super.dismiss();
            isShowing = false;
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_common, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setClickView(view, R.id.dialog_left, R.id.dialog_right, R.id.dialog_content_2);

        dialogTitle = view.findViewById(R.id.dialog_title);
        dialogContent = view.findViewById(R.id.dialog_content);
        dialogContent2 = view.findViewById(R.id.dialog_content_2);
        dialogLeft = view.findViewById(R.id.dialog_left);
        dialogRight = view.findViewById(R.id.dialog_right);
        dialogCbLl = view.findViewById(R.id.dialog_cb_ll);
        dialogCb = view.findViewById(R.id.dialog_cb);
        dialogCbTv = view.findViewById(R.id.dialog_cb_tv);

        dialogContent2.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);

        mViews = new SparseArray<>();
        mViews.put(ID_TITLE, dialogTitle);
        mViews.put(ID_CONTENT, dialogContent);
        mViews.put(ID_VICE_CONTENT, dialogContent2);
        mViews.put(ID_LEFT, dialogLeft);
        mViews.put(ID_RIGHT, dialogRight);

        Bundle bundle = getArguments();
        titleRes = bundle.getInt(TITLE);
        contentRes = bundle.getInt(CONTENT);
        viceContentRes = bundle.getInt(VICE_CONTENT_RES);
        viceContent = bundle.getString(VICE_CONTENT);
        leftRes = bundle.getInt(LEFT);
        rightRes = bundle.getInt(RIGHT);
        autoDismiss = bundle.getBoolean(AUTO_DISMISS);
        canTouchOutside = bundle.getBoolean(CAN_TOUCH_OUTSIDE);
        cbRes = bundle.getInt(CB_RES);
        isShowCb = bundle.getBoolean(IS_SHOW_CB);
        isCbChecked = bundle.getBoolean(IS_CB_CHECKED);

        getDialog().setCanceledOnTouchOutside(canTouchOutside);
        if (isShowCb) {
            dialogCb.setChecked(isCbChecked);
            dialogCbTv.setText(cbRes);
        } else {
            dialogCbLl.setVisibility(View.GONE);
        }

        dialogCbTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogCb.setChecked(!dialogCb.isChecked());
            }
        });

        stringShowJudge(dialogTitle, titleRes);
        stringShowJudge(dialogContent, contentRes);
        stringShowJudge(dialogContent2, viceContentRes, viceContent);
        stringShowJudge(dialogLeft, leftRes);
        stringShowJudge(dialogRight, rightRes);
    }

    /**
     * 根据Id更新显示的文本
     *
     * @param id        依据的Id
     * @param stringRes 被替换的文本的资源地址
     * @return 弹窗控件
     */
    public CommonDialog update(@IdRes int id, @StringRes int stringRes) {
        TextView textView = mViews.get(id);
        if (null != textView) {
            if (textView.getVisibility() == View.GONE) {
                textView.setVisibility(View.VISIBLE);
            }
            textView.setText(stringRes);
        }
        return this;
    }

    /**
     * 根据Id更新显示的文本
     *
     * @param id     依据的Id
     * @param string 被替换的文本
     * @return 弹窗控件
     */
    public CommonDialog update(@IdRes int id, String string) {
        TextView textView = mViews.get(id);
        if (null != textView) {
            if (textView.getVisibility() == View.GONE) {
                textView.setVisibility(View.VISIBLE);
            }
            textView.setText(string);
        }
        return this;
    }


    /**
     * 设置是否可见
     *
     * @param id      控件Id
     * @param visible 可见性
     * @return 弹窗控件
     */
    public CommonDialog setVisible(@IdRes int id, @Visibility int visible) {
        TextView textView = mViews.get(id);
        if (null != textView) {
            textView.setVisibility(visible);
        }
        return this;
    }

    /**
     * 是否选中CheckBox
     *
     * @return 是否选中
     */
    public boolean isCbChecked() {
        return dialogCb.isChecked();
    }

    /**
     * 隐藏弹窗
     */
    public void hide() {
        dismiss();
    }

    /**
     * 是否显示对应控件的判断工具，若资源设置，则显示，若没有设置则不显示
     *
     * @param view      被判断控件
     * @param stringRes 显示文本对应的资源
     */
    private void stringShowJudge(TextView view, int stringRes, String... strings) {
        if (0 != stringRes) {
            view.setText(stringRes);
        } else {
            if (strings.length == 0)
                view.setVisibility(View.GONE);
            else
                view.setText(strings[0]);
        }
    }

    public void setOnClickListener(OnCommonClickListener listener) {
        this.listener = listener;
    }

    public void setActivity(FragmentActivity activity) {
        this.activity = activity;
    }

    public void setTag(Object tag) {
        this.tag = tag;
    }

    /**
     * 批量设置点击事件
     *
     * @param viewIds 被点击控件的Id
     */
    protected void setClickView(View view, @IdRes int... viewIds) {
        for (int layout : viewIds) {
            view.findViewById(layout).setOnClickListener(onClickListener);
        }
    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (null == listener) {
                return;
            }
            if (autoDismiss) {
                dismiss();
            }
            listener.onClick(v, tag);
        }
    };

    public interface OnCommonClickListener {
        /**
         * 弹窗点击监听
         *
         * @param view 被点击的控件
         * @param tag  tag 标签
         */
        void onClick(View view, Object tag);
    }

    /**
     * 构造器
     */
    public static class Builder {
        /**
         * 标题的资源
         */
        private int titleRes;
        /**
         * 提示文本的资源
         */
        private int contentRes;
        /**
         * 副文本的资源
         */
        private int viceContentRes;
        /**
         * 副文本
         */
        private String viceContent;
        /**
         * 副文本下划线是否显示
         */
        private boolean isShowViceContentUnderline;
        /**
         * 左侧按键的资源
         */
        private int leftRes;
        /**
         * 右侧按键的资源
         */
        private int rightRes;
        /**
         * CheckBox提示文本
         */
        private int cbRes;
        /**
         * 是否显示CheckBox
         */
        private boolean isShowCb = false;
        /**
         * CheckBox选择状态
         */
        private boolean isCbChecked = false;
        /**
         * 点击按钮后是否自动关闭弹窗，默认可以关闭
         */
        private boolean autoDismiss = true;
        /**
         * 点击弹窗外部是否允许关闭弹窗，默认允许
         */
        private boolean canTouchOutside = true;
        /**
         * 标签，用于点击弹窗复用或点击事件传参时使用
         */
        private Object tag;

        /**
         * 设置标签
         *
         * @param tag {@link CommonDialog#tag}
         * @return 构造器
         */
        public Builder setTag(Object tag) {
            this.tag = tag;
            return this;
        }

        /**
         * 设置弹窗标题
         *
         * @param titleRes {@link CommonDialog#titleRes}
         * @return 构造器
         */
        public Builder setTitleRes(@StringRes int titleRes) {
            this.titleRes = titleRes;
            return this;
        }

        /**
         * 设置弹窗文本
         *
         * @param contentRes {@link CommonDialog#contentRes}
         * @return 构造器
         */
        public Builder setContentRes(@StringRes int contentRes) {
            this.contentRes = contentRes;
            return this;
        }

        /**
         * 设置弹窗副文本（蓝色字体）
         *
         * @param viceContentRes {@link CommonDialog#viceContentRes}
         * @return 构造器
         */
        public Builder setViceContentRes(@StringRes int viceContentRes) {
            setViceContentRes(viceContentRes, true);
            return this;
        }

        /**
         * 设置弹窗副文本（蓝色字体）
         *
         * @param viceContentRes             {@link CommonDialog#viceContentRes}
         * @param isShowViceContentUnderline {@link CommonDialog#isShowViceContentUnderline}
         * @return 构造器
         */
        public Builder setViceContentRes(@StringRes int viceContentRes, boolean isShowViceContentUnderline) {
            this.viceContentRes = viceContentRes;
            this.isShowViceContentUnderline = isShowViceContentUnderline;
            return this;
        }

        /**
         * 设置弹窗副文本（蓝色字体）
         *
         * @param viceContent {@link CommonDialog#viceContent}
         * @return 构造器
         */
        public Builder setViceContent(String viceContent) {
            setViceContent(viceContent, true);
            return this;
        }

        /**
         * 设置弹窗副文本（蓝色字体）
         *
         * @param viceContent                {@link CommonDialog#viceContent}
         * @param isShowViceContentUnderline {@link CommonDialog#isShowViceContentUnderline}
         * @return 构造器
         */
        public Builder setViceContent(String viceContent, boolean isShowViceContentUnderline) {
            this.viceContent = viceContent;
            this.isShowViceContentUnderline = isShowViceContentUnderline;
            return this;
        }

        /**
         * 设置左侧按键文字
         *
         * @param leftRes {@link CommonDialog#leftRes}
         * @return 构造器
         */
        public Builder setLeftRes(@StringRes int leftRes) {
            this.leftRes = leftRes;
            return this;
        }

        /**
         * 设置右侧按键文字
         *
         * @param rightRes {@link CommonDialog#rightRes}
         * @return 构造器
         */
        public Builder setRightRes(@StringRes int rightRes) {
            this.rightRes = rightRes;
            return this;
        }

        /**
         * 设置是否可以点击弹窗外关闭，默认为false
         *
         * @param canTouchOutside {@link CommonDialog#canTouchOutside}
         * @return 构造器
         */
        public Builder setCanTouchOutside(boolean canTouchOutside) {
            this.canTouchOutside = canTouchOutside;
            return this;
        }

        /**
         * 设置点击按键后是否自动关闭弹窗，默认为true
         *
         * @param autoDismiss {@link CommonDialog#canTouchOutside}
         * @return 构造器
         */
        public Builder setAutoDismiss(boolean autoDismiss) {
            this.autoDismiss = autoDismiss;
            return this;
        }

        /**
         * 设置显示CheckBox
         *
         * @return 构造器
         */
        public Builder setShowCb() {
            this.isShowCb = true;
            this.cbRes = R.string.common_string_dialog_cb;
            return this;
        }

        /**
         * 设置显示CheckBox，以及提示文本
         *
         * @param cbRes {@link CommonDialog#cbRes}
         * @return 构造器
         */
        public Builder setShowCb(int cbRes) {
            this.isShowCb = true;
            this.cbRes = cbRes;
            return this;
        }

        /**
         * 设置显示CheckBox，以及提示文本
         *
         * @param cbRes       {@link CommonDialog#cbRes}
         * @param isCbChecked {@link CommonDialog#isCbChecked}
         * @return 构造器
         */
        public Builder setShowCb(int cbRes, boolean isCbChecked) {
            this.isShowCb = true;
            this.cbRes = cbRes;
            this.isCbChecked = isCbChecked;
            return this;
        }

        public CommonDialog build(FragmentActivity activity, OnCommonClickListener listener) {
            Bundle bundle = new Bundle();
            bundle.putInt(TITLE, titleRes);
            bundle.putInt(CONTENT, contentRes);
            bundle.putString(VICE_CONTENT, viceContent);
            bundle.putInt(VICE_CONTENT_RES, viceContentRes);
            bundle.putInt(LEFT, leftRes);
            bundle.putInt(RIGHT, rightRes);
            bundle.putInt(CB_RES, cbRes);
            bundle.putBoolean(IS_SHOW_CB, isShowCb);
            bundle.putBoolean(IS_CB_CHECKED, isCbChecked);
            bundle.putBoolean(AUTO_DISMISS, autoDismiss);
            bundle.putBoolean(CAN_TOUCH_OUTSIDE, canTouchOutside);
            CommonDialog commonDialog = new CommonDialog();
            commonDialog.setArguments(bundle);
            commonDialog.setOnClickListener(listener);
            commonDialog.setActivity(activity);
            commonDialog.setTag(tag);
            return commonDialog;
        }
    }
}
