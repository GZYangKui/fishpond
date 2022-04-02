package cn.navclub.fishpond.server

import cn.navclub.fishpond.server.config.GlobalIDGen
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CountDownLatch

class GlobalIDGenTest {
    @Test
    fun `test generate global id`() {
        val id = GlobalIDGen.createGen(0).globalId()
        println(id)
    }

    @Test
    fun `test concurrent global id`() {
        val latch = CountDownLatch(2)
        val gen = GlobalIDGen.createGen(0)
        val map = ConcurrentHashMap<Long, Long>()
        val runnable: () -> Unit = {
            for (i in 0..5000) {
                val id = gen.globalId()
                if (map.contains(id)) {
                    Assertions.fail<String>("[{$id}]重复")
                } else {
                    map[id] = 0
                }
            }
            latch.countDown()
        }
        Thread(runnable).start()
        Thread(runnable).start()

        latch.await()
    }
}