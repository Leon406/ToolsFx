package me.leon.toolsfx.plugin.net

import me.leon.ext.fromJson
import me.leon.ext.toJson
import me.leon.toolsfx.plugin.net.HttpUrlUtil.toParams

fun String.paramsParse() =
    split("&").fold(mutableMapOf<String, Any>()) { acc, param ->
        println(param)
        acc.apply {
            if (param.isNotEmpty()) {
                val (key, value) = param.split("=")
                acc[key] = value
            }
        }
    }

fun String.cookieParse() =
    split("; *".toRegex()).fold(mutableMapOf<String, Any>()) { acc, param ->
        acc.apply {
            if (param.isNotEmpty()) {
                val (key, value) = param.split("=")
                acc[key] = value
            }
        }
    }

fun String.parseCurl() =
    trim()
        // 去掉浏览器多余的分割符
        .replace("""[\^\\]""".toRegex(), "")
        .split("""\n|\r\n""".toRegex())
        .map { it.trim() }
        .fold(Request(this)) { acc, s ->
            acc.apply {
                when {
                    s.startsWith("curl") -> acc.url = s.removeFirstAndEndQuotes(5)
                    s.startsWith("-X") -> acc.method = s.removeFirstAndEndQuotes(3)
                    s.startsWith("--data-raw") ->
                        acc.method = "POST".also { acc.rawBody = s.removeFirstAndEndQuotes(11) }
                    s.startsWith("-d") ->
                        acc.method =
                            ("POST".takeIf { acc.method == "GET" } ?: acc.method).also {
                                val value = s.removeFirstAndEndQuotes(3)
                                if (value.contains("@file")) {
                                    if (value.startsWith("{") || value.startsWith("[")) {
                                        acc.params.putAll(
                                            value.fromJson(MutableMap::class.java)
                                                as Map<out String, Any>
                                        )
                                    } else {
                                        acc.params.putAll(value.paramsParse().also { println(it) })
                                    }
                                } else if (
                                    this@parseCurl.contains("Content-Type: application/json", true)
                                ) {
                                    acc.rawBody = value
                                } else {
                                    acc.params.putAll(value.paramsParse())
                                }
                            }
                    s.startsWith("--data-binary") ->
                        acc.method =
                            ("POST".takeIf { acc.method == "GET" } ?: acc.method).also {
                                acc.rawBody = s.removeFirstAndEndQuotes(14)
                            }
                    s.startsWith("--data") ->
                        acc.method =
                            ("POST".takeIf { acc.method == "GET" } ?: acc.method).also {
                                val value = s.removeFirstAndEndQuotes(7)
                                if (value.contains("@file")) {
                                    acc.params.putAll(
                                        value.fromJson(MutableMap::class.java)
                                            as Map<out String, Any>
                                    )
                                } else if (
                                    this@parseCurl.contains("Content-Type: application/json", true)
                                ) {
                                    acc.rawBody = value
                                } else {
                                    acc.params.putAll(value.paramsParse())
                                }
                            }
                    s.startsWith("-H") ->
                        with(s.removeFirstAndEndQuotes(3)) {
                            acc.headers[substringBefore(":")] = substringAfter(":").trim()
                        }
                    else -> {}
                }
            }
        }
        .also { println(it) }

fun Request.toCurl(): String =
    StringBuilder()
        .append("curl $url")
        .also {
            if (method == "GET" && params.isNotEmpty()) it.append("?").append(params.toParams())
        }
        .appendLine()
        .append("-X $method")
        .appendLine()
        .also {
            for ((key, value) in headers) {
                it.append("-H \"$key:$value\"").appendLine()
            }
            if (fileParamName.isNotEmpty()) params[fileParamName] = "@file"
            val data =
                if (isJson) params.toJson() else if (method != "GET") params.toParams() else ""
            if (rawBody.isNotEmpty()) {
                it.append("--data-raw $rawBody")
            } else if (data.isNotEmpty()) {
                it.append("-d \"${data.replace("\"", "\\\"")}\"")
            }
        }
        .toString()

fun String.removeFirstAndEndQuotes(from: Int = 0) =
    substring(from).replace("^([\"'])(.*?)\\1?$".toRegex(), "$2").trim()
