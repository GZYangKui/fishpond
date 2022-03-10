package cn.navclub.fishpond.app.socket;

import cn.navclub.fishpond.app.Main;
import cn.navclub.fishpond.protocol.impl.DefaultDecoder;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.net.NetSocket;

import java.util.ArrayList;
import java.util.List;


public class SocketHolder {
    private final int port;
    private final String host;
    private NetSocket socket;
    private final List<SocketHook> hooks;

    public SocketHolder(String host, int port) {
        this.host = host;
        this.port = port;
        this.hooks = new ArrayList<>();
    }

    public Future<Void> connect() {
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
                    for (SocketHook hook : this.hooks) {
                        try {
                            hook.onMessage(tPro);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                })
                .exHandler(Throwable::printStackTrace);
        socket.handler(decoder);
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
}
