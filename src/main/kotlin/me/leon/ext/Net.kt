package me.leon.ext

import java.net.HttpURLConnection
import java.net.URL

private const val DEFAULT_READ_TIME_OUT = 30000
private const val DEFAULT_CONNECT_TIME_OUT = 30000
const val RESPONSE_OK = 200

fun String.readBytesFromNet(method: String = "GET") =
    runCatching {
        (URL(this).openConnection() as HttpURLConnection)
            .apply {
                connectTimeout = DEFAULT_CONNECT_TIME_OUT
                readTimeout = DEFAULT_READ_TIME_OUT
                setRequestProperty("Accept-Language", "zh-CN,zh;q=0.9,en;q=0.8")
                setRequestProperty(
                    "user-agent",
                    "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) " +
                        "Chrome/86.0.4240.198 Safari/537.36"
                )
                requestMethod = method
            }
            .takeIf { it.responseCode == RESPONSE_OK }
            ?.inputStream
            ?.readBytes()
            ?: byteArrayOf()
    }
        .getOrElse {
            println("read bytes err ${it.stacktrace()} ")
            byteArrayOf()
        }

fun String.readFromNet(resumeUrl: String = ""): String =
    runCatching { if (startsWith("http")) String(this.readBytesFromNet()) else "" }.getOrElse {
        println("read err ${it.stacktrace()} ")
        if (resumeUrl.isEmpty()) "" else resumeUrl.readFromNet()
    }

fun String.readHeadersFromNet() =
    runCatching {
        (URL(this).openConnection() as HttpURLConnection)
            .apply {
                connectTimeout = DEFAULT_CONNECT_TIME_OUT
                readTimeout = DEFAULT_READ_TIME_OUT
                setRequestProperty("Accept-Language", "zh-CN,zh;q=0.9,en;q=0.8")
                setRequestProperty(
                    "user-agent",
                    "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) " +
                        "Chrome/86.0.4240.198 Safari/537.36"
                )
            }
            .headerFields
            .toList()
            .joinToString(System.lineSeparator()) {
                it.second
                    .foldIndexed(StringBuilder()) { i, acc, s ->
                        acc.apply {
                            acc.append("${it.first}: $s").apply {
                                if (i != it.second.lastIndex)
                                    append(System.lineSeparator()).also { println("_____") }
                            }
                        }
                    }
                    .toString()
            }
    }
        .getOrElse {
            val stacktrace = it.stacktrace()
            println("read bytes err $stacktrace ")
            stacktrace
        }
