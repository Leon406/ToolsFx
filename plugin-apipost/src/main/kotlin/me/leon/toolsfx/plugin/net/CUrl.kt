package me.leon.toolsfx.plugin.net

import me.leon.ext.fromJson
import me.leon.ext.toJson
import me.leon.toolsfx.plugin.net.HttpUrlUtil.toParams

fun String.paramsParse() =
    split("&").fold(mutableMapOf<String, Any>()) { acc, param ->
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

val separator = """\s*[\^\\]\s+""".toRegex()
val winEscapeReg = """\^(\W)""".toRegex()

fun String.winEscape() = replace("^\\^\"", "\"").replace(winEscapeReg, "$1")

fun String.parseCurl(): Request {
    var r = this.winEscape()
    // 兼容旧版
    if (r.contains("\n") && !r.contains(separator)) {
        r = r.replace("\n", " \\\n")
    }

    return r.split(separator)
        .map { it.trim() }
        .fold(Request("")) { req, s ->
            val startIndex = s.indexOf(" ") + 1
            when {
                s.startsWith("-X ") -> req.method = s.removeFirstAndEndQuotes(startIndex).trim()
                s.startsWith("-H ") ->
                    with(s.removeFirstAndEndQuotes(startIndex)) {
                        req.headers[substringBefore(":")] = substringAfter(":").trim()
                    }
                s.startsWith("--data-binary ") || s.startsWith("--data-raw ") -> {
                    if (req.method == "GET") {
                        req.method = "POST"
                    }
                    val offset =
                        if (s.substring(startIndex).startsWith("$")) {
                            1
                        } else {
                            0
                        }
                    req.rawBody = s.removeFirstAndEndQuotes(startIndex + offset)
                }
                s.startsWith("--data") -> {
                    if (req.method == "GET") {
                        req.method = "POST"
                    }
                    s.properBody(req, r, startIndex)
                }
                s.startsWith("-d ") -> {
                    if (req.method == "GET") {
                        req.method = "POST"
                    }
                    s.properBody(req, r, startIndex)
                }
                s.startsWith("curl ") ->
                    with(s.removeFirstAndEndQuotes(startIndex)) {
                        if (startsWith("-X")) {
                            val str = substring(3)
                            req.method = str.substringBefore(" ").trim()
                            req.url = str.substringAfter(" ").removeFirstAndEndQuotes()
                        } else {
                            req.url = this
                        }
                    }
                else ->
                    if (s.startsWith("http")) {
                        req.url = this
                    }
            }
            req
        }
}

private fun String.properBody(acc: Request, s: String, from: Int = 3) {
    val value = removeFirstAndEndQuotes(from)
    if (value.contains("@file")) {
        if (value.startsWith("{") || value.startsWith("[")) {
            acc.params.putAll(value.fromJson(MutableMap::class.java) as Map<out String, Any>)
        } else {
            acc.params.putAll(value.paramsParse())
        }
    } else if (s.contains("Content-Type: application/json", true)) {
        acc.rawBody = value
    } else {
        acc.params.putAll(value.paramsParse())
    }
}

fun Request.toCurl(): String =
    buildString {
            append("curl \"$url\"  \\")
            if (method == "GET" && params.isNotEmpty()) append("?").append(params.toParams())
            appendLine()
            append("-X $method  \\")
            if (headers.isNotEmpty() || params.isNotEmpty() || rawBody.isNotEmpty()) {
                appendLine()
            }
            for ((key, value) in headers) {
                append("-H \"$key:$value\" \\").appendLine()
            }
            if (fileParamName.isNotEmpty()) params[fileParamName] = "@file"
            val data =
                if (isJson) params.toJson() else if (method != "GET") params.toParams() else ""
            if (rawBody.isNotEmpty()) {
                append("--data-raw $rawBody")
            } else if (data.isNotEmpty()) {
                append("-d \"${data.replace("\"", "\\\"")}\"")
            }
        }
        .trimEnd('\\')
        .trim()

fun String.removeFirstAndEndQuotes(from: Int = 0) =
    substring(from).replace("^([\"'])(.*?)\\1?$".toRegex(), "$2").trim('\'', '"')
