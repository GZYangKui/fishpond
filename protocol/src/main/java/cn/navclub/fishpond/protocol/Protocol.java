package cn.navclub.fishpond.protocol;


import io.vertx.core.buffer.Buffer;


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
}
