package cn.navclub.fishpond.server.router

import cn.navclub.fishpond.server.HRouter
import io.vertx.core.Vertx
import io.vertx.ext.web.Router

class UserRouter(vertx: Vertx) : HRouter(vertx) {

    override fun create(router: Router) {
        //用户登录
        router.post("/login").handler {

        }
    }
}