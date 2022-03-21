package cn.navclub.fishpond.server.service

import cn.navclub.fishpond.server.util.DBUtil
import io.vertx.kotlin.coroutines.await
import io.vertx.sqlclient.SqlConnection
import io.vertx.sqlclient.Transaction

open class SimpleVXRepository {

    suspend fun <T> transaction(handler: suspend (con: SqlConnection, ctx: Transaction) -> T): T {
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