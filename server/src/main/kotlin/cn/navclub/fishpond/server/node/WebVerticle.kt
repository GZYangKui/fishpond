package cn.navclub.fishpond.server.node

import cn.navclub.fishpond.core.config.Constant.*
import cn.navclub.fishpond.server.AbstractFDVerticle
import cn.navclub.fishpond.server.router.FileRouter
import cn.navclub.fishpond.server.router.KaptRouter
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
        router.route().handler(BodyHandler.create().setUploadsDirectory(json.getString("uploadDir")))
        //会话检查
        router.route().handler(FPSHandler.create(vertx, json))
        //用户路由
        router.route("/api/user/*").subRouter(UserRouter(vertx, config).router)
        //图形相关借口
        router.route("/api/kapt/*").subRouter(KaptRouter(vertx, config).router)
        //文件相关接口
        router.route("/api/file/*").subRouter(FileRouter(vertx, config).router)

        val port = json.getInteger(PORT)

        vertx
            .createHttpServer()
            .requestHandler(router)
            .listen(port)
            .await()

        logger.info("Web server success listen in {} port.", port)
    }
}