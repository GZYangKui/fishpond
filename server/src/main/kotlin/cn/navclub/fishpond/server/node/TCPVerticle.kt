package cn.navclub.fishpond.server.node

import cn.navclub.fishpond.core.config.Constant.*
import cn.navclub.fishpond.protocol.enums.MessageT
import cn.navclub.fishpond.protocol.enums.ServiceCode
import cn.navclub.fishpond.protocol.impl.DefaultDecoder
import cn.navclub.fishpond.protocol.model.TProMessage
import cn.navclub.fishpond.server.AbstractFDVerticle
import cn.navclub.fishpond.core.config.SysProperty
import cn.navclub.fishpond.core.config.SysProperty.SYS_ID
import cn.navclub.fishpond.protocol.util.TProUtil
import cn.navclub.fishpond.protocol.api.APIECode
import cn.navclub.fishpond.server.internal.ITCode
import cn.navclub.fishpond.server.internal.ITModel
import cn.navclub.fishpond.server.internal.ITResult
import com.google.common.collect.BiMap
import com.google.common.collect.HashBiMap
import io.vertx.core.buffer.Buffer
import io.vertx.core.json.JsonObject
import io.vertx.core.net.NetSocket
import io.vertx.kotlin.coroutines.await
import kotlinx.coroutines.launch
import java.util.UUID

class TCPVerticle : AbstractFDVerticle<JsonObject>() {

    private val idSocketMap: BiMap<String, NetSocket> = HashBiMap.create()

    override suspend fun start() {
        val port = config.getInteger(TCP_PORT)
        val netServer = vertx.createNetServer()
        netServer.connectHandler { socket ->
            this.idSocketMap[null] = socket
            val decoder = DefaultDecoder
                .create()
                .handler { launch { handleTPro(it, socket) } }
                .exHandler { t ->
                    this.idSocketMap.inverse().remove(socket)
                    println("Socket data transform fail:${t.message}")
                }
            socket.handler(decoder)
            socket.write(hello().toMessage())
        }
        netServer.listen(port).await()
    }

    private suspend fun handleTPro(tPro: TProMessage, socket: NetSocket) {
        //TCP连接注册
        if (tPro.serviceCode == ServiceCode.TCP_REGISTER) {
            this.tcpRegister(tPro, socket)
        }
    }

    private suspend fun tcpRegister(tPro: TProMessage, socket: NetSocket) {
        val json = tPro.data.toJsonObject()
        val model = ITModel.create(ITCode.CHECK_SESSION, json)

        val result = vertx
            .eventBus()
            .request<JsonObject>(SessionVerticle::class.java.name, model.toJson())
            .await()
            .body()
        val code = result.getInteger(CODE)
        if (code == APIECode.OK.code) {
            val sessionId = json.getString(SESSION_ID)
            if (!idSocketMap.containsKey(sessionId)) {
                idSocketMap.inverse()[socket] = json.getString(SESSION_ID)
            }
            TProUtil.feedback(socket, tPro, ITResult.success<Any>("注册成功").toJson())
        } else {
            //反馈添加结果
            TProUtil.feedback(socket, tPro, result)
        }
    }

    private fun hello(): TProMessage {
        val uuid = UUID.randomUUID().toString().replace("-", "")

        val msg = TProMessage()

        msg.uuid = uuid
        msg.userId = SYS_ID
        msg.type = MessageT.TEXT
        msg.serviceCode = ServiceCode.SYSTEM_NOTIFY
        msg.data = Buffer.buffer(SysProperty.WELCOME)


        return msg
    }
}