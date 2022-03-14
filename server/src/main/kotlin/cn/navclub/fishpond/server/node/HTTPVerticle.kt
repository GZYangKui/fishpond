package cn.navclub.fishpond.server.node

import cn.navclub.fishpond.core.config.Constant.*
import cn.navclub.fishpond.server.AbstractFDVerticle
import cn.navclub.fishpond.server.router.UserRouter
import io.vertx.core.json.JsonObject
import io.vertx.ext.web.Router
import io.vertx.ext.web.handler.BodyHandler
import io.vertx.kotlin.coroutines.await

class HTTPVerticle : AbstractFDVerticle<JsonObject>() {
    override suspend fun start() {
        val router = Router.router(vertx)

        router.route().handler(BodyHandler.create())
        //用户路由
        router.route("/user/*").subRouter(UserRouter(vertx).router)

        val json = config.getJsonObject(HTTP)

        vertx
            .createHttpServer()
            .requestHandler(router)
            .listen(json.getInteger(PORT))
            .await()
    }
}