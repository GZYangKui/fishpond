package cn.navclub.fishpond.server.util

import cn.navclub.fishpond.protocol.api.APIECode
import cn.navclub.fishpond.protocol.api.CommonResult
import io.vertx.ext.web.RoutingContext
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class CoroutineUtil {
    companion object {
        /**
         *
         * 封装 RestAPI 风格协程hook
         *
         */
        @OptIn(DelicateCoroutinesApi::class)
        fun <T> restCoroutine(ctx: RoutingContext, handler: suspend () -> CommonResult<T>) {
            GlobalScope.launch {
                val rs = try {
                    handler.invoke()
                } catch (e: Exception) {
                    e.printStackTrace()
                    CommonResult.fail(APIECode.SERVER_ERROR)
                }
                ctx.json(rs)
            }
        }
    }

}