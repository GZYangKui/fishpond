package cn.navclub.fishpond.core.util;

import java.util.Locale;
import java.util.UUID;

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
     *
     * 生成UUID字符串
     *
     */
    public static String uuid() {
        return UUID
                .randomUUID()
                .toString()
                .replaceAll("-", "")
                .toUpperCase(Locale.ROOT);
    }
}
