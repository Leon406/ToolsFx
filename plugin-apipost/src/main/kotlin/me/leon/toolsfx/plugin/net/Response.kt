package me.leon.toolsfx.plugin.net

import me.leon.hash2String

data class Response(
    val code: Int,
    val data: ByteArray,
    val headers: MutableMap<String?, Any>,
    val time: Long,
) {
    val statusInfo: String
        get() =
            "${headers[null]}  Time : $time ms  " +
                "Size : ${headers["Content-Length"] ?: headers["content-length"] ?: "unknown"} Bytes"

    val length: Int
        get() = "${headers["Content-Length"] ?: headers["content-length"] ?: "-1"}".toInt()

    val headerInfo: String
        get() =
            headers
                .filterNot { it.key.isNullOrEmpty() }
                .entries
                .joinToString("\n\n") { "${it.key}: ${it.value}" }
}

data class LiteResponse(val code: Int, val dataLength: Int, val hash: String)

fun Response.toLiteResponse() = LiteResponse(code, length, data.hash2String())
