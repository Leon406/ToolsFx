package me.leon

import java.io.File
import java.net.Proxy
import kotlin.test.Test
import kotlinx.coroutines.*
import me.leon.ext.readFromNet
import me.leon.ext.toFile
import me.leon.toolsfx.plugin.net.HttpUrlUtil
import me.leon.toolsfx.plugin.net.HttpUrlUtil.verifySSL

private const val RUSHB_URL = "https://rushb.pro/article/JetBrains-license-server.html"
private val REG_HTML_TAG = "<[^>]+>".toRegex()
private const val TMP_FILE = "C:\\Users\\Leon\\Desktop\\jblicense.txt"
private val REG_LINK = "https?://[^\"<]+".toRegex()
private val REG_FILTER_LINK =
    "\\.(?:png|html|js|css)|github\\.com|greasyfork\\.org|sms-activate\\.org".toRegex()
private const val UA =
    "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) " +
        "Chrome/95.0.4638.69 Safari/537.36"

val commonHeaders = mutableMapOf<String, Any>("user-agent" to UA)

val RPC =
    "/rpc/obtainTicket.action?buildDate=20230328" +
        // 软件版本
        "&buildNumber=2023.1.4+Build+IU-231.9225.16" +
        "&clientVersion=14" +
        "&hostName=DESKTOP-V4GADAN" +
        "&machineId=b66470e8-8a99-4de3-9a19-95d4f8c64d7d" +
        "&productCode=49c202d4-ac56-452b-bb84-735056242fb3" +
        "&productFamilyId=49c202d4-ac56-452b-bb84-735056242fb3&salt=1690279992325" +
        "&secure=false" +
        "&userName=Leon" +
        "&version=2023100" +
        "&versionNumber=2023100"

val regMsg = "<message>([^<]+)</message>".toRegex()

class JbLicenseTest {

    @Test
    fun list() {
        HttpUrlUtil.setupProxy(Proxy.Type.SOCKS, "127.0.0.1", 7890)
        println(crawlFromRushb())
    }

    @Test
    fun checkJbAuth() {

        HttpUrlUtil.get("https://jb.samuraism.com$RPC").also {
            println(regMsg.find(it.data)?.groupValues?.get(1))
            println()
            println(it.data)
        }
    }

    @Test
    fun licenseServerValidate() {
        //        HttpUrlUtil.setupProxy(Proxy.Type.SOCKS,"127.0.0.1",7890)
        val servers = crawlFromNet()
        println("success from net ${servers.size}")
        val localServers = parseFromFile()
        println("localServers ${localServers.size}")
        servers.addAll(localServers)
        println("servers total ${servers.size}")
        val sortedServers = servers.toSortedSet()
        HttpUrlUtil.setupProxy()
        HttpUrlUtil.followRedirect = true
        verifySSL(false)
        runBlocking {
            sortedServers
                .map { async(Dispatchers.IO) { it to checkUrl(it) } }
                .awaitAll()
                .filter { it.second }
                .map { it.first }
                .also {
                    println("${it.size} / ${sortedServers.size} ")
                    println("\tok\n${it.joinToString("\n")}")
                    println("\tfail\n${(sortedServers - it).joinToString("\n")}")
                }
        }
    }

    private fun crawlFromNet(): MutableSet<String> {
        val servers = mutableSetOf<String>()
        runCatching {
            servers.addAll(crawlFromRushb())
            println("success from RUSHUB ${servers.size}")
        }
        return servers
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
            HttpUrlUtil.get("$url$RPC").also {
                regMsg.find(it.data)?.groupValues?.get(1) ?: return true
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

    private fun crawlFromRushb(): List<String> {
        val response = HttpUrlUtil.get(RUSHB_URL, headers = commonHeaders)

        return REG_HTML_TAG.replace(response.data, "")
            .substringAfter(RUSHB_URL)
            .substringBefore(RUSHB_URL)
            .lines()
            .filter { it.startsWith("http") }
            .map { it.substringBefore("捐赠") }
    }
}
