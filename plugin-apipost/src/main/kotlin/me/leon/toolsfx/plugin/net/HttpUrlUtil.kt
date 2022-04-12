package me.leon.toolsfx.plugin.net

import java.io.DataOutputStream
import java.io.File
import java.net.*
import java.util.UUID
import javax.net.ssl.HttpsURLConnection
import kotlin.collections.component1
import kotlin.collections.component2
import kotlin.collections.set
import kotlin.system.measureTimeMillis
import tornadofx.*

object HttpUrlUtil {
    private val httpsDelegate by lazy {
        val clazz = Class.forName("sun.net.www.protocol.https.HttpsURLConnectionImpl")
        clazz.getDeclaredField("delegate").apply { isAccessible = true }
    }

    private var DEFAULT_PRE_ACTION: (Request) -> Unit = {}
    private var DEFAULT_POST_ACTION: (ByteArray) -> String = { it.decodeToString() }
    private var isDebug = false
    var timeOut = 10000
    private var proxy: Proxy = Proxy.NO_PROXY
    var downloadFolder = File(File("").absoluteFile, "downloads")
    private var preAction: (Request) -> Unit = DEFAULT_PRE_ACTION
    private var postAction: (ByteArray) -> String = DEFAULT_POST_ACTION

    private const val PREFIX = "--"
    private const val LINE_END = "\r\n"
    private const val CONTENT_TYPE_FORM_DATA = "multipart/form-data"

    fun setupProxy(type: Proxy.Type, host: String, port: Int) {
        proxy =
            if (type == Proxy.Type.DIRECT) Proxy.NO_PROXY
            else Proxy(type, InetSocketAddress(host, port))
    }

    fun setupProxy(proxy: Proxy = Proxy.NO_PROXY) {
        HttpUrlUtil.proxy = proxy
    }

    fun addPreHandle(action: (Request) -> Unit) {
        preAction = action
    }

    fun addPostHandle(action: (ByteArray) -> String) {
        postAction = action
    }

    val globalHeaders =
        mutableMapOf(
            "Accept" to "*/*",
            "Connection" to "Keep-Alive",
            "Content-Type" to "application/json; charset=utf-8",
            "User-Agent" to
                "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko)" +
                    " Chrome/86.0.4240.198 Safari/537.36",
        )

    fun get(
        url: String,
        params: MutableMap<String, Any> = mutableMapOf(),
        headers: MutableMap<String, Any> = mutableMapOf(),
        isDownload: Boolean = false
    ): Response {
        val req = Request(url, "GET", params, headers)
        preAction(req)
        val realUrl =
            URL(req.url.takeIf { req.params.isEmpty() } ?: "${req.url}?${req.params.toParams()}")
        val conn = realUrl.openConnection(proxy) as HttpURLConnection
        var rsp: String
        val realHeaders = makeHeaders(req.headers)
        val time = measureTimeMillis {
            conn.requestMethod = req.method
            for ((k, v) in realHeaders) conn.setRequestProperty(k, v.toString())
            httpConfig(conn)
            conn.connect()
            if (isDownload) {
                if (!downloadFolder.exists()) downloadFolder.mkdirs()
                File(downloadFolder, NetHelper.getNetFileName(conn))
                    .also { rsp = "fileLocation: ${it.absolutePath}" }
                    .outputStream()
                    .buffered()
                    .use { it.write(conn.inputStream.readBytes()) }
            } else {
                rsp =
                    if (conn.responseCode == HttpURLConnection.HTTP_OK) {
                        conn.inputStream.use { postAction(it.readBytes()) }
                    } else {
                        conn.body().decodeToString()
                    }
            }
        }
        return afterResponse(conn, time, rsp)
    }

    private fun httpConfig(conn: HttpURLConnection, info: String = "") {
        conn.doOutput = true
        conn.instanceFollowRedirects = true
        conn.readTimeout = timeOut
        conn.connectTimeout = timeOut
        showRequestInfo(conn, info)
    }

    private fun afterResponse(conn: HttpURLConnection, time: Long, rsp: String): Response {
        if (conn.doInput) (conn.errorStream ?: conn.inputStream).close()
        showResponseInfo(conn, time, rsp)
        val responseHeaders =
            conn.headerFields.entries.fold(mutableMapOf<String?, Any>()) { acc, mutableEntry ->
                acc.apply { this[mutableEntry.key] = mutableEntry.value.joinToString(";") }
            }
        return Response(conn.responseCode, rsp, responseHeaders, time)
    }

