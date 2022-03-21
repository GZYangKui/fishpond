package cn.navclub.fishpond.server

import cn.navclub.fishpond.core.config.Constant.*
import cn.navclub.fishpond.core.util.NumUtil
import cn.navclub.fishpond.server.util.CoroutineUtil
import cn.navclub.fishpond.server.util.DBUtil
import cn.navclub.fishpond.server.util.RedisUtil
import io.vertx.core.DeploymentOptions
import io.vertx.core.Vertx
import io.vertx.kotlin.coroutines.await
import java.lang.management.ManagementFactory
import java.math.RoundingMode
import kotlin.system.exitProcess

/**
 * 根据程序启动时传入参数动态读取配置文件
 */
private fun getProfile(args: Array<String>): String {
    var profile = ""
    for (arg in args) {
        if (arg.startsWith(PROFILE)) {
            val index = arg.indexOf("=")
            if (index != -1 && index != arg.length - 1) {
                profile = "-" + arg.substring(index + 1)
                break
            }
        }
    }
    return profile
}

private fun calTime(t: Long, t1: Long): String {
    val bean = ManagementFactory.getRuntimeMXBean()
    val span = NumUtil.lDiv(
        t1 - t,
        1000,
        3,
        RoundingMode.UP
    )
    val startTime = NumUtil.lDiv(
        t - bean.startTime,
        1000,
        3,
        RoundingMode.UP
    )
    return "$span seconds (JVM running for $startTime)"
}

fun main(args: Array<String>) {
    CoroutineUtil.launch({
        val t = System.currentTimeMillis()

        val vertx = Vertx.vertx()
        val profile = getProfile(args)
        val fileSystem = vertx.fileSystem()
        val config = fileSystem.readFile("config/application${profile}.json").await().toJsonObject()

        //初始化redis
        RedisUtil.createRedisClient(vertx, config.getJsonObject(REDIS))
        //初始化数据源
        DBUtil.createSharedDatabase(vertx, config.getJsonObject(DATA_SOURCE))

        //部署配置
        val options = DeploymentOptions().setConfig(config)


        //部署Vertx节点
        vertx.deployVerticle("kt:cn.navclub.fishpond.server.node.TCPVerticle", options).await()
        vertx.deployVerticle("kt:cn.navclub.fishpond.server.node.WebVerticle", options).await()
        vertx.deployVerticle("kt:cn.navclub.fishpond.server.node.SessionVerticle", options).await()

        print("Started Fishpond in ${calTime(t, System.currentTimeMillis())}")
    }) {
        println("Application exception exit!")
        it.printStackTrace()
        exitProcess(1)
    }
    Thread.sleep(500)

}