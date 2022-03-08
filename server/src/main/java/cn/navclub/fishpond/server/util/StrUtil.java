package cn.navclub.fishpond.server.util;

public class StrUtil {
    public static boolean isEmpty(String str) {
        if (str == null) {
            return true;
        }
        return str.trim().equals("");
    }
}
