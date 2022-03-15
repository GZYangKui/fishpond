package cn.navclub.fishpond.server.node

import cn.navclub.fishpond.core.config.Constant.*
import cn.navclub.fishpond.server.AbstractFDVerticle
import cn.navclub.fishpond.server.router.UserRouter
import cn.navclub.fishpond.server.security.FPSHandler
import io.vertx.core.json.JsonObject
import io.vertx.ext.web.Router
import io.vertx.ext.web.handler.BodyHandler
import io.vertx.kotlin.coroutines.await

class WebVerticle : AbstractFDVerticle<JsonObject>() {
    override suspend fun start() {
        val json = config.getJsonObject(HTTP)

        val router = Router.router(vertx)

        //Post请求体数据转换
        router.route().handler(BodyHandler.create())
        //会话检查
        router.route().handler(FPSHandler.create(vertx, json))
        //用户路由
        router.route("/user/*").subRouter(UserRouter(vertx).router)


        vertx
            .createHttpServer()
            .requestHandler(router)
            .listen(json.getInteger(PORT))
            .await()
    }
}