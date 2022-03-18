package cn.navclub.fishpond.server.security

import cn.navclub.fishpond.core.config.Constant.SESSION_ID
import cn.navclub.fishpond.core.config.Constant.SKIP
import cn.navclub.fishpond.core.util.StrUtil
import cn.navclub.fishpond.protocol.api.APIECode
import cn.navclub.fishpond.protocol.api.CommonResult
import cn.navclub.fishpond.server.internal.ITCode
import cn.navclub.fishpond.server.internal.ITModel
import cn.navclub.fishpond.server.model.FPSession
import cn.navclub.fishpond.server.node.SessionVerticle
import cn.navclub.fishpond.server.pattern.PathMatter
import cn.navclub.fishpond.server.util.CoroutineUtil
import io.vertx.core.Handler
import io.vertx.core.Vertx
import io.vertx.core.json.JsonObject
import io.vertx.ext.web.RoutingContext


class FPSHandler private constructor(private val vertx: Vertx, private val config: JsonObject) :
    Handler<RoutingContext> {

    private val pathMatter: PathMatter = PathMatter()
    private val list: List<String> = config.getJsonArray(SKIP).map((Any::toString))

    override fun handle(event: RoutingContext) {
        CoroutineUtil.launch({ checkSession(event) }) {
            event.json(CommonResult.fail<Any>(APIECode.SERVER_ERROR))
            it.printStackTrace()
        }
    }

    private suspend fun checkSession(event: RoutingContext) {
        var pass = false
        val path = event.request().path()
        for (pattern in list) {
            pass = pathMatter.matcher(path, pattern, true)
            if (pass) {
                break
            }
        }
        val sessionId = event.request().getHeader(SESSION_ID)

        //如果不是跳过认证资源及会话字段不为空则通过EventBus校验会话真实性
        if (!pass && !StrUtil.isEmpty(sessionId)) {
            val model = ITModel(sessionId, ITCode.CHECK_SESSION)

            val result = CoroutineUtil.requestEB(
                vertx,
                SessionVerticle::class.java.name,
                model,
                FPSession::class.java
            )
            //认证成功
            pass = result.success()
            if (pass) {
                event.setUser(FPUser(result.data))
            }
        }

        if (!pass) {
            event.json(CommonResult.fail<Any>(APIECode.FORBIDDEN))
        } else {
            event.next()
        }
    }

    companion object {
        fun create(vertx: Vertx, config: JsonObject): FPSHandler {
            return FPSHandler(vertx, config)
        }
    }
}