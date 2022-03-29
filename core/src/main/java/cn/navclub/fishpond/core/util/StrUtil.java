package cn.navclub.fishpond.core.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Locale;
import java.util.UUID;
import java.util.regex.Pattern;

/**
 * 字符串处理工具类
 */
public class StrUtil {
    public static boolean isEmpty(String str) {
        if (str == null) {
            return true;
        }
        return str.trim().equals("");
    }

    /**
     * 生成UUID字符串
     */
    public static String uuid() {
        return UUID
                .randomUUID()
                .toString()
                .replaceAll("-", "")
                .toUpperCase(Locale.ROOT);
    }

    /**
     * 将目标字符串转换为md5字符串
     */
    public static String md5Str(String text) {
        try {
            var md5 = MessageDigest.getInstance("md5");
            var bytes = md5.digest(text.getBytes(StandardCharsets.UTF_8));
            var chars = new char[]{
                    '0',
                    '1',
                    '2',
                    '3',
                    '4',
                    '5',
                    '6',
                    '7',
                    '8',
                    '9',
                    'A',
                    'B',
                    'C',
                    'D',
                    'E',
                    'F'
            };
            var sb = new StringBuilder();
            for (byte b : bytes) {
                sb.append(chars[(b >> 4) & 15]);
                sb.append(chars[b & 15]);
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 生成随机字符串
     */
    public static String rdStr(int len) {
        var arr = new byte[len];
        for (int i = 0; i < len; i++) {
            var j = 97 + (byte) (Math.random() * 26);
            arr[i] = (byte) j;
        }
        return new String(arr);
    }

    /**
     * 判断url或者文件名是否图片文件
     */
    public static boolean isPicture(String url) {
        return url.matches(".+((\\.JPEG|\\.jpeg|\\.JPG|\\.jpg|\\.png|\\.PNG)(\\?)?(.)*)$");
    }

    public static boolean validMD5(String str) {
        return !isEmpty(str) && str.matches("[a-fA-F\\d]{32}");
    }
}
