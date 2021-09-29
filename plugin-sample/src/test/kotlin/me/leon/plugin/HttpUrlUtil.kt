package me.leon.plugin

import java.io.File
import java.net.HttpURLConnection
import java.net.URL
import kotlin.system.measureTimeMillis
import org.junit.Test
import tornadofx.JsonBuilder

object HttpUrlUtil {

    private val commonHeaders =
        mutableMapOf(
            "accept" to "*/*",
            "connection" to "Keep-Alive",
            "content-type" to "application/json; charset=utf-8",
            "user-agent" to
                "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko)" +
                    " Chrome/86.0.4240.198 Safari/537.36",
        )

    fun get(
        url: String,
        params: MutableMap<String, Any> = mutableMapOf(),
        headers: MutableMap<String, Any> = mutableMapOf()
    ) {
        //        Proxy(Proxy.Type.DIRECT,InetSocketAddress(host,port))

        val conn =
            URL(url.takeIf { params.isEmpty() } ?: "$url?${params.toParams()}").openConnection() as
                HttpURLConnection
        var rsp = ""
        val time = measureTimeMillis {
            conn.requestMethod = "GET"
            makeHeaders(headers).forEach { (t, u) -> conn.setRequestProperty(t, u.toString()) }
            conn.doOutput = true
            conn.instanceFollowRedirects = true

            showRequestInfo(url, conn, "")
            conn.connect()

            if (conn.responseCode == HttpURLConnection.HTTP_OK) {
                rsp = conn.inputStream.readBytes().decodeToString().replace("\\u", "\\u")
            }
        }
        showResponseInfo(url, conn, time, rsp)
    }

    private fun makeHeaders(headers: MutableMap<String, Any>): MutableMap<String, Any> {
        val composeHeaders = mutableMapOf<String, Any>()
        composeHeaders.putAll(commonHeaders)
        composeHeaders.putAll(headers)
        return composeHeaders
    }

    fun post(
        url: String,
        params: MutableMap<String, Any> = mutableMapOf(),
        headers: MutableMap<String, Any> = mutableMapOf(),
        isJson: Boolean = false
    ) {
        //        Proxy(Proxy.Type.DIRECT,InetSocketAddress(host,port))
        val conn = URL(url).openConnection() as HttpURLConnection
        var rsp = ""
        val data = if (isJson) params.toJson() else params.toParams()
        val time = measureTimeMillis {
            conn.requestMethod = "POST"
            makeHeaders(headers).forEach { (t, u) -> conn.setRequestProperty(t, u.toString()) }
            conn.doOutput = true
            conn.instanceFollowRedirects = true
            showRequestInfo(url, conn, data)
            conn.outputStream.write(data.toByteArray())
            conn.connect()

            rsp =
                if (conn.responseCode == HttpURLConnection.HTTP_OK) {
                    conn.inputStream.readBytes().decodeToString()
                } else {
                    conn.errorStream.readBytes().decodeToString()
                }
        }
        showResponseInfo(url, conn, time, rsp)
    }

    private fun showResponseInfo(url: String, conn: HttpURLConnection, time: Long, rsp: String) {
        val sb = StringBuilder()
        sb.append("<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<")
            .appendLine()
            .append(conn.requestMethod)
            .append(" ")
            .append(url)
            .append("  ${time}ms")
            .appendLine()
            .appendLine()
            .append(conn.headerFields[null]?.joinToString(""))
            .appendLine()
        conn.headerFields.filter { it.key != null }.forEach { (t, u) ->
            sb.append("$t: ${u.joinToString(";")}").appendLine()
        }
        sb.appendLine().append(rsp)
        println(sb.toString())
    }

    private fun showRequestInfo(url: String, conn: HttpURLConnection, req: String) {
        val sb = StringBuilder()
        sb.append(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>")
            .appendLine()
            .append(conn.requestMethod)
            .append(" ")
            .append(url)
            .append(" HTTP/1.1")
            .appendLine()
        conn.requestProperties.entries.fold(sb) { acc, mutableEntry ->
            acc.apply {
                append(mutableEntry.key)
                    .append(": ")
                    .append(mutableEntry.value.joinToString(";"))
                    .appendLine()
            }
        }
        sb.appendLine()
        sb.append(req)
        println(sb.toString())
    }

    @JvmStatic
    fun main(args: Array<String>) {
        File("C:\\Users\\Leon\\Desktop\\anji_lawyere.txt").readLines().forEach {
            get(
                "https://device.jpush.cn/v3/aliases/" + it,
                mutableMapOf(),
                mutableMapOf(
                    "Authorization" to
                        "Basic NTgxMjE1ZTk2OWM4NzliOGVjYzk5NDhlOjNhYmI4OGU1M2ZkYjRhZDI4MzA3NTRmNQ=="
                )
            )
        }

        //        get("https://www.baidu.com")
        //
        // get("http://sf.0575.org/plugin/api/2019/getinfo?action=userlist&page=1&filterArea=373")
        //        get("http://wifi.vivo.com.cn/generate_204")
        //        get(
        //            "http://suo.im/api.htm", mutableMapOf(
        //                "url" to "https://netcut.cn/leon",
        //                "key" to "5e71b8fbb1b63c4165e7f2eb@0311fc0df3c5fbe37e455c26601880a1",
        //                "expireDate" to "2099-03-31"
        //            )
        //        )
        //
        //        post(
        //            "http://api.map.baidu.com/geoconv/v1/", mutableMapOf(
        //                "coords" to "114.21892734521,29.575429778924",
        //                "from" to "1",
        //                "to" to "5",
        //                "ak" to "V0AKhZ3wN8CTU3zx8lGf4QvwyOs5rGIn"
        //            )
        //        )
        //        post(
        //            "http://api.map.baidu.com/geoconv/v1/", mutableMapOf(
        //                "coords" to "114.21892734521,29.575429778924",
        //                "from" to "1",
        //                "to" to "5",
        //                "ak" to "V0AKhZ3wN8CTU3zx8lGf4QvwyOs5rGIn"
        //            ), mutableMapOf(), true
        //        )
        //        post("https://lab.magiconch.com/api/nbnhhsh/guess", mutableMapOf("text" to
        // "cylx"), isJson = true)
    }

    fun Map<String, Any>.toParams() = entries.joinToString("&") { it.key + "=" + it.value }

    @Test
    fun jsonParse() {
        JsonBuilder()
            .add("a", "b")
            .add("c", "b")
            .add("2", "3")
            .add("list", listOf("a", "b", "c"))
            .add("4", 3)
            .build()
            .toString()
            .also { println(it) }
    }

    fun Map<String, Any>.toJson(): String =
        entries
            .fold(JsonBuilder()) { acc, entry ->
                acc.apply {
                    when (entry.value) {
                        is Number -> acc.add(entry.key, entry.value as Number)
                        is String -> acc.add(entry.key, entry.value as String)
                        is Boolean -> acc.add(entry.key, entry.value as String)
                        is Iterable<*> -> acc.add(entry.key, entry.value as Iterable<Any>?)
                        else -> acc.add(entry.key, "")
                    }
                }
            }
            .build()
            .toString()
}
