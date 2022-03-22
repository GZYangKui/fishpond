package cn.navclub.fishpond.server.service.impl

import cn.navclub.fishpond.core.config.Constant.*
import cn.navclub.fishpond.core.util.StrUtil
import cn.navclub.fishpond.mapper.entity.FPUser
import cn.navclub.fishpond.mapper.entity.FPUserRowMapper
import cn.navclub.fishpond.protocol.api.CommonResult
import cn.navclub.fishpond.server.internal.ITCode
import cn.navclub.fishpond.server.internal.ITModel
import cn.navclub.fishpond.server.node.SessionVerticle
import cn.navclub.fishpond.server.service.BaseService
import cn.navclub.fishpond.server.service.KaptService
import cn.navclub.fishpond.server.service.UserService
import cn.navclub.fishpond.server.util.DBUtil
import cn.navclub.fishpond.server.util.RedisUtil
import io.vertx.core.Vertx
import io.vertx.core.json.JsonObject

import io.vertx.ext.mail.MailClient
import io.vertx.ext.mail.MailConfig
import io.vertx.ext.mail.MailMessage
import io.vertx.kotlin.coroutines.await
import io.vertx.mysqlclient.MySQLClient

import io.vertx.sqlclient.templates.SqlTemplate
import java.time.LocalDate

class
UserServiceImpl(private val vertx: Vertx) : UserService, BaseService(vertx) {
    private val emc: JsonObject
    private val mailClient: MailClient

    init {
        val config = MailConfig()
        this.emc = this.config(EMAIL)

        config.isSsl = true
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
        val str = KaptService.create(vertx).decode(uuid)
        val index = str.indexOf("/")
        val cs = str.substring(0, index)
        //获取图片验证码剩余过期市场
        val expire = (str.substring(index + 1).toLong() - System.currentTimeMillis()) / 1000
        if (expire <= 0) {
            return CommonResult.fail("图形验证过期,请刷新后重试!")
        }
        if (cs != code.trim()) {
            return CommonResult.fail("验证码错误!")
        }
        val key = this.redisKey(email)
        val resp = RedisUtil.redisAPI().ttl(key).await()
        if (resp.toInteger() > 0) {
            return CommonResult.fail("请勿频繁发送验证码!")
        }
        val sql = "SELECT * FROM fp_user WHERE email=#{email}"
        val optional = DBUtil.findOne(FPUserRowMapper.INSTANCE, sql, mapOf(Pair(EMAIL, email))).await()
        if (optional.isPresent) {
            return CommonResult.fail("该邮箱已注册其他帐号!")
        }
        val VCode = 1000 + (Math.random() * 10000).toInt()
        val message = MailMessage()
        message.subject = "新用户注册"
        message.cc = arrayListOf(email)
        message.from = emc.getString(USERNAME)
        message.text = "你正在申请注册Fishpond,你的验证码:$VCode,若非本人操作,请忽略本信息!"

        mailClient.sendMail(message).await()
        RedisUtil.redisAPI().setex(key, (expire + 3).toString(), VCode.toString()).await()

        return CommonResult.success("发送成功!")
    }

    override suspend fun register(email: String, code: String, pw: String): CommonResult<String> {
        val key = this.redisKey(email)
        val resp = RedisUtil.redisAPI().get(key).await() ?: return CommonResult.fail("验证码过期!")
        if (resp.toString() != code) {
            return CommonResult.fail("验证码不正确!")
        }

        return this.transaction<CommonResult<String>> { con, ctx ->
            var sql = "SELECT * FROM fp_user WHERE email=#{email}"
            val rs = SqlTemplate.forQuery(con, sql).execute(mapOf(Pair(EMAIL, email))).await()
            if (rs.size() > 0) {
                return@transaction CommonResult.fail("该邮箱已注册过!")
            }
            sql =
                "INSERT INTO fp_user(nickname,password,create_time,email) VALUES(#{nickname},#{password},#{createTime},#{email})"
            val param: MutableMap<String, Any> = HashMap()

            param[EMAIL] = email
            param[PASSWORD] = pw
            param[CREATE_TIME] = LocalDate.now()
            param[NICKNAME] = StrUtil.rdStr(6)

            //插入数据
            var row = SqlTemplate.forUpdate(con, sql).execute(param).await()

            sql = "UPDATE fp_user SET username=#{username} WHERE id=#{id}"

            //更新用户帐号
            if (row.rowCount() > 0) {
                val id = row.property(MySQLClient.LAST_INSERTED_ID)

                param[ID] = id
                param[USERNAME] = (1000 + id)

                row = SqlTemplate.forUpdate(con, sql).execute(param).await()
            }

            if (row.rowCount() <= 0) {
                //回滚事物
                ctx.rollback().await()
                return@transaction CommonResult.fail("注册失败,请重试!")
            }

            return@transaction CommonResult.success("注册成功")
        }
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