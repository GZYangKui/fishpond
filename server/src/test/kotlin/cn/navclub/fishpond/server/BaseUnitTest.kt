package cn.navclub.fishpond.server

import cn.navclub.fishpond.core.config.Constant
import cn.navclub.fishpond.core.util.StrUtil
import io.vertx.core.Future
import io.vertx.core.Promise
import io.vertx.core.Vertx
import io.vertx.core.buffer.Buffer
import io.vertx.core.json.JsonObject
import io.vertx.core.net.NetSocket
import io.vertx.ext.web.client.HttpResponse
import io.vertx.ext.web.client.WebClient
import io.vertx.junit5.VertxExtension
import io.vertx.junit5.VertxTestContext
import io.vertx.kotlin.coroutines.await
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(VertxExtension::class)
open class BaseUnitTest {

     val host: String = "127.0.0.1"
     val httPort: Int = 10000

    fun createNetClient(vertx: Vertx, ctx: VertxTestContext): Future<NetSocket> {
        val promise = Promise.promise<NetSocket>()
        vertx.createNetClient().connect(9000, "127.0.0.1") {
            if (it.failed()) {
                promise.fail(it.cause())
                ctx.failNow(it.cause())
            } else {
                promise.complete(it.result())
            }
        }
        return promise.future();
    }

    suspend fun login(vertx: Vertx, ctx: VertxTestContext): String {
        val json = JsonObject()
        json.put(Constant.USERNAME, 1005)
        json.put(Constant.PASSWORD, StrUtil.md5Str("123456"))
        val client = WebClient.create(vertx)
        val request = client.post(httPort, host, "/api/user/login")
        val resp = request.sendBuffer(json.toBuffer()).await()
        val data = this.httpRespCheck<JsonObject>(ctx, resp)
        return data!!.getString(Constant.SESSION_ID)
    }

    protected fun <T> httpRespCheck(ctx: VertxTestContext, resp: HttpResponse<Buffer>): T? {
        if (resp.statusCode() != 200) {
            ctx.failNow("HTTP 响应状态吗:${resp.statusCode()}")
            return null
        }
        val json = resp.bodyAsJsonObject()
        if (json.getInteger(Constant.CODE) != 200) {
            ctx.failNow(json.getString(Constant.MESSAGE))
            return null
        }
        return (json.getValue(Constant.DATA)) as T
    }

}