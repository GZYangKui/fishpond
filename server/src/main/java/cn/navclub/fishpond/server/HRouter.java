package cn.navclub.fishpond.server;

import cn.navclub.fishpond.server.api.APIECode;
import cn.navclub.fishpond.server.api.CommonResult;
import io.vertx.core.Vertx;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;

public abstract class HRouter {
    private final Vertx vertx;
    private final Router router;

    public HRouter(Vertx vertx) {
        this.vertx = vertx;
        this.router = Router.router(vertx);
        this.create(router);
    }

    protected void paramValidFail(String message, RoutingContext ctx) {
        ctx.json(CommonResult.fail(APIECode.PARAM_VALID_FAIL, message).toJson());
    }


    protected abstract void create(Router router);

    public Vertx getVertx() {
        return vertx;
    }

    public Router getRouter() {
        return router;
    }
}
