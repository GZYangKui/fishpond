package cn.navclub.fishpond.server.router

import cn.navclub.fishpond.server.HRouter
import io.vertx.core.Vertx
import io.vertx.ext.web.Router

class GraRouter(vertx: Vertx) : HRouter(vertx) {
    override fun create(router: Router) {
        router.get("/code").handler {

        }
    }
}