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
        //定时器检查会话是否超时
        this.vertx.setPeriodic(3 * 1000, this::checkSSExpire)
    }

    override suspend fun onMessage(code: ITCode, data: JsonObject): Any {
        if (code == ITCode.CREATE_SESSION) {
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
            this.calSSExpire(0),
            data.getLong(ID),
            data.getString(USERNAME),
            data.getString(NICKNAME),
            data.getString(AVATAR)
        )
        userMap[username] = fsSession
        return JsonObject.mapFrom(fsSession).put(SESSION_ID, uuid)
    }

    /**
     *
     * 计算会话过期时间
     *
     */
    private fun calSSExpire(timestamp: Long): Long {
        var start = timestamp
        if (timestamp == 0L) {
            start = System.currentTimeMillis()
        }
        val valid = config.getLong(EXPIRE) * 1000
        return start + valid
    }

    /**
     *
     * 检查当前用户列表中是否存在会话超时
     *
     */
    private fun checkSSExpire(timestamp: Long) {
        val list: MutableList<String> = arrayListOf()
        for (entry in this.userMap) {
            val session = entry.value
            if (session.expire < System.currentTimeMillis()) {
                list.add(entry.key)
            }
        }
        for (s in list) {
            //移出会话信息
            sessionMap.inverse().remove(s)
            //移出缓存用户信息
            userMap.remove(s)
            println("[$s]用户会话超时")
        }
    }

    data class FPSession(
        val expire: Long,
        val id: Long,
        val username: String,
        val nickname: String,
        val avatar: String
    )
}