package cn.navclub.fishpond.protocol.util;

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
}
