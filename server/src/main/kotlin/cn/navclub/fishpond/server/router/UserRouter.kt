package cn.navclub.fishpond.server.router

import cn.navclub.fishpond.server.HRouter
import cn.navclub.fishpond.server.config.PASSWORD
import cn.navclub.fishpond.server.config.USERNAME
import cn.navclub.fishpond.server.service.UserService
import cn.navclub.fishpond.server.service.impl.UserServiceImpl
import cn.navclub.fishpond.server.util.StrUtil
import io.vertx.core.Vertx
import io.vertx.ext.web.Router
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class UserRouter(vertx: Vertx) : HRouter(vertx) {

    private lateinit var service: UserService

    @OptIn(DelicateCoroutinesApi::class)
    override fun create(router: Router) {
        this.service = UserServiceImpl(vertx)
        //用户登录
        router.post("/login").handler {
            val json = it.bodyAsJson
            val username = json.getString(USERNAME)
            val password = json.getString(PASSWORD)
            if (StrUtil.isEmpty(username) || StrUtil.isEmpty(password)) {
                paramValidFail("用户名/密码不能为空!", it)
                return@handler
            }

            GlobalScope.launch {
                it.json(service.login(username, password))
            }
        }
    }
}