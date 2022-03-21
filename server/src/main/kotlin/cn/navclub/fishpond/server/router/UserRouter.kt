package cn.navclub.fishpond.server.router

import cn.navclub.fishpond.core.config.Constant.*
import cn.navclub.fishpond.core.util.StrUtil
import cn.navclub.fishpond.server.HRouter
import cn.navclub.fishpond.server.service.UserService
import cn.navclub.fishpond.server.service.impl.UserServiceImpl
import cn.navclub.fishpond.server.util.CoroutineUtil
import io.vertx.core.Vertx
import io.vertx.core.json.JsonObject
import io.vertx.ext.web.Router

class UserRouter(vertx: Vertx) : HRouter(vertx) {

    private lateinit var service: UserService

    override fun create(router: Router) {
        this.service = UserServiceImpl(vertx)
        //用户登录
        router.post("/login").handler {
            val json = it.bodyAsJson
            val username = json.getInteger(USERNAME)
            val password = json.getString(PASSWORD)
            if (username == null || StrUtil.isEmpty(password)) {
                paramValidFail("用户名/密码不能为空!", it)
                return@handler
            }
            CoroutineUtil.restCoroutine(it) { service.login(username, password) }
        }

        //发送邮箱验证码
        router.post("/VCode").handler {
            val json = it.bodyAsJson
            val uuid = json.getString(UUID)
            val code = json.getString(CODE)
            val email = json.getString(EMAIL)
            if (StrUtil.isEmpty(uuid) || StrUtil.isEmpty(code) || StrUtil.isEmpty(email)) {
                paramValidFail("UUID/CODE/EMAIL不能为空!", it)
                return@handler
            }
            CoroutineUtil.restCoroutine(it) { service.VCode(uuid, code, email) }
        }
        
        //用户注册
        router.post("/register").handler {
            val json = it.bodyAsJson
            val code = json.getString(CODE)
            val email = json.getString(EMAIL)
            val pw = json.getString(PASSWORD)

            if (StrUtil.isEmpty(code) || StrUtil.isEmpty(email) || StrUtil.validMD5(pw)) {
                paramValidFail("CODE/EMAIL不能为空!", it)
                return@handler
            }
            CoroutineUtil.restCoroutine(it) { service.register(email, code, pw) }
        }
    }
}