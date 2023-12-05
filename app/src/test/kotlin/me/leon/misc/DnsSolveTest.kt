package me.leon.misc

import java.io.File
import java.time.Duration
import kotlin.test.Ignore
import kotlin.test.Test
import kotlinx.coroutines.*
import me.leon.ext.readResourceText
import me.leon.misc.net.*
import org.xbill.DNS.*

/**
 * @author Leon
 * @since 2023-04-10 8:37
 * @email deadogone@gmail.com
 */
class DnsSolveTest {
    private val duration = Duration.ofSeconds(3L)
    private val okDns =
        File(TEST_PRJ_DIR, "dns/okDns").also {
            if (!it.exists()) {
                it.parentFile.mkdirs()
                it.createNewFile()
            }
        }

    @Test
    @Ignore
    fun dnsCheck() {
        var dns = readDns()
        println("before " + dns.size)
        dns = dns.batchTcPing(53).filter { it.second > 0 }.map { it.first }
        okDns.appendText(
            (dns - okDns.readText().lines().toSet()).joinToString(System.lineSeparator())
        )
        println(dns.size)
        okDns.appendText(System.lineSeparator())
    }

    @Test
    @Ignore
    fun dnsQuery() {
        val domain = "translate.googleapis.com"
        val queryMessage = domain.dnsQuery().query("45.90.28.217")
        println(queryMessage)
    }

    @Test
    @Ignore
    fun dnsLookup() {
        // 获取所有dns解析ip, 再ping或者tcping
        // 全球dns服务器 https://public-dns.info/
        // https://public-dns.info/nameservers-all.txt
        val domain = "translate.googleapis.com"
        val queryMessage = domain.dnsQuery()
        val resolves = mutableSetOf<String>()
        val dns = readOkDns()
        println("before " + dns.size)

        runBlocking {
            dns
                //                .take(1000)
                .map { host ->
                    async(DISPATCHER) {
                        runCatching {
                            queryMessage.query(host).run {
                                if (isNotEmpty()) {
                                    println("\t==>$host got $this")
                                    resolves.addAll(this)
                                }
                            }
                        }
                    }
                }
                .awaitAll()
        }

        println(resolves.size)
        val file =
            File(TEST_PRJ_DIR, "dns/$domain").also {
                if (!it.exists()) {
                    it.parentFile.mkdirs()
                    it.createNewFile()
                }
            }
        file.writeText(resolves.joinToString(System.lineSeparator()))
        resolves
            .batchPingResult()
            .filter { it.second > 0 }
            .sortedBy { it.second }
            .filterNot {
                it.first.contains(LOCAL_IP_A) ||
                    it.first.contains(LOCAL_IP_B) ||
                    it.first.contains(LOCAL_IP_C) ||
                    it.first == LOOPBACK_IP
            }
            .joinToString(System.lineSeparator()) { it.first }
            .also { println(it) }
    }

    @Test
    @Ignore
    fun dns() {
        val urls = readResourceText("/domains").lines().filterNot { it.startsWith("#") }
        assert(urls.size > 1)
        println(dnsSolve(urls))
    }

    @Test
    @Ignore
    fun ping() {
        val ipV6 = "fe80::389f:2c91:f9a7:2ad8%55"
        println(ipV6.ping())
        println(ipV6.connect(445))
    }

    private fun String.dnsQuery(type: Int = Type.A): Message {
        val queryRecord: Record = Record.newRecord(Name.fromString("$this."), type, DClass.IN)
        return Message.newQuery(queryRecord)
    }

    private fun Message.query(dns: String): List<String> {
        val r = SimpleResolver(dns)
        r.timeout = duration
        return r.send(this).getSection(Section.ANSWER).map { it.rdataToString() }
    }

    private fun readDns(): List<String> {
        return readResourceText("/dns.txt").lines().filter { !it.contains(":") }
    }

    private fun readOkDns(): List<String> {
        return okDns.readLines().filter { it.isNotBlank() }
    }
}
