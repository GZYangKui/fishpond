package cn.navclub.fishpond.server

import io.vertx.core.Future
import io.vertx.core.Promise
import io.vertx.core.Vertx
import io.vertx.core.net.NetSocket
import io.vertx.junit5.VertxExtension
import io.vertx.junit5.VertxTestContext
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(VertxExtension::class)
open class BaseUnitTest {

    fun createNetClient(vertx: Vertx, ctx: VertxTestContext): Future<NetSocket> {
        val promise = Promise.promise<NetSocket>()
        vertx.createNetClient().connect(9000, "127.0.0.1") {
            if (it.failed()) {
                promise.fail(it.cause())
                ctx.failNow(it.cause())
            } else {
                promise.complete(it.result())
            }
        }
        return promise.future();
    }

}