package cn.navclub.fishpond.server

import io.vertx.core.Vertx
import io.vertx.kotlin.coroutines.await

suspend fun main() {
    val vertx = Vertx.vertx()
    try {
        vertx.deployVerticle("kt:cn.navclub.fishpond.server.TCPVerticle").await()
    } catch (e: java.lang.Exception) {
        println("verticle deploy fail:${e.message}")
    }
}