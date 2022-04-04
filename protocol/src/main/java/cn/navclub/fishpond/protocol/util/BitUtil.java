package cn.navclub.fishpond.protocol.util;


import java.util.Arrays;

public class BitUtil {
    /**
     * 将整数转换为字节数组
     */
    public static byte[] int2Byte4(int i) {
        byte[] targets = new byte[4];
        targets[3] = (byte) (i & 0xFF);
        targets[2] = (byte) (i >> 8 & 0xFF);
        targets[1] = (byte) (i >> 16 & 0xFF);
        targets[0] = (byte) (i >> 24 & 0xFF);
        return targets;
    }

    /**
     * byte转化成int
     */
    public static int byte2Int(byte[] src) {
        int value;
        value = (src[3] & 0xFF)
                | ((src[2] & 0xFF) << 8)
                | ((src[1] & 0xFF) << 16)
                | ((src[0] & 0xFF) << 24);
        return value;
    }

    /**
     * 将long型数据转换位字节数组
     */
    public static byte[] long2Byte(long value) {
        var array = new byte[8];
        for (var i = 0; i < array.length; i++) {
            array[i] = (byte) (value >> (i * 8) & 0xFF);
        }
        return array;
    }

    /**
     * 将字节数组转换为long数据
     */
    public static long byte2Long(byte[] array) {
        var value = 0L;
        var length = Math.min(8, array.length);
        for (int i = 0; i < length; i++) {
            value = value | ((long) (array[i] & 0xff) << (i * 8));
        }
        return value;
    }
}
