package com.zxycloud.zszw;

import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.math.BigInteger;
import java.security.MessageDigest;
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

    @Test
    public void judge() {
        String str = "qwe!@#1";
        String regExp = "^(?![a-zA-Z]+$)(?![A-Z0-9]+$)(?![A-Z\\\\W_!@#$%^&*`~()-+=.,;<>:]+$)(?![a-z0-9]+$)(?![a-z\\\\W_!@#$%^&*`~()-+=.,;<>:]+$)(?![0-9\\\\W_!@#$%^&*`~()-+=.,;<>:]+$)[a-zA-Z0-9\\\\W_!@#$%^&*`~()-+=.,;<>:]{8,15}$";
        Pattern p = Pattern.compile(regExp);
        Matcher m = p.matcher(str);
        System.out.println(m.matches());
    }

    public void booleanTest(){
        Boolean.valueOf("");
    }

    @Test
    public void test(){
        String a="0-未知设备类型";
        System.out.println(a.substring(a.indexOf("-") + 1));
    }

    @Test
    public void subString(){
        String a="123456:123456";
        System.out.println(a.substring(0,a.indexOf(":")));
    }

    @Test
    public void urlJudge(){
        String regExp = "^(?=^.{3,255}$)[a-zA-Z0-9][-a-zA-Z0-9]{0,62}(\\.[a-zA-Z0-9][-a-zA-Z0-9]{0,62})+$";
        Pattern p = Pattern.compile(regExp);
        CharSequence str = null;
        Matcher m = p.matcher(str);
        System.out.println(m.matches());;
    }

    @Test
    public void getMd5() {
        File file = new File("F:\\Projects\\hzyzs\\app\\build\\outputs\\apk\\_hzyzs_\\debug\\app-_hzyzs_-debug.apk");
        MessageDigest digest = null;
        FileInputStream fis = null;
        byte[] buffer = new byte[1024];

        try {
            if (!file.isFile()) {
            }

            digest = MessageDigest.getInstance("MD5");
            fis = new FileInputStream(file);

            while (true) {
                int len;
                if ((len = fis.read(buffer, 0, 1024)) == -1) {
                    fis.close();
                    break;
                }

                digest.update(buffer, 0, len);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        BigInteger var5 = new BigInteger(1, digest.digest());
        System.out.println(String.format("%1$032x", new Object[]{var5}));
    }

}