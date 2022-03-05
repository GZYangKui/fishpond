package cn.navclub.fishpond.protocol;

import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;


public abstract class Decoder<T extends Protocol> implements Handler<Buffer> {
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

}
