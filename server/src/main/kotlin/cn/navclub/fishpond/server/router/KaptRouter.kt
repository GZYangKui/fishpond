package cn.navclub.fishpond.server.router

import cn.navclub.fishpond.server.HRouter
import cn.navclub.fishpond.server.service.KaptService
import cn.navclub.fishpond.server.util.CoroutineUtil
import io.vertx.core.Vertx
import io.vertx.core.json.JsonObject
import io.vertx.ext.web.Router

class KaptRouter(vertx: Vertx, config: JsonObject) : HRouter(vertx, config) {
    override fun create(router: Router) {
        router.get("/code").handler {
            CoroutineUtil.restCoroutine(it) { KaptService.create(vertx).code() }
        }
    }
}