    fun request(
        url: String,
        method: String,
        params: MutableMap<String, Any> = mutableMapOf(),
        headers: MutableMap<String, Any> = mutableMapOf()
    ): Response {
        val req = Request(url, method, params, headers)
        preAction(req)
        val realUrl =
            req.url.takeIf { req.params.isEmpty() } ?: "${req.url}?${req.params.toParams()}"
        val conn = URL(realUrl).openConnection(proxy) as HttpURLConnection
        var rsp: String
        val header = makeHeaders(req.headers)
        val time = measureTimeMillis {
            if (method == "PATCH" || method == "CONNECT") patchConnection(conn, method)
            else conn.requestMethod = req.method
            for ((k, v) in header) conn.setRequestProperty(k, v.toString())
            httpConfig(conn)
            conn.connect()
            rsp =
                if (conn.responseCode == HttpURLConnection.HTTP_OK) {
                    conn.inputStream.use { postAction(it.readBytes()) }
                } else {
                    conn.body().decodeToString()
                }
        }
        return afterResponse(conn, time, rsp)
    }

    private fun patchConnection(http: HttpURLConnection, method: String = "PATCH") {
        val target = if (http is HttpsURLConnection) httpsDelegate.get(http) else http
        val f = HttpURLConnection::class.java.getDeclaredField("method")
        f.isAccessible = true
        f.set(target, method)
    }

    private fun makeHeaders(headers: MutableMap<String, Any>): MutableMap<String, Any> {
        val composeHeaders = mutableMapOf<String, Any>()
        composeHeaders.putAll(globalHeaders)
        composeHeaders.putAll(headers)
        return composeHeaders
    }

    private fun HttpURLConnection.body() = (errorStream ?: inputStream).readBytes()

    fun post(
        url: String,
        params: MutableMap<String, Any> = mutableMapOf(),
        headers: MutableMap<String, Any> = mutableMapOf(),
        isJson: Boolean = false,
    ): Response {
        val req = Request(url, "POST", params, headers)
        preAction(req)
        val data = if (isJson) req.params.toJson() else req.params.toParams()
        return postData(url, data, headers)
    }

    fun getBody(
        url: String,
        data: String,
        headers: MutableMap<String, Any> = mutableMapOf()
    ): Response {
        val req = Request(url, "GET", mutableMapOf(), headers)
        preAction(req)
        val conn = URL(url).openConnection(proxy) as HttpURLConnection
        var rsp: String

        val header = makeHeaders(req.headers)
        val dataBytes = data.toByteArray()
        val time = measureTimeMillis {
            conn.requestMethod = req.method
            for ((k, v) in header) conn.setRequestProperty(k, v.toString())
            if (dataBytes.isNotEmpty())
                conn.addRequestProperty("Content-Length", dataBytes.size.toString())
            httpConfig(conn, data)
            conn.connect()
            conn.outputStream.write(dataBytes)
            conn.outputStream.flush()
            conn.outputStream.close()
            rsp =
                if (conn.responseCode == HttpURLConnection.HTTP_OK) {
                    conn.inputStream.use { postAction(it.readBytes()) }
                } else {
                    conn.body().decodeToString()
                }
        }

        return afterResponse(conn, time, rsp)
    }

    fun postData(
        url: String,
        data: String,
        headers: MutableMap<String, Any> = mutableMapOf()
    ): Response {
        val req = Request(url, "POST", mutableMapOf(), headers)
        preAction(req)
        val conn = URL(url).openConnection(proxy) as HttpURLConnection
        var rsp: String

        val header = makeHeaders(req.headers)
        val dataBytes = data.toByteArray()
        val time = measureTimeMillis {
            conn.requestMethod = req.method
            for ((k, v) in header) conn.setRequestProperty(k, v.toString())
            if (dataBytes.isNotEmpty())
                conn.addRequestProperty("Content-Length", dataBytes.size.toString())
            httpConfig(conn, data)
            conn.connect()
            conn.outputStream.write(dataBytes)
            conn.outputStream.flush()
            conn.outputStream.close()
            rsp =
                if (conn.responseCode == HttpURLConnection.HTTP_OK) {
                    conn.inputStream.use { postAction(it.readBytes()) }
                } else {
                    conn.body().decodeToString()
                }
        }

        return afterResponse(conn, time, rsp)
    }

