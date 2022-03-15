package cn.navclub.fishpond.server.util

import cn.navclub.fishpond.core.config.Constant
import cn.navclub.fishpond.protocol.api.APIECode
import cn.navclub.fishpond.protocol.api.CommonResult
import cn.navclub.fishpond.server.internal.ITModel
import cn.navclub.fishpond.server.internal.ITResult
import io.vertx.core.Vertx
import io.vertx.core.json.JsonObject
import io.vertx.ext.web.RoutingContext
import io.vertx.kotlin.coroutines.await
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

open class CoroutineUtil {
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

        @OptIn(DelicateCoroutinesApi::class)
        fun launch(handle: suspend () -> Unit) {
            GlobalScope.launch {
                handle.invoke()
            }
        }

        suspend fun <T, R> requestEB(vertx: Vertx, address: String, model: ITModel<T>, clazz: Class<R>): ITResult<R> {
            try {
                val data = model.toJson()
                val json = vertx.eventBus().request<JsonObject>(address, data).await().body()
                val t = json.getValue(Constant.DATA)
                val tt: R = if (t is JsonObject) {
                    t.mapTo(clazz)
                } else {
                    t as R
                }
                return ITResult<R>(tt, json.getInteger(Constant.CODE), json.getString(Constant.MESSAGE))
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return ITResult.fail("EventBus执行错误!")
        }
    }

}