package cn.navclub.fishpond.server.impl

import cn.navclub.fishpond.protocol.impl.DefaultDecoder
import cn.navclub.fishpond.server.BaseUnitTest
import io.vertx.core.Vertx
import io.vertx.junit5.VertxTestContext
import org.junit.jupiter.api.Test

class SocketTest : BaseUnitTest() {
    @Test
    fun test(vertx: Vertx, ctx: VertxTestContext) {
        val future = this.createNetClient(vertx, ctx)
        future.onSuccess {
            it.handler(
                DefaultDecoder
                    .create()
                    .handler { pro ->
                        println(pro.toString())
                        ctx.completeNow()
                    }
                    .exHandler { t ->
                        ctx.failNow(t)
                    }
            )
        }
    }
}