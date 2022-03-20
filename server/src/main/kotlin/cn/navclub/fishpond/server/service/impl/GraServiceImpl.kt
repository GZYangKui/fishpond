package cn.navclub.fishpond.server.service.impl

import cn.navclub.fishpond.core.config.Constant
import cn.navclub.fishpond.core.util.ASEUtil
import cn.navclub.fishpond.protocol.api.CommonResult
import cn.navclub.fishpond.server.service.GraService
import cn.navclub.fishpond.server.util.KaptUtil
import io.vertx.core.Vertx
import io.vertx.core.json.JsonObject
import io.vertx.kotlin.coroutines.await


class GraServiceImpl(private val vertx: Vertx) : GraService {
    private val key: String = "abcdefghikj@@@@#abcdefghikj@@@@#"

    override suspend fun code(): CommonResult<JsonObject> {
        //生成10-20之间随机数
        val rs = 10 + (Math.random() * 20).toInt()
        val a = rs shr 2
        val b = rs - a

        val base64Str = KaptUtil.create("$a+$b=?", 10).await()
        //设置过期时间为3分钟
        val expire = System.currentTimeMillis() + 3 * 60 * 1000
        val uuid = ASEUtil.encrypt("$rs/$expire", key)

        val json = JsonObject()

        json.put(Constant.UUID, uuid)
        json.put(Constant.IMG, base64Str)

        return CommonResult.success(json)
    }

    override fun decode(str: String): String {
        return ASEUtil.decrypt(str, key)
    }

}