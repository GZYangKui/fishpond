package cn.navclub.fishpond.server.service

import cn.navclub.fishpond.server.util.DBUtil
import io.vertx.core.Vertx
import io.vertx.kotlin.coroutines.await
import io.vertx.sqlclient.SqlConnection
import io.vertx.sqlclient.Transaction

abstract class BaseService(private val vertx: Vertx) {
    /**
     *
     * 获取配置信息
     *
     */
    protected fun <T> config(field: String? = null): T {
        val config = vertx.orCreateContext.config()
        return if (field != null) {
            config.getValue(field) as T
        } else {
            config as T
        }
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