package cn.navclub.fishpond.server.node

import cn.navclub.fishpond.core.config.Constant.*
import cn.navclub.fishpond.protocol.enums.MessageT
import cn.navclub.fishpond.protocol.enums.ServiceCode
import cn.navclub.fishpond.protocol.impl.DefaultDecoder
import cn.navclub.fishpond.protocol.model.TProMessage
import cn.navclub.fishpond.server.AbstractFDVerticle
import cn.navclub.fishpond.core.config.SysProperty
import cn.navclub.fishpond.core.config.SysProperty.SYS_ID
import cn.navclub.fishpond.core.util.StrUtil
import cn.navclub.fishpond.protocol.util.TProUtil
import cn.navclub.fishpond.protocol.enums.ContentType
import cn.navclub.fishpond.server.internal.ITCode
import cn.navclub.fishpond.server.internal.ITModel
import cn.navclub.fishpond.server.internal.ITResult
import com.google.common.collect.BiMap
import com.google.common.collect.HashBiMap
import io.vertx.core.json.JsonArray
import io.vertx.core.json.JsonObject
import io.vertx.core.net.NetSocket
import io.vertx.kotlin.coroutines.await
import kotlinx.coroutines.launch

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
        if (!tPro.serviceCode.isSsCheck && !this.checkSession(socket, tPro)) {
            return
        }
        //TCP连接注册
        if (tPro.serviceCode == ServiceCode.TCP_REGISTER) {
            this.tcpRegister(tPro, socket)
        }
        //群发消息
        if (tPro.serviceCode == ServiceCode.GROUP_MESSAGE) {
            this.idSocketMap.inverse().keys.forEach {
                //未注册用户不推送消息
                if (this.idSocketMap.inverse()[it] == null) {
                    return
                }
                socket.write(tPro.toMessage())
            }
        }
    }

    private suspend fun checkSession(socket: NetSocket, tPro: TProMessage): Boolean {
        var success = false
        val sessionId = this.idSocketMap.inverse()[socket]
        if (sessionId != null) {
            val model = ITModel(sessionId, ITCode.CHECK_SESSION)
            //检查TCP是否注册
            val itResult = requestEB(
                SessionVerticle::class.java.name,
                model,
                ITResult<SessionVerticle.FPSession>().javaClass
            )
            success = itResult.success()
            if (success) {
                tPro.from = itResult.data.username
            }
        }
        //会话检查失败=>关闭连接
        if (!success) {
            this.idSocketMap.inverse().remove(socket)
        }
        return success
    }

    private suspend fun tcpRegister(tPro: TProMessage, socket: NetSocket) {
        val json = tPro.data.toJsonObject()
        val model = ITModel.create(ITCode.CHECK_SESSION, json.getString(SESSION_ID, ""))

        val result = this.requestEB(
            SessionVerticle::class.java.name,
            model,
            ITResult<SessionVerticle.FPSession>().javaClass
        )
        if (result.success()) {
            val sessionId = json.getString(SESSION_ID)
            if (!idSocketMap.containsKey(sessionId)) {
                idSocketMap.inverse()[socket] = json.getString(SESSION_ID)
            }
            TProUtil.feedback(socket, tPro, ITResult.success<Any>("注册成功").toJson())
        } else {
            //断开连接
            this.idSocketMap.inverse().remove(socket)
            //反馈添加结果
            TProUtil.feedback(socket, tPro, result.toJson())
        }
    }

    private fun hello(): TProMessage {
        val msg = TProMessage()

        msg.to = SYS_ID
        msg.from = SYS_ID
        msg.type = MessageT.JSON
        msg.uuid = StrUtil.uuid()
        msg.serviceCode = ServiceCode.GROUP_MESSAGE

        val item = JsonObject()

        item.put(TYPE, ContentType.PLAIN_TEXT.value)
        item.put(MESSAGE, SysProperty.WELCOME)

        msg.data = JsonObject()
            .put(TIMESTAMP, System.currentTimeMillis())
            .put(ITEMS, JsonArray().add(item))
            .toBuffer()


        return msg
    }
}