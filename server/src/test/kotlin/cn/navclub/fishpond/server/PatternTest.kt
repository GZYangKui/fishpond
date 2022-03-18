package cn.navclub.fishpond.server

import cn.navclub.fishpond.server.pattern.PathMatter
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class PatternTest {
    @Test
    fun `test fix model`() {
        val matcher = PathMatter().matcher(
            "/api/ttt/test/tt",
            "/api/**/ttt/*/tt",
            false
        )

        Assertions.assertTrue(matcher, "混合模式匹配失败")
    }

    @Test
    fun `test signal model`() {
        val matcher = PathMatter().matcher(
            "/user/detail/10",
            "/user/detail/*",
            false
        )
        Assertions.assertTrue(matcher, "单级匹配失败")
    }

    @Test
    fun `test multi model`() {
        val matcher = PathMatter().matcher(
            "/user/detail/10",
            "/**",
            false
        )
        Assertions.assertTrue(matcher, "多级匹配失败")
    }

    @Test
    fun `precise matcher`() {
        val matcher = PathMatter().matcher(
            "/api/user/login",
            "/api/user/login",
            false
        )
        Assertions.assertTrue(matcher,"精确匹配失败")
    }
}