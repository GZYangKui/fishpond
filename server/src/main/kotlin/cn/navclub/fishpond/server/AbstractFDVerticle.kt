package cn.navclub.fishpond.server


import cn.navclub.fishpond.core.config.Constant
import cn.navclub.fishpond.core.config.Constant.*
import cn.navclub.fishpond.protocol.api.APIECode
import cn.navclub.fishpond.server.internal.ITCode
import cn.navclub.fishpond.server.internal.ITModel
import cn.navclub.fishpond.server.internal.ITResult
import io.vertx.core.json.Json
import io.vertx.core.json.JsonObject
import io.vertx.kotlin.coroutines.CoroutineVerticle
import io.vertx.kotlin.coroutines.await
import kotlinx.coroutines.launch

abstract class AbstractFDVerticle<T> : CoroutineVerticle() {

    protected fun consumerEB() {
        val that = this
        val ebName = this::class.java.name
        vertx.eventBus().consumer<JsonObject>(ebName) {
            launch {
                val json = it.body()
                val data = json.getValue(DATA)
                val code = ITCode.valueOf(json.getString(Constant.CODE))
                try {
                    //响应客户端
                    it.reply(that.onMessage(code, data))
                } catch (e: Exception) {
                    e.printStackTrace()
                    it.reply(ITResult.fail<Any>("服务器错误").toJson())
                }
            }
        }
        println("$ebName 成功注册在EventBus总线上!")
    }

    protected suspend fun <T, R> requestEB(address: String, model: ITModel<T>, clazz: Class<R>): ITResult<R> {
        try {
            val data = model.toJson()
            val json = vertx.eventBus().request<JsonObject>(address, data).await().body()
            val t = json.getValue(DATA)
            val tt: R = if (t is JsonObject) {
                t.mapTo(clazz)
            } else {
                t as R
            }
            return ITResult<R>(tt, json.getInteger(CODE), json.getString(MESSAGE))
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return ITResult.fail("EventBus执行错误!")
    }


    protected open suspend fun onMessage(code: ITCode, data: Any): Any {
        return JsonObject()
    }
}