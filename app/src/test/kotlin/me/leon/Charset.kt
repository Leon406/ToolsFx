package me.leon

import java.net.URLDecoder
import java.net.URLEncoder
import java.nio.charset.Charset
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import me.leon.ext.math.circleIndex
import me.leon.ext.recoverEncoding
import org.junit.Test

class Charset {
    private val raw = "开发工具集合 by leon406@52pojie.cn"

    @Test
    fun urlEncode() {
        URLEncoder.encode(raw, "utf-8").also {
            assertEquals(
                "%E5%BC%80%E5%8F%91%E5%B7%A5%E5%85%B7%E9%9B%86%E5%90%88+by+leon406%4052pojie.cn",
                it
            )
            assertEquals(raw, URLDecoder.decode(it, "utf-8"))
        }
        URLEncoder.encode(raw, "gbk").also {
            assertEquals("%BF%AA%B7%A2%B9%A4%BE%DF%BC%AF%BA%CF+by+leon406%4052pojie.cn", it)
            assertEquals(raw, URLDecoder.decode(it, "gbk"))
        }
        val bytes = raw.toByteArray(Charset.forName("gbk"))
        URLEncoder.encode(bytes.toString(Charset.forName("gbk")), "gbk")?.replace("+", "%20").also {
            println(it)
        }
    }

    @Test
    fun recoverEncoding() {
        val dd = "璋冭В瀹℃牳绠＄悊".also { println(it.recoverEncoding()) }
        dd.toByteArray(Charsets.UTF_8).toString(Charset.forName("gbk")).also { println(it) }
        "杩愮淮瀹夊叏".also { println(it.recoverEncoding()) }

        for (i in 1..25) {
            assertTrue { i.circleIndex() == i.mod(26) }
        }
    }
}
