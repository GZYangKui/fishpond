package cn.navclub.fishpond.server

import cn.navclub.fishpond.protocol.impl.DefaultDecoder
import io.vertx.core.Vertx
import io.vertx.core.net.NetSocket
import io.vertx.junit5.VertxExtension
import io.vertx.junit5.VertxTestContext
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(VertxExtension::class)
internal class TestSocket {

    @Test
    fun `test socket connect`(vertx: Vertx, ctx: VertxTestContext) {

        vertx.createNetClient().connect(9000, "127.0.0.1") {
            if (it.failed()) {
                ctx.failNow(it.cause())
            } else {
                val socket = it.result()
                val decoder = DefaultDecoder.create()
                    .handler { pro ->
                        println(pro.toString())
                        ctx.completeNow()
                    }
                socket.handler(decoder)
            }
        }
    }

}