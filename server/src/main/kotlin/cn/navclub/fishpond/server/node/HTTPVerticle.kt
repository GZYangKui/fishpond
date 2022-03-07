package cn.navclub.fishpond.server.node

import cn.navclub.fishpond.server.AbstractFDVerticle
import cn.navclub.fishpond.server.config.HTTP_PORT
import io.vertx.core.json.JsonObject
import io.vertx.ext.web.Router
import io.vertx.kotlin.coroutines.await

class HTTPVerticle : AbstractFDVerticle<JsonObject>() {
    override suspend fun start() {
        val router = Router.router(vertx)
        val port = config.getInteger(HTTP_PORT)
        vertx.createHttpServer().requestHandler(router).listen(port).await()
    }
}