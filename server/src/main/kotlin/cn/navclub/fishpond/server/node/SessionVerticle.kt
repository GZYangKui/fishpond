package cn.navclub.fishpond.server.node

import cn.navclub.fishpond.core.config.Constant.*
import cn.navclub.fishpond.core.util.StrUtil
import cn.navclub.fishpond.server.AbstractFDVerticle
import cn.navclub.fishpond.server.internal.ITCode
import cn.navclub.fishpond.server.internal.ITModel
import cn.navclub.fishpond.server.internal.ITResult
import cn.navclub.fishpond.server.model.FPSession
import cn.navclub.fishpond.server.util.CoroutineUtil.Companion.requestEB
import com.google.common.collect.BiMap
import com.google.common.collect.HashBiMap
import io.vertx.core.json.JsonObject
import kotlinx.coroutines.launch

/**
 *
 *
 * 管理全局会话信息
 *
 */
class SessionVerticle : AbstractFDVerticle<JsonObject>() {

    private val userMap: MutableMap<Int, FPSession> = HashMap()
    private val sessionMap: BiMap<String, Int> = HashBiMap.create()

    override suspend fun start() {
        this.consumerEB()
        //定时器检查会话是否超时
        this.vertx.setPeriodic(3 * 1000) {
            launch {
                checkSSExpire(it)
            }
        }
    }

    override suspend fun onMessage(code: ITCode, data: Any): Any? {
        return when (code) {
            ITCode.CREATE_SESSION -> this.flushSession(data as JsonObject)
            ITCode.CHECK_SESSION -> this.checkSession(data as String)
            else -> ITResult.success("").toJson()
        }
    }

    private fun checkSession(sessionId: String): JsonObject {
        val has = this.sessionMap.containsKey(sessionId)
        return if (has) {
            val username = this.sessionMap[sessionId]
            ITResult.success(this.userMap[username])
        } else {
            ITResult.fail("会话无效!")
        }.toJson()
    }

    private fun flushSession(data: JsonObject): JsonObject {
        val uuid = StrUtil.uuid()
        val username = data.getInteger(USERNAME)
        //移出先前记录
        userMap.remove(username)
        sessionMap.inverse().remove(username)
        //重新写入会话信息
        sessionMap[uuid] = username
        //缓存用户信息
        val fsSession = FPSession(
            data.getLong(ID),
            this.calSSExpire(0),
            data.getInteger(USERNAME),
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
        val json = config.getJsonObject(HTTP).getJsonObject(SESSION)
        return (start + json.getLong(EXPIRE) * 1000)
    }

    /**
     *
     * 检查当前用户列表中是否存在会话超时
     *
     */
    private suspend fun checkSSExpire(t: Long) {
        val list: MutableList<Int> = arrayListOf()
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
        val model = ITModel(list, ITCode.REMOVE_TCP_SESSION)
        //移出对应TCP连接
        requestEB(vertx, TCPVerticle::class.java.name, model, Unit.javaClass)
    }
}