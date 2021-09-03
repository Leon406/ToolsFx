package me.leon.ext

import java.net.HttpURLConnection
import java.net.URL

private const val DEFAULT_READ_TIME_OUT = 30000
private const val DEFAULT_CONNECT_TIME_OUT = 30000
const val RESPONSE_OK = 200

fun String.readBytesFromNet() =
    (URL(this).openConnection().apply {
            //                setRequestProperty("Referer",
            // "https://pc.woozooo.com/mydisk.php")
            connectTimeout = DEFAULT_CONNECT_TIME_OUT
            readTimeout = DEFAULT_READ_TIME_OUT
            setRequestProperty("Accept-Language", "zh-CN,zh;q=0.9,en;q=0.8")
            setRequestProperty(
                "user-agent",
                "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) " +
                    "Chrome/86.0.4240.198 Safari/537.36"
            )
        } as
            HttpURLConnection)
        .takeIf {
            //            println("$this __ ${it.responseCode}")
            it.responseCode == RESPONSE_OK
        }
        ?.inputStream
        ?.readBytes()
        ?: byteArrayOf()

fun String.readFromNet() =
    runCatching { String(this.readBytesFromNet()) }.getOrElse {
        println("read err ${it.message}")
        ""
    }
