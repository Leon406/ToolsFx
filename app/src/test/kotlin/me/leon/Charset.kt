package me.leon

import java.net.URLDecoder
import java.net.URLEncoder
import java.nio.charset.Charset
import me.leon.ext.recoverEncoding
import org.junit.Test

class Charset {
    private val raw = "开发工具集合 by leon406@52pojie.cn"
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

    @Test
    fun ee() {
        val dd = "璋冭В瀹℃牳绠＄悊".also { println(it.recoverEncoding()) }
        dd.toByteArray(Charsets.UTF_8).toString(Charset.forName("gbk")).also { println(it) }
        "杩愮淮瀹夊叏".also { println(it.recoverEncoding()) }
    }
}
