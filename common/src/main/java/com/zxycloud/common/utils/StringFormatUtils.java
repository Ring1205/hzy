package com.zxycloud.common.utils;

import android.content.Context;
import android.support.annotation.StringRes;
import android.text.TextUtils;
import android.util.SparseArray;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 文本格式化类
 *
 * @author leiming
 * @date 2018/12/21.
 */
public class StringFormatUtils {

    /*-------------------------用于展示后台返回数据（若展示数据为空，则返回“-”，或设置默认项）------------------------*/

    /**
     * 判断传入文本是否为空，若为空，则显示"-"
     *
     * @param text 待判断文本
     * @return 返回字符串
     */
    public String getString(String text) {
        return getString(text, "-");
    }

    /**
     * 判断传入文本是否为空，若为空，则显示默认字符串
     *
     * @param text 待判断文本
     * @return 返回字符串
     */
    public String getString(String text, String defaultString) {
        return TextUtils.isEmpty(text) ? defaultString : text;
    }

    /**
     * 将int类型数据转换为String
     *
     * @param text int文本
     * @return String
     */
    public String getString(int text) {
        return getString(String.format(Locale.CHINA, "%d", text));
    }

    /**
     * 将long类型数据转换为String
     *
     * @param text long文本
     * @return String
     */
    public String getString(long text) {
        return getString(String.format(Locale.CHINA, "%d", text));
    }

    /**
     * 将double类型数据转换为String
     *
     * @param text double文本
     * @return String
     */
    public String getString(double text) {
        return getString(String.format(Locale.CHINA, "%s", text));
    }

    /**
     * 将double类型数据转换为String
     *
     * @param text double文本
     * @return String
     */
    public String getString(float text) {
        return getString(String.format(Locale.CHINA, "%s", text));
    }

    /*-------------------------用于根据业务逻辑显示或获取用户输入数据/APP显示的文本/封装的String------------------------*/

    /**
     * 获取stringId指向的文本
     *
     * @param context    上下文
     * @param resourceId 文本Id
     * @return 文本对应的字符串
     */
    public String getString(Context context, int resourceId) {
        return context.getResources().getString(resourceId);
    }

    /**
     * 获取stringId指向的文本
     *
     * @param context    上下文
     * @param resourceId 文本Id
     * @param spanValues 拼接的参数
     * @return 文本对应的字符串
     */
    public String getFormatString(Context context, int resourceId, Object... spanValues) {
        return String.format(context.getResources().getString(resourceId), spanValues);
    }

    /**
     * 字符串拼接
     *
     * @param interval 间隔符号
     * @param strings  待拼接字符串数组
     * @return 拼接后的字符串
     */
    public String append(String interval, String... strings) {
        if (CommonUtils.judgeListNull(strings) == 0) {
            return "";
        }
        boolean hasInterval = !isEmpty(interval);
        StringBuilder builder = new StringBuilder();
        builder.append(strings[0]);
        for (int i = 1; i < strings.length; i++) {
            if (hasInterval) {
                builder.append(interval);
            }
            builder.append(strings[i]);
        }
        return String.valueOf(builder);
    }

    /**
     * 字符串拼接
     *
     * @param strings 待拼接字符串数组
     * @return 拼接后的字符串
     */
    public String append(String... strings) {
        return append(null, strings);
    }

    /**
     * 是否为空
     *
     * @param s 待判断字符串
     * @return 是否为空
     */
    public boolean isEmpty(String s) {
        return TextUtils.isEmpty(s);
    }

    /**
     * 过滤空格、回车
     *
     * @param str 待处理字符串
     * @return 过滤后的结果
     */
    public String replaceBlank(String str) {
        String dest = "";
        if (str != null) {
            Pattern p = Pattern.compile("\\s*|\t|\r|\n");
            Matcher m = p.matcher(str);
            dest = m.replaceAll("");
        }
        return dest;
    }

    /*-------------------------控件文本提取------------------------*/

