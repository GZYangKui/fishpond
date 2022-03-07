package cn.navclub.fishpond.server.node

import cn.navclub.fishpond.server.AbstractFDVerticle
import cn.navclub.fishpond.server.config.TCP_PORT
import io.vertx.core.json.JsonObject
import io.vertx.kotlin.coroutines.await

class TCPVerticle : AbstractFDVerticle<JsonObject>() {
    override suspend fun start() {
        val port = config.getInteger(TCP_PORT)
        val netServer = vertx.createNetServer()
        netServer.connectHandler {
        }
        netServer.listen(port).await()

    }
}