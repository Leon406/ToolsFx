package me.leon

import java.net.URLDecoder
import java.net.URLEncoder
import java.nio.charset.Charset
import org.junit.Test

class Charset {
    val raw = "开发工具集合 by leon406@52pojie.cn"
    @Test
    fun urlEncode() {
        URLEncoder.encode(raw, "utf-8").also {
            println(it)
            println(URLDecoder.decode(it, "utf-8"))
        }
        URLEncoder.encode(raw, "gbk").also {
            println(it)
            println(URLDecoder.decode(it, "gbk"))
        }
        val bytes = raw.toByteArray(Charset.forName("gbk"))
        val encoded =
            URLEncoder.encode(String(bytes, Charset.forName("gbk")), "gbk")
                ?.replace("+", "%20")
                .also { println(it) }

        URLDecoder.decode(encoded, "gbk").toByteArray()
    }
}