    /**
     * 获取String类型
     *
     * @param textText 文本控件，EditText/Button都是继承TextView，不加额外封装
     * @return 返回字符串
     */
    public String getString(TextView textText) {
        return textText.getText().toString().trim();
    }

    /**
     * 获取Integer类型
     *
     * @param textText 文本控件，EditText/Button都是继承TextView，不加额外封装
     * @return 返回数字
     */
    public Integer getInt(TextView textText) throws NumberFormatException {
        try {
            return Integer.valueOf(textText.getText().toString().trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /**
     * 获取Float类型
     *
     * @param textText 文本控件，EditText/Button都是继承TextView，不加额外封装
     * @return 返回Float
     */
    public Float getFloat(TextView textText) throws NumberFormatException {
        try {
            return Float.valueOf(textText.getText().toString().trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /**
     * 获取Double类型
     *
     * @param textText 文本控件，EditText/Button都是继承TextView，不加额外封装
     * @return 返回Double
     */
    public Double getDouble(TextView textText) throws NumberFormatException {
        try {
            return Double.valueOf(textText.getText().toString().trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /**
     * 获取Long类型
     *
     * @param textText 文本控件，EditText/Button都是继承TextView，不加额外封装
     * @return 返回Long
     */
    public Long getLong(TextView textText) throws NumberFormatException {
        try {
            return Long.valueOf(textText.getText().toString().trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /**
     * 获取Boolean类型
     *
     * @param textText 文本控件，EditText/Button都是继承TextView，不加额外封装
     * @return 返回Boolean
     */
    public Boolean getBoolean(TextView textText) throws ClassFormatError {
        return Boolean.valueOf(textText.getText().toString().trim());
    }

    /**
     * 格式化字符串，右对齐
     *
     * @param context   上下文
     * @param stringRes 字符串Res
     * @return 对齐后的字符串map
     */
    public SparseArray<String> formatStringLength(Context context, @StringRes int... stringRes) {
        List<Integer> lengths = new ArrayList<>();
        SparseArray<String> stringMap = new SparseArray<>();
        int maxLength = 0;
        int currentLength;
        String currentString;
        for (int string : stringRes) {
            currentString = getString(context, string);
            if (isContainChinese(currentString)) {
                char[] charArray = currentString.toCharArray();
                currentLength = 0;
                // 过滤到中文及中文字符
                for (char aCharArray : charArray) {
                    if (isChineseChar(aCharArray)) {
                        // 中文显示占四个字节
                        currentLength += 4;
                    } else {
                        currentLength++;
                    }
                }
            } else {
                currentLength = currentString.length();
            }
            if (currentLength > maxLength) {
                maxLength = currentLength;
            }
            lengths.add(currentLength);
        }
        if (maxLength > 0) {
            for (int i = 0; i < stringRes.length; i++) {
                int fillString = stringRes[i];
                stringMap.put(fillString, fillSpace(maxLength - lengths.get(i), getString(context, fillString)));
            }
        }
        return stringMap;
    }

    private String fillSpace(int count, String s) {
        if (count > 0) {
            StringBuilder stringBuilder = new StringBuilder();
            for (int i = 0; i < count; i++) {
                stringBuilder.append(" ");
            }
            stringBuilder.append(s);
            return stringBuilder.toString();
        }
        return s;
    }

    /**
     * 校验一个字符是否是汉字
     *
     * @param c 被校验的字符
     * @return true代表是汉字
     */
    public boolean isChineseChar(char c) {
        try {
            return String.valueOf(c).getBytes("UTF-8").length > 1;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 判断字符串中是否包含中文
     *
     * @param str 待校验字符串
     * @return 是否为中文
     * @warn 不能校验是否为中文标点符号
     */
    private boolean isContainChinese(String str) {
        Pattern p = Pattern.compile("[\u4e00-\u9fa5]");
        Matcher m = p.matcher(str);
        return m.find();
    }

}
