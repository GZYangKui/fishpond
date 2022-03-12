package cn.navclub.fishpond.app.socket;

import cn.navclub.fishpond.app.Main;
import cn.navclub.fishpond.app.http.HTTPUtil;
import cn.navclub.fishpond.core.config.Constant;
import cn.navclub.fishpond.core.config.SysProperty;
import cn.navclub.fishpond.core.util.StrUtil;
import cn.navclub.fishpond.protocol.enums.MessageT;
import cn.navclub.fishpond.protocol.enums.ServiceCode;
import cn.navclub.fishpond.protocol.impl.DefaultDecoder;
import cn.navclub.fishpond.protocol.model.TProMessage;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonObject;
import io.vertx.core.net.NetSocket;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;


@Data
public class SocketHolder {
    private int port;
    private String host;
    private NetSocket socket;
    private final List<SocketHook> hooks;
    //心跳定时器id
    private final AtomicLong plusTId;
    //记录当前心跳次数
    private final AtomicInteger plusNum;

    private SocketHolder(String host, int port) {
        this.host = host;
        this.port = port;
        this.hooks = new ArrayList<>();
        this.plusTId = new AtomicLong(-1);
        this.plusNum = new AtomicInteger(0);
    }

    public Future<Void> connect() {
        this.plusNum.set(0);
        this.plusTId.set(-1);
        var socket = Main.vertx.createNetClient();
        var future = socket.connect(port, host);
        var promise = Promise.<Void>promise();
        future.onComplete(ar -> {
            if (ar.succeeded()) {
                this.initSocket(ar.result());
                promise.complete();
            } else {
                promise.fail(ar.cause());
            }
        });
        return promise.future();
    }

    private void initSocket(NetSocket socket) {
        this.socket = socket;
        var decoder = DefaultDecoder
                .create()
                .handler(tPro -> {
                    //心跳次数置零
                    this.plusNum.set(0);
                    for (SocketHook hook : this.hooks) {
                        try {
                            hook.onMessage(tPro);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                })
                .exHandler(this::exAfter);
        socket.handler(decoder);
        socket.exceptionHandler(this::exAfter);
        socket.closeHandler(this::closeAfter);
        //注册TCP
        this.tcpRegister();
    }


    private void tcpRegister() {
        var tPro = new TProMessage();

        tPro.setType(MessageT.JSON);
        tPro.setUuid(StrUtil.uuid());
        tPro.setTo(SysProperty.SYS_ID);
        tPro.setFrom(SysProperty.SYS_ID);
        tPro.setServiceCode(ServiceCode.TCP_REGISTER);

        var json = new JsonObject();
        json.put(Constant.SESSION_ID, HTTPUtil.getSessionId());
        tPro.setData(json.toBuffer());

        this.write(tPro);
    }

    /**
     * 心跳
     */
    public void plus() {
        if (this.plusTId.get() != -1) {
            return;
        }
        var tPro = new TProMessage();
        tPro.setType(MessageT.TEXT);
        tPro.setTo(SysProperty.SYS_ID);
        tPro.setFrom(SysProperty.SYS_ID);
        tPro.setServiceCode(ServiceCode.HEART_BEAT);
        //定时发送心跳包
        this.plusTId.set(Main.vertx.setPeriodic(5 * 1000, t -> this.plus0(t, tPro)));
    }

    private void plus0(long t, TProMessage tPro) {
        //如果五次心跳均无响应则可认为连接已断开
        if (this.plusNum.get() > 5) {
            this.socket.close();
            Main.vertx.cancelTimer(this.plusTId.get());
            return;
        }
        this.write(tPro);
        //自增心跳次数
        this.plusNum.getAndAdd(1);
    }

    private void closeAfter(Void v) {
        System.out.println("TCP连接关闭");
    }

    private void exAfter(Throwable t) {
        System.out.println("TCP连接发生异常:" + t.getMessage());
    }

    public Future<Void> write(TProMessage message) {
        return this.socket.write(message.toMessage());
    }

    public void addHook(SocketHook hook) {
        if (this.hooks.contains(hook)) {
            return;
        }
        this.hooks.add(hook);
    }

    public void removeHook(SocketHook hook) {
        this.hooks.remove(hook);
    }

    private static SocketHolder socketHolder;

    public synchronized static SocketHolder getInstance() {
        if (socketHolder == null) {
            socketHolder = new SocketHolder("0.0.0.0", 0);
        }
        return socketHolder;
    }
}
