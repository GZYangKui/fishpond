package cn.navclub.fishpond.core.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Locale;
import java.util.UUID;

/**
 * 字符串处理工具类
 */
public class
StrUtil {
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
}
