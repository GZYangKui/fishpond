package cn.navclub.fishpond.server.node

import cn.navclub.fishpond.server.AbstractFDVerticle
import io.vertx.core.json.JsonObject

/**
 *
 *
 * 管理全局回话信息(TCP+HTTP)
 *
 */
class SessionVerticle : AbstractFDVerticle<JsonObject>() {
    override suspend fun start() {
        this.consumerEB()
    }

    override suspend fun onMessage(message: JsonObject): Any {
        return JsonObject()
    }
}