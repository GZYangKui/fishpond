package cn.navclub.fishpond.protocol;

import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;


public abstract class Decoder<T extends Protocol> implements Handler<Buffer> {
    protected int maxSize;
    protected Handler<T> handler;
    protected Handler<Throwable> exHandler;

    /**
     * 设置数据handler,当数据可用时将会回调该handler
     */
    public final Decoder<T> handler(Handler<T> handler) {
        this.handler = handler;
        return this;
    }

    /**
     * 设置一场handler,当在转换过程发生异常,将会会掉该handler
     */
    public final Decoder<T> exHandler(Handler<Throwable> handler) {
        this.exHandler = handler;
        return this;
    }

    /**
     *
     * 设置当前解码器最大缓存数据(字节),操过该设定值将会清空缓存数据并抛出异常
     *
     */
    public final Decoder<T> maxSize(int maxSize){
        this.maxSize = maxSize;
        return this;
    }

}
