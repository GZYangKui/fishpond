package cn.navclub.fishpond.server


import cn.navclub.fishpond.core.config.Constant
import cn.navclub.fishpond.core.config.Constant.DATA
import cn.navclub.fishpond.server.internal.ITCode
import cn.navclub.fishpond.server.internal.ITResult
import io.vertx.core.json.JsonObject
import io.vertx.kotlin.coroutines.CoroutineVerticle
import kotlinx.coroutines.launch

abstract class AbstractFDVerticle<T> : CoroutineVerticle() {

    protected fun consumerEB() {
        val that = this
        val ebName = this::class.java.name
        vertx.eventBus().consumer<JsonObject>(ebName) {
            launch {
                val json = it.body()
                val data = json.getJsonObject(DATA)
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

    protected open suspend fun onMessage(code: ITCode, data: JsonObject): Any {
        return JsonObject()
    }
}