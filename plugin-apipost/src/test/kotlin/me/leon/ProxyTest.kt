package me.leon

import java.net.InetSocketAddress
import java.net.Proxy
import kotlin.test.Test
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.runBlocking
import me.leon.toolsfx.plugin.net.HttpUrlUtil
import okhttp3.*

class ProxyTest {
    private val user = "qLgJhyws3g"
    private val pass = "Gc8loDSz3u"
    private val ip = "1.1.1.1"
    private val port = 37104

    @Test
    fun proxy() {
        HttpUrlUtil.setupProxy(Proxy.Type.HTTP, ip, port, user, pass)
        println(HttpUrlUtil.get("https://httpbin.org/anything"))
        HttpUrlUtil.setupProxy()
        println(HttpUrlUtil.get("https://httpbin.org/anything"))
    }

    @Test
    fun ok() {

        val proxies = arrayOf("1.1.1.1:80", "1.1.1.1:55283")

        runBlocking {
            proxies
                .map { p ->
                    async(Dispatchers.IO) {
                        val (ip, port) = p.split(":")
                        val proxy = Proxy(Proxy.Type.HTTP, InetSocketAddress(ip, port.toInt()))
                        val client = OkHttpClient.Builder().proxy(proxy).build()
                        val request = Request.Builder().url("https://httpbin.org/anything").build()
                        val response =
                            runCatching { client.newCall(request).execute() }.getOrElse { null }
                        p to (response?.code == 200)
                    }
                }
                .awaitAll()
                .filter { it.second }
                .map { it.first }
                .also { println(it) }
        }
    }
}
