package cn.navclub.fishpond.server.impl

import cn.navclub.fishpond.core.config.Constant
import cn.navclub.fishpond.core.config.Constant.CONTENT
import cn.navclub.fishpond.core.config.Constant.MESSAGE
import cn.navclub.fishpond.core.config.SysProperty
import cn.navclub.fishpond.core.util.StrUtil
import cn.navclub.fishpond.protocol.enums.MessageT
import cn.navclub.fishpond.protocol.enums.ServiceCode
import cn.navclub.fishpond.protocol.impl.DefaultDecoder
import cn.navclub.fishpond.protocol.model.TProMessage
import cn.navclub.fishpond.server.BaseUnitTest
import cn.navclub.fishpond.protocol.api.APIECode
import io.vertx.core.Vertx
import io.vertx.core.json.JsonObject
import io.vertx.junit5.VertxTestContext
import io.vertx.kotlin.coroutines.await
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.junit.jupiter.api.Test

class SocketTest : BaseUnitTest() {
    @Test
    fun test(vertx: Vertx, ctx: VertxTestContext) {
        val future = this.createNetClient(vertx, ctx)
        future.onSuccess {
            it.handler(
                DefaultDecoder
                    .create()
                    .handler { pro ->
                        println(pro.toString())
                        ctx.completeNow()
                    }
                    .exHandler { t ->
                        ctx.failNow(t)
                    }
            )
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    @Test
    fun `test tcp register session`(vertx: Vertx, ctx: VertxTestContext) {
        val that = this
        GlobalScope.launch {
            val client = that.createNetClient(vertx, ctx).await()
            val decoder = DefaultDecoder
                .create()
                .handler {
                    if (it.serviceCode == ServiceCode.OPERATE_FEEDBACK) {
                        val json = it.data.toJsonObject().getJsonObject(CONTENT)
                        val code = json.getInteger(Constant.CODE)
                        if (code == APIECode.OK.code) {
                            ctx.completeNow()
                        } else {
                            ctx.failNow(RuntimeException(json.getString(MESSAGE)))
                        }
                    }
                }
                .exHandler(ctx::failNow)
            client.handler(decoder)
            val json = JsonObject()
            json.put(Constant.SESSION_ID, "50E0D9BA8B8544BF81977DA847245EC5")
            val tPro = TProMessage()
            tPro.from = SysProperty.SYS_ID
            tPro.to = SysProperty.SYS_ID
            tPro.serviceCode = ServiceCode.TCP_REGISTER
            tPro.uuid = StrUtil.uuid()
            tPro.data = json.toBuffer()
            tPro.type = MessageT.JSON
            client.write(tPro.toMessage()).await()
        }
    }
}