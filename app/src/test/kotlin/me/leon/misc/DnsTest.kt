package me.leon.misc

import kotlin.test.Test
import me.leon.misc.net.dnsQuery
import me.leon.misc.net.dnsSolveResult

class DnsTest {
    @Test
    fun dnsCheck() {
        println(dnsSolveResult(listOf("www.baidu.com", "aistudio.google.com", "linux.do")))
    }

    @Test
    fun dnsQuery() {
        "baidu.com".dnsQuery("TXT").also { println(it) }
    }
}
