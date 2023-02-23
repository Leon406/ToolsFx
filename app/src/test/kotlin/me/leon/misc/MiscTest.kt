package me.leon.misc

import kotlin.test.Ignore
import kotlin.test.Test
import me.leon.misc.net.*

/**
 * @author Leon
 * @since 2023-01-31 16:25
 * @email deadogone@gmail.com
 */
class MiscTest {
    @Test
    fun port() {
        println("127.0.0.1".connect(7890, 20))
        val ports = 7882..10_000
        ports.filter { "127.0.0.1".connect(it) >= 0 }.forEach { println("~~~~~~~~$it") }
    }

    @Test
    @Ignore
    fun portAsync() {
        println("baidu.com".portScan())
    }

    @Test
    @Ignore
    fun lanScan() {
        println("192.168.0".lanScan())
    }
}
