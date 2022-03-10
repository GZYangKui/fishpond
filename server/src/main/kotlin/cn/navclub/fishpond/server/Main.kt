package cn.navclub.fishpond.server

import cn.navclub.fishpond.core.config.Constant.*
import cn.navclub.fishpond.server.util.DBUtil
import io.vertx.core.DeploymentOptions
import io.vertx.core.Vertx
import io.vertx.core.json.JsonObject
import io.vertx.kotlin.coroutines.await
import io.vertx.mysqlclient.MySQLConnectOptions
import io.vertx.sqlclient.PoolOptions

import kotlin.system.exitProcess

suspend fun createSharedDatabase(vertx: Vertx, sharedDBName: String) {

    val options = MySQLConnectOptions()

    options.user = "root"
    options.charset = "utf8"
    options.password = "root"
    options.database = "fishpond"

    val pOptions = PoolOptions()
    pOptions.maxSize = 20
    pOptions.isShared = true
    pOptions.name = sharedDBName

    DBUtil.createSharedDatabase(vertx, options, pOptions).await()
}

suspend fun main() {
    try {
        val vertx = Vertx.vertx()

        val shareDBName = "fishpond-pool"


        createSharedDatabase(vertx, shareDBName)

        //项目配置
        val config = JsonObject()
        config.put(EXPIRE, 30 * 60)
        config.put(TCP_PORT, 9000)
        config.put(HTTP_PORT, 10000)
        config.put(DB_POOL_NAME, shareDBName)

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