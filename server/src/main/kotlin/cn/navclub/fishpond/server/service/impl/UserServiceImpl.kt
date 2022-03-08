package cn.navclub.fishpond.server.service.impl

import cn.navclub.fishpond.server.api.CommonResult
import cn.navclub.fishpond.server.service.UserService
import io.vertx.core.Vertx

class UserServiceImpl(private val vertx: Vertx) : UserService {
    override suspend fun login(username: String, password: String): CommonResult<String> {
        return CommonResult.success("")
    }

}