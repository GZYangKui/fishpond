package cn.navclub.fishpond.server.service

import cn.navclub.fishpond.protocol.api.CommonResult
import cn.navclub.fishpond.server.service.impl.GraServiceImpl
import io.vertx.core.Vertx
import io.vertx.core.json.JsonObject

interface GraService {
    /**
     * 生成验证码
     */
    suspend fun code(): CommonResult<JsonObject>


    companion object {
        fun create(vertx: Vertx): GraService {
            return GraServiceImpl(vertx)
        }
    }
}