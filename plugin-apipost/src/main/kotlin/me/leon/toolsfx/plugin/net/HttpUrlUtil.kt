package me.leon.toolsfx.plugin.net

import java.io.*
import java.net.*
import java.util.UUID
import kotlin.system.measureTimeMillis
import tornadofx.JsonBuilder

object HttpUrlUtil {
    var DEFAULT_PRE_ACTION: (Request) -> Unit = {}
    var DEFAULT_POST_ACTION: (ByteArray) -> String = { it.decodeToString() }
    var isDebug = true
    private var proxy: Proxy = Proxy.NO_PROXY
    var downloadFolder: String =
        File(File("").absoluteFile, "downloads").also { if (!it.exists()) it.mkdirs() }.absolutePath
    private var preAction: (Request) -> Unit = DEFAULT_PRE_ACTION
    private var postAction: (ByteArray) -> String = DEFAULT_POST_ACTION

    const val PREFIX = "--"
    const val LINE_END = "\r\n"
    const val CONTENT_TYPE_FORM_DATA = "multipart/form-data"

    fun setupProxy(type: Proxy.Type, host: String, port: Int) {
        proxy = Proxy(type, InetSocketAddress(host, port))
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
            "content-type" to "application/json; charset=utf-8",
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
        var rsp = ""
        val realHeaders = makeHeaders(req.headers)
        val time = measureTimeMillis {
            conn.requestMethod = req.method
            for ((k, v) in realHeaders) conn.setRequestProperty(k, v.toString())
            conn.doOutput = true
            conn.instanceFollowRedirects = true
            showRequestInfo(conn, "")
            conn.connect()
            if (isDownload) {
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
        if (conn.doInput) (conn.errorStream ?: conn.inputStream).close()
        showResponseInfo(conn, time, rsp)
        val responseHeaders =
            conn.headerFields.entries.fold(mutableMapOf<String?, Any>()) { acc, mutableEntry ->
                acc.apply { this[mutableEntry.key] = mutableEntry.value.joinToString(";") }
            }
        return Response(conn.responseCode, rsp, responseHeaders, time)
    }

    fun head(
        url: String,
        params: MutableMap<String, Any> = mutableMapOf(),
        headers: MutableMap<String, Any> = mutableMapOf()
    ) = request(url, "HEAD", params, headers)

    fun put(
        url: String,
        params: MutableMap<String, Any> = mutableMapOf(),
        headers: MutableMap<String, Any> = mutableMapOf()
    ) = request(url, "PUT", params, headers)

    fun patch(
        url: String,
        params: MutableMap<String, Any> = mutableMapOf(),
        headers: MutableMap<String, Any> = mutableMapOf()
    ): Response {
        headers["X-HTTP-Method-Override"] = "PATCH"
        return request(url, "POST", params, headers)
    }

    fun delete(
        url: String,
        params: MutableMap<String, Any> = mutableMapOf(),
        headers: MutableMap<String, Any> = mutableMapOf()
    ) = request(url, "DELETE", params, headers)

    fun options(
        url: String,
        params: MutableMap<String, Any> = mutableMapOf(),
        headers: MutableMap<String, Any> = mutableMapOf()
    ) = request(url, "OPTIONS", params, headers)

    fun request(
        url: String,
        method: String,
        params: MutableMap<String, Any> = mutableMapOf(),
        headers: MutableMap<String, Any> = mutableMapOf()
    ): Response {
        if (method == "PATCH") return patch(url, params, headers)
        val req = Request(url, method, params, headers)
        preAction(req)
        val realUrl =
            req.url.takeIf { req.params.isEmpty() } ?: "${req.url}?${req.params.toParams()}"
        val conn = URL(realUrl).openConnection(proxy) as HttpURLConnection
        var rsp: String
        val header = makeHeaders(req.headers)
        val time = measureTimeMillis {
            conn.requestMethod = req.method
            for ((k, v) in header) conn.setRequestProperty(k, v.toString())
            conn.doOutput = true
            conn.instanceFollowRedirects = true

            showRequestInfo(conn, "")
            conn.connect()
            rsp =
                if (conn.responseCode == HttpURLConnection.HTTP_OK) {
                    conn.inputStream.use { postAction(it.readBytes()) }
                } else {
                    conn.body().decodeToString()
                }
        }
        showResponseInfo(conn, time, rsp)
        val responseHeaders =
            conn.headerFields.entries.fold(mutableMapOf<String?, Any>()) { acc, mutableEntry ->
                acc.apply { this[mutableEntry.key] = mutableEntry.value.joinToString(";") }
            }
        return Response(conn.responseCode, rsp, responseHeaders, time)
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

    fun postData(url: String, data: String, headers: MutableMap<String, Any>): Response {
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
            conn.doOutput = true
            conn.instanceFollowRedirects = true
            showRequestInfo(conn, data)
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
        if (conn.doInput) (conn.errorStream ?: conn.inputStream).close()
        showResponseInfo(conn, time, rsp)
        val responseHeaders =
            conn.headerFields.entries.fold(mutableMapOf<String?, Any>()) { acc, mutableEntry ->
                acc.apply { this[mutableEntry.key] = mutableEntry.value.joinToString(";") }
            }
        return Response(conn.responseCode, rsp, responseHeaders, time)
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
            conn.doOutput = true
            conn.instanceFollowRedirects = true
            for ((k, v) in header) conn.setRequestProperty(k, v.toString())

            conn.setRequestProperty("content-type", "$CONTENT_TYPE_FORM_DATA; boundary=$boundary")
            showRequestInfo(conn, "octet-stream")
            DataOutputStream(conn.outputStream).use {
                val sbParams = makeMultiPartParamBody(req, boundary)
                it.write(sbParams.toString().also { println(it) }.toByteArray())
                for (file in files) {
                    val sb = makeMultipartFileBody(boundary, name, file)
                    it.write(sb.toString().also { println(it) }.toByteArray())
                    it.write(file.inputStream().readBytes().also { println(it.decodeToString()) })
                    it.write(LINE_END.toByteArray())
                    it.write(
                        (PREFIX + boundary + PREFIX + LINE_END).also { println(it) }.toByteArray()
                    )
                }
                it.flush()
            }

            conn.connect()
            rsp =
                if (conn.responseCode == HttpURLConnection.HTTP_OK) {
                    conn.inputStream.use { postAction(it.readBytes()) }
                } else {
                    conn.body().decodeToString()
                }
        }
        if (conn.doInput) (conn.errorStream ?: conn.inputStream).close()
        showResponseInfo(conn, time, rsp)
        val responseHeaders =
            conn.headerFields.entries.fold(mutableMapOf<String?, Any>()) { acc, mutableEntry ->
                acc.apply { this[mutableEntry.key] = mutableEntry.value.joinToString(";") }
            }
        return Response(conn.responseCode, rsp, responseHeaders, time)
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
            //            .append(" HTTP/1.1")
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
