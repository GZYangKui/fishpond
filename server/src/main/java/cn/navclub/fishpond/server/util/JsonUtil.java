package cn.navclub.fishpond.server.util;

import io.vertx.core.json.JsonObject;

public class JsonUtil {
    public static <T> T jsonValue(JsonObject json, String... paths) {
        if (paths.length == 0) {
            throw new RuntimeException("Json search path not empty!");
        }
        T data = null;
        for (int i = 0; i < paths.length; i++) {
            var item = paths[i];
            if (i == paths.length - 1) {
                data = (T) json.getValue(item);
            } else {
                json = json.getJsonObject(item);
            }
        }
        return data;
    }
}
