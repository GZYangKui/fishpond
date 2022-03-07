package cn.navclub.fishpond.server

import cn.navclub.fishpond.server.config.HTTP_PORT
import cn.navclub.fishpond.server.config.TCP_PORT
import io.vertx.core.DeploymentOptions
import io.vertx.core.Vertx
import io.vertx.core.json.JsonObject
import io.vertx.kotlin.coroutines.await
import kotlin.system.exitProcess

suspend fun main() {
    try {

        val vertx = Vertx.vertx()

        //项目配置
        val config = JsonObject()
        config.put(TCP_PORT, 9000)
        config.put(HTTP_PORT, 10000)

        //部署配置
        val options = DeploymentOptions()
        options.config = config

        //部署Vertx节点
        vertx.deployVerticle("kt:cn.navclub.fishpond.server.node.TCPVerticle", options).await()
        vertx.deployVerticle("kt:cn.navclub.fishpond.server.node.HTTPVerticle", options).await()
        vertx.deployVerticle("kt:cn.navclub.fishpond.server.node.SessionVerticle", options).await()

    } catch (e: java.lang.Exception) {
        println("verticle deploy fail:${e.message}")
        exitProcess(1)
    }
    print("Fishpond startup success!")
}