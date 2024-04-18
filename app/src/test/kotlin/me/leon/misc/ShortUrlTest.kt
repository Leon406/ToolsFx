package me.leon.misc

import kotlin.test.Ignore
import kotlin.test.Test
import me.leon.misc.net.*

/**
 * @author Leon
 * @since 2023-07-28 14:45
 * @email deadogone@gmail.com
 */
@Ignore
class ShortUrlTest {

    private val url = "https://github.com/Leon406/ToolsFx"

    @Test
    fun baka() {
        println(ShortUrl.baka(url))
    }

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
