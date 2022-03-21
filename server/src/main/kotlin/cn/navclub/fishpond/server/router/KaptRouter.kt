package cn.navclub.fishpond.server.router

import cn.navclub.fishpond.server.HRouter
import cn.navclub.fishpond.server.service.GraService
import cn.navclub.fishpond.server.util.CoroutineUtil
import io.vertx.core.Vertx
import io.vertx.ext.web.Router

class KaptRouter(vertx: Vertx) : HRouter(vertx) {
    override fun create(router: Router) {
        router.get("/code").handler {
            CoroutineUtil.restCoroutine(it) { GraService.create(vertx).code() }
        }
    }
}