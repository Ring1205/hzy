package com.zxycloud.startup;

import org.junit.Test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }

    /**
     * 正则表达式匹配“大写字母，小写字母，数字，特殊字符”四项中的至少三项
     * 特使字符：_!@#$%^&*`~()-+=.,;<>:
     */
    @Test
    public void passwordTest() {
        String str = "123456a,";
        String pattern = "^(?![a-zA-Z]+$)(?![A-Z0-9]+$)(?![A-Z\\W_!@#$%^&*`~()-+=.,;<>:]+$)(?![a-z0-9]+$)(?![a-z\\W_!@#$%^&*`~()-+=.,;<>:]+$)(?![0-9\\W_!@#$%^&*`~()-+=.,;<>:]+$)[a-zA-Z0-9\\W_!@#$%^&*`~()-+=.,;<>:]{6,15}$";

        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher(str);
        System.out.println(m.matches());
    }
}