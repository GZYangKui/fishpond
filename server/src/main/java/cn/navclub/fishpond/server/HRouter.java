package cn.navclub.fishpond.server;

import io.vertx.core.Vertx;
import io.vertx.ext.web.Router;

public abstract class HRouter {
    private final Vertx vertx;
    private final Router router;

    public HRouter(Vertx vertx) {
        this.vertx = vertx;
        this.router = Router.router(vertx);
        this.create(router);
    }

    protected abstract void create(Router router);

    public Vertx getVertx() {
        return vertx;
    }

    public Router getRouter() {
        return router;
    }
}
