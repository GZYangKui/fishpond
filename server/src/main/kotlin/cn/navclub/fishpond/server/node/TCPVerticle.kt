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

    private val idSocketMap: BiMap<String, NetSocket> = HashBiMap.create()

    override suspend fun start() {
        this.consumerEB()
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
            //发生异常->关闭连接
            socket.exceptionHandler { socket.close() }
            //连接关闭->移出连接
            socket.closeHandler { this.idSocketMap.inverse().remove(socket) }

            socket.write(TProUtil.hello().toMessage())
        }
        netServer.listen(json.getInteger(PORT)).await()
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
                it.write(tPro.toMessage())
            }
        }
        //响应客户端发送心跳包
        if (tPro.serviceCode == ServiceCode.HEART_BEAT) {
            TProUtil.feedback(socket, tPro, null)
        }
    }

    private suspend fun checkSession(socket: NetSocket, tPro: TProMessage): Boolean {
        var success = false
        val sessionId = this.idSocketMap.inverse()[socket]
        if (sessionId != null) {
            val model = ITModel(sessionId, ITCode.CHECK_SESSION)
            //检查TCP是否注册
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
        //会话检查失败=>关闭连接
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
            TProUtil.feedback(socket, tPro, ITResult.success<Any>("注册成功").toJson())
        } else {
            //反馈添加结果
            TProUtil.feedback(socket, tPro, result.toJson())
            //Close socket connect
            socket.close()
        }
    }

    override suspend fun onMessage(code: ITCode, data: Any): Any? {
        return when (code) {
            //批量移出会话列表
            ITCode.REMOVE_TCP_SESSION -> (data as JsonArray).forEach { this.idSocketMap.remove(it as String)?.close() }
            else -> ITResult.success("")
        }
    }
}