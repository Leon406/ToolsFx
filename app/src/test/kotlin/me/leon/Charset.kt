package me.leon

import java.net.URLDecoder
import java.net.URLEncoder
import kotlin.test.Test
import kotlin.test.assertEquals
import me.leon.ext.*

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
        val bytes = raw.toByteArray(GBK)
        URLEncoder.encode(bytes.toString(GBK), "gbk")?.replace("+", "%20").also { println(it) }
    }

    @Test
    fun recoverEncoding() {
        val dd = "璋冭В瀹℃牳绠＄悊".also { println(it.recoverEncoding()) }
        dd.toByteArray(Charsets.UTF_8).toString(GBK).also { println(it) }
        "杩愮淮瀹夊叏".also { println(it.recoverEncoding()) }

        val text = "好好学习天天向上"

        // 古文码  不认识的古文夹杂日韩文  utf-8以gbk读取
        var encode = text.toByteArray(Charsets.UTF_8).toString(GBK)
        println(encode)
        println(encode.recoverEncoding())
        // 口子码, 无法还原  gbk以utf-8读取
        encode = text.toByteArray(GBK).toString(Charsets.UTF_8)
        println(encode)
        println(encode.recoverEncoding())
        // 符号码, 可还原  utf-8以iso8859-1读取
        encode = text.toByteArray(Charsets.UTF_8).toString(Charsets.ISO_8859_1)
        println(encode)
        println(encode.recoverEncoding())
        // 拼音码, 可还原  gbk以iso8859-1读取
        encode = text.toByteArray(GBK).toString(Charsets.ISO_8859_1)
        println(encode)
        println(encode.recoverEncoding())

        println("~~~~~")
        // 问句码, 长度偶数正确,奇数最后字符变问号  不可还原 utf8编码 转gbk后再转utf8
        encode =
            text
                .substring(0, 7)
                .toByteArray(Charsets.UTF_8)
                .toString(GBK)
                .toByteArray(GBK)
                .toString(Charsets.UTF_8)
        println(encode)
        println(encode.recoverEncoding())

        // 锟拷码  不可还原  gbk编码 转utf8后再转gbk
        encode =
            text.toByteArray(GBK).toString(Charsets.UTF_8).toByteArray(Charsets.UTF_8).toString(GBK)
        println(encode)
        println(encode.recoverEncoding())
    }

    @Test
    fun `half and full width char`() {
        val raw = "ABCDEF GHIJLMNOPQRSTUVWXYZ,?., 1234567890"
        val full = "ＡＢＣＤＥＦ　ＧＨＩＪＬＭＮＯＰＱＲＳＴＵＶＷＸＹＺ，？．，　１２３４５６７８９０"
        assertEquals(full, raw.toFullWidth())
        assertEquals(raw, full.toHalfWidth())
    }
}
