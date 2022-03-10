package cn.navclub.fishpond.server.service

import cn.navclub.fishpond.protocol.api.CommonResult
import io.vertx.core.json.JsonObject

interface UserService {
    /**
     * 用户登录
     */
    suspend fun login(username:Int,password:String): CommonResult<JsonObject>
}