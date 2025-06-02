package me.leon.misc

import kotlin.test.Test
import me.leon.misc.net.dnsSolveResult

class DnsTest {
    @Test
    fun dnsCheck() {
        println(dnsSolveResult(listOf("www.baidu.com", "aistudio.google.com", "linux.do")))
    }
}
