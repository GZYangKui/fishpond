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

suspend fun createSharedDatabase(vertx: Vertx, dataSource: JsonObject) {

    val options = MySQLConnectOptions()

    options.user = dataSource.getString(USERNAME)
    options.charset = dataSource.getString(CHARSET)
    options.password = dataSource.getString(PASSWORD)
    options.database = dataSource.getString(DATABASE)


    val pool = dataSource.getJsonObject(POOL)

    val pOptions = PoolOptions()

    pOptions.name = pool.getString(NAME)
    pOptions.isShared = pool.getBoolean(SHARE)
    pOptions.maxSize = pool.getInteger(MAX_SIZE)

    DBUtil.createSharedDatabase(vertx, options, pOptions).await()
}

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
    return String.format("application%s.json", profile)
}

suspend fun main(args: Array<String>) {
    try {

        val vertx = Vertx.vertx()
        val profile = getProfile(args)
        val fileSystem = vertx.fileSystem()
        val config = fileSystem.readFile("config/$profile").await().toJsonObject()

        createSharedDatabase(vertx, config.getJsonObject(DATA_SOURCE))

        //部署配置
        val options = DeploymentOptions().setConfig(config)


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