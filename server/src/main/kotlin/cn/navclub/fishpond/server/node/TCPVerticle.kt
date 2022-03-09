package cn.navclub.fishpond.server.node

import cn.navclub.fishpond.core.config.Constant.TCP_PORT
import cn.navclub.fishpond.protocol.enums.MessageT
import cn.navclub.fishpond.protocol.enums.ServiceCode
import cn.navclub.fishpond.protocol.impl.DefaultDecoder
import cn.navclub.fishpond.protocol.model.TProMessage
import cn.navclub.fishpond.server.AbstractFDVerticle
import cn.navclub.fishpond.core.config.SysProperty
import io.vertx.core.buffer.Buffer
import io.vertx.core.json.JsonObject
import io.vertx.core.net.NetSocket
import io.vertx.kotlin.coroutines.await
import java.util.UUID

class TCPVerticle : AbstractFDVerticle<JsonObject>() {

    private val map: MutableMap<String, NetSocket> = HashMap()

    override suspend fun start() {
        val port = config.getInteger(TCP_PORT)
        val netServer = vertx.createNetServer()
        netServer.connectHandler { socket ->
            this.map[socket.writeHandlerID()] = socket
            val decoder = DefaultDecoder
                .create()
                .handler { pro ->
                    println(pro.toMessage())
                }
                .exHandler { t ->
                    this.map.remove(socket.writeHandlerID())
                    println("Socket data transform fail:${t.message}")
                }
            socket.handler(decoder)
            socket.write(hello().toMessage())
        }
        netServer.listen(port).await()
    }

    private fun hello(): TProMessage {
        val uuid = UUID.randomUUID().toString().replace("-", "")

        val msg = TProMessage()
        msg.uuid = uuid
        msg.type = MessageT.TEXT
        msg.userId = String(SysProperty.SYS_ID)
        msg.serviceCode = ServiceCode.SYSTEM_NOTIFY
        msg.targetId = String(SysProperty.SYS_ID)
        msg.data = Buffer.buffer(SysProperty.WELCOME)

        println(uuid)

        return msg
    }
}