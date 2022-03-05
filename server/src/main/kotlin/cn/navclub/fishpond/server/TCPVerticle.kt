package cn.navclub.fishpond.server

import io.vertx.kotlin.coroutines.CoroutineVerticle
import io.vertx.kotlin.coroutines.await
import kotlin.system.exitProcess

class TCPVerticle : CoroutineVerticle() {
    override suspend fun start() {
        val port = 9000
        val netServer = vertx.createNetServer()
        netServer.connectHandler {

        }
        try {
            netServer.listen().await()
        } catch (e: Exception) {
            println("服务器启动失败:${e.message}")
            exitProcess(1)
        }
        println("服务器成功监听在${port}端口")
    }
}