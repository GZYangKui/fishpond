package cn.navclub.fishpond.server.impl

import cn.navclub.fishpond.core.config.Constant
import cn.navclub.fishpond.server.BaseUnitTest
import cn.navclub.fishpond.server.util.CoroutineUtil
import io.vertx.core.Vertx
import io.vertx.core.json.Json
import io.vertx.ext.web.client.WebClient
import io.vertx.ext.web.multipart.MultipartForm
import io.vertx.junit5.VertxTestContext
import io.vertx.kotlin.coroutines.await
import org.junit.jupiter.api.Test

class WebTest : BaseUnitTest() {
    @Test
    fun `test upload file`(vertx: Vertx, ctx: VertxTestContext) {
        CoroutineUtil.launch({
            val sessionId = login(vertx, ctx)
            val client = WebClient.create(vertx)
            val request = client
                .post(httPort, host, "/api/file/upload")
                .putHeader(Constant.SESSION_ID, sessionId)
            val form = MultipartForm.create()
            form.binaryFileUpload(
                "iso",
                "debian.log",
                "/home/yangkui/java_error_in_idea_9003.log",
                "oct/application"
            )
            val resp = request.sendMultipartForm(form).await()
            this.httpRespCheck<String>(ctx, resp)
            println(resp.bodyAsJsonObject())
            ctx.completeNow()
        }, ctx::failNow)
    }
}