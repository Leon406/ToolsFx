package me.leon

import java.io.File
import java.net.Proxy
import kotlin.test.Ignore
import kotlin.test.Test
import kotlinx.coroutines.*
import me.leon.ext.stacktrace
import me.leon.ext.toFile
import me.leon.toolsfx.plugin.net.HttpUrlUtil
import me.leon.toolsfx.plugin.net.HttpUrlUtil.verifySSL

private const val RUSHB_URL = "https://rushb.pro/article/JetBrains-license-server.html"
private val REG_HTML_CODE = "(?s)<code class=\"lang-url\">(.+)</code>".toRegex()
private val REG_SHODAN_CODE =
    "https://account\\.jetbrains\\.com/fls-auth</strong>\\?.*?url=(.*?)/auth".toRegex()
private const val SHODAN_URL =
    "https://www.shodan.io/search?query=Location%3A+https%3A%2F%2Faccount.jetbrains.com%2Ffls-auth"

private const val TMP_FILE = "C:\\Users\\Leon\\Desktop\\jblicense.txt"

class JbLicenseTest {

    @Test
    @Ignore
    fun list() {
        val response = HttpUrlUtil.get(RUSHB_URL)
        REG_HTML_CODE.findAll(response.data).forEach { println(it.groupValues[1].lines()) }
    }

    @Test
    @Ignore
    fun sodan() {
        HttpUrlUtil.setupProxy(Proxy.Type.SOCKS, "127.0.0.1", 7890)
        val response = HttpUrlUtil.get(SHODAN_URL)
        REG_SHODAN_CODE.findAll(response.data).forEach { println(it.groupValues[1].lines()) }
    }

    @Test
    //    @Ignore
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
                .map { it to async(DISPATCHER) { checkUrl(it) } }
                .filter { it.second.await() }
                .map { it.first }
                .also {
                    println("${it.size} / ${sortedServers.size} ")
                    println("\tok\n${it.joinToString("\n")}")
                    println("\tfail\n${(sortedServers - it).joinToString("\n")}")
                }
        }
    }

    private fun crawlFromNet(): MutableSet<String> {
        val response = HttpUrlUtil.get(RUSHB_URL)
        val servers = mutableSetOf<String>()
        REG_HTML_CODE.findAll(response.data).forEach { servers.addAll(it.groupValues[1].lines()) }
        println("success from RUSHUB ${servers.size}")
        runCatching {
                val response2 = HttpUrlUtil.get(SHODAN_URL)
                REG_SHODAN_CODE.findAll(response2.data).forEach {
                    servers.addAll(it.groupValues[1].lines())
                }
            }
            .getOrElse { println("error fetch  shoda ${it.stacktrace()}") }

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
        HttpUrlUtil.followRedirect = true
        val response = runCatching { HttpUrlUtil.get(url) }.getOrNull()
        val location = response?.headers?.get("Location")
        val hasAuth = location?.run { this.toString().contains("fls-auth") } ?: false

        // 域名设置followRedirect会自动跳转,只有ip会有location
        if (location == null) {
            return response?.data?.contains("loader_config={") ?: false
        }
        var validate = false
        if (hasAuth) {
            // 设置followRedirect会自动会重定向到 /login
            validate =
                runCatching {
                        HttpUrlUtil.get(location.toString()).data.contains("loader_config={")
                    }
                    .getOrDefault(false)
        }
        return validate
    }

    @Test
    fun check() {
        checkUrl("https://35.188.104.230").also { println(it) }
        verifySSL(false)
        checkUrl("https://35.188.104.230").also { println(it) }
        verifySSL(true)
        checkUrl("https://35.188.104.230").also { println(it) }
        verifySSL(false)
        checkUrl("https://35.188.104.230").also { println(it) }
    }
}
