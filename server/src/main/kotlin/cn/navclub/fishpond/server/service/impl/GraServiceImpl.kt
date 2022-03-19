package cn.navclub.fishpond.server.service.impl

import cn.navclub.fishpond.core.config.Constant
import cn.navclub.fishpond.core.util.StrUtil
import cn.navclub.fishpond.protocol.api.CommonResult
import cn.navclub.fishpond.server.service.GraService
import cn.navclub.fishpond.server.util.KaptUtil
import cn.navclub.fishpond.server.util.RedisUtil
import io.vertx.core.Vertx
import io.vertx.core.json.JsonObject
import io.vertx.kotlin.coroutines.await


class GraServiceImpl(private val vertx: Vertx) : GraService {
    override suspend fun code(): CommonResult<JsonObject> {
        //生成10-20之间随机数
        val rs = 10 + (Math.random() * 20).toInt()
        //计算第一个操作数
        val a = rs shr 2
        //计算第二个操作数
        val b = rs - a

        val base64Str = KaptUtil.create("$a+$b=?", 10).await()

        val uuid = StrUtil.uuid()

        RedisUtil.redisAPI().setex(getRedisKey(uuid), "30", rs.toString()).await()

        val json = JsonObject()
        json.put(Constant.UUID, uuid)
        json.put(Constant.IMG, base64Str)

        return CommonResult.success(json)
    }

    private fun getRedisKey(uuid: String): String {
        return "fishpond:code:$uuid";
    }
}