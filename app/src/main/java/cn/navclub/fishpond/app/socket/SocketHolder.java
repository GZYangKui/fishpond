package cn.navclub.fishpond.app.socket;

import cn.navclub.fishpond.app.Main;
import cn.navclub.fishpond.app.http.HTTPUtil;
import cn.navclub.fishpond.core.config.Constant;
import cn.navclub.fishpond.core.config.SysProperty;
import cn.navclub.fishpond.core.util.StrUtil;
import cn.navclub.fishpond.protocol.api.APIECode;
import cn.navclub.fishpond.protocol.enums.MessageT;
import cn.navclub.fishpond.protocol.enums.ServiceCode;
import cn.navclub.fishpond.protocol.impl.DefaultDecoder;
import cn.navclub.fishpond.protocol.model.TProMessage;
import cn.navclub.fishpond.protocol.util.TProUtil;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonObject;
import io.vertx.core.net.NetSocket;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;


@Data
@Slf4j
public class SocketHolder {
    private int port;
    private String host;
    private NetSocket socket;
    private final List<SocketHook> hooks;
    //心跳定时器id
    private final AtomicLong plusTId;
    //Record re-connect timer id
    private final AtomicLong RCId;
    //记录当前心跳次数
    private final AtomicInteger plusNum;
    //Record current tcp connect is register
    private final AtomicBoolean register;
    //Record current tcp connect status
    private final AtomicReference<TCNStatus> tcnStatus;

    private SocketHolder(String host, int port) {
        this.host = host;
        this.port = port;
        this.hooks = new ArrayList<>();
        this.RCId = new AtomicLong(-1);
        this.plusTId = new AtomicLong(-1);
        this.plusNum = new AtomicInteger(0);
        this.register = new AtomicBoolean(false);
        this.tcnStatus = new AtomicReference<>(TCNStatus.TO_BE_CONNECTED);
    }

    public Future<Void> connect() {
        if (!(tcnStatus.get() == TCNStatus.TO_BE_CONNECTED || tcnStatus.get() == TCNStatus.CLOSED)) {
            return Future.failedFuture("Current tcp located connecting or connected!");
        }
        //Setting connecting status
        this.tcnChange(TCNStatus.CONNECTING);
        var socket = Main.vertx.createNetClient();
        var future = socket.connect(port, host);
        var promise = Promise.<Void>promise();
        future.onComplete(ar -> {
            if (ar.succeeded()) {
                promise.complete();
                this.initSocket(ar.result());
            } else {
                promise.fail(ar.cause());
                //Set to be connection status
                this.tcnChange(TCNStatus.TO_BE_CONNECTED);
            }
        });
        return promise.future();
    }

    private void initSocket(NetSocket socket) {
        this.socket = socket;
        var decoder = DefaultDecoder
                .create()
                .handler(tPro -> {
                    log.info("收到TCP消息:\n{}", tPro.toString());
                    //心跳次数置零
                    this.plusNum.set(0);
                    if (TProUtil.getFBCode(tPro) == ServiceCode.TCP_REGISTER) {
                        var json = tPro.toJson().getJsonObject(Constant.CONTENT);
                        var code = json.getInteger(Constant.CODE);
                        var register = (code == APIECode.OK.getCode());
                        //TCP register success start plus timer and set connected status
                        if (register) {
                            this.plus();
                            this.tcnChange(TCNStatus.CONNECTED);
                            //尝试清除重连定时器
                            Main.vertx.cancelTimer(this.RCId.getAndSet(-1));
                        }
                        this.register.set(register);
                    }
                    for (SocketHook hook : this.hooks) {
                        try {
                            if (tPro.getServiceCode() == ServiceCode.OPERATE_FEEDBACK) {
                                var fc = TProUtil.getFBCode(tPro);
                                //心跳反馈不做处理
                                if (fc == ServiceCode.HEART_BEAT) {
                                    return;
                                }
                                var data = tPro.toJson();
                                var content = data.getJsonObject(Constant.CONTENT);
                                var code = APIECode.getInstance(content.getInteger(Constant.CODE));
                                hook.feedback(fc, code, content, tPro);
                            } else {
                                hook.onMessage(tPro);
                            }
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
        this.TRegister();
    }


    private void TRegister() {
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
        log.info("<<<<<TCP连接关闭>>>>>>>");
        this.tcnChange(TCNStatus.CLOSED);
        //清除重连定时器
        Main.vertx.cancelTimer(this.RCId.getAndSet(-1));
        //清除心跳定时器
        Main.vertx.cancelTimer(this.plusTId.getAndSet(-1));
        this.checkRCondition();
    }

    private void exAfter(Throwable t) {
        log.error("TCP连接发生异常!", t);
        //Manual close socket connect
        this.socket.close();
    }

    private void tcnChange(TCNStatus status) {
        var oldStatus = this.tcnStatus.getAndSet(status);
        for (SocketHook hook : this.hooks) {
            try {
                hook.onTCNStatusChange(oldStatus, status);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Check whether use re-connection
     */
    private void checkRCondition() {
        //已经通过TCP注册才进行重连
        if (!this.register.get() && this.RCId.get() != -1) {
            log.info("<<<<当前TCP会话尚未注册,取消本次重连>>>>>");
            return;
        }
        var count = new AtomicInteger(1);
        //Try re-connect to tcp server
        this.RCId.set(Main.vertx.setPeriodic(3 * 1000, t -> {
            if (!this.register.get()) {
                log.info("Due to tcp connect not register so cancel re-connect operate.");
                Main.vertx.cancelTimer(this.RCId.get());
                return;
            }
            log.info("Try re-connect " + (count.getAndIncrement()) + " times.");
            this.connect();
        }));
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
