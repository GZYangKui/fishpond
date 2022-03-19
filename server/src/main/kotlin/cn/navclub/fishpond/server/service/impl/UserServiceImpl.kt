package cn.navclub.fishpond.server.service.impl

import cn.navclub.fishpond.core.config.Constant.*
import cn.navclub.fishpond.mapper.entity.FPUser
import cn.navclub.fishpond.mapper.entity.FPUserRowMapper
import cn.navclub.fishpond.protocol.api.CommonResult
import cn.navclub.fishpond.server.internal.ITCode
import cn.navclub.fishpond.server.internal.ITModel
import cn.navclub.fishpond.server.node.SessionVerticle
import cn.navclub.fishpond.server.service.GraService
import cn.navclub.fishpond.server.service.UserService
import cn.navclub.fishpond.server.util.DBUtil
import cn.navclub.fishpond.server.util.RedisUtil
import io.vertx.core.Vertx
import io.vertx.core.json.JsonObject
import io.vertx.ext.mail.MailClient
import io.vertx.ext.mail.MailConfig
import io.vertx.ext.mail.MailMessage
import io.vertx.kotlin.coroutines.await
import java.util.Random

class
UserServiceImpl(private val vertx: Vertx) : UserService {
    private val mailClient: MailClient


    init {

        val emc = vertx.orCreateContext.config().getJsonObject(EMAIL)

        val config = MailConfig()

        config.isKeepAlive = true
        config.port = emc.getInteger(PORT)
        config.hostname = emc.getString(HOST)
        config.username = emc.getString(USERNAME)
        config.password = emc.getString(PASSWORD)

        this.mailClient = MailClient.create(vertx, config)
    }

    override suspend fun login(username: Int, password: String): CommonResult<JsonObject> {
        val sql = "SELECT * FROM fp_user WHERE username=#{username}"
        val optional = DBUtil.findOne(FPUserRowMapper.INSTANCE, sql, mapOf(Pair(USERNAME, username))).await()
        if (optional.isEmpty) {
            return CommonResult.fail("用户信息不存在!")
        }
        val user = optional.get()
        if (!user.password.equals(password)) {
            return CommonResult.fail("密码错误!")
        }
        return CommonResult.success(this.flushSession(user))
    }

    override suspend fun VCode(uuid: String, code: String, email: String): CommonResult<String> {

        val key = this.redisKey(email)
        val resp = RedisUtil.redisAPI().ttl(key).await()
        if (resp.toInteger() > 0) {
            return CommonResult.fail("请勿频繁发送验证码!")
        }
        val str = GraService.create(vertx).decode(uuid)
        if (str != code.trim()) {
            return CommonResult.fail("验证码错误!")
        }
        val sql = "SELECT * FROM fp_user WHERE email=#{email}"
        val optional = DBUtil.findOne(FPUserRowMapper.INSTANCE, sql, mapOf(Pair(EMAIL, email))).await()
        if (optional.isPresent) {
            return CommonResult.fail("该邮箱已注册其他帐号!")
        }
        val VCode = 1000 + (Math.random() * 10000).toInt()
        val message = MailMessage()
        message.from = ""
        message.to = arrayListOf(email)
        message.text = "你正在申请注册Fishpond,你的验证码:$VCode,若非本人操作,请忽略本信息!"

        mailClient.sendMail(message).await()
        RedisUtil.redisAPI().setex(key, "120", VCode.toString()).await()

        return CommonResult.success("发送成功!")
    }

    private fun redisKey(email: String): String {
        return "fishpond:register:$email"
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