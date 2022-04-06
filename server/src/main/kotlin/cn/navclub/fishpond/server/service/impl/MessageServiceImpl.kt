package cn.navclub.fishpond.server.service.impl

import cn.navclub.fishpond.core.config.Constant
import cn.navclub.fishpond.protocol.enums.ServiceCode
import cn.navclub.fishpond.protocol.model.TProMessage
import cn.navclub.fishpond.server.service.BaseService
import cn.navclub.fishpond.server.service.MessageService
import io.vertx.core.Vertx
import io.vertx.core.json.JsonObject
import io.vertx.kotlin.coroutines.await
import io.vertx.sqlclient.templates.SqlTemplate
import java.time.LocalDateTime


class MessageServiceImpl(vertx: Vertx, config: JsonObject) : MessageService, BaseService(vertx, config) {
    override suspend fun save(tPro: TProMessage) {
        if (tPro.serviceCode == ServiceCode.TCP_REGISTER
            || tPro.serviceCode == ServiceCode.HEART_BEAT) {
            return
        }
        val sql =
            "INSERT INTO fp_message(id,type,sender,receiver,message,create_time,s_code) VALUES(#{id},#{type},#{sender},#{receiver},#{message},#{createTime},#{serviceCode})"
        this.transaction { con, _ ->
            val map: MutableMap<String, Any> = HashMap()

            map[Constant.SENDER] = tPro.from
            map[Constant.RECEIVER] = tPro.to
            map[Constant.TYPE] = tPro.type.`val`
            map[Constant.MESSAGE] = tPro.dataStr
            map[Constant.ID] = this.autoIncrementId()
            map[Constant.CREATE_TIME] = LocalDateTime.now()
            map[Constant.SERVICE_CODE] = tPro.serviceCode.value

            SqlTemplate.forUpdate(con, sql).execute(map).await()
        }
    }
}