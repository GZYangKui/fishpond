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
import cn.navclub.fishpond.server.model.FPSession
import cn.navclub.fishpond.server.service.MessageService
import cn.navclub.fishpond.server.service.impl.MessageServiceImpl
import cn.navclub.fishpond.server.util.CoroutineUtil.Companion.requestEB
import com.google.common.collect.BiMap
import com.google.common.collect.HashBiMap
import io.vertx.core.json.JsonArray
import io.vertx.core.json.JsonObject
import io.vertx.core.net.NetServerOptions
import io.vertx.core.net.NetSocket
import io.vertx.kotlin.coroutines.await
import kotlinx.coroutines.launch

class TCPVerticle : AbstractFDVerticle<JsonObject>() {

    private lateinit var service: MessageService

    private val idSocketMap: BiMap<String, NetSocket> = HashBiMap.create()


    override suspend fun start() {

        this.consumerEB()
        this.service = MessageServiceImpl(vertx, config)

        val json = config.getJsonObject(TCP)
        val netServer = vertx.createNetServer(
            NetServerOptions()
                .setIdleTimeout(json.getInteger(IDLE))
                .setTcpKeepAlive(true)
        )
        netServer.connectHandler { socket ->
            this.idSocketMap[null] = socket

            val decoder = DefaultDecoder
                .create()
                .exHandler { socket.close() }
                .maxSize(json.getInteger(MAX_SIZE))
                .handler { launch { handleTPro(it, socket) } }

            socket.handler(decoder)
            //????????????->????????????
            socket.exceptionHandler { socket.close() }
            //????????????->????????????
            socket.closeHandler { this.idSocketMap.inverse().remove(socket) }
        }
        val port = json.getInteger(PORT)

        netServer.listen(port).await()

        logger.info("TCP server success listen in {} port.", port)
    }

    private suspend fun handleTPro(tPro: TProMessage, socket: NetSocket) {
        if (!tPro.serviceCode.isSsCheck && !this.checkSession(socket, tPro)) {
            return
        }
        //TCP????????????
        if (tPro.serviceCode == ServiceCode.TCP_REGISTER) {
            this.tcpRegister(tPro, socket)
        }
        //????????????
        if (tPro.serviceCode == ServiceCode.GROUP_MESSAGE) {
            this.idSocketMap.inverse().keys.forEach {
                //??????????????????????????????
                if (this.idSocketMap.inverse()[it] == null) {
                    return
                }
                it.write(tPro.toMessage())
            }
        }
        //??????????????????????????????
        if (tPro.serviceCode == ServiceCode.HEART_BEAT) {
            TProUtil.feedback(socket, tPro, null)
        } else {
            this.service.save(tPro)
        }
    }

    private suspend fun checkSession(socket: NetSocket, tPro: TProMessage): Boolean {
        var success = false
        val sessionId = this.idSocketMap.inverse()[socket]
        if (sessionId != null) {
            val model = ITModel(sessionId, ITCode.CHECK_SESSION)
            //??????TCP????????????
            val itResult = requestEB(
                vertx,
                SessionVerticle::class.java.name,
                model,
                FPSession::class.java
            )
            success = itResult.success()
            if (success) {
                tPro.from = itResult.data.username
            }
        }
        //??????????????????=>????????????
        if (!success) {
            socket.close()
        }
        return success
    }

    private suspend fun tcpRegister(tPro: TProMessage, socket: NetSocket) {
        val json = tPro.data.toJsonObject()
        val model = ITModel.create(ITCode.CHECK_SESSION, json.getString(SESSION_ID, ""))

        val result = requestEB(
            vertx,
            SessionVerticle::class.java.name,
            model,
            FPSession::class.java
        )
        if (result.success()) {
            val sessionId = json.getString(SESSION_ID)
            if (!idSocketMap.containsKey(sessionId)) {
                idSocketMap.inverse()[socket] = json.getString(SESSION_ID)
            }
            TProUtil.feedback(socket, tPro, ITResult.success<Any>("????????????").toJson())
        } else {
            //??????????????????
            TProUtil.feedback(socket, tPro, result.toJson())
            //Close socket connect
            socket.close()
        }
    }

    override suspend fun onMessage(code: ITCode, data: Any): Any? {
        return when (code) {
            //????????????????????????
            ITCode.REMOVE_TCP_SESSION -> (data as JsonArray).forEach { this.idSocketMap.remove(it as String)?.close() }
            else -> ITResult.success("")
        }
    }
}