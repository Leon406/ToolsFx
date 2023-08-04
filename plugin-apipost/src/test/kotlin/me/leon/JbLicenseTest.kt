package me.leon

import java.io.File
import kotlin.test.Test
import kotlinx.coroutines.*
import me.leon.ext.readFromNet
import me.leon.ext.toFile
import me.leon.toolsfx.plugin.net.HttpUrlUtil
import me.leon.toolsfx.plugin.net.HttpUrlUtil.toParams
import me.leon.toolsfx.plugin.net.HttpUrlUtil.verifySSL

private const val TMP_FILE = "C:\\Users\\Leon\\Desktop\\jblicense.txt"
private val REG_LINK = "https?://[^\"<]+".toRegex()
private val REG_FILTER_LINK =
    "\\.(?:png|html|js|css)|github\\.com|greasyfork\\.org|sms-activate\\.org".toRegex()
private const val UA =
    "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) " +
        "Chrome/95.0.4638.69 Safari/537.36"

const val RPC_RELEASE = "/rpc/releaseTicket.action?"
const val RPC = "/rpc/obtainTicket.action?"
val queries
    get() =
        mapOf(
                "machineId" to "b66470e8-8a99-4de3-9a19-95d4f8c64d7d",
                "productCode" to "49c202d4-ac56-452b-bb84-735056242fb3",
                "salt" to System.currentTimeMillis(),
                "clientVersion" to "13",
                "hostName" to "DESKTOP-V4GADAN",
                "userName" to "Leon",
                "ticketId" to "ld7cgjawjz",
            )
            .toParams()

val regMsg = "<message>([^<]+)</message>".toRegex()

class JbLicenseTest {

    @Test
    fun checkJbAuth() {
        val server = "http://vip.52shizhan.cn"
        HttpUrlUtil.get("$server$RPC_RELEASE$queries").also {
            println(it)
            if (it.data.contains("<responseCode>OK</responseCode>")) {
                HttpUrlUtil.get("$server$RPC$queries".also { println(it) }).also {
                    println(regMsg.find(it.data)?.groupValues?.get(1))
                    println()
                    println(it.data)
                }
            }
        }
    }

    @Test
    fun licenseServerValidate() {
        //        HttpUrlUtil.setupProxy(Proxy.Type.SOCKS,"127.0.0.1",7890)
        val servers = mutableSetOf<String>()
        val localServers = parseFromFile()
        println("localServers ${localServers.size}")
        servers.addAll(localServers)
        println("servers total ${servers.size}")
        val sortedServers = servers.toSortedSet()
        HttpUrlUtil.setupProxy()
        HttpUrlUtil.followRedirect = true
        verifySSL(false)
        var tmpServers = sortedServers.toSet()
        val okServers = mutableSetOf<String>()
        repeat(1) {
            runBlocking {
                tmpServers
                    .map { async(Dispatchers.IO) { it to checkUrl(it) } }
                    .awaitAll()
                    .filter { it.second }
                    .map { it.first }
                    .also {
                        okServers.addAll(it)
                        tmpServers = tmpServers - okServers
                        println("\tok\n${it.joinToString("\n")}")
                    }
            }
        }
        println("${okServers.size} / ${sortedServers.size} ")
        //        println("\tfail\n${sortedServers - okServers}")
        println("\tok\n${okServers.joinToString("\n")}")
    }

    private fun parseFromFile(file: File = TMP_FILE.toFile()): MutableSet<String> {
        if (!file.exists()) {
            return mutableSetOf()
        }

        return file
            .readLines()
            .distinct()
            .filterNot { it.startsWith("#") || it.isBlank() }
            .toMutableSet()
    }

    private fun checkUrl(url: String): Boolean {

        runCatching {
            HttpUrlUtil.get("$url$RPC_RELEASE$queries").also {
                if (it.data.contains("<responseCode>OK</responseCode>")) {
                    HttpUrlUtil.get("$url$RPC$queries").also {
                        regMsg.find(it.data)?.groupValues?.get(1).also { println("$url\t\t$it") }
                            ?: return true
                    }
                } else {
                    return false
                }
            }
        }
        return false
    }

    @Test
    fun check() {
        checkUrl("https://35.188.104.230").also { println(it) }
        HttpUrlUtil.get("https://www.baidu.com/").also { println(it.data.length) }
        verifySSL(false)
        checkUrl("https://35.188.104.230").also { println(it) }
        HttpUrlUtil.get("https://www.baidu.com/").also { println(it.data.length) }
        verifySSL(true)
        checkUrl("https://35.188.104.230").also { println(it) }
        HttpUrlUtil.get("https://www.baidu.com/").also { println(it.data.length) }
        verifySSL(false)
        checkUrl("https://35.188.104.230").also { println(it) }
        HttpUrlUtil.get("https://www.baidu.com/").also { println(it.data.length) }
    }

    @Test
    fun gptMirror() {
        val urls =
            arrayOf(
                "https://c.aalib.net/tool/chatgpt/",
                "https://chatgpts.ninvfeng.xyz/",
                "https://jichangtuijian.com/chatgpt%E5%A5%97%E5%A3%B3%E9%95%9C%E5%83%8F.html",
            )

        mutableMapOf<String, Any>().toParams()
        runBlocking {
            urls
                .map { async { it.readFromNet() } }
                .awaitAll()
                .forEach {
                    REG_LINK.findAll(it)
                        .map { it.value }
                        .distinct()
                        .filterNot { REG_FILTER_LINK.containsMatchIn(it) }
                        .toList()
                        .map {
                            async(Dispatchers.IO) {
                                it to
                                    runCatching { HttpUrlUtil.get(it).code == 200 }
                                        .getOrDefault(false)
                            }
                        }
                        .awaitAll()
                        .filter { it.second }
                        .map { it.first }
                        .forEach { println(it) }
                }
        }
    }
}
