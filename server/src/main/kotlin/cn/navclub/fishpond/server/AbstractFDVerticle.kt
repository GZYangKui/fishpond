package cn.navclub.fishpond.server


import cn.navclub.fishpond.core.config.Constant.*
import cn.navclub.fishpond.server.internal.ITCode
import cn.navclub.fishpond.server.internal.ITResult
import io.vertx.core.json.JsonObject
import io.vertx.kotlin.coroutines.CoroutineVerticle
import kotlinx.coroutines.launch
import org.slf4j.Logger
import org.slf4j.LoggerFactory

abstract class AbstractFDVerticle<T> : CoroutineVerticle() {
    protected val logger:Logger = LoggerFactory.getLogger(this::class.java)

    protected fun consumerEB() {
        val that = this
        val ebName = this::class.java.name
        vertx.eventBus().consumer<JsonObject>(ebName) {
            launch {
                val json = it.body()
                val data = json.getValue(DATA)
                val code = ITCode.valueOf(json.getString(CODE))
                try {
                    var reply = that.onMessage(code, data)
                    //响应数据为null或者Unit响应成功
                    if (reply == null || reply is Unit) {
                        reply = ITResult.success("").toJson()
                    }
                    //响应客户端
                    it.reply(reply)
                } catch (e: Exception) {
                    e.printStackTrace()
                    it.reply(ITResult.fail<Any>("服务器错误").toJson())
                }
            }
        }
    }

    protected open suspend fun onMessage(code: ITCode, data: Any): Any? {
        return JsonObject()
    }
}