package me.leon.misc

import kotlin.test.Ignore
import kotlin.test.Test
import me.leon.misc.net.*

@Ignore
class ShortUrlTest {

    private val url = "https://github.com/Leon406/ToolsFx"

    @Test
    fun tinyUrl() {
        println(ShortUrl.tinyUrl(url))
    }

    @Test
    fun aadTw() {
        println(ShortUrl.aadTw(url))
    }

    @Test
    fun ecx() {
        println(ShortUrl.ecx(url))
    }

    @Test
    fun e() {
        ShortUrlEnum.entries.map { it.name }.forEach { println(url.shortUrl(it)) }
    }
}
