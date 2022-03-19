package cn.navclub.fishpond.server.service

import cn.navclub.fishpond.protocol.api.CommonResult
import io.vertx.core.json.JsonObject

interface UserService {
    /**
     * 用户登录
     */
    suspend fun login(username:Int,password:String): CommonResult<JsonObject>

    /**
     * 通过邮箱发送注册验证码
     */
    suspend fun VCode(uuid:String,code:String,email:String):CommonResult<String>
}