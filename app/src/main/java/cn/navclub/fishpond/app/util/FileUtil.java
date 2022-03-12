package cn.navclub.fishpond.app.util;

public class FileUtil {
    public static enum ResourcePos {
        LOCAL,
        JAR,
        NETWORK
    }

    public static ResourcePos getResourcePos(String url) {
        if (url.startsWith("http://") || url.startsWith("https://")) {
            return ResourcePos.NETWORK;
        }
        if (url.startsWith("jar://")) {
            return ResourcePos.JAR;
        }
        return ResourcePos.LOCAL;
    }
}
