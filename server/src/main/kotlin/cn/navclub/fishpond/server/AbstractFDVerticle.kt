package cn.navclub.fishpond.server


import io.vertx.core.json.JsonObject
import io.vertx.kotlin.coroutines.CoroutineVerticle
import kotlinx.coroutines.launch

abstract class AbstractFDVerticle<T> : CoroutineVerticle() {

    protected fun consumerEB() {
        val ebName = this::class.java.name
        vertx.eventBus().consumer<T>(ebName) {
            launch {
                onMessage(it.body())
            }
        }
        println("$ebName 成功注册在EventBus总线上!")
    }

    protected suspend fun onMessage(message: T): Any {
        return JsonObject()
    }
}