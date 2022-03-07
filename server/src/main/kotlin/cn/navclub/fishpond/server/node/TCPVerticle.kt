package cn.navclub.fishpond.server.node

import cn.navclub.fishpond.protocol.enums.MessageT
import cn.navclub.fishpond.protocol.impl.DefaultDecoder
import cn.navclub.fishpond.protocol.model.DefaultPro
import cn.navclub.fishpond.server.AbstractFDVerticle
import cn.navclub.fishpond.server.config.SysProperty
import cn.navclub.fishpond.server.config.TCP_PORT
import io.vertx.core.buffer.Buffer
import io.vertx.core.json.JsonObject
import io.vertx.core.net.NetSocket
import io.vertx.kotlin.coroutines.await

class TCPVerticle : AbstractFDVerticle<JsonObject>() {

    private val map: MutableMap<String, NetSocket> = HashMap()

    override suspend fun start() {
        val port = config.getInteger(TCP_PORT)
        val netServer = vertx.createNetServer()
        netServer.connectHandler {
            this.map[it.writeHandlerID()] = it
            val decoder = DefaultDecoder.create()
                .handler { pro ->
                    println(pro.toMessage())
                }
                .exHandler { t ->
                    this.map.remove(it.writeHandlerID())
                    println("Socket data transform fail:${t.message}")
                }
            it.handler(decoder)
            it.write(hello().toMessage())
        }
        netServer.listen(port).await()
    }

    private fun hello(): DefaultPro {
        val pro = DefaultPro()
        pro.type = MessageT.TEXT
        pro.userId = String(SysProperty.SYS_ID)
        pro.data = Buffer.buffer("Welcome use fishpond application!")
        return pro
    }
}