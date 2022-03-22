package cn.navclub.fishpond.server;

import cn.navclub.fishpond.protocol.api.APIECode;
import cn.navclub.fishpond.protocol.api.CommonResult;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;

public abstract class HRouter {
    private final Vertx vertx;
    private final Router router;
    private final JsonObject config;

    public HRouter(final Vertx vertx, final JsonObject config) {
        this.vertx = vertx;
        this.config = config;
        this.router = Router.router(vertx);
        this.create(router);
    }

    /**
     * 参数校验错误,响应客户端
     */
    protected void paramValidFail(String message, RoutingContext ctx) {
        ctx.json(CommonResult.fail(APIECode.PARAM_VALID_FAIL, message));
    }


    protected abstract void create(Router router);

    public Vertx getVertx() {
        return vertx;
    }

    public Router getRouter() {
        return router;
    }

    public JsonObject getConfig() {
        return config;
    }
}
