package cn.navclub.fishpond.server.node

import cn.navclub.fishpond.core.config.Constant.*
import cn.navclub.fishpond.core.util.StrUtil
import cn.navclub.fishpond.server.AbstractFDVerticle
import cn.navclub.fishpond.server.internal.ITCode
import com.google.common.collect.BiMap
import com.google.common.collect.HashBiMap
import io.vertx.core.json.JsonObject

/**
 *
 *
 * 管理全局回话信息(TCP+HTTP)
 *
 */
class SessionVerticle : AbstractFDVerticle<JsonObject>() {

    private val userMap: MutableMap<String, FPSession> = HashMap()
    private val sessionMap: BiMap<String, String> = HashBiMap.create()

    override suspend fun start() {
        this.consumerEB()
    }

    override suspend fun onMessage(code: ITCode, data: JsonObject): Any {
        if (code == ITCode.UPDATE_SESSION) {
            return this.flushSession(data)
        }

        return JsonObject()
    }

    private fun flushSession(data: JsonObject): JsonObject {
        val uuid = StrUtil.uuid()
        val username = data.getString(USERNAME)
        //移出先前记录
        userMap.remove(username)
        sessionMap.inverse().remove(username)
        //重新写入会话信息
        sessionMap[uuid] = username
        //缓存用户信息
        val fsSession = FPSession(
            data.getLong(ID),
            data.getString(USERNAME),
            data.getString(NICKNAME),
            data.getString(AVATAR)
        )
        userMap[username] = fsSession
        return JsonObject.mapFrom(fsSession).put(SESSION_ID, uuid)
    }

    data class FPSession(val id: Long, val username: String, val nickname: String, val avatar: String)
}