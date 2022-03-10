package cn.navclub.fishpond.server.service.impl

import cn.navclub.fishpond.core.config.Constant.*
import cn.navclub.fishpond.mapper.entity.FPUser
import cn.navclub.fishpond.mapper.entity.FPUserRowMapper
import cn.navclub.fishpond.server.api.CommonResult
import cn.navclub.fishpond.server.internal.ITCode
import cn.navclub.fishpond.server.internal.ITModel
import cn.navclub.fishpond.server.node.SessionVerticle
import cn.navclub.fishpond.server.service.UserService
import cn.navclub.fishpond.server.util.DBUtil
import io.vertx.core.Vertx
import io.vertx.core.json.JsonObject
import io.vertx.kotlin.coroutines.await

class
UserServiceImpl(private val vertx: Vertx) : UserService {
    override suspend fun login(username: String, password: String): CommonResult<JsonObject> {
        val sql = "SELECT * FROM fp_user WHERE #{username}"
        val optional = DBUtil.findOne(FPUserRowMapper.INSTANCE, sql, mapOf(Pair("username", username))).await()
        if (optional.isEmpty) {
            return CommonResult.fail("用户信息不存在!")
        }
        val user = optional.get()
        if (!user.password.equals(password)) {
            return CommonResult.fail("密码错误!")
        }
        return CommonResult.success(this.flushSession(user))
    }

    private suspend fun flushSession(user: FPUser): JsonObject {
        val json = JsonObject()

        json.put(ID, user.id)
        json.put(SEX, user.sex)
        json.put(NICKNAME, user.nickname)
        json.put(USERNAME, user.username)
        json.put(AVATAR, user.avatar ?: "")

        val model = ITModel.create(ITCode.CREATE_SESSION, json)
        val address = SessionVerticle::class.java.name
        //请求Session管理
        return vertx
            .eventBus()
            .request<JsonObject>(address, model.toJson())
            .await()
            .body()
    }

}