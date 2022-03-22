package cn.navclub.fishpond.server.service

import cn.navclub.fishpond.server.util.DBUtil
import cn.navclub.fishpond.server.util.JsonUtil
import io.vertx.core.Vertx
import io.vertx.core.json.JsonObject
import io.vertx.kotlin.coroutines.await
import io.vertx.sqlclient.SqlConnection
import io.vertx.sqlclient.Transaction

open class BaseService(vertx: Vertx, config: JsonObject) {
    protected val vertx: Vertx
    private val config: JsonObject

    init {
        this.vertx = vertx
        this.config = config
    }

    protected fun <T> getJsonFile(vararg paths: String): T {
        return JsonUtil.jsonValue(this.config, *paths)
    }

    /**
     *
     * 事物执行sql语句
     *
     */
    protected suspend fun <T> transaction(handler: suspend (con: SqlConnection, ctx: Transaction) -> T): T {
        val con = DBUtil.getConnect().await()
        var ctx: Transaction? = null
        try {
            ctx = con.begin().await()
            val rs = handler.invoke(con, ctx)
            ctx.commit()
            return rs
        } catch (e: Exception) {
            ctx?.rollback()
            throw RuntimeException(e)
        } finally {
            con.close()
        }
    }


}