    fun postFile(
        url: String,
        files: List<File>,
        name: String = "file",
        params: MutableMap<String, Any> = mutableMapOf(),
        headers: MutableMap<String, Any> = mutableMapOf()
    ): Response {
        val req = Request(url, "POST", params, headers)
        val boundary = "Leon406_" + UUID.randomUUID().toString().replace("-", "")

        preAction(req)
        val conn = URL(url).openConnection(proxy) as HttpURLConnection
        var rsp: String
        val header = makeHeaders(req.headers)
        val time = measureTimeMillis {
            conn.requestMethod = req.method
            for ((k, v) in header) conn.setRequestProperty(k, v.toString())

            conn.setRequestProperty("Content-Type", "$CONTENT_TYPE_FORM_DATA; boundary=$boundary")
            httpConfig(conn, "octet-stream")
            DataOutputStream(conn.outputStream).use { out ->
                val sbParams = makeMultiPartParamBody(req, boundary)
                out.write(sbParams.toString().also { println(it) }.toByteArray())
                for (file in files) {
                    val sb = makeMultipartFileBody(boundary, name, file)
                    out.write(sb.toString().also { println(it) }.toByteArray())
                    file.inputStream().use {
                        out.write(it.readBytes().also { println(it.decodeToString()) })
                    }
                    out.write(LINE_END.toByteArray())
                    out.write(
                        (PREFIX + boundary + PREFIX + LINE_END).also { println(it) }.toByteArray()
                    )
                }
                out.flush()
            }

            conn.connect()
            rsp =
                if (conn.responseCode == HttpURLConnection.HTTP_OK) {
                    conn.inputStream.use { postAction(it.readBytes()) }
                } else {
                    conn.body().decodeToString()
                }
        }
        return afterResponse(conn, time, rsp)
    }

    private fun makeMultipartFileBody(
        boundary: String,
        name: String,
        file: File
    ): java.lang.StringBuilder? {
        return StringBuilder(PREFIX)
            .append(boundary)
            .append(LINE_END)
            .append("Content-Disposition: form-data; name=\"$name\"; filename=\"${file.name}\"")
            .append(LINE_END)
            .append("Content-Type: application/octet-stream; charset=UTF-8")
            .append(LINE_END)
            .append(LINE_END)
    }

    private fun makeMultiPartParamBody(req: Request, boundary: String): StringBuilder {
        val sbParams = StringBuilder()
        for ((k, v) in req.params) {
            sbParams
                .append(PREFIX)
                .append(boundary)
                .append(LINE_END)
                .append("Content-Disposition: form-data; name=\"$k\"")
                .append(LINE_END)
                .append(LINE_END) // 参数头设置完以后需要两个换行，然后才是参数内容
                .append(v)
                .append(LINE_END)
        }
        return sbParams
    }

    private fun showResponseInfo(conn: HttpURLConnection, time: Long, rsp: String) {
        if (!isDebug) return
        val sb = StringBuilder()
        sb.append("<-- ")
            .append(conn.requestMethod)
            .append(" ")
            .append(conn.headerFields[null]?.joinToString(""))
            .append(" ")
            .append(conn.url.toString())
            .append(" (${time}ms)")
            .appendLine()
        conn.headerFields.filter { it.key != null }.forEach { (t, u) ->
            sb.append("\t$t: ${u.joinToString(";")}").appendLine()
        }
        sb.appendLine()
            .also {
                if (rsp.isNotEmpty())
                    it.append("\t")
                        .append("body:")
                        .appendLine()
                        .append(rsp.split("\n").joinToString("\n") { "\t\t$it" })
                        .appendLine()
            }
            .append("<-- END HTTP")
        println(sb.toString())
    }

    private fun showRequestInfo(conn: HttpURLConnection, req: String) {
        if (!isDebug) return
        val sb = StringBuilder()
        sb.append("--> ")
            .append(conn.requestMethod)
            .append(" ")
            .append(conn.url.toString())
            .appendLine()
        conn.requestProperties.entries.fold(sb) { acc, mutableEntry ->
            acc.apply {
                append("\t")
                append(mutableEntry.key)
                    .append(": ")
                    .append(mutableEntry.value.joinToString(";"))
                    .appendLine()
            }
        }
        sb.appendLine()
            .also { if (req.isNotEmpty()) it.append("\t").append("body:").append(req).appendLine() }
            .append("--> END ${conn.requestMethod}")
        println(sb.toString())
    }

    fun Map<String, Any>.toParams() = entries.joinToString("&") { it.key + "=" + it.value }

    private fun Map<String, Any>.toJson(): String =
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
