package cn.navclub.fishpond.protocol;


import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;


public abstract class Protocol {
    /**
     * 将当前对象序列化为对应消息格式
     */
    public abstract Buffer toMessage();

    /**
     *
     * 将Data数据转换为字符串
     *
     */
    public abstract String getDataStr();

    /**
     *
     *  将Data转换为json对象
     *
     */
    public JsonObject toJson(){
        return new JsonObject();
    }

    /**
     *
     *
     * 将Data转换为json数组
     *
     */
    public JsonArray toJsonArray(){
        return new JsonArray();
    }
}
