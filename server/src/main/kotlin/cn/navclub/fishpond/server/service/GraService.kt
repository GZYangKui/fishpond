package cn.navclub.fishpond.server.service

import cn.navclub.fishpond.protocol.api.CommonResult
import cn.navclub.fishpond.server.service.impl.KaptServiceImpl
import io.vertx.core.Vertx
import io.vertx.core.json.JsonObject

interface GraService {
    /**
     * 生成验证码
     */
    suspend fun code(): CommonResult<JsonObject>

    /**
     * 解码加密数据
     */
    fun decode(str: String): String


    companion object {

        fun create(vertx: Vertx): GraService {
            return KaptServiceImpl(vertx)
        }
    }
}