package me.leon.toolsfx.plugin.net

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

val separator = """\s*[\^\\]\s+""".toRegex()
val winEscapeReg = """\^([{%\d])""".toRegex()

fun String.winEscape() = replace("^\\^\"", "\"").replace(winEscapeReg, "$1")

fun String.parseCurl(): Request {
    var r = this
    // 兼容旧版
    if (r.contains("\n") && !r.contains(separator)) {
        r = r.replace("\n", " \\\n")
    }
    return r.split(separator)
        .map { it.trim() }
        .fold(Request("")) { req, s ->
            when {
                s.startsWith("-X ") -> req.method = s.removeFirstAndEndQuotes(3).trim()
                s.startsWith("-H ") ->
                    with(s.removeFirstAndEndQuotes(3)) {
                        req.headers[substringBefore(":")] = substringAfter(":").trim().winEscape()
                    }

                s.startsWith("--data-raw ") -> {
                    if (req.method == "GET") {
                        req.method = "POST"
                    }
                    req.rawBody =
                        s.removeFirstAndEndQuotes(11).winEscape().also { println("raw $it") }
                }

                s.startsWith("--data-binary ") -> {
                    if (req.method == "GET") {
                        req.method = "POST"
                    }
                    req.rawBody = s.removeFirstAndEndQuotes(14).winEscape()
                }

                s.startsWith("--data ") -> {
                    if (req.method == "GET") {
                        req.method = "POST"
                    }
                    req.rawBody = s.removeFirstAndEndQuotes(7).winEscape()
                }

                s.startsWith("-d ") -> {
                    if (req.method == "GET") {
                        req.method = "POST"
                    }
                    req.rawBody = s.removeFirstAndEndQuotes(3).winEscape()
                }

                s.startsWith("curl ") ->
                    with(s.removeFirstAndEndQuotes(5)) {
